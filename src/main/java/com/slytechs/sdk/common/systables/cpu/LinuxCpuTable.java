/*
 * Copyright 2005-2026 Sly Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slytechs.sdk.common.systables.cpu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Linux implementation of CPU table using /proc filesystem.
 *
 * @author Mark Bednarczyk
 */
public class LinuxCpuTable extends CpuTable {

	private static final Path CPUINFO_PATH = Path.of("/proc/cpuinfo");
	private static final Path STAT_PATH = Path.of("/proc/stat");

	@Override
	public LinuxCpuInfo getInfo() {
		String model = "";
		String vendor = "";
		int logicalCores = 0;
		Set<Integer> physicalIds = new HashSet<>();
		Set<Integer> coreIds = new HashSet<>();
		double frequency = 0.0;
		List<String> flags = List.of();

		try (var lines = Files.lines(CPUINFO_PATH)) {
			int currentProcessor = -1;
			int currentPhysicalId = -1;

			for (String line : lines.toList()) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}

				String[] parts = line.split(":", 2);
				if (parts.length < 2) {
					continue;
				}

				String key = parts[0].trim();
				String value = parts[1].trim();

				switch (key) {
				case "processor":
					currentProcessor = Integer.parseInt(value);
					logicalCores = Math.max(logicalCores, currentProcessor + 1);
					break;

				case "model name":
					if (model.isEmpty()) {
						model = value;
					}
					break;

				case "vendor_id":
					if (vendor.isEmpty()) {
						vendor = value;
					}
					break;

				case "physical id":
					currentPhysicalId = Integer.parseInt(value);
					physicalIds.add(currentPhysicalId);
					break;

				case "core id":
					if (currentPhysicalId >= 0) {
						// Track unique core IDs per physical CPU
						coreIds.add(currentPhysicalId * 1000 + Integer.parseInt(value));
					}
					break;

				case "cpu MHz":
					if (frequency == 0.0) {
						frequency = Double.parseDouble(value);
					}
					break;

				case "flags":
					if (flags.isEmpty()) {
						flags = Arrays.asList(value.split("\\s+"));
					}
					break;
				}
			}
		} catch (IOException e) {
			// Return defaults
		}

		int sockets = Math.max(1, physicalIds.size());
		int physicalCores = Math.max(1, coreIds.isEmpty() ? logicalCores / 2 : coreIds.size());

		return new LinuxCpuInfo(model, vendor, logicalCores, physicalCores, sockets, frequency, flags);
	}

	@Override
	public List<CpuStats> getStats() {
		List<CpuStats> stats = new ArrayList<>();

		try (var lines = Files.lines(STAT_PATH)) {
			for (String line : lines.toList()) {
				if (!line.startsWith("cpu")) {
					break; // CPU lines are at the top
				}

				parseCpuStatLine(line).ifPresent(stats::add);
			}
		} catch (IOException e) {
			// Return empty list
		}

		return stats;
	}

	@Override
	public Optional<CpuStats> getStats(int cpu) {
		return getStats().stream()
				.filter(s -> s.cpu() == cpu)
				.findFirst();
	}

	@Override
	public CpuStats getAggregateStats() {
		return getStats().stream()
				.filter(CpuStats::isAggregate)
				.findFirst()
				.orElse(new LinuxCpuStats(-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
	}

	@Override
	public Optional<CpuAffinity> getProcessAffinity(long pid) {
		Path statusPath = Path.of("/proc/" + pid + "/status");
		return parseAffinityFromStatus(statusPath);
	}

	@Override
	public Optional<CpuAffinity> getThreadAffinity(long pid, long tid) {
		Path statusPath = Path.of("/proc/" + pid + "/task/" + tid + "/status");
		return parseAffinityFromStatus(statusPath);
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}

	/**
	 * Parses a CPU stat line from /proc/stat.
	 * 
	 * Format: cpu[N] user nice system idle iowait irq softirq steal guest
	 * guest_nice
	 *
	 * @param line the stat line
	 * @return the parsed CPU stats, or empty if parsing fails
	 */
	private Optional<CpuStats> parseCpuStatLine(String line) {
		try {
			String[] parts = line.trim().split("\\s+");
			if (parts.length < 5) {
				return Optional.empty();
			}

			// Parse CPU number
			int cpu;
			if (parts[0].equals("cpu")) {
				cpu = -1; // Aggregate
			} else {
				cpu = Integer.parseInt(parts[0].substring(3));
			}

			// Parse time values (default to 0 if not present)
			long user = Long.parseLong(parts[1]);
			long nice = parts.length > 2 ? Long.parseLong(parts[2]) : 0;
			long system = parts.length > 3 ? Long.parseLong(parts[3]) : 0;
			long idle = parts.length > 4 ? Long.parseLong(parts[4]) : 0;
			long iowait = parts.length > 5 ? Long.parseLong(parts[5]) : 0;
			long irq = parts.length > 6 ? Long.parseLong(parts[6]) : 0;
			long softirq = parts.length > 7 ? Long.parseLong(parts[7]) : 0;
			long steal = parts.length > 8 ? Long.parseLong(parts[8]) : 0;
			long guest = parts.length > 9 ? Long.parseLong(parts[9]) : 0;
			long guestNice = parts.length > 10 ? Long.parseLong(parts[10]) : 0;

			return Optional.of(new LinuxCpuStats(
					cpu, user, nice, system, idle, iowait, irq, softirq, steal, guest, guestNice));

		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Parses CPU affinity from /proc/[pid]/status file.
	 *
	 * @param statusPath the path to status file
	 * @return the CPU affinity, or empty if not found
	 */
	private Optional<CpuAffinity> parseAffinityFromStatus(Path statusPath) {
		try (var lines = Files.lines(statusPath)) {
			for (String line : lines.toList()) {
				if (line.startsWith("Cpus_allowed_list:")) {
					String cpuList = line.substring("Cpus_allowed_list:".length()).trim();
					return Optional.of(CpuAffinity.parse(cpuList));
				}
			}
		} catch (IOException e) {
			// Return empty
		}

		return Optional.empty();
	}
}
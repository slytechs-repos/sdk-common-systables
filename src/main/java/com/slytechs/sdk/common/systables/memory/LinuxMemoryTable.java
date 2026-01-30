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
package com.slytechs.sdk.common.systables.memory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.slytechs.sdk.common.memory.MemorySize;
import com.slytechs.sdk.common.systables.Platform;

/**
 * Linux implementation of memory table using /proc filesystem.
 *
 * @author Mark Bednarczyk
 */
public class LinuxMemoryTable extends MemoryTable {

	private static final Path MEMINFO_PATH = Path.of("/proc/meminfo");

	@Override
	public LinuxMemoryInfo getInfo() {
		Map<String, Long> memInfo = parseMeminfo();

		// All values in /proc/meminfo are in kB
		MemorySize total = MemorySize.ofKilobytes(memInfo.getOrDefault("MemTotal", 0L));
		MemorySize free = MemorySize.ofKilobytes(memInfo.getOrDefault("MemFree", 0L));
		MemorySize available = MemorySize.ofKilobytes(memInfo.getOrDefault("MemAvailable", 0L));
		MemorySize buffers = MemorySize.ofKilobytes(memInfo.getOrDefault("Buffers", 0L));
		MemorySize cached = MemorySize.ofKilobytes(memInfo.getOrDefault("Cached", 0L));
		MemorySize swapTotal = MemorySize.ofKilobytes(memInfo.getOrDefault("SwapTotal", 0L));
		MemorySize swapFree = MemorySize.ofKilobytes(memInfo.getOrDefault("SwapFree", 0L));

		return new LinuxMemoryInfo(total, free, available, buffers, cached, swapTotal, swapFree);
	}

	@Override
	public Optional<LinuxProcessMemoryInfo> getProcessMemory(long pid) {
		Path statusPath = Path.of("/proc/" + pid + "/status");

		if (!Files.exists(statusPath)) {
			return Optional.empty();
		}

		Map<String, Long> status = parseStatus(statusPath);

		// All values in /proc/[pid]/status are in kB
		MemorySize vmSize = MemorySize.ofKilobytes(status.getOrDefault("VmSize", 0L));
		MemorySize vmRss = MemorySize.ofKilobytes(status.getOrDefault("VmRSS", 0L));
		MemorySize rssFile = MemorySize.ofKilobytes(status.getOrDefault("RssFile", 0L));
		MemorySize vmData = MemorySize.ofKilobytes(status.getOrDefault("VmData", 0L));
		MemorySize vmStk = MemorySize.ofKilobytes(status.getOrDefault("VmStk", 0L));

		return Optional.of(new LinuxProcessMemoryInfo(
				pid,
				vmSize,
				vmRss,
				rssFile, // Shared memory approximation
				vmData,
				vmStk));
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}

	/**
	 * Parses /proc/meminfo into a map of key-value pairs.
	 * 
	 * Format: MemTotal: 65536 kB
	 *
	 * @return map of memory info keys to values (in kB)
	 */
	private Map<String, Long> parseMeminfo() {
		Map<String, Long> info = new HashMap<>();

		try (var lines = Files.lines(MEMINFO_PATH)) {
			for (String line : lines.toList()) {
				String[] parts = line.split(":", 2);
				if (parts.length < 2) {
					continue;
				}

				String key = parts[0].trim();
				String value = parts[1].trim();

				// Remove " kB" suffix
				value = value.replace(" kB", "").trim();

				try {
					info.put(key, Long.parseLong(value));
				} catch (NumberFormatException e) {
					// Skip invalid lines
				}
			}
		} catch (IOException e) {
			// Return empty map
		}

		return info;
	}

	/**
	 * Parses /proc/[pid]/status into a map of key-value pairs.
	 * 
	 * Format: VmSize: 1234 kB
	 *
	 * @param statusPath the path to status file
	 * @return map of status keys to values (in kB)
	 */
	private Map<String, Long> parseStatus(Path statusPath) {
		Map<String, Long> status = new HashMap<>();

		try (var lines = Files.lines(statusPath)) {
			for (String line : lines.toList()) {
				String[] parts = line.split(":", 2);
				if (parts.length < 2) {
					continue;
				}

				String key = parts[0].trim();
				String value = parts[1].trim();

				// Remove " kB" suffix if present
				value = value.replace(" kB", "").trim();

				// Only parse memory-related fields
				if (key.startsWith("Vm") || key.startsWith("Rss")) {
					try {
						status.put(key, Long.parseLong(value));
					} catch (NumberFormatException e) {
						// Skip invalid lines
					}
				}
			}
		} catch (IOException e) {
			// Return empty map
		}

		return status;
	}
}
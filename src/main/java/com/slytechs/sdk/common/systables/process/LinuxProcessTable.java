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
package com.slytechs.sdk.common.systables.process;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.slytechs.sdk.common.memory.MemorySize;
import com.slytechs.sdk.common.systables.Platform;
import com.slytechs.sdk.common.systables.cpu.CpuAffinity;
import com.slytechs.sdk.common.systables.cpu.CpuTable;

/**
 * Linux implementation of process table using /proc filesystem.
 *
 * @author Mark Bednarczyk
 */
public class LinuxProcessTable extends ProcessTable {

	private static final Path PROC_PATH = Path.of("/proc");
	private static final long CLOCK_TICKS_PER_SECOND = getClockTicks();

	@Override
	public List<LinuxProcessInfo> list() {
		List<LinuxProcessInfo> processes = new ArrayList<>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(PROC_PATH, "[0-9]*")) {
			for (Path path : stream) {
				try {
					long pid = Long.parseLong(path.getFileName().toString());
					parseProcess(pid).ifPresent(processes::add);
				} catch (NumberFormatException e) {
					// Not a PID directory
				}
			}
		} catch (IOException e) {
			// Return what we have
		}

		return processes;
	}

	@Override
	public Optional<LinuxProcessInfo> lookup(long pid) {
		return parseProcess(pid);
	}

	@Override
	public List<LinuxProcessInfo> listByUser(long uid) {
		return list().stream()
				.filter(p -> p.uid() == uid)
				.toList();
	}

	@Override
	public List<LinuxProcessInfo> listByName(String name) {
		return list().stream()
				.filter(p -> p.name().equals(name))
				.toList();
	}

	@Override
	public List<LinuxProcessInfo> listByState(ProcessState state) {
		return list().stream()
				.filter(p -> p.state() == state)
				.toList();
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}

	/**
	 * Parses process information for a specific PID.
	 *
	 * @param pid the process ID
	 * @return the process info, or empty if process not found or can't be read
	 */
	private Optional<LinuxProcessInfo> parseProcess(long pid) {
		Path procDir = PROC_PATH.resolve(String.valueOf(pid));

		if (!Files.exists(procDir)) {
			return Optional.empty();
		}

		try {
			// Parse /proc/[pid]/stat for basic info
			Map<String, Object> statInfo = parseStat(procDir.resolve("stat"));

			// Parse /proc/[pid]/status for detailed info
			Map<String, String> statusInfo = parseStatus(procDir.resolve("status"));

			// Parse /proc/[pid]/cmdline
			Optional<String> cmdline = parseCmdline(procDir.resolve("cmdline"));

			// Extract fields
			String name = (String) statInfo.get("comm");
			long ppid = (Long) statInfo.get("ppid");
			ProcessState state = (ProcessState) statInfo.get("state");
			int priority = (Integer) statInfo.get("nice");
			long utime = (Long) statInfo.get("utime");
			long stime = (Long) statInfo.get("stime");
			long rssPages = (Long) statInfo.get("rss");

			// Get from status file
			int threads = Integer.parseInt(statusInfo.getOrDefault("Threads", "1"));
			long uid = Long.parseLong(statusInfo.getOrDefault("Uid", "0").split("\\s+")[0]);
			long vmSizeKb = Long.parseLong(statusInfo.getOrDefault("VmSize", "0"));

			// Calculate values
			long cpuTimeMillis = ((utime + stime) * 1000) / CLOCK_TICKS_PER_SECOND;
			MemorySize rss = MemorySize.ofBytes(rssPages * 4096); // Page size = 4096
			MemorySize vmSize = MemorySize.ofKilobytes(vmSizeKb);

			// Get CPU affinity
			Optional<CpuAffinity> affinity = CpuTable.linux().getProcessAffinity(pid);

			return Optional.of(new LinuxProcessInfo(
					pid, ppid, name, cmdline, state, uid, priority, threads,
					rss, vmSize, cpuTimeMillis, affinity));

		} catch (Exception e) {
			// Process may have disappeared, or we don't have permission
			return Optional.empty();
		}
	}

	/**
	 * Parses /proc/[pid]/stat file.
	 * 
	 * Format: pid (comm) state ppid pgrp session tty_nr tpgid flags minflt cminflt
	 * majflt cmajflt utime stime cutime cstime priority nice num_threads ...
	 *
	 * @param statPath the path to stat file
	 * @return map of parsed fields
	 */
	private Map<String, Object> parseStat(Path statPath) throws IOException {
		Map<String, Object> stat = new HashMap<>();
		String content = Files.readString(statPath);

		// Process name can contain spaces and is in parentheses
		int commStart = content.indexOf('(');
		int commEnd = content.lastIndexOf(')');

		if (commStart < 0 || commEnd < 0) {
			throw new IOException("Invalid stat format");
		}

		String comm = content.substring(commStart + 1, commEnd);
		String[] parts = content.substring(commEnd + 2).split("\\s+");

		stat.put("comm", comm);
		stat.put("state", ProcessState.fromCode(parts[0].charAt(0)));
		stat.put("ppid", Long.parseLong(parts[1]));
		stat.put("utime", Long.parseLong(parts[11]));
		stat.put("stime", Long.parseLong(parts[12]));
		stat.put("priority", Integer.parseInt(parts[15]));
		stat.put("nice", Integer.parseInt(parts[16]));
		stat.put("rss", Long.parseLong(parts[21]));

		return stat;
	}

	/**
	 * Parses /proc/[pid]/status file.
	 *
	 * @param statusPath the path to status file
	 * @return map of key-value pairs
	 */
	private Map<String, String> parseStatus(Path statusPath) {
		Map<String, String> status = new HashMap<>();

		try (var lines = Files.lines(statusPath)) {
			for (String line : lines.toList()) {
				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					String key = parts[0].trim();
					String value = parts[1].trim().replaceAll(" kB$", "");
					status.put(key, value);
				}
			}
		} catch (IOException e) {
			// Return empty map
		}

		return status;
	}

	/**
	 * Parses /proc/[pid]/cmdline file.
	 * 
	 * Command line arguments are separated by null bytes.
	 *
	 * @param cmdlinePath the path to cmdline file
	 * @return the command line, or empty if unavailable
	 */
	private Optional<String> parseCmdline(Path cmdlinePath) {
		try {
			byte[] bytes = Files.readAllBytes(cmdlinePath);
			if (bytes.length == 0) {
				return Optional.empty();
			}

			// Replace null bytes with spaces
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				if (b == 0) {
					sb.append(' ');
				} else {
					sb.append((char) b);
				}
			}

			String cmdline = sb.toString().trim();
			return cmdline.isEmpty() ? Optional.empty() : Optional.of(cmdline);

		} catch (IOException e) {
			return Optional.empty();
		}
	}

	/**
	 * Gets the system clock ticks per second (usually 100).
	 *
	 * @return clock ticks per second
	 */
	private static long getClockTicks() {
		// Try to get from system
		try {
			ProcessBuilder pb = new ProcessBuilder("getconf", "CLK_TCK");
			Process p = pb.start();
			byte[] output = p.getInputStream().readAllBytes();
			p.waitFor();
			return Long.parseLong(new String(output).trim());
		} catch (Exception e) {
			return 100; // Default value
		}
	}
}
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Represents CPU affinity - which CPUs a process/thread is allowed to run on.
 *
 * @author Mark Bednarczyk
 */
public record CpuAffinity(BitSet mask) {

	/**
	 * Parses CPU affinity from Linux format string.
	 * 
	 * <p>
	 * Examples:
	 * </p>
	 * <ul>
	 * <li>"0-31" → CPUs 0 through 31</li>
	 * <li>"0,2,4,6" → CPUs 0, 2, 4, 6</li>
	 * <li>"0-7,16-23" → CPUs 0-7 and 16-23</li>
	 * </ul>
	 *
	 * @param cpuList the CPU list string
	 * @return the CPU affinity
	 */
	public static CpuAffinity parse(String cpuList) {
		BitSet mask = new BitSet();

		if (cpuList == null || cpuList.isEmpty()) {
			return new CpuAffinity(mask);
		}

		String[] ranges = cpuList.split(",");
		for (String range : ranges) {
			range = range.trim();
			if (range.isEmpty()) {
				continue;
			}

			if (range.contains("-")) {
				// Range: "0-7"
				String[] parts = range.split("-", 2);
				int start = Integer.parseInt(parts[0].trim());
				int end = Integer.parseInt(parts[1].trim());
				for (int i = start; i <= end; i++) {
					mask.set(i);
				}
			} else {
				// Single CPU: "5"
				int cpu = Integer.parseInt(range);
				mask.set(cpu);
			}
		}

		return new CpuAffinity(mask);
	}

	/**
	 * Creates affinity for all CPUs (0 through cpuCount-1).
	 *
	 * @param cpuCount the number of CPUs
	 * @return affinity for all CPUs
	 */
	public static CpuAffinity all(int cpuCount) {
		BitSet mask = new BitSet(cpuCount);
		mask.set(0, cpuCount);
		return new CpuAffinity(mask);
	}

	/**
	 * Creates affinity for a single CPU.
	 *
	 * @param cpu the CPU number
	 * @return affinity for single CPU
	 */
	public static CpuAffinity of(int cpu) {
		BitSet mask = new BitSet();
		mask.set(cpu);
		return new CpuAffinity(mask);
	}

	/**
	 * Gets the list of allowed CPU numbers.
	 *
	 * @return list of CPU numbers
	 */
	public List<Integer> cpus() {
		List<Integer> cpus = new ArrayList<>();
		for (int i = mask.nextSetBit(0); i >= 0; i = mask.nextSetBit(i + 1)) {
			cpus.add(i);
		}
		return cpus;
	}

	/**
	 * Gets the number of allowed CPUs.
	 *
	 * @return count of allowed CPUs
	 */
	public int count() {
		return mask.cardinality();
	}

	/**
	 * Checks if a specific CPU is allowed.
	 *
	 * @param cpu the CPU number
	 * @return true if CPU is allowed
	 */
	public boolean isAllowed(int cpu) {
		return mask.get(cpu);
	}

	/**
	 * Formats affinity as a CPU list string (Linux format).
	 * 
	 * <p>
	 * Examples: "0-31", "0,2,4,6", "0-7,16-23"
	 * </p>
	 *
	 * @return the formatted CPU list
	 */
	public String toCpuList() {
		List<Integer> cpus = cpus();
		if (cpus.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		int rangeStart = cpus.get(0);
		int rangeLast = rangeStart;

		for (int i = 1; i < cpus.size(); i++) {
			int cpu = cpus.get(i);

			if (cpu == rangeLast + 1) {
				// Continue range
				rangeLast = cpu;
			} else {
				// End of range, output it
				appendRange(sb, rangeStart, rangeLast);
				sb.append(',');
				rangeStart = cpu;
				rangeLast = cpu;
			}
		}

		// Output final range
		appendRange(sb, rangeStart, rangeLast);

		return sb.toString();
	}

	private void appendRange(StringBuilder sb, int start, int end) {
		if (start == end) {
			sb.append(start);
		} else {
			sb.append(start).append('-').append(end);
		}
	}

	@Override
	public String toString() {
		return toCpuList();
	}
}
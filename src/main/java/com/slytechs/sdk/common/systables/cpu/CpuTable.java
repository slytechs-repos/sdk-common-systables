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

import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for CPU information and statistics access.
 *
 * @author Mark Bednarczyk
 */
public abstract class CpuTable {

	/**
	 * Gets the CPU table for the current platform.
	 *
	 * @return the CPU table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static CpuTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxCpuTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxCpuTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the CPU table for Linux.
	 *
	 * @return the Linux CPU table
	 */
	public static CpuTable linux() {
		return new LinuxCpuTable();
	}

	/**
	 * Gets CPU hardware information.
	 *
	 * @return the CPU information
	 */
	public abstract CpuInfo getInfo();

	/**
	 * Gets current CPU usage statistics for all CPUs.
	 * 
	 * <p>
	 * Returns aggregate stats (all CPUs combined) plus per-core stats.
	 * </p>
	 *
	 * @return list of CPU statistics (index 0 is aggregate, rest are per-core)
	 */
	public abstract List<CpuStats> getStats();

	/**
	 * Gets usage statistics for a specific CPU core.
	 *
	 * @param cpu the CPU core number
	 * @return the CPU statistics, or empty if invalid CPU number
	 */
	public abstract Optional<CpuStats> getStats(int cpu);

	/**
	 * Gets aggregate CPU statistics (all CPUs combined).
	 *
	 * @return the aggregate CPU statistics
	 */
	public abstract CpuStats getAggregateStats();

	/**
	 * Gets CPU affinity for a process.
	 *
	 * @param pid the process ID
	 * @return the CPU affinity, or empty if process not found
	 */
	public abstract Optional<CpuAffinity> getProcessAffinity(long pid);

	/**
	 * Gets CPU affinity for a thread.
	 *
	 * @param pid the process ID
	 * @param tid the thread ID
	 * @return the CPU affinity, or empty if thread not found
	 */
	public abstract Optional<CpuAffinity> getThreadAffinity(long pid, long tid);

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
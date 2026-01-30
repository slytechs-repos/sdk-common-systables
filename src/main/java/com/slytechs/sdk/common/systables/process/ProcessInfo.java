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

import java.util.Optional;

import com.slytechs.sdk.common.memory.MemorySize;
import com.slytechs.sdk.common.systables.cpu.CpuAffinity;

/**
 * Common interface for process information.
 *
 * @author Mark Bednarczyk
 */
public interface ProcessInfo {

	/**
	 * Gets the process ID.
	 *
	 * @return the PID
	 */
	long pid();

	/**
	 * Gets the parent process ID.
	 *
	 * @return the parent PID
	 */
	long ppid();

	/**
	 * Gets the process name (executable name).
	 *
	 * @return the process name
	 */
	String name();

	/**
	 * Gets the full command line.
	 *
	 * @return the command line, or empty if unavailable
	 */
	Optional<String> commandLine();

	/**
	 * Gets the process state.
	 *
	 * @return the state
	 */
	ProcessState state();

	/**
	 * Gets the user ID that owns this process.
	 *
	 * @return the user ID
	 */
	long uid();

	/**
	 * Gets the priority (nice value). Range: -20 (highest) to 19 (lowest).
	 *
	 * @return the nice value
	 */
	int priority();

	/**
	 * Gets the number of threads in this process.
	 *
	 * @return the thread count
	 */
	int threads();

	/**
	 * Gets the resident set size (physical memory).
	 *
	 * @return the RSS
	 */
	MemorySize rss();

	/**
	 * Gets the virtual memory size.
	 *
	 * @return the virtual size
	 */
	MemorySize virtualSize();

	/**
	 * Gets CPU time used by this process (user + system).
	 *
	 * @return CPU time in milliseconds
	 */
	long cpuTimeMillis();

	/**
	 * Gets CPU affinity (which CPUs this process can run on).
	 *
	 * @return the CPU affinity, or empty if unavailable
	 */
	Optional<CpuAffinity> cpuAffinity();

	/**
	 * Checks if this process is running.
	 *
	 * @return true if in RUNNING state
	 */
	default boolean isRunning() {
		return state().isRunning();
	}

	/**
	 * Checks if this process is alive (not zombie/dead).
	 *
	 * @return true if alive
	 */
	default boolean isAlive() {
		return !state().isDead();
	}
}
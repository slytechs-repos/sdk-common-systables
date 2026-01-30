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

import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for process information access.
 *
 * @author Mark Bednarczyk
 */
public abstract class ProcessTable {

	/**
	 * Gets the process table for the current platform.
	 *
	 * @return the process table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static ProcessTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxProcessTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxProcessTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the process table for Linux.
	 *
	 * @return the Linux process table
	 */
	public static ProcessTable linux() {
		return new LinuxProcessTable();
	}

	/**
	 * Lists all processes on the system.
	 *
	 * @return list of all processes
	 */
	public abstract List<? extends ProcessInfo> list();

	/**
	 * Looks up a specific process by PID.
	 *
	 * @param pid the process ID
	 * @return the process info, or empty if not found
	 */
	public abstract Optional<? extends ProcessInfo> lookup(long pid);

	/**
	 * Lists processes owned by a specific user.
	 *
	 * @param uid the user ID
	 * @return list of processes owned by the user
	 */
	public abstract List<? extends ProcessInfo> listByUser(long uid);

	/**
	 * Lists processes with a specific name.
	 *
	 * @param name the process name (executable name)
	 * @return list of matching processes
	 */
	public abstract List<? extends ProcessInfo> listByName(String name);

	/**
	 * Lists processes in a specific state.
	 *
	 * @param state the process state
	 * @return list of processes in that state
	 */
	public abstract List<? extends ProcessInfo> listByState(ProcessState state);

	/**
	 * Gets the current process (this JVM process).
	 *
	 * @return the current process info
	 */
	public Optional<? extends ProcessInfo> currentProcess() {
		return lookup(ProcessHandle.current().pid());
	}

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
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

import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for memory information access.
 *
 * @author Mark Bednarczyk
 */
public abstract class MemoryTable {

	/**
	 * Gets the memory table for the current platform.
	 *
	 * @return the memory table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static MemoryTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxMemoryTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxMemoryTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the memory table for Linux.
	 *
	 * @return the Linux memory table
	 */
	public static MemoryTable linux() {
		return new LinuxMemoryTable();
	}

	/**
	 * Gets system-wide memory information.
	 *
	 * @return the memory information
	 */
	public abstract MemoryInfo getInfo();

	/**
	 * Gets memory usage for a specific process.
	 *
	 * @param pid the process ID
	 * @return the process memory info, or empty if process not found
	 */
	public abstract Optional<? extends ProcessMemoryInfo> getProcessMemory(long pid);

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
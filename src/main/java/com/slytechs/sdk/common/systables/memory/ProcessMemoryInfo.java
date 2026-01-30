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

import com.slytechs.sdk.common.memory.MemorySize;

/**
 * Common interface for per-process memory information.
 *
 * @author Mark Bednarczyk
 */
public interface ProcessMemoryInfo {

	/**
	 * Gets the process ID.
	 *
	 * @return the process ID
	 */
	long pid();

	/**
	 * Gets virtual memory size (total address space).
	 *
	 * @return virtual memory size
	 */
	MemorySize virtualSize();

	/**
	 * Gets resident set size (physical RAM actually used).
	 *
	 * @return RSS
	 */
	MemorySize rss();

	/**
	 * Gets shared memory size.
	 *
	 * @return shared memory
	 */
	MemorySize shared();

	/**
	 * Gets data segment size.
	 *
	 * @return data size
	 */
	MemorySize data();

	/**
	 * Gets stack size.
	 *
	 * @return stack size
	 */
	MemorySize stack();
}
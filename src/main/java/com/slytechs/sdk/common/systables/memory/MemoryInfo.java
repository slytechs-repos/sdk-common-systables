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
 * Common interface for system-wide memory information.
 *
 * @author Mark Bednarczyk
 */
public interface MemoryInfo {

	/**
	 * Gets total physical RAM.
	 *
	 * @return total memory
	 */
	MemorySize total();

	/**
	 * Gets free memory (completely unused).
	 *
	 * @return free memory
	 */
	MemorySize free();

	/**
	 * Gets available memory (free + reclaimable).
	 * 
	 * <p>
	 * This is the best estimate of memory available for starting new
	 * applications, including cache that can be reclaimed.
	 * </p>
	 *
	 * @return available memory
	 */
	MemorySize available();

	/**
	 * Gets used memory (total - available).
	 *
	 * @return used memory
	 */
	MemorySize used();

	/**
	 * Gets memory used for buffers.
	 *
	 * @return buffers
	 */
	MemorySize buffers();

	/**
	 * Gets memory used for cache.
	 *
	 * @return cached memory
	 */
	MemorySize cached();

	/**
	 * Gets total swap space.
	 *
	 * @return total swap
	 */
	MemorySize swapTotal();

	/**
	 * Gets free swap space.
	 *
	 * @return free swap
	 */
	MemorySize swapFree();

	/**
	 * Gets used swap space.
	 *
	 * @return used swap
	 */
	MemorySize swapUsed();

	/**
	 * Calculates memory usage percentage (used / total * 100).
	 *
	 * @return usage percentage (0.0 to 100.0)
	 */
	double usagePercent();

	/**
	 * Calculates swap usage percentage (swapUsed / swapTotal * 100).
	 *
	 * @return swap usage percentage (0.0 to 100.0)
	 */
	double swapUsagePercent();
}
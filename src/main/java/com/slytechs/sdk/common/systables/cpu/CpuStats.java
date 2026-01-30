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

/**
 * Common interface for CPU usage statistics.
 *
 * @author Mark Bednarczyk
 */
public interface CpuStats {

	/**
	 * Gets the CPU core number. -1 for aggregate (all cores).
	 *
	 * @return the CPU core number, or -1 for total
	 */
	int cpu();

	/**
	 * Gets time spent in user mode (ticks).
	 *
	 * @return user time in ticks
	 */
	long user();

	/**
	 * Gets time spent in user mode with low priority (nice) (ticks).
	 *
	 * @return nice time in ticks
	 */
	long nice();

	/**
	 * Gets time spent in system mode (ticks).
	 *
	 * @return system time in ticks
	 */
	long system();

	/**
	 * Gets time spent idle (ticks).
	 *
	 * @return idle time in ticks
	 */
	long idle();

	/**
	 * Gets time spent waiting for I/O (ticks).
	 *
	 * @return iowait time in ticks
	 */
	long iowait();

	/**
	 * Gets time spent servicing hardware interrupts (ticks).
	 *
	 * @return irq time in ticks
	 */
	long irq();

	/**
	 * Gets time spent servicing software interrupts (ticks).
	 *
	 * @return softirq time in ticks
	 */
	long softirq();

	/**
	 * Gets the total time (sum of all times).
	 *
	 * @return total time in ticks
	 */
	long total();

	/**
	 * Calculates usage percentage compared to previous stats.
	 * 
	 * <p>
	 * Usage = (total_delta - idle_delta) / total_delta * 100
	 * </p>
	 *
	 * @param previous the previous CPU stats snapshot
	 * @return usage percentage (0.0 to 100.0)
	 */
	double usagePercent(CpuStats previous);

	/**
	 * Checks if this is aggregate stats (all CPUs combined).
	 *
	 * @return true if aggregate, false if per-core
	 */
	boolean isAggregate();
}
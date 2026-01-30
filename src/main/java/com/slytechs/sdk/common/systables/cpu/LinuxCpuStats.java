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
 * Linux CPU usage statistics from /proc/stat.
 *
 * @author Mark Bednarczyk
 */
public record LinuxCpuStats(
		int cpu,
		long user,
		long nice,
		long system,
		long idle,
		long iowait,
		long irq,
		long softirq,
		long steal,
		long guest,
		long guestNice) implements CpuStats {

	@Override
	public long total() {
		return user + nice + system + idle + iowait + irq + softirq + steal + guest + guestNice;
	}

	@Override
	public double usagePercent(CpuStats previous) {
		if (!(previous instanceof LinuxCpuStats prev)) {
			return 0.0;
		}

		long totalDelta = total() - prev.total();
		if (totalDelta == 0) {
			return 0.0;
		}

		long idleDelta = idle() - prev.idle();
		long usedDelta = totalDelta - idleDelta;

		return (usedDelta * 100.0) / totalDelta;
	}

	@Override
	public boolean isAggregate() {
		return cpu == -1;
	}
}
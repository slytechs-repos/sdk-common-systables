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
 * Linux system-wide memory information from /proc/meminfo.
 *
 * @author Mark Bednarczyk
 */
public record LinuxMemoryInfo(
		MemorySize total,
		MemorySize free,
		MemorySize available,
		MemorySize buffers,
		MemorySize cached,
		MemorySize swapTotal,
		MemorySize swapFree) implements MemoryInfo {

	@Override
	public MemorySize used() {
		return MemorySize.ofBytes(total.toBytes() - available.toBytes());
	}

	@Override
	public MemorySize swapUsed() {
		return MemorySize.ofBytes(swapTotal.toBytes() - swapFree.toBytes());
	}

	@Override
	public double usagePercent() {
		if (total.toBytes() == 0) {
			return 0.0;
		}
		return (used().toBytes() * 100.0) / total.toBytes();
	}

	@Override
	public double swapUsagePercent() {
		if (swapTotal.toBytes() == 0) {
			return 0.0;
		}
		return (swapUsed().toBytes() * 100.0) / swapTotal.toBytes();
	}
}
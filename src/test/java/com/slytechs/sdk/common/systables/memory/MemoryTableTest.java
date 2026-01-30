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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test cases for MemoryTable.
 *
 * @author Mark Bednarczyk
 */
class MemoryTableTest {

	@Test
	void testGetCurrent() {
		MemoryTable table = MemoryTable.current();
		assertNotNull(table);
	}

	@Test
	void testGetInfo() {
		MemoryTable table = MemoryTable.current();
		MemoryInfo info = table.getInfo();

		assertNotNull(info);
		assertTrue(info.total().toBytes() > 0);

		System.out.println("Memory Information:");
		System.out.printf("  Total: %s%n", info.total());
		System.out.printf("  Used: %s (%.1f%%)%n",
				info.used(),
				info.usagePercent());
		System.out.printf("  Free: %s%n", info.free());
		System.out.printf("  Available: %s%n", info.available());
		System.out.printf("  Buffers: %s%n", info.buffers());
		System.out.printf("  Cached: %s%n", info.cached());

		if (info.swapTotal().toBytes() > 0) {
			System.out.printf("  Swap Total: %s%n", info.swapTotal());
			System.out.printf("  Swap Used: %s (%.1f%%)%n",
					info.swapUsed(),
					info.swapUsagePercent());
		} else {
			System.out.println("  Swap: disabled");
		}
	}

	@Test
	void testUsagePercent() {
		MemoryTable table = MemoryTable.current();
		MemoryInfo info = table.getInfo();

		double usage = info.usagePercent();
		assertTrue(usage >= 0.0 && usage <= 100.0);
	}

	@Test
	void testGetProcessMemory() {
		MemoryTable table = MemoryTable.current();
		long pid = ProcessHandle.current().pid();

		var procMem = table.getProcessMemory(pid);
		assertTrue(procMem.isPresent(), "Should get memory for current process");

		var mem = procMem.get();
		assertEquals(pid, mem.pid());
		assertTrue(mem.rss().toBytes() > 0);
		assertTrue(mem.virtualSize().toBytes() > 0);

		System.out.println("Current process memory:");
		System.out.printf("  PID: %d%n", mem.pid());
		System.out.printf("  RSS: %s%n", mem.rss());
		System.out.printf("  Virtual: %s%n", mem.virtualSize());
		System.out.printf("  Shared: %s%n", mem.shared());
		System.out.printf("  Data: %s%n", mem.data());
		System.out.printf("  Stack: %s%n", mem.stack());
	}

	@Test
	void testInvalidProcess() {
		MemoryTable table = MemoryTable.current();
		var procMem = table.getProcessMemory(999999);

		assertFalse(procMem.isPresent(), "Should not find non-existent process");
	}
}
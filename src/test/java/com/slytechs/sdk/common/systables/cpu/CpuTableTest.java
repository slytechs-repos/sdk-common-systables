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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test cases for CpuTable.
 *
 * @author Mark Bednarczyk
 */
class CpuTableTest {

	@Test
	void testGetCurrent() {
		CpuTable table = CpuTable.current();
		assertNotNull(table);
	}

	@Test
	void testGetInfo() {
		CpuTable table = CpuTable.current();
		CpuInfo info = table.getInfo();

		assertNotNull(info);
		assertTrue(info.logicalCores() > 0);
		assertTrue(info.physicalCores() > 0);
		assertTrue(info.sockets() > 0);

		System.out.println("CPU Information:");
		System.out.printf("  Model: %s%n", info.model());
		System.out.printf("  Vendor: %s%n", info.vendor());
		System.out.printf("  Logical cores: %d%n", info.logicalCores());
		System.out.printf("  Physical cores: %d%n", info.physicalCores());
		System.out.printf("  Sockets: %d%n", info.sockets());
		System.out.printf("  Frequency: %.2f MHz%n", info.frequencyMHz());
		System.out.printf("  Flags: %d features%n", info.flags().size());
	}

	@Test
	void testGetStats() {
		CpuTable table = CpuTable.current();
		List<CpuStats> stats = table.getStats();

		assertNotNull(stats);
		assertFalse(stats.isEmpty());

		// First entry should be aggregate
		assertTrue(stats.get(0).isAggregate());

		System.out.println("CPU Statistics:");
		System.out.printf("  Total CPUs: %d%n", stats.size() - 1); // -1 for aggregate
	}

	@Test
	void testCalculateUsage() throws InterruptedException {
		CpuTable table = CpuTable.current();

		// Take two snapshots
		List<CpuStats> snapshot1 = table.getStats();
		Thread.sleep(1000); // Wait 1 second
		List<CpuStats> snapshot2 = table.getStats();

		// Calculate aggregate usage
		CpuStats prev = snapshot1.get(0);
		CpuStats curr = snapshot2.get(0);
		double usage = curr.usagePercent(prev);

		assertTrue(usage >= 0.0 && usage <= 100.0);

		System.out.printf("CPU Usage: %.2f%%%n", usage);

		// Show per-core usage (first 8 cores)
		System.out.println("Per-core usage:");
		int maxCores = Math.min(8, snapshot1.size() - 1);
		for (int i = 0; i < maxCores; i++) {
			CpuStats prevCore = snapshot1.get(i + 1); // +1 to skip aggregate
			CpuStats currCore = snapshot2.get(i + 1);
			double coreUsage = currCore.usagePercent(prevCore);
			System.out.printf("  CPU%d: %.1f%%%n", i, coreUsage);
		}
	}

	@Test
	void testGetProcessAffinity() {
		CpuTable table = CpuTable.current();
		long pid = ProcessHandle.current().pid();

		var affinity = table.getProcessAffinity(pid);
		assertTrue(affinity.isPresent(), "Should get affinity for current process");

		System.out.printf("Current process affinity: %s (%d CPUs)%n",
				affinity.get().toCpuList(),
				affinity.get().count());
	}

	@Test
	void testCpuAffinity() {
		// Test parsing
		CpuAffinity affinity = CpuAffinity.parse("0-7,16-23");

		assertEquals(16, affinity.count());
		assertTrue(affinity.isAllowed(0));
		assertTrue(affinity.isAllowed(7));
		assertTrue(affinity.isAllowed(16));
		assertTrue(affinity.isAllowed(23));
		assertFalse(affinity.isAllowed(8));
		assertFalse(affinity.isAllowed(15));

		System.out.printf("Parsed affinity: %s%n", affinity.toCpuList());
	}
}
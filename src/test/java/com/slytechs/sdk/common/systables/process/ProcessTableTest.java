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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test cases for ProcessTable.
 *
 * @author Mark Bednarczyk
 */
class ProcessTableTest {

	@Test
	void testGetCurrent() {
		ProcessTable table = ProcessTable.current();
		assertNotNull(table);
	}

	@Test
	void testListProcesses() {
		ProcessTable table = ProcessTable.current();
		var processes = table.list();

		assertNotNull(processes);
		assertFalse(processes.isEmpty());

		System.out.printf("Found %d processes%n", processes.size());

		// Show first 10 processes
		System.out.println("Sample processes:");
		processes.stream().limit(10).forEach(p -> {
			System.out.printf("  PID:%d %s (%s) - %s%n",
					p.pid(),
					p.name(),
					p.state(),
					p.rss());
		});
	}

	@Test
	void testLookupCurrentProcess() {
		ProcessTable table = ProcessTable.current();
		long pid = ProcessHandle.current().pid();

		var process = table.lookup(pid);
		assertTrue(process.isPresent(), "Should find current process");

		var proc = process.get();
		assertEquals(pid, proc.pid());
		assertTrue(proc.isAlive());

		System.out.println("Current process:");
		System.out.printf("  PID: %d%n", proc.pid());
		System.out.printf("  Name: %s%n", proc.name());
		System.out.printf("  State: %s%n", proc.state());
		System.out.printf("  Threads: %d%n", proc.threads());
		System.out.printf("  RSS: %s%n", proc.rss());
		System.out.printf("  CPU time: %d ms%n", proc.cpuTimeMillis());
		proc.commandLine().ifPresent(cmd ->
				System.out.printf("  Command: %s%n", cmd));
		proc.cpuAffinity().ifPresent(aff ->
				System.out.printf("  CPU affinity: %s%n", aff.toCpuList()));
	}

	@Test
	void testListByState() {
		ProcessTable table = ProcessTable.current();

		var running = table.listByState(ProcessState.RUNNING);
		var sleeping = table.listByState(ProcessState.SLEEPING);
		var zombies = table.listByState(ProcessState.ZOMBIE);

		assertNotNull(running);
		assertNotNull(sleeping);
		assertNotNull(zombies);

		System.out.printf("Process states:%n");
		System.out.printf("  Running: %d%n", running.size());
		System.out.printf("  Sleeping: %d%n", sleeping.size());
		System.out.printf("  Zombies: %d%n", zombies.size());
	}

	@Test
	void testListByName() {
		ProcessTable table = ProcessTable.current();

		// Try to find common system processes
		var init = table.listByName("systemd");
		if (init.isEmpty()) {
			init = table.listByName("init");
		}

		if (!init.isEmpty()) {
			System.out.printf("Found %d init/systemd process(es)%n", init.size());
			init.forEach(p ->
					System.out.printf("  PID:%d %s%n", p.pid(), p.name()));
		}
	}

	@Test
	void testTopMemoryProcesses() {
		ProcessTable table = ProcessTable.current();
		var processes = table.list();

		System.out.println("Top 5 memory-consuming processes:");
		processes.stream()
				.sorted((a, b) -> Long.compare(b.rss().toBytes(), a.rss().toBytes()))
				.limit(5)
				.forEach(p -> {
					System.out.printf("  PID:%d %s - %s%n",
							p.pid(),
							p.name(),
							p.rss());
				});
	}

	@Test
	void testInvalidProcess() {
		ProcessTable table = ProcessTable.current();
		var process = table.lookup(999999);

		assertFalse(process.isPresent(), "Should not find non-existent process");
	}
}
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
package com.slytechs.sdk.common.systables.iface;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test cases for IfTable.
 *
 * @author Mark Bednarczyk
 */
class IfTableTest {

	@Test
	void testGetCurrent() {
		IfTable table = IfTable.current();
		assertNotNull(table);
	}

	@Test
	void testListInterfaces() {
		IfTable table = IfTable.current();
		var interfaces = table.list();

		assertNotNull(interfaces);
		assertFalse(interfaces.isEmpty(), "Should have at least one interface (loopback)");

		// Print for inspection
		System.out.println("Found " + interfaces.size() + " interfaces:");
		interfaces.forEach(iface -> {
			System.out.printf("  %s: up=%s, virtual=%s%n",
					iface.name(),
					iface.isUp(),
					IfTable.isVirtual(iface.name()));
		});
	}

	@Test
	void testLookupLoopback() {
		IfTable table = IfTable.current();
		var loopback = table.lookup("lo");

		assertTrue(loopback.isPresent(), "Loopback interface should exist");

		var lo = loopback.get();
		assertEquals("lo", lo.name());
		assertTrue(IfTable.isLoopback("lo"));
		assertTrue(IfTable.isVirtual("lo"));

		System.out.printf("Loopback: mtu=%d, up=%s%n", lo.mtu(), lo.isUp());
	}

	@Test
	void testLinkSpeed() {
		IfTable table = IfTable.current();

		// Try to find a physical interface
		var interfaces = table.list();
		var physical = interfaces.stream()
				.filter(iface -> !IfTable.isVirtual(iface.name()))
				.findFirst();

		if (physical.isPresent()) {
			var iface = physical.get();
			var speed = iface.linkSpeed();

			System.out.printf("Physical interface %s: speed=%s%n",
					iface.name(),
					speed.isPresent() ? speed.get() : "unknown");

			if (speed.isPresent()) {
				assertTrue(speed.get().toBitsPerSecond() > 0);
			}
		} else {
			System.out.println("No physical interfaces found, skipping link speed test");
		}
	}

	@Test
	void testVirtualDetection() {
		// Loopback should be virtual
		assertTrue(IfTable.isVirtual("lo"));
		assertTrue(IfTable.isLoopback("lo"));

		// Common virtual interface names
		if (IfTable.current().lookup("docker0").isPresent()) {
			assertTrue(IfTable.isVirtual("docker0"));
		}
	}
}
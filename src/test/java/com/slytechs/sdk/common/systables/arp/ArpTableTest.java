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
package com.slytechs.sdk.common.systables.arp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HexFormat;

import org.junit.jupiter.api.Test;

/**
 * Test cases for ArpTable.
 *
 * @author Mark Bednarczyk
 */
class ArpTableTest {

	@Test
	void testGetCurrent() {
		ArpTable table = ArpTable.current();
		assertNotNull(table);
	}

	@Test
	void testListArpEntries() {
		ArpTable table = ArpTable.current();
		var entries = table.list();

		assertNotNull(entries);

		System.out.println("Found " + entries.size() + " ARP entries:");
		entries.forEach(entry -> {
			System.out.printf("  %s -> %s (%s) on %s%n",
					entry.ipAddress().getHostAddress(),
					formatMac(entry.hardwareAddress()),
					entry.state(),
					entry.interfaceName());
		});
	}

	@Test
	void testLookupEntry() {
		ArpTable table = ArpTable.current();
		var entries = table.list();

		if (!entries.isEmpty()) {
			var first = entries.get(0);
			var found = table.lookup(first.ipAddress());

			assertTrue(found.isPresent(), "Should find entry we just saw");
			assertEquals(first.ipAddress(), found.get().ipAddress());

			System.out.printf("Lookup successful: %s -> %s%n",
					first.ipAddress().getHostAddress(),
					formatMac(first.hardwareAddress()));
		} else {
			System.out.println("No ARP entries found, skipping lookup test");
		}
	}

	@Test
	void testListByInterface() throws Exception {
		ArpTable table = ArpTable.current();
		var entries = table.list();

		if (!entries.isEmpty()) {
			String ifname = entries.get(0).interfaceName();
			var filtered = table.listByInterface(ifname);

			assertNotNull(filtered);
			assertFalse(filtered.isEmpty());

			System.out.printf("Found %d ARP entries on interface %s%n",
					filtered.size(), ifname);
		}
	}

	private static String formatMac(byte[] mac) {
		if (mac == null || mac.length == 0) {
			return "??:??:??:??:??:??";
		}
		return HexFormat.ofDelimiter(":").formatHex(mac);
	}
}
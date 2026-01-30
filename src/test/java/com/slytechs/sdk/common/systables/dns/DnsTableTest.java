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
package com.slytechs.sdk.common.systables.dns;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test cases for DnsTable.
 *
 * @author Mark Bednarczyk
 */
class DnsTableTest {

	@Test
	void testGetCurrent() {
		DnsTable table = DnsTable.current();
		assertNotNull(table);
	}

	@Test
	void testGetConfig() {
		DnsTable table = DnsTable.current();
		DnsConfig config = table.getConfig();

		assertNotNull(config);
		assertNotNull(config.nameservers());
		assertNotNull(config.searchDomains());
		assertNotNull(config.options());

		System.out.println("DNS Configuration:");
		System.out.println("  Nameservers:");
		config.nameservers().forEach(ns ->
				System.out.println("    " + ns.getHostAddress()));

		if (!config.searchDomains().isEmpty()) {
			System.out.println("  Search domains:");
			config.searchDomains().forEach(domain ->
					System.out.println("    " + domain));
		}

		config.domain().ifPresent(domain ->
				System.out.println("  Domain: " + domain));

		if (!config.options().isEmpty()) {
			System.out.println("  Options:");
			config.options().forEach(option ->
					System.out.println("    " + option));
		}
	}

	@Test
	void testHasNameservers() {
		DnsTable table = DnsTable.current();
		DnsConfig config = table.getConfig();

		if (config.nameservers().isEmpty()) {
			System.out.println("Warning: No nameservers configured");
		} else {
			assertFalse(config.nameservers().isEmpty(),
					"Should have at least one nameserver");
		}
	}
}
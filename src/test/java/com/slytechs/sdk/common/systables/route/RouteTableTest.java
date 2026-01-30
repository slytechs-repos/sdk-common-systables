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
package com.slytechs.sdk.common.systables.route;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test cases for RouteTable.
 *
 * @author Mark Bednarczyk
 */
class RouteTableTest {

	@Test
	void testGetCurrent() {
		RouteTable table = RouteTable.current();
		assertNotNull(table);
	}

	@Test
	void testListRoutes() {
		RouteTable table = RouteTable.current();
		var routes = table.list();

		assertNotNull(routes);
		assertFalse(routes.isEmpty(), "Should have at least one route");

		System.out.println("Found " + routes.size() + " routes:");
		routes.forEach(route -> {
			System.out.printf("  %s via %s dev %s (metric %d)%n",
					route.destination().map(d -> d.getHostAddress() + "/" + route.prefixLength())
							.orElse("default"),
					route.gateway().map(g -> g.getHostAddress()).orElse("direct"),
					route.interfaceName(),
					route.metric());
		});
	}

	@Test
	void testGetDefaultRoute() {
		RouteTable table = RouteTable.current();
		var defaultRoute = table.getDefault();

		if (defaultRoute.isPresent()) {
			var route = defaultRoute.get();
			assertTrue(route.isDefault());
			assertTrue(route.gateway().isPresent(), "Default route should have a gateway");

			System.out.printf("Default gateway: %s dev %s%n",
					route.gateway().get().getHostAddress(),
					route.interfaceName());
		} else {
			System.out.println("No default route found");
		}
	}

	@Test
	void testListIPv4Routes() {
		RouteTable table = RouteTable.current();
		var ipv4Routes = table.listIPv4();

		assertNotNull(ipv4Routes);
		assertFalse(ipv4Routes.isEmpty());

		System.out.println("Found " + ipv4Routes.size() + " IPv4 routes");
	}

	@Test
	void testListIPv6Routes() {
		RouteTable table = RouteTable.current();
		var ipv6Routes = table.listIPv6();

		assertNotNull(ipv6Routes);

		if (!ipv6Routes.isEmpty()) {
			System.out.println("Found " + ipv6Routes.size() + " IPv6 routes");
		} else {
			System.out.println("No IPv6 routes found");
		}
	}

	@Test
	void testListByInterface() {
		RouteTable table = RouteTable.current();
		var routes = table.list();

		if (!routes.isEmpty()) {
			String ifname = routes.get(0).interfaceName();
			var filtered = table.listByInterface(ifname);

			assertNotNull(filtered);
			assertFalse(filtered.isEmpty());

			System.out.printf("Found %d routes on interface %s%n",
					filtered.size(), ifname);
		}
	}
}
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

import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for routing table access.
 *
 * @author Mark Bednarczyk
 */
public abstract class RouteTable {

	/**
	 * Gets the routing table for the current platform.
	 *
	 * @return the routing table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static RouteTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxRouteTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxRouteTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the routing table for Linux.
	 *
	 * @return the Linux routing table
	 */
	public static RouteTable linux() {
		return new LinuxRouteTable();
	}

	/**
	 * Lists all routing entries.
	 *
	 * @return list of all routes
	 */
	public abstract List<? extends RouteEntry> list();

	/**
	 * Lists all IPv4 routing entries.
	 *
	 * @return list of IPv4 routes
	 */
	public abstract List<? extends RouteEntry> listIPv4();

	/**
	 * Lists all IPv6 routing entries.
	 *
	 * @return list of IPv6 routes
	 */
	public abstract List<? extends RouteEntry> listIPv6();

	/**
	 * Gets the default route (gateway).
	 *
	 * @return the default route, or empty if not found
	 */
	public abstract Optional<? extends RouteEntry> getDefault();

	/**
	 * Gets the default IPv4 route.
	 *
	 * @return the default IPv4 route, or empty if not found
	 */
	public abstract Optional<? extends RouteEntry> getDefaultIPv4();

	/**
	 * Gets the default IPv6 route.
	 *
	 * @return the default IPv6 route, or empty if not found
	 */
	public abstract Optional<? extends RouteEntry> getDefaultIPv6();

	/**
	 * Lists routes for a specific interface.
	 *
	 * @param interfaceName the interface name
	 * @return list of routes for the interface
	 */
	public abstract List<? extends RouteEntry> listByInterface(String interfaceName);

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
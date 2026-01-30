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

import java.net.InetAddress;
import java.util.Optional;

/**
 * Common interface for routing table entries.
 *
 * @author Mark Bednarczyk
 */
public interface RouteEntry {

	/**
	 * Gets the destination network. Empty for default route.
	 *
	 * @return the destination, or empty for default route
	 */
	Optional<InetAddress> destination();

	/**
	 * Gets the destination prefix length (CIDR).
	 *
	 * @return the prefix length, or 0 for default route
	 */
	int prefixLength();

	/**
	 * Gets the gateway address. Empty for direct routes.
	 *
	 * @return the gateway address, or empty if direct route
	 */
	Optional<InetAddress> gateway();

	/**
	 * Gets the outgoing interface name.
	 *
	 * @return the interface name
	 */
	String interfaceName();

	/**
	 * Gets the route metric (priority/cost).
	 *
	 * @return the metric
	 */
	int metric();

	/**
	 * Checks if this is the default route.
	 *
	 * @return true if default route
	 */
	boolean isDefault();

	/**
	 * Checks if this is a direct route (no gateway).
	 *
	 * @return true if direct route
	 */
	boolean isDirect();
}
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
 * Linux routing table entry.
 *
 * @author Mark Bednarczyk
 */
public record LinuxRouteEntry(
		Optional<InetAddress> destination,
		int prefixLength,
		Optional<InetAddress> gateway,
		String interfaceName,
		int metric,
		RouteProtocol protocol,
		RouteScope scope,
		Optional<InetAddress> source,
		boolean linkDown,
		boolean isDefault) implements RouteEntry {

	@Override
	public boolean isDirect() {
		return gateway.isEmpty();
	}

	/**
	 * Formats this route as a CIDR string.
	 *
	 * @return the route in CIDR notation (e.g., "192.168.1.0/24")
	 */
	public String toCidr() {
		if (isDefault) {
			return destination.map(d -> d instanceof java.net.Inet6Address ? "::/0" : "0.0.0.0/0")
					.orElse("default");
		}
		return destination.map(d -> d.getHostAddress() + "/" + prefixLength)
				.orElse("unknown");
	}
}
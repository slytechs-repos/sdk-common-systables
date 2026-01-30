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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Linux implementation of routing table using 'ip route' command.
 * 
 * <p>
 * Parses output from 'ip route show' (IPv4) and 'ip -6 route show' (IPv6).
 * </p>
 *
 * @author Mark Bednarczyk
 */
public class LinuxRouteTable extends RouteTable {

	@Override
	public List<LinuxRouteEntry> list() {
		List<LinuxRouteEntry> routes = new ArrayList<>();
		routes.addAll(listIPv4());
		routes.addAll(listIPv6());
		return routes;
	}

	@Override
	public List<LinuxRouteEntry> listIPv4() {
		return parseIpRoute("ip", "route", "show");
	}

	@Override
	public List<LinuxRouteEntry> listIPv6() {
		return parseIpRoute("ip", "-6", "route", "show");
	}

	@Override
	public Optional<LinuxRouteEntry> getDefault() {
		return getDefaultIPv4().or(this::getDefaultIPv6);
	}

	@Override
	public Optional<LinuxRouteEntry> getDefaultIPv4() {
		return listIPv4().stream()
				.filter(RouteEntry::isDefault)
				.findFirst();
	}

	@Override
	public Optional<LinuxRouteEntry> getDefaultIPv6() {
		return listIPv6().stream()
				.filter(RouteEntry::isDefault)
				.findFirst();
	}

	@Override
	public List<LinuxRouteEntry> listByInterface(String interfaceName) {
		return list().stream()
				.filter(route -> route.interfaceName().equals(interfaceName))
				.toList();
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}

	/**
	 * Parses the output of 'ip route show' or 'ip -6 route show'.
	 *
	 * @param command the command and arguments
	 * @return list of parsed routes
	 */
	private List<LinuxRouteEntry> parseIpRoute(String... command) {
		List<LinuxRouteEntry> routes = new ArrayList<>();

		try {
			Process process = new ProcessBuilder(command).start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					parseRouteLine(line).ifPresent(routes::add);
				}
			}
			process.waitFor();
		} catch (Exception e) {
			// Return empty list
		}

		return routes;
	}

	/**
	 * Parses a single line from 'ip route show' output.
	 * 
	 * Examples: 
	 * - default via 192.168.1.1 dev enp15s0 proto dhcp src 192.168.1.232 metric 100
	 * - 10.42.0.0/24 dev cni0 proto kernel scope link src 10.42.0.1
	 * - 192.168.1.0/24 dev enp15s0 proto kernel scope link src 192.168.1.232 metric 100
	 *
	 * @param line the line to parse
	 * @return the parsed route, or empty if parsing fails
	 */
	private Optional<LinuxRouteEntry> parseRouteLine(String line) {
		try {
			String[] tokens = line.trim().split("\\s+");
			if (tokens.length < 2) {
				return Optional.empty();
			}

			int i = 0;
			boolean isDefault = false;
			Optional<InetAddress> destination = Optional.empty();
			int prefixLength = 0;
			Optional<InetAddress> gateway = Optional.empty();
			String interfaceName = null;
			int metric = 0;
			RouteProtocol protocol = RouteProtocol.UNKNOWN;
			RouteScope scope = RouteScope.GLOBAL;
			Optional<InetAddress> source = Optional.empty();
			boolean linkDown = false;

			// Parse destination (or "default")
			if ("default".equals(tokens[i])) {
				isDefault = true;
				i++;
			} else {
				String[] parts = tokens[i++].split("/");
				destination = Optional.of(InetAddress.getByName(parts[0]));
				prefixLength = parts.length > 1 ? Integer.parseInt(parts[1]) : 
					(destination.get() instanceof Inet6Address ? 128 : 32);
			}

			// Parse remaining tokens
			while (i < tokens.length) {
				String token = tokens[i++];

				switch (token) {
				case "via":
					if (i < tokens.length) {
						gateway = Optional.of(InetAddress.getByName(tokens[i++]));
					}
					break;

				case "dev":
					if (i < tokens.length) {
						interfaceName = tokens[i++];
					}
					break;

				case "metric":
					if (i < tokens.length) {
						metric = Integer.parseInt(tokens[i++]);
					}
					break;

				case "proto":
					if (i < tokens.length) {
						protocol = parseProtocol(tokens[i++]);
					}
					break;

				case "scope":
					if (i < tokens.length) {
						scope = parseScope(tokens[i++]);
					}
					break;

				case "src":
					if (i < tokens.length) {
						source = Optional.of(InetAddress.getByName(tokens[i++]));
					}
					break;

				case "linkdown":
					linkDown = true;
					break;

				default:
					// Ignore unknown tokens (pref, etc.)
					break;
				}
			}

			if (interfaceName == null) {
				return Optional.empty();
			}

			return Optional.of(new LinuxRouteEntry(
					destination,
					prefixLength,
					gateway,
					interfaceName,
					metric,
					protocol,
					scope,
					source,
					linkDown,
					isDefault));

		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Parses protocol string to enum.
	 *
	 * @param proto the protocol string
	 * @return the protocol enum
	 */
	private RouteProtocol parseProtocol(String proto) {
		return switch (proto.toLowerCase()) {
		case "dhcp" -> RouteProtocol.DHCP;
		case "kernel" -> RouteProtocol.KERNEL;
		case "ra" -> RouteProtocol.RA;
		case "bird" -> RouteProtocol.BIRD;
		case "boot" -> RouteProtocol.BOOT;
		case "static" -> RouteProtocol.STATIC;
		default -> RouteProtocol.UNKNOWN;
		};
	}

	/**
	 * Parses scope string to enum.
	 *
	 * @param scopeStr the scope string
	 * @return the scope enum
	 */
	private RouteScope parseScope(String scopeStr) {
		return switch (scopeStr.toLowerCase()) {
		case "global" -> RouteScope.GLOBAL;
		case "site" -> RouteScope.SITE;
		case "link" -> RouteScope.LINK;
		case "host" -> RouteScope.HOST;
		case "nowhere" -> RouteScope.NOWHERE;
		default -> RouteScope.UNKNOWN;
		};
	}
}
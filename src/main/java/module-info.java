/*
 * Sly Technologies Free License
 * 
 * Copyright 2025 Sly Technologies Inc.
 *
 * Licensed under the Sly Technologies Free License (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.slytechs.com/free-license-text
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/**
 * System tables module providing access to operating system network
 * configuration and statistics.
 * 
 * <h2>Network Interfaces</h2>
 * <p>
 * Access network interface information including link speeds, MTU, MAC
 * addresses, and capabilities:
 * </p>
 * <ul>
 * <li>{@code IfTable} - Query and manage network interfaces</li>
 * <li>{@code IfDevice} - Interface properties and statistics</li>
 * <li>Platform-specific implementations for Linux, Windows, macOS</li>
 * </ul>
 * 
 * <h2>Routing Tables</h2>
 * <p>
 * Read and modify system routing tables:
 * </p>
 * <ul>
 * <li>{@code RouteTable} - Query and manage routing entries</li>
 * <li>{@code RouteEntry} - Route information and metrics</li>
 * <li>Support for IPv4 and IPv6 routes</li>
 * </ul>
 * 
 * <h2>ARP Cache</h2>
 * <p>
 * Access and manipulate ARP (Address Resolution Protocol) cache:
 * </p>
 * <ul>
 * <li>{@code ArpTable} - Query and manage ARP entries</li>
 * <li>{@code ArpEntry} - IP to MAC address mappings</li>
 * <li>Add, delete, and flush operations</li>
 * </ul>
 * 
 * <h2>DNS Configuration</h2>
 * <p>
 * Read and modify DNS resolver configuration:
 * </p>
 * <ul>
 * <li>{@code DnsTable} - Query and manage DNS settings</li>
 * <li>{@code DnsEntry} - DNS server and search domain information</li>
 * <li>Platform-specific resolver access</li>
 * </ul>
 * 
 * <h2>Platform Support</h2>
 * <p>
 * Automatic platform detection with native bindings for:
 * </p>
 * <ul>
 * <li>Linux - via sysfs, ioctl, netlink</li>
 * <li>Windows - via IP Helper API, WMI</li>
 * <li>macOS - via sysctl, ioctl (future)</li>
 * <li>BSD variants (future)</li>
 * </ul>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies Inc.
 */
module com.slytechs.sdk.common.systables {
	exports com.slytechs.sdk.common.systables;
	exports com.slytechs.sdk.common.systables.foreign;
	exports com.slytechs.sdk.common.systables.iface;
//	exports com.slytechs.sdk.common.systables.route;
//	exports com.slytechs.sdk.common.systables.arp;
//	exports com.slytechs.sdk.common.systables.dns;

	requires transitive com.slytechs.sdk.common;
}
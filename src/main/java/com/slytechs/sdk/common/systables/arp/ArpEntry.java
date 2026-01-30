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

import java.net.InetAddress;

/**
 * Common interface for ARP (Address Resolution Protocol) cache entries.
 *
 * @author Mark Bednarczyk
 */
public interface ArpEntry {

	/**
	 * Gets the IP address.
	 *
	 * @return the IP address
	 */
	InetAddress ipAddress();

	/**
	 * Gets the hardware (MAC) address.
	 *
	 * @return the hardware address
	 */
	byte[] hardwareAddress();

	/**
	 * Gets the network interface name.
	 *
	 * @return the interface name
	 */
	String interfaceName();

	/**
	 * Gets the ARP entry state.
	 *
	 * @return the state
	 */
	ArpState state();
}
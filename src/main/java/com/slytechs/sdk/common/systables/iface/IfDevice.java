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

import java.util.Optional;

import com.slytechs.sdk.common.util.DataRate;

/**
 * Common interface for network interface information across all platforms.
 *
 * @author Mark Bednarczyk
 */
public interface IfDevice {

	/**
	 * Gets the interface name.
	 *
	 * @return the interface name (e.g., "eth0", "wlan0")
	 */
	String name();

	/**
	 * Gets the link speed of the interface.
	 *
	 * @return the link speed, or empty if unavailable
	 */
	Optional<DataRate> linkSpeed();

	/**
	 * Gets the MTU (Maximum Transmission Unit) size.
	 *
	 * @return the MTU in bytes
	 */
	int mtu();

	/**
	 * Checks if the interface is up (administratively enabled).
	 *
	 * @return true if the interface is up
	 */
	boolean isUp();

	/**
	 * Gets the hardware (MAC) address.
	 *
	 * @return the hardware address, or empty array if unavailable
	 */
	Optional<byte[]> hardwareAddress();
}
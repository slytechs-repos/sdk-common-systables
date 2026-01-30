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
import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for ARP (Address Resolution Protocol) cache access.
 *
 * @author Mark Bednarczyk
 */
public abstract class ArpTable {

	/**
	 * Gets the ARP table for the current platform.
	 *
	 * @return the ARP table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static ArpTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxArpTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxArpTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the ARP table for Linux.
	 *
	 * @return the Linux ARP table
	 */
	public static ArpTable linux() {
		return new LinuxArpTable();
	}

	/**
	 * Lists all ARP cache entries.
	 *
	 * @return list of all ARP entries
	 */
	public abstract List<? extends ArpEntry> list();

	/**
	 * Looks up an ARP entry by IP address.
	 *
	 * @param ipAddress the IP address to look up
	 * @return the ARP entry, or empty if not found
	 */
	public abstract Optional<? extends ArpEntry> lookup(InetAddress ipAddress);

	/**
	 * Lists all ARP entries for a specific interface.
	 *
	 * @param interfaceName the interface name
	 * @return list of ARP entries for the interface
	 */
	public abstract List<? extends ArpEntry> listByInterface(String interfaceName);

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Linux implementation of ARP table using /proc/net/arp.
 * 
 * <p>
 * Note: This implementation only supports IPv4. For IPv6 neighbor cache, use
 * netlink or parse 'ip -6 neigh' command output.
 * </p>
 *
 * @author Mark Bednarczyk
 */
public class LinuxArpTable extends ArpTable {

	private static final Path ARP_FILE = Path.of("/proc/net/arp");

	// ARP flags from linux/if_arp.h
	private static final int ATF_COM = 0x02;      // completed entry
	private static final int ATF_PERM = 0x04;     // permanent entry
	private static final int ATF_PUBL = 0x08;     // publish entry
	private static final int ATF_USETRAILERS = 0x10;
	private static final int ATF_NETMASK = 0x20;
	private static final int ATF_DONTPUB = 0x40;

	@Override
	public List<LinuxArpEntry> list() {
		List<LinuxArpEntry> entries = new ArrayList<>();

		try (var lines = Files.lines(ARP_FILE)) {
			lines.skip(1) // Skip header line
					.forEach(line -> parseEntry(line).ifPresent(entries::add));
		} catch (IOException e) {
			// Return empty list
		}

		return entries;
	}

	@Override
	public Optional<LinuxArpEntry> lookup(InetAddress ipAddress) {
		return list().stream()
				.filter(entry -> entry.ipAddress().equals(ipAddress))
				.findFirst();
	}

	@Override
	public List<LinuxArpEntry> listByInterface(String interfaceName) {
		return list().stream()
				.filter(entry -> entry.interfaceName().equals(interfaceName))
				.toList();
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}

	/**
	 * Parses a single line from /proc/net/arp.
	 * 
	 * Format: IP address HW type Flags HW address Mask Device Example:
	 * 192.168.1.1 0x1 0x2 aa:bb:cc:dd:ee:ff * eth0
	 *
	 * @param line the line to parse
	 * @return the parsed entry, or empty if parsing fails
	 */
	private Optional<LinuxArpEntry> parseEntry(String line) {
		try {
			String[] parts = line.trim().split("\\s+");
			if (parts.length < 6) {
				return Optional.empty();
			}

			// Parse IP address
			InetAddress ipAddress = InetAddress.getByName(parts[0]);

			// Parse HW type (0x1 = Ethernet)
			int hwType = parseHex(parts[1]);

			// Parse flags (0x2 = complete, 0x4 = permanent)
			int flags = parseHex(parts[2]);

			// Parse hardware address (MAC)
			byte[] hwAddress = parseMacAddress(parts[3]);

			// parts[4] is mask (usually "*", unused)

			// Parse device name
			String device = parts[5];

			// Determine state from flags
			ArpState state = determineState(flags);

			return Optional.of(new LinuxArpEntry(
					ipAddress,
					hwAddress,
					device,
					state,
					hwType,
					flags));

		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Parses a hexadecimal string (e.g., "0x2" or "0x1").
	 *
	 * @param hex the hex string
	 * @return the integer value
	 */
	private int parseHex(String hex) {
		if (hex.startsWith("0x") || hex.startsWith("0X")) {
			return Integer.parseInt(hex.substring(2), 16);
		}
		return Integer.parseInt(hex, 16);
	}

	/**
	 * Parses a MAC address string (e.g., "aa:bb:cc:dd:ee:ff").
	 *
	 * @param mac the MAC address string
	 * @return the MAC address as byte array
	 */
	private byte[] parseMacAddress(String mac) {
		String[] parts = mac.split(":");
		byte[] address = new byte[6];

		for (int i = 0; i < 6 && i < parts.length; i++) {
			address[i] = (byte) Integer.parseInt(parts[i], 16);
		}

		return address;
	}

	/**
	 * Determines the ARP state from flags.
	 *
	 * @param flags the ARP flags
	 * @return the ARP state
	 */
	private ArpState determineState(int flags) {
		if ((flags & ATF_PERM) != 0) {
			return ArpState.PERMANENT;
		}
		if ((flags & ATF_COM) != 0) {
			return ArpState.REACHABLE;
		}
		return ArpState.INCOMPLETE;
	}
}
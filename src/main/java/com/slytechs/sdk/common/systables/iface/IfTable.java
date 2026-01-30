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

import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;
import com.slytechs.sdk.common.util.DataRate;

/**
 * Abstract base class for network interface table access.
 * 
 * <p>
 * Provides methods to query and manage network interfaces on the system.
 * Platform-specific implementations handle the details of accessing interface
 * information through native APIs.
 * </p>
 *
 * @author Mark Bednarczyk
 */
public abstract class IfTable {

	/**
	 * Gets the interface table for the current platform.
	 *
	 * @return the interface table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static IfTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxIfTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxIfTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the interface type classification.
	 */
	public static IfType getType(String ifname) {
		if (isLoopback(ifname)) {
			return IfType.LOOPBACK;
		}
		if (isVirtual(ifname)) {
			return IfType.VIRTUAL;
		}
		return IfType.PHYSICAL;
	}

	/**
	 * Checks if an interface is a loopback interface.
	 */
	public static boolean isLoopback(String ifname) {
		return switch (Platform.current()) {
		case LINUX -> LinuxIfTable.isLoopback(ifname);
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	public static boolean isUp(String ifname) {
		return switch (Platform.current()) {
		case LINUX -> LinuxIfTable.isUp(ifname);
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	};

	/**
	 * Checks if an interface is virtual (not backed by physical hardware). Virtual
	 * interfaces include: loopback, bridges, tun/tap, veth, VLANs, etc.
	 */
	public static boolean isVirtual(String ifname) {
		return switch (Platform.current()) {
		case LINUX -> LinuxIfTable.isVirtual(ifname);
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	public static Optional<DataRate> linkSpeed(String ifname) {
		return switch (Platform.current()) {
		case LINUX -> LinuxIfTable.linkSpeed(ifname);
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};

	}

	/**
	 * Gets the interface table for Linux.
	 *
	 * @return the Linux interface table
	 */
	public static IfTable linux() {
		return new LinuxIfTable();
	}

	/**
	 * Gets the interface table for Unix/Linux platforms.
	 *
	 * @return the Unix interface table
	 */
	public static IfTable unix() {
		return new LinuxIfTable();
	}

	/**
	 * Lists all network interfaces on the system.
	 *
	 * @return list of all interfaces
	 */
	public abstract List<? extends IfDevice> list();

	/**
	 * Lists only active (up) network interfaces.
	 *
	 * @return list of active interfaces
	 */
	public abstract List<? extends IfDevice> listActive();

	/**
	 * Looks up information for a specific network interface by name.
	 *
	 * @param name the interface name (e.g., "eth0", "wlan0")
	 * @return the interface information, or empty if not found
	 */
	public abstract Optional<? extends IfDevice> lookup(String name);

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();

}
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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;
import com.slytechs.sdk.common.util.DataRate;

/**
 * Linux/Unix/POSIX implementation of network interface table.
 * 
 * <p>
 * Accesses network interface information using POSIX APIs such as getifaddrs,
 * ioctl, and sysfs (on Linux). Supports Linux, macOS, and BSD variants.
 * </p>
 *
 * @author Mark Bednarczyk
 */
public class LinuxIfTable extends IfTable {

	public static Optional<String> driver(String ifname) {
		try {
			// /sys/class/net/eth0/device/driver is a symlink to driver directory
			Path driverLink = Path.of("/sys/class/net/" + ifname + "/device/driver");
			Path driverPath = Files.readSymbolicLink(driverLink);
			return Optional.of(driverPath.getFileName().toString());
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	// In IfTable or LinuxIfTable

	public static Optional<LinkDuplex> duplex(String ifname) {
		try {
			String duplex = Files.readString(Path.of("/sys/class/net/" + ifname + "/duplex")).trim();
			return Optional.of(LinkDuplex.valueOf(duplex.toUpperCase()));
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static Optional<byte[]> hardwareAddress(String ifname) {
		try {
			String mac = Files.readString(Path.of("/sys/class/net/" + ifname + "/address")).trim();
			// Parse "aa:bb:cc:dd:ee:ff" format
			String[] parts = mac.split(":");
			byte[] addr = new byte[6];
			for (int i = 0; i < 6; i++) {
				addr[i] = (byte) Integer.parseInt(parts[i], 16);
			}
			return Optional.of(addr);
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static boolean hasCarrier(String ifname) {
		try {
			String carrier = Files.readString(Path.of("/sys/class/net/" + ifname + "/carrier")).trim();
			return "1".equals(carrier);
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * Checks if an interface is a loopback interface.
	 */
	public static boolean isLoopback(String ifname) {
		// Method 1: Check name (simplest)
		if ("lo".equals(ifname)) {
			return true;
		}

		// Method 2: Use Java NetworkInterface
		try {
			NetworkInterface ni = NetworkInterface.getByName(ifname);
			return ni != null && ni.isLoopback();
		} catch (SocketException e) {
			return false;
		}
	}

	public static boolean isUp(String ifname) {
		try {
			String operstate = Files.readString(Path.of("/sys/class/net/" + ifname + "/operstate")).trim();
			return "up".equals(operstate);
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * Checks if an interface is virtual (not backed by physical hardware).
	 * 
	 * @param ifname the interface name
	 * @return true if virtual, false if physical hardware
	 */
	public static boolean isVirtual(String ifname) {
		try {
			// Physical NICs have a 'device' symlink to PCI/USB bus
			// Virtual interfaces (bridges, tun/tap, veth, etc.) don't
			Path deviceLink = Path.of("/sys/class/net/" + ifname + "/device");
			return !Files.exists(deviceLink);
		} catch (Exception e) {
			// If we can't check sysfs, assume virtual (safe default)
			return true;
		}
	}

	public static Optional<DataRate> linkSpeed(String ifname) {

		// Build path string
		String path = "/sys/class/net/" + ifname + "/speed";

		try {
			String str = Files.readString(Path.of(path)).trim();
			var speed = DataRate.ofMegabitsPerSecond(Long.parseLong(str));

			return Optional.of(speed);

		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static int mtu(String ifname) {
		try {
			String str = Files.readString(Path.of("/sys/class/net/" + ifname + "/mtu"));
			return Integer.parseInt(str.trim());
		} catch (Throwable e) {
			return -1;
		}
	}

	public static Optional<IfStats> statistics(String ifname) {
		try {
			Path statsPath = Path.of("/sys/class/net/" + ifname + "/statistics");
			return Optional.of(new UnixIfStats(
					Long.parseLong(Files.readString(statsPath.resolve("rx_bytes")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("tx_bytes")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("rx_packets")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("tx_packets")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("rx_errors")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("tx_errors")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("rx_dropped")).trim()),
					Long.parseLong(Files.readString(statsPath.resolve("tx_dropped")).trim())));
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static int txQueueLen(String ifname) {
		try {
			String str = Files.readString(Path.of("/sys/class/net/" + ifname + "/tx_queue_len"));
			return Integer.parseInt(str.trim());
		} catch (Throwable e) {
			return -1;
		}
	}

	@Override
	public List<LinuxIfDevice> list() {
		try {
			var list = NetworkInterface.networkInterfaces()
					.map(ni -> new LinuxIfDevice(ni.getName()))
					.toList();

			return list;
		} catch (SocketException e) {
			return List.of();
		}
	}

	@Override
	public List<LinuxIfDevice> listActive() {
		return list().stream()
				.filter(device -> device.isUp())
				.toList();
	}

	@Override
	public Optional<LinuxIfDevice> lookup(String name) {
		try {
			NetworkInterface ni = NetworkInterface.getByName(name);
			if (ni != null) {
				return Optional.of(new LinuxIfDevice(name));
			}
		} catch (SocketException e) {
			// Fall through
		}
		return Optional.empty();
	}

	@Override
	public Platform platform() {
		return Platform.current().isUnix() ? Platform.current() : Platform.LINUX;
	}
}
/*
 * Sly Technologies Free License
 * 
 * Copyright 2024 Sly Technologies Inc.
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
package com.slytechs.sdk.common.systables;

/**
 * Enumeration of supported operating system platforms with automatic detection.
 * 
 * <p>
 * This enum provides platform detection based on system properties and utility
 * methods for checking platform characteristics. The current platform is
 * automatically detected at class initialization time.
 * </p>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * {@snippet :
 * Platform platform = Platform.current();
 * 
 * if (platform.isUnix()) {
 *     // POSIX-compatible operations
 * }
 * 
 * if (platform == Platform.LINUX) {
 *     // Linux-specific operations
 * }
 * 
 * System.out.println("Running on: " + Platform.osName());
 * }
 *
 * @author Mark Bednarczyk
 */
public enum Platform {

	/** Linux operating system. */
	LINUX,

	/** Microsoft Windows operating system. */
	WINDOWS,

	/** Apple macOS (Darwin) operating system. */
	MACOS,

	/** FreeBSD operating system. */
	FREEBSD,

	/** OpenBSD operating system. */
	OPENBSD,

	/** NetBSD operating system. */
	NETBSD,

	/** Oracle Solaris operating system. */
	SOLARIS,

	/** Unknown or unsupported operating system. */
	UNKNOWN;

	/** The detected current platform. */
	private static final Platform CURRENT;

	/** The operating system name from system properties. */
	private static final String OS;

	/** The CPU architecture from system properties. */
	private static final String ARCH;

	static {
		OS = System.getProperty("os.name");
		ARCH = System.getProperty("os.arch");

		CURRENT = detectPlatform();
	}

	/**
	 * Detects the current platform based on system properties.
	 *
	 * @return the detected platform
	 */
	private static Platform detectPlatform() {
		if (OS == null)
			return UNKNOWN;

		String osLower = OS.toLowerCase();

		if (osLower.startsWith("windows"))
			return WINDOWS;
		if (osLower.startsWith("linux"))
			return LINUX;
		if (osLower.startsWith("mac") || osLower.startsWith("darwin"))
			return MACOS;
		if (osLower.contains("freebsd"))
			return FREEBSD;
		if (osLower.contains("openbsd"))
			return OPENBSD;
		if (osLower.contains("netbsd"))
			return NETBSD;
		if (osLower.contains("sunos") || osLower.contains("solaris"))
			return SOLARIS;

		return UNKNOWN;
	}

	/**
	 * Gets the current platform.
	 * 
	 * <p>
	 * This method returns the automatically detected platform. If the platform
	 * cannot be detected or is not supported, it throws an
	 * UnsupportedOperationException.
	 * </p>
	 *
	 * @return the current platform
	 * @throws UnsupportedOperationException if the platform is unknown or
	 *                                       unsupported
	 */
	public static Platform current() {
		if (CURRENT == UNKNOWN)
			throw new UnsupportedOperationException(
					"Unsupported platform: " + OS + ", " + ARCH);

		return CURRENT;
	}

	/**
	 * Gets the operating system name from system properties.
	 *
	 * @return the OS name (e.g., "Linux", "Windows 10", "Mac OS X")
	 */
	public static String osName() {
		return OS;
	}

	/**
	 * Gets the CPU architecture from system properties.
	 *
	 * @return the architecture (e.g., "amd64", "x86_64", "aarch64")
	 */
	public static String osArch() {
		return ARCH;
	}

	/**
	 * Checks if this platform is a Unix-like operating system.
	 * 
	 * <p>
	 * Returns true for all platforms except Windows and Unknown.
	 * </p>
	 *
	 * @return true if this is a Unix-like platform
	 */
	public boolean isUnix() {
		return this != WINDOWS && this != UNKNOWN;
	}

	/**
	 * Checks if this platform is POSIX-compliant.
	 * 
	 * <p>
	 * Currently equivalent to {@link #isUnix()}.
	 * </p>
	 *
	 * @return true if this is a POSIX-compliant platform
	 */
	public boolean isPosix() {
		return isUnix();
	}

	/**
	 * Checks if this platform is a BSD-derived operating system.
	 * 
	 * <p>
	 * Returns true for macOS, FreeBSD, OpenBSD, and NetBSD.
	 * </p>
	 *
	 * @return true if this is a BSD-derived platform
	 */
	public boolean isBsd() {
		return this == MACOS || this == FREEBSD || this == OPENBSD || this == NETBSD;
	}

	/**
	 * Checks if this platform is Linux.
	 *
	 * @return true if this platform is Linux
	 */
	public boolean isLinux() {
		return this == LINUX;
	}

	/**
	 * Checks if this platform is Windows.
	 *
	 * @return true if this platform is Windows
	 */
	public boolean isWindows() {
		return this == WINDOWS;
	}

	/**
	 * Checks if this platform is macOS.
	 *
	 * @return true if this platform is macOS
	 */
	public boolean isMacOS() {
		return this == MACOS;
	}
}
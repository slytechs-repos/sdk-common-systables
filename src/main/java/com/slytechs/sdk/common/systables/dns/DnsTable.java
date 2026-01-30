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
package com.slytechs.sdk.common.systables.dns;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Abstract base class for DNS resolver configuration access.
 *
 * @author Mark Bednarczyk
 */
public abstract class DnsTable {

	/**
	 * Gets the DNS configuration table for the current platform.
	 *
	 * @return the DNS table
	 * @throws UnsupportedOperationException if the current platform is not
	 *                                       supported
	 */
	public static DnsTable current() {
		return switch (Platform.current()) {
		case LINUX -> new LinuxDnsTable();
		case WINDOWS -> throw new UnsupportedOperationException("Windows not yet implemented");
		case MACOS -> new LinuxDnsTable();
		default -> throw new UnsupportedOperationException("Platform not supported: " + Platform.current());
		};
	}

	/**
	 * Gets the DNS configuration table for Linux.
	 *
	 * @return the Linux DNS table
	 */
	public static DnsTable linux() {
		return new LinuxDnsTable();
	}

	/**
	 * Gets the current DNS resolver configuration.
	 *
	 * @return the DNS configuration
	 */
	public abstract DnsConfig getConfig();

	/**
	 * Gets the platform this table is for.
	 *
	 * @return the platform
	 */
	public abstract Platform platform();
}
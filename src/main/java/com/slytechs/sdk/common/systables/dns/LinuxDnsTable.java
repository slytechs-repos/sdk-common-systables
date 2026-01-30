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

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.slytechs.sdk.common.systables.Platform;

/**
 * Linux implementation of DNS configuration table.
 * 
 * <p>
 * Parses /etc/resolv.conf to extract DNS nameservers, search domains, and
 * options. Supports both traditional resolv.conf and systemd-resolved
 * configurations.
 * </p>
 *
 * @author Mark Bednarczyk
 */
public class LinuxDnsTable extends DnsTable {

	private static final Path RESOLV_CONF = Path.of("/etc/resolv.conf");
	private static final Path SYSTEMD_RESOLV_CONF = Path.of("/run/systemd/resolve/resolv.conf");

	@Override
	public LinuxDnsConfig getConfig() {
		// Try systemd-resolved first, then fall back to traditional resolv.conf
		Path configPath = Files.exists(SYSTEMD_RESOLV_CONF) ? SYSTEMD_RESOLV_CONF : RESOLV_CONF;

		List<InetAddress> nameservers = new ArrayList<>();
		List<String> searchDomains = new ArrayList<>();
		Optional<String> domain = Optional.empty();
		List<String> options = new ArrayList<>();

		try (var lines = Files.lines(configPath)) {
			for (String line : lines.toList()) {
				// Skip comments and empty lines
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				String[] parts = line.split("\\s+", 2);
				if (parts.length < 2) {
					continue;
				}

				String keyword = parts[0];
				String value = parts[1];

				switch (keyword) {
				case "nameserver":
					try {
						nameservers.add(InetAddress.getByName(value));
					} catch (Exception e) {
						// Skip invalid nameserver
					}
					break;

				case "search":
					// Multiple search domains separated by spaces
					searchDomains.addAll(Arrays.asList(value.split("\\s+")));
					break;

				case "domain":
					domain = Optional.of(value);
					break;

				case "options":
					// Multiple options separated by spaces
					options.addAll(Arrays.asList(value.split("\\s+")));
					break;

				default:
					// Ignore unknown keywords
					break;
				}
			}
		} catch (IOException e) {
			// Return empty configuration
		}

		return new LinuxDnsConfig(nameservers, searchDomains, domain, options);
	}

	@Override
	public Platform platform() {
		return Platform.LINUX;
	}
}
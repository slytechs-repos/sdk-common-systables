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

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

/**
 * Common interface for DNS resolver configuration.
 *
 * @author Mark Bednarczyk
 */
public interface DnsConfig {

	/**
	 * Gets the list of DNS nameservers.
	 *
	 * @return list of nameserver addresses
	 */
	List<InetAddress> nameservers();

	/**
	 * Gets the search domains.
	 *
	 * @return list of search domains
	 */
	List<String> searchDomains();

	/**
	 * Gets the local domain name.
	 *
	 * @return the domain name, or empty if not set
	 */
	Optional<String> domain();

	/**
	 * Gets resolver options.
	 *
	 * @return map of option names to values
	 */
	List<String> options();
}
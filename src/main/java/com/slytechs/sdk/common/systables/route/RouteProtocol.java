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
package com.slytechs.sdk.common.systables.route;

/**
 * Routing protocol type.
 *
 * @author Mark Bednarczyk
 */
public enum RouteProtocol {

	/** Route added by DHCP client. */
	DHCP,

	/** Route added by kernel. */
	KERNEL,

	/** Route added by router advertisement (IPv6). */
	RA,

	/** Route added by routing daemon. */
	BIRD,

	/** Route added by boot process. */
	BOOT,

	/** Statically configured route. */
	STATIC,

	/** Unknown or unspecified protocol. */
	UNKNOWN
}
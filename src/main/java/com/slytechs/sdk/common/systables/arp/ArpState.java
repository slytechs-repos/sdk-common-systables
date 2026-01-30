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

/**
 * ARP entry state.
 *
 * @author Mark Bednarczyk
 */
public enum ArpState {

	/** Resolution is incomplete (in progress). */
	INCOMPLETE,

	/** Entry is valid and recently confirmed. */
	REACHABLE,

	/** Entry is valid but needs reconfirmation. */
	STALE,

	/** Entry is scheduled for reconfirmation. */
	DELAY,

	/** Sending ARP probes to confirm reachability. */
	PROBE,

	/** Resolution failed. */
	FAILED,

	/** No ARP needed for this entry. */
	NOARP,

	/** Static/permanent entry (manually configured). */
	PERMANENT,

	/** Unknown state. */
	UNKNOWN
}
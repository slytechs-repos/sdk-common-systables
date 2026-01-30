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
package com.slytechs.sdk.common.systables.cpu;

import java.util.List;

/**
 * Common interface for CPU hardware information.
 *
 * @author Mark Bednarczyk
 */
public interface CpuInfo {

	/**
	 * Gets the CPU model name.
	 *
	 * @return the CPU model name
	 */
	String model();

	/**
	 * Gets the CPU vendor.
	 *
	 * @return the vendor (e.g., "GenuineIntel", "AuthenticAMD")
	 */
	String vendor();

	/**
	 * Gets the number of logical processors (cores with hyperthreading).
	 *
	 * @return the logical processor count
	 */
	int logicalCores();

	/**
	 * Gets the number of physical CPU cores.
	 *
	 * @return the physical core count
	 */
	int physicalCores();

	/**
	 * Gets the number of physical CPU sockets/packages.
	 *
	 * @return the socket count
	 */
	int sockets();

	/**
	 * Gets the CPU frequency in MHz.
	 *
	 * @return the frequency in MHz
	 */
	double frequencyMHz();

	/**
	 * Gets the list of CPU flags/features.
	 *
	 * @return list of CPU features (e.g., "sse", "avx", "avx2")
	 */
	List<String> flags();
}
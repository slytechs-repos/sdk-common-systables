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
package com.slytechs.sdk.common.systables.foreign;

import java.lang.invoke.MethodHandles;

import com.slytechs.sdk.common.foreign.ForeignInitializer;

/**
 * Foreign function interface initializer for system table operations.
 * 
 * <p>
 * This class provides platform-specific native function bindings for accessing
 * system tables such as network interfaces, routing tables, ARP cache, and DNS
 * configuration. Functions are loaded based on the current platform at class
 * initialization time.
 * </p>
 *
 * @author Mark Bednarczyk
 */
public class SystableForeignInitializer extends ForeignInitializer<SystableDowncall, SystableException> {

	public SystableForeignInitializer(Class<?> initializerClass) {
		super(
				initializerClass.toGenericString(),
				SystableDowncall::new,
				SystableDowncall::new,
				MethodHandles.lookup());
	}

}
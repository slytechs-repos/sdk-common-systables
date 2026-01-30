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

import com.slytechs.sdk.common.foreign.ForeignException;

/**
 * Exception thrown when system table operations fail.
 *
 * @author Mark Bednarczyk
 */
public class SystableException extends Exception implements ForeignException {

	private static final long serialVersionUID = 1L;

	private final int errorCode;

	public SystableException(String message) {
		super(message);
		this.errorCode = -1;
	}

	public SystableException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public SystableException(long errorCode, String message) {
		super(message);
		this.errorCode = (int) errorCode;
	}

	public SystableException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = -1;
	}

	@Override
	public int getCode() {
		return errorCode;
	}
}
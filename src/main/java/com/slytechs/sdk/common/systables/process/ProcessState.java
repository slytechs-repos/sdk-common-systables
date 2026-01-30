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
package com.slytechs.sdk.common.systables.process;

/**
 * Process state.
 *
 * @author Mark Bednarczyk
 */
public enum ProcessState {

	/** Running or runnable (on run queue). */
	RUNNING('R'),

	/** Interruptible sleep (waiting for an event). */
	SLEEPING('S'),

	/** Uninterruptible sleep (usually I/O). */
	DISK_SLEEP('D'),

	/** Stopped (on a signal) or traced. */
	STOPPED('T'),

	/** Zombie (terminated but not reaped by parent). */
	ZOMBIE('Z'),

	/** Dead (should never be seen). */
	DEAD('X'),

	/** Idle kernel thread. */
	IDLE('I'),

	/** Unknown state. */
	UNKNOWN('?');

	private final char code;

	ProcessState(char code) {
		this.code = code;
	}

	/**
	 * Gets the single-character state code.
	 *
	 * @return the state code
	 */
	public char code() {
		return code;
	}

	/**
	 * Parses a state code character to enum.
	 *
	 * @param code the state code
	 * @return the process state
	 */
	public static ProcessState fromCode(char code) {
		for (ProcessState state : values()) {
			if (state.code == code) {
				return state;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Checks if this is a running state.
	 *
	 * @return true if running or runnable
	 */
	public boolean isRunning() {
		return this == RUNNING;
	}

	/**
	 * Checks if this is a sleeping state.
	 *
	 * @return true if sleeping (interruptible or disk sleep)
	 */
	public boolean isSleeping() {
		return this == SLEEPING || this == DISK_SLEEP;
	}

	/**
	 * Checks if this is a dead/zombie state.
	 *
	 * @return true if zombie or dead
	 */
	public boolean isDead() {
		return this == ZOMBIE || this == DEAD;
	}
}
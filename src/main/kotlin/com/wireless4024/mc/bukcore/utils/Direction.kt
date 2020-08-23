/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2020, Wittawat Manha
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.wireless4024.mc.bukcore.utils

import org.bukkit.Location

object Direction {

	const val BASE: Byte = 0
	const val UP: Byte = 1
	const val NORTH: Byte = 2
	const val SOUTH: Byte = 3
	const val WEST: Byte = 4
	const val EAST: Byte = 5
	const val DOWN: Byte = 6

	fun fromYaw(yaw: Float): Byte {
		@Suppress("NAME_SHADOWING") val yaw = (yaw % 360 + 360) % 360
		return if (yaw >= 315 || yaw < 45) SOUTH
		else if (yaw < 135) WEST
		else if (yaw < 225) NORTH
		else if (yaw < 315) EAST
		else NORTH // ensure?
	}

	fun direction4(loc: Location) = if (loc.yaw == 0f && loc.pitch == 0f)/* direction not set */ NORTH else fromYaw(loc.yaw)

	fun rotate4(data: Byte, amount: Int): Byte {
		return when (data) {
			NORTH -> direction4(amount)
			EAST -> direction4(amount + 1)
			SOUTH -> direction4(amount + 2)
			WEST -> direction4(amount + 3)
			else  -> BASE
		}
	}

	fun normalize4(now: Byte, base: Byte) = when (base) {
		EAST -> rotate4(now, 3)
		SOUTH -> rotate4(now, 2)
		WEST -> rotate4(now, 1)
		else  -> now
	}

	fun destabilize4(normalized: Byte, to: Byte) = when (to) {
		EAST -> rotate4(normalized, 1)
		SOUTH -> rotate4(normalized, 2)
		WEST -> rotate4(normalized, 3)
		else  -> normalized
	}

	fun rotate6(data: Byte, amount: Int): Byte {
		return when (data) {
			NORTH -> direction4(amount)
			EAST -> direction4(amount + 1)
			SOUTH -> direction4(amount + 2)
			WEST -> direction4(amount + 3)
			UP -> direction6(amount + 4)
			DOWN -> direction6(amount + 5)
			else  -> BASE
		}
	}

	fun direction4(amount: Int) = when (amount and 3) {
		0 -> NORTH
		1 -> EAST
		2 -> SOUTH
		3 -> WEST
		else -> BASE
	}

	fun direction6(amount: Int) = when (amount and 5) {
		0 -> NORTH
		1 -> EAST
		2 -> SOUTH
		3 -> WEST
		4 -> UP
		5 -> DOWN
		else -> BASE
	}
}
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

package com.wireless4024.mc.bukcore.utils.blocks


@Suppress("NOTHING_TO_INLINE")
class Position(val pos: Long) {
	val x get() = getX(pos)
	val y get() = getY(pos)
	val z get() = getZ(pos)

	companion object {
		@JvmStatic
		inline fun toInts(n: Long): IntArray {
			return intArrayOf((n shr 38).toInt(), (n shl 26 shr 52).toInt(), (n shl 38 shr 38).toInt())
		}

		@JvmStatic
		inline fun getX(n: Long): Int = (n shr 38).toInt()
		@JvmStatic
		inline fun getY(n: Long): Int = ((n shl 26) shr 52).toInt()
		@JvmStatic
		inline fun getZ(n: Long): Int = ((n shl 38) shr 38).toInt()

		@JvmStatic
		inline fun toLong(x: Int, y: Int, z: Int): Long {
			return ((x.toLong() and 0x3FFFFFFL) shl 38) or ((y.toLong() and 0xFFFL) shl 26) or (z.toLong() and 0x3FFFFFFL)
		}

//		const val c: Int = 26
//		const val d = 26
//		const val f =12
//		const val g = 26
//		const val h = 38
//		const val i = 67108863L
//		const val j = 4095L
//		const val k =67108863L
	}
}
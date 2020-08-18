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

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SortedArrayListTest {

	@Test
	fun testInsert() {
		val list = SortedArrayList<Int>()

		list.add(1)
		assertArrayEquals(intArrayOf(1), list.toIntArray())

		list.add(-1)
		assertArrayEquals(intArrayOf(-1, 1), list.toIntArray())

		list.add(10)
		assertArrayEquals(intArrayOf(-1, 1, 10), list.toIntArray())

		list.add(-10)
		assertArrayEquals(intArrayOf(-10, -1, 1, 10), list.toIntArray())

		list.add(Int.MIN_VALUE)
		list.add(Int.MIN_VALUE)
		assertArrayEquals(intArrayOf(Int.MIN_VALUE, Int.MIN_VALUE, -10, -1, 1, 10), list.toIntArray())

		assertThrows<Throwable> {
			list.add(0, 1)
		}
	}

	@Test
	fun testUnique() {
		val list = UniqueSortedArrayList<Int>()

		list.add(1)
		assertArrayEquals(intArrayOf(1), list.toIntArray())

		list.add(1)
		assertArrayEquals(intArrayOf(1), list.toIntArray())

		list.add(2)
		assertArrayEquals(intArrayOf(1, 2), list.toIntArray())
	}
}
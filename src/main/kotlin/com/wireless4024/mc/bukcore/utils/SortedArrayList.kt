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

import java.util.*

open class SortedArrayList<T : Comparable<T>>(initialCapacity: Int = 4) : ArrayList<T>(initialCapacity) {

	override fun add(element: T): Boolean {
		super.add(this.binarySearch(element).let { if (it < 0) it.inv() else it }, element)
		return true
	}

	fun uniqueAdd(element: T): Boolean {
		val where = this.binarySearch(element)
		if (where < 0) {
			super.add(where.inv(), element)
		}
		return false
	}

	override fun add(index: Int, element: T) {
		throw UnsupportedOperationException("list is sorted")
	}

	override fun contains(element: T): Boolean {
		return this.binarySearch(element) >= 0
	}

	override fun indexOf(element: T): Int {
		return this.binarySearch(element).let { if (it < 0) -1 else it }
	}

	override fun addAll(elements: Collection<T>): Boolean {
		for (element in elements)
			add(element)
		return true
	}

	override fun addAll(index: Int, elements: Collection<T>): Boolean {
		throw UnsupportedOperationException("list is sorted")
	}

	override fun spliterator(): Spliterator<T> {
		return Spliterators.spliterator(this, Spliterator.SIZED or Spliterator.ORDERED or Spliterator.SORTED)
	}
}
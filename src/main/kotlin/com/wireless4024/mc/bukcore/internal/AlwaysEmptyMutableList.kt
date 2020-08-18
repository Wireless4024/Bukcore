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

package com.wireless4024.mc.bukcore.internal

object AlwaysEmptyMutableList : MutableList<Any> {

	@Suppress("UNCHECKED_CAST")
	fun <T> get() = AlwaysEmptyMutableList as MutableList<T>

	override val size: Int = 0

	override fun contains(element: Any): Boolean = false

	override fun containsAll(elements: Collection<Any>): Boolean = false

	override fun get(index: Int): Any = throw IndexOutOfBoundsException()

	override fun indexOf(element: Any): Int = -1

	override fun isEmpty(): Boolean = true

	override fun iterator(): MutableIterator<Any> = AlwaysEmptyMutableIterator

	override fun lastIndexOf(element: Any): Int = -1

	override fun add(element: Any): Boolean = false

	override fun add(index: Int, element: Any) {
	}

	override fun addAll(index: Int, elements: Collection<Any>): Boolean = false

	override fun addAll(elements: Collection<Any>): Boolean = false

	override fun clear() {}

	override fun listIterator(): MutableListIterator<Any> = AlwaysEmptyMutableIterator

	override fun listIterator(index: Int): MutableListIterator<Any> = AlwaysEmptyMutableIterator

	override fun remove(element: Any): Boolean = false

	override fun removeAll(elements: Collection<Any>): Boolean = false

	override fun removeAt(index: Int): Any = throw IndexOutOfBoundsException()

	override fun retainAll(elements: Collection<Any>): Boolean = false

	override fun set(index: Int, element: Any): Any = throw IndexOutOfBoundsException()

	override fun subList(fromIndex: Int, toIndex: Int): MutableList<Any> = this

	private object AlwaysEmptyMutableIterator : MutableListIterator<Any> {

		override fun hasNext(): Boolean = false

		override fun next(): Any = throw UnsupportedOperationException()

		override fun remove() = throw UnsupportedOperationException()
		override fun hasPrevious(): Boolean = false

		override fun nextIndex(): Int = 0

		override fun previous(): Any = throw UnsupportedOperationException()

		override fun previousIndex(): Int = 0

		override fun add(element: Any) {}

		override fun set(element: Any) {}
	}
}
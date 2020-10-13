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

import java.util.*
import java.util.function.Consumer

interface PositionIterator : Iterator<Long>, Iterable<Long> {
	@JvmDefault
	override fun iterator() = this

	fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit)

	fun reset()
}

class SingletonIterator(val x: Int, val y: Int, val z: Int) : PositionIterator {
	private var active = true

	@Suppress("OVERRIDE_BY_INLINE")
	final override inline fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit) {
		consumer(x, y, z)
	}

	override fun reset() {
		active = true
	}

	override fun forEachRemaining(action: Consumer<in Long>) {
		if (active) action.accept(Position.toLong(x, y, z))
	}


	override fun spliterator(): Spliterator<Long> {
		return Arrays.spliterator(longArrayOf(Position.toLong(x, y, z)))
	}

	override fun hasNext(): Boolean = active

	override fun next(): Long = if (active) {
		active = false;Position.toLong(x, y, z)
	} else throw IndexOutOfBoundsException("Iterator end")

	override fun toString(): String = "Iterator(${Position.toLong(x, y, z)})"
}

class PositionIteratorXYZ(val x1: Int, val x2: Int,
                          val y1: Int, val y2: Int,
                          val z1: Int, val z2: Int) : PositionIterator {
	private var x = x1 - 1
	private var y = y1
	private var z = z1

	override fun reset() {
		x = x1 - 1
		y = y1
		z = z1
	}

	override fun hasNext(): Boolean = x < x2 && y < y2 && z < z2

	override fun next(): Long {
		if (x < x2) return Position.toLong(++x, y, z)
		x = x1
		if (y < y2) return Position.toLong(x, ++y, z)
		y = y1
		if (z < z2) return Position.toLong(x, y, ++z)
		throw IndexOutOfBoundsException("Iterator end")
	}

	@Suppress("OVERRIDE_BY_INLINE")
	final override inline fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (z in z1..z2)
			for (y in y1..y2)
				for (x in x1..x2)
					consumer(x, y, z)
	}

	override fun forEach(action: Consumer<in Long>) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (z in z1..z2)
			for (y in y1..y2)
				for (x in x1..x2)
					action.accept(Position.toLong(x, y, z))
	}

	override fun toString(): String = this.joinToString(prefix = "Iterator(", postfix = ")")
}

class PositionIteratorYZX(val x1: Int, val x2: Int,
                          val y1: Int, val y2: Int,
                          val z1: Int, val z2: Int) : PositionIterator {
	private var x = x1 - 1
	private var y = y1
	private var z = z1

	override fun hasNext(): Boolean = x < x2 && y < y2 && z < z2

	override fun next(): Long {
		if (y < y2) return Position.toLong(x, ++y, z)
		y = y1
		if (z < z2) return Position.toLong(x, y, ++z)
		z = z1
		if (x < x2) return Position.toLong(++x, y, z)
		throw IndexOutOfBoundsException("Iterator end")
	}

	override fun reset() {
		x = x1 - 1
		y = y1
		z = z1
	}

	@Suppress("OVERRIDE_BY_INLINE")
	final override inline fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (x in x1..x2)
			for (z in z1..z2)
				for (y in y1..y2)
					consumer(x, y, z)
	}

	override fun forEach(action: Consumer<in Long>) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (x in x1..x2)
			for (z in z1..z2)
				for (y in y1..y2)
					action.accept(Position.toLong(x, y, z))
	}

	override fun toString(): String = this.joinToString(prefix = "Iterator(", postfix = ")")
}

class PositionIteratorZXY(val x1: Int, val x2: Int,
                          val y1: Int, val y2: Int,
                          val z1: Int, val z2: Int) : PositionIterator {
	private var x = x1
	private var y = y1
	private var z = z1 - 1

	override fun hasNext(): Boolean = x < x2 && y < y2 && z < z2

	override fun next(): Long {
		if (z < z2) return Position.toLong(x, y, ++z)
		z = z1
		if (y < y2) return Position.toLong(x, ++y, z)
		y = y1
		if (x < x2) return Position.toLong(++x, y, z)
		throw IndexOutOfBoundsException("Iterator end")
	}

	override fun reset() {
		x = x1
		y = y1
		z = z1 - 1
	}

	@Suppress("OVERRIDE_BY_INLINE")
	final override inline fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (y in y1..y2)
			for (x in x1..x2)
				for (z in z1..z2)
					consumer(x, y, z)
	}

	final override fun forEach(action: Consumer<in Long>) {
		/* load variable to local to prevent kotlin from doing get value from field every loop */
		val x1: Int = x1
		val x2: Int = x2
		val y1: Int = y1
		val y2: Int = y2
		val z1: Int = z1
		val z2: Int = z2
		for (y in y1..y2)
			for (x in x1..x2)
				for (z in z1..z2)
					action.accept(Position.toLong(x, y, z))
	}


	override fun toString(): String = this.joinToString(prefix = "Iterator(", postfix = ")")
}

class PositionIteratorSpread(val x1: Int, val x2: Int,
                             val y1: Int, val y2: Int,
                             val z1: Int, val z2: Int) : PositionIterator {
	private var x = (x2 + x1) shr 1
	private var y = (y2 + y1) shr 1
	private var z = (z2 + z1) shr 1
	private var area = 0

	override fun hasNext(): Boolean = x < x2 && y < y2 && z < z2

	override fun next(): Long {
		throw IndexOutOfBoundsException("Iterator end")
	}

	override fun reset() {
	}

	@Suppress("OVERRIDE_BY_INLINE")
	final override inline fun forEach(consumer: (x: Int, y: Int, z: Int) -> Unit) {
		TODO()
	}

	override fun forEach(action: Consumer<in Long>) {
		TODO()
	}

	override fun toString(): String = this.joinToString(prefix = "Iterator(", postfix = ")")
}
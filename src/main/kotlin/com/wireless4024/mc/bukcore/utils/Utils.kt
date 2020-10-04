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

import com.google.common.util.concurrent.AtomicDouble
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Utilities
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
public interface Utils {

	companion object {

		private val SQUARE_TABLE = IntArray(100) { it * it }

		fun parseInt(s: String?, default: Int = 0): Int = s?.toIntOrNull() ?: default

		/**
		 * return square of integer
		 * @param i integer
		 * @return i * i
		 */
		@JvmStatic
		fun square(i: Int): Int {
			if (i == 0) return 0
			return if (i in 1..99) SQUARE_TABLE[i] else i * i
		}

		@JvmStatic
		fun square(i: Double): Double {
			return i * i
		}

		/**
		 * map list to mutable list
		 * @receiver List<T> source list
		 * @param func function to change value
		 * @returnlist of a results
		 */
		fun <T, R> List<T>.mapToMutable(func: (T) -> R): MutableList<R> {
			val new = mutableListOf<R>()
			for (x in this.indices)
				new.add(func(this[x]))
			return new
		}

		/**
		 * filter list to mutable list
		 * @receiver List<T> source list
		 * @param func function to filter
		 * @returnlist of a results
		 */
		fun <T> List<T>.filterToMutable(func: (T) -> Boolean): MutableList<T> {
			val new = mutableListOf<T>()
			for (x in this.indices) {
				val v = this[x]
				if (func(v))
					new.add(v)
			}
			return new
		}

		/**
		 * update all elements in list
		 * @receiver MutableList<T> source list
		 * @param func function to change value
		 * @return itself
		 */
		fun <T> MutableList<T>.update(func: (T) -> T): MutableList<T> {
			for (i in this.indices)
				this[i] = func(this[i])
			return this
		}

		/**
		 * update all elements in array
		 * @receiver Array<T> source array
		 * @param func function to change value
		 * @return itself
		 */
		fun <T> Array<T>.update(func: (T) -> T): Array<T> {
			for (i in this.indices)
				this[i] = func(this[i])
			return this
		}

		inline fun <T, reified R> Array<T>.mapToArray(func: (T) -> R): Array<R> = Array(this.size) { func(this[it]) }

		/**
		 * update all elements in list with element location
		 * @receiver MutableList<T> source list
		 * @param func  function to change value
		 * @return itself
		 */
		fun <T> MutableList<T>.updateIndexed(func: (T, Int) -> T): MutableList<T> {
			for (i in this.indices)
				this[i] = func(this[i], i)
			return this
		}

		/**
		 * drop first index and join to string
		 * @receiver Array<T> source array
		 * @return String
		 */
		fun <T> Array<T>.dropJoinToString(from: Int = 1): String {
			val sb = StringBuilder()
			for (i in from until this.size)
				sb.append(this[i]).append(' ')
			return sb.trim().toString()
		}

		/**
		 *
		 * @param count repeat count
		 * @param obj objects list
		 * @return array of repeated object
		 */
		fun repeat(count: Int, vararg obj: Any): Array<Any> {
			val n = Arrays.copyOf(obj, obj.size * count)
			val c = obj.size
			var i = 0
			while (i < c * count) {
				System.arraycopy(obj, 0, n, i, c)
				i += c
			}
			return n
		}

		/**
		 * find first index that value start with [str]
		 * @receiver List<String> source list
		 * @param str String
		 * @return index if found else null
		 */
		fun List<String>.istartWith(str: String): Int? {
			for (i in this.indices)
				if (this[i].startsWith(str)) return i
			return null
		}

		/**
		 * find first index that value start with [str]
		 * @receiver List<String> source list
		 * @param str String
		 * @return index if found else null
		 */
		fun List<out String?>.strMatch(wildcard: String): Int? {
			for (i in this.indices) {
				val str: String? = this[i]
				if (str != null && str.wildcardMatch(wildcard)) return i
			}
			return null
		}

		/**
		 * find last index that value start with [str]
		 * @receiver List<String> source list
		 * @param str String
		 * @return index if found else null
		 */
		fun List<String>.iLastStartWith(str: String): Int? {
			for (i in this.indices.reversed())
				if (this[i].startsWith(str)) return i
			return null
		}

		/**
		 * find first value that start with [str]
		 * @receiver List<String> source list
		 * @param str String
		 * @return index if found else null
		 */
		fun List<String>.startWith(str: String): String? = istartWith(str)?.let { this[it] }

		/**
		 * find first index that value start with [str]
		 * @receiver Array<String> source array
		 * @param str String
		 * @return index if found else null
		 */
		fun Array<String>.istartWith(str: String): Int? {
			for (i in this.indices)
				if (this[i].startsWith(str)) return i
			return null
		}

		/**
		 * find first value that start with [str]
		 * @receiver List<String> source list
		 * @param str String
		 * @return index if found else null
		 */
		fun Array<String>.startWith(str: String): String? = istartWith(str)?.let { this[it] }

		/**
		 * find value that start with [str] with binary search
		 * @param array Array<String> sorted array
		 * @param str String
		 * @return index if found else -1
		 */
		fun sortedInclude(array: Array<String>, str: String): Int {
			var low = 0
			var high = array.lastIndex
			var mid: Int

			while (low <= high) {
				mid = (low + high) ushr 1
				val m = array[mid]

				if (str.startsWith(m)) return mid

				val cmp = m.compareTo(str)

				when {
					cmp < 0 -> low = mid + 1
					cmp > 0 -> high = mid - 1
					else -> return mid
				}
			}

			return -1
		}

		fun somethingMatch(array: Array<String>, wildcard: String): Int {
			for ((i, m) in array.withIndex()) {
				if (m.wildcardMatch(wildcard)) return i
			}

			return -1
		}

		fun <T : Comparable<T>> List<T>.binaryContains(element: T?) = this.binarySearch(element) >= 0

		fun String?.wildcardMatch(wildcard: String): Boolean {
			val len_s = this?.length ?: return false
			val len_p = wildcard.length
			if (len_s == 0 && len_p == 0) return true
			var i = 0
			var j = 0
			// save the last matched index
			var start_s = -1
			var start_p = -1
			while (i < len_s) {
				if (j < len_p && (this[i] == wildcard[j] || wildcard[j] == '?')) {
					++i
					++j
				} else if (j < len_p && wildcard[j] == '*') {
					while (j < len_p && wildcard[j] == '*') ++j
					if (j == len_p) return true
					start_p = j
					start_s = i
				} else if ((j >= len_p || this[i] != wildcard[j]) && start_p > -1) {
					++start_s
					j = start_p
					i = start_s
				} else {
					return false
				}
			}
			while (j < len_p) {
				if (wildcard[j] != '*') return false
				++j
			}
			return true
		}
	}


}

fun Player.sendMessage(o: Any?) {
	this.sendMessage(if (o is Array<*>) o.contentDeepToString() else if (o is String) o else o?.toString())
}

var <R> AtomicReference<R>.value
	get() = get()
	set(value) = set(value)
var AtomicInteger.value
	get() = get()
	set(value) = set(value)
var AtomicLong.value
	get() = get()
	set(value) = set(value)
var AtomicDouble.value
	get() = get()
	set(value) = set(value)
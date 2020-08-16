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

@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.wireless4024.mc.bukcore.utils

import com.wireless4024.mc.bukcore.utils.Utils.Companion.iLastStartWith
import com.wireless4024.mc.bukcore.utils.Utils.Companion.istartWith
import com.wireless4024.mc.bukcore.utils.Utils.Companion.update
import com.wireless4024.mc.bukcore.utils.Utils.Companion.updateIndexed
import org.bukkit.Material
import org.bukkit.Material.AIR
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * warp [ItemMeta.getLore] with indexed search feature
 * @property _meta ItemMeta
 * @property original original lore list
 * @property index Array<String> array of string to pre-index
 * @property indexed HashMap<String, Int> indexed string
 * @property raw MutableList<String> raw string from lore
 * @property size Int size of lores
 * @property lores List<String> get original lore list
 * @property meta ItemMeta get [ItemMeta] that replace lore with [original]
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class LoreWrapper(private val _meta: ItemMeta,
                  _index: Array<String> = emptyArray(),
                  private val skipNotIndex: Boolean = false) : MutableList<String> {

	companion object {

		/**
		 * regex to replace format string from minecraft into raw string
		 */
		val MC_RAW_STR = "§.".toRegex()

		/**
		 * if this throw NoSuchMethodError please use [Lore.wrap]
		 * wrap item into [LoreWrapper]
		 * @param item ItemStack item to wrap lore
		 * @param force Boolean force return [LoreWrapper] if don't have lores
		 * @param index Array<String> pre-index key
		 * @return LoreWrapper if item has lores else null
		 */
		@JvmStatic
		@JvmOverloads
		fun wrap(item: ItemStack?,
		         force: Boolean = false,
		         index: Array<String> = emptyArray(),
		         skipNotIndex: Boolean = false): LoreWrapper? {
			if (item == null || item.type == AIR) return null

			if (!force && (!item.hasItemMeta() || !item.itemMeta.hasLore())) return null
			return LoreWrapper(item.itemMeta, index, skipNotIndex)
		}
	}

	private val original: MutableList<String> = _meta.lore ?: mutableListOf()

	/**
	 * sorted array of pre-index string
	 */
	private val index = _index.update { it.toLowerCase() }.also { sort() }

	/**
	 * indexed value
	 */
	private val indexed: HashMap<String, Int> = HashMap(index.size, 1f)

	/**
	 * list of raw lore string
	 */
	private val raw = original.toMutableList().updateIndexed { s, i ->
		val str = s.toLowerCase().replace(MC_RAW_STR, "")
		val idx = Utils.sortedInclude(index, str)
		if (idx >= 0) indexed[index[idx]] = i
		str
	}

	/**
	 * re-index when lores change
	 */
	private fun reIndex() {
		raw.clear()
		indexed.clear()
		original.forEachIndexed { i, s ->
			val str = s.toLowerCase().replace(MC_RAW_STR, "")
			val idx = Utils.sortedInclude(index, str)
			if (idx >= 0) indexed[index[idx]] = i
			raw.add(str)
		}
	}

	/**
	 * length or lores
	 */
	override val size: Int = raw.size

	/**
	 * check if having lore [prefix]
	 * @param prefix lore to find
	 * @return Boolean
	 */
	override fun contains(prefix: String): Boolean = indexOf(prefix) >= 0

	/**
	 * Checks if all elements in the specified collection are contained in this collection.
	 * @param elements Collection<String>
	 * @return Boolean
	 */
	override fun containsAll(elements: Collection<String>): Boolean = elements.map { indexOf(it) > 0 }
			.reduce { a, b -> a && b }

	override fun get(index: Int): String = if (index in this.indices) raw[index] else ""

	operator fun get(prefix: String): String? {
		val i = indexOf(prefix)
		return if (i in this.indices) raw[i] else null
	}

	/**
	 * find index of first lore that start with [prefix]
	 * @param prefix String
	 * @return index of element, -1 if not found
	 */
	override fun indexOf(prefix: String): Int {
		val p = if (prefix.indexOf('§') >= 0) prefix.replace(MC_RAW_STR, "") else prefix
		return indexed[p] ?: if (this.skipNotIndex) -1 else raw.istartWith(p) ?: -1
	}

	/**
	 * replace first lore that start with [prefix] with [prefix]
	 * @param key String to search
	 * @param prefix String to replace
	 */
	fun replace(prefix: String, lore: String): String? {
		val where = indexOf(prefix)
		if (where < 0) return null
		val old = original[where]
		original[where] = lore
		raw[where] = lore.toLowerCase().replace(MC_RAW_STR, "")
		return old
	}

	/**
	 * replace first lore that start with [prefix] with [prefix] else add to lore-list
	 * @param key String to search
	 * @param prefix String to replace
	 */
	fun replaceOrAdd(prefix: String, lore: String): String? {
		val where = indexOf(prefix)
		if (where !in indices) {
			add(lore)
			return null
		}
		val old = raw[where]
		original[where] = lore
		raw[where] = lore.toLowerCase().replace(MC_RAW_STR, "")
		return old
	}

	override fun isEmpty(): Boolean = raw.isEmpty()

	override fun iterator(): MutableIterator<String> = raw.iterator()

	override fun lastIndexOf(prefix: String): Int {
		val p = if (prefix.indexOf('§') >= 0) prefix.replace(MC_RAW_STR, "") else prefix
		return raw.iLastStartWith(p) ?: -1
	}

	override fun add(lore: String): Boolean {
		original.add(lore)
		return raw.add(lore.toLowerCase().replace(MC_RAW_STR, "")).also { reIndex() }
	}

	override fun add(index: Int, lore: String) {
		original.add(index, lore)
		return raw.add(index, lore.toLowerCase().replace(MC_RAW_STR, "")).also { reIndex() }
	}

	override fun addAll(index: Int, lores: Collection<String>): Boolean {
		if (lores.isEmpty()) return false
		var idx = index
		for (s in lores) add(idx++, s)
		return true
	}

	override fun addAll(lores: Collection<String>): Boolean {
		if (lores.isEmpty()) return false
		for (s in lores) add(s)
		return true
	}

	override fun clear() {
		original.clear()
		raw.clear()
	}

	override fun listIterator(): MutableListIterator<String> = raw.listIterator()

	override fun listIterator(index: Int): MutableListIterator<String> = raw.listIterator(index)

	/**
	 * Removes a single instance of the specified lore from this collection, if it is present.
	 * @param lores String
	 * @return true if the lore has been successfully removed; false if it was not present in the collection.
	 */
	override fun remove(lores: String): Boolean {
		if (lores.isEmpty() || isEmpty()) return false
		var index = indexOf(lores)
		if (index < 0) index = original.indexOf(lores)
		if (index < 0) return false
		original.removeAt(index)
		raw.removeAt(index)
		reIndex()
		return true
	}

	/**
	 * Removes all of this collection's lore that are also contained in the specified collection.
	 * @param lores Collection<String>
	 * @return true if any of the specified lore was removed from the collection, false if the collection was not modified.
	 */
	override fun removeAll(lores: Collection<String>): Boolean {
		if (this.lores.isEmpty() || isEmpty()) return false
		var success = false
		for (l in this.lores)
			success = success or remove(l)
		return success
	}

	/**
	 * Removes all of this collection's lore that are startwith prefix
	 * @param prefix String
	 * @return true if any of the specified lore was removed from the collection, false if the collection was not modified.
	 */
	fun removeAll(prefix: String): Boolean {
		var rem = false
		while (indexOf(prefix) >= 0)
			rem = rem or remove(prefix)
		return rem
	}

	/**
	 * Removes an lore at the specified [index] from the list.
	 *
	 * @return the lore that has been removed.
	 */
	override fun removeAt(index: Int): String {
		original.removeAt(index)
		return raw.removeAt(index).also { reIndex() }
	}

	/**
	 * Retains only the lores in this collection that are contained in the specified lores.
	 * @param lores Collection<String>
	 * @return true if any lore was removed from the lore-list, false if the collection was not modified.
	 */
	override fun retainAll(lores: Collection<String>): Boolean {
		var succ = false
		for (l in lores)
			if (contains(l))
				succ = succ or removeAll(l)
		return succ
	}

	/**
	 * Replace the lore at the specified position in this list with the specified lore.
	 * @param index Int
	 * @param lore String
	 * @return the lore previously at the specified position.
	 */
	override fun set(index: Int, lore: String): String {
		if (index !in indices) return ""
		original[index] = lore
		return raw.set(index, lore.replace(MC_RAW_STR, "")).also { reIndex() }
	}

	/**
	 * replace first lore that start with [prefix] with [lore]
	 * @param prefix String to search
	 * @param lore String to replace with
	 */
	operator fun set(prefix: String, lore: String) {
		replace(prefix, lore)
	}

	/**
	 * Creates a string from all the elements
	 * @return String
	 * @see MutableList.joinToString
	 */
	override fun toString(): String {
		return raw.joinToString()
	}

	/**
	 * @see MutableList.subList
	 */
	override fun subList(fromIndex: Int, toIndex: Int): LoreWrapper {
		val m = meta.clone()
		m.lore = lores.subList(fromIndex, toIndex)
		return LoreWrapper(m)
	}

	/**
	 * get updated lores
	 */
	val lores: List<String>
		get() = original

	/**
	 * get copy of current [ItemMeta] with updated lores
	 * @see ItemMeta.setLore
	 */
	val meta: ItemMeta
		get() {
			_meta.lore = original
			return _meta
		}

	/**
	 * [LoreWrapper.item] = [ItemStack] to set item lores
	 * @see ItemStack.getItemMeta
	 * @see ItemMeta.setLore
	 */
	@Suppress("SetterBackingFieldAssignment")
	var item: ItemStack = ItemStack(Material.AIR)
		set(value) {
			value.itemMeta = value.itemMeta.apply { lore = original }
		}
}
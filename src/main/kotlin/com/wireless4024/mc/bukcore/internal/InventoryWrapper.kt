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

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random

/**
 * Inventory wrapper for silent inventory open
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
@Suppress("DEPRECATION")
internal class InventoryWrapper(private val inv: Inventory, private val _title: String? = null) : Inventory {

	companion object {

		fun sort(inv: Inventory) {
			Arrays.sort(inv.contents) { left, right ->
				if (left.type == right.type)
					left.amount - right.amount
				else
					left.typeId - right.typeId
			}
		}

		fun shuffle(inv: Inventory) {
			// shuffle by upper bound
			Arrays.sort(inv.contents) { _, _ -> Random.nextInt(4) - 1 }
		}
	}

	override fun contains(materialId: Int): Boolean = inv.contains(materialId)

	override fun contains(material: Material?): Boolean = inv.contains(material)

	override fun contains(item: ItemStack?): Boolean = inv.contains(item)

	override fun contains(materialId: Int, amount: Int): Boolean = inv.contains(materialId, amount)

	override fun contains(material: Material?, amount: Int): Boolean = inv.contains(material, amount)

	override fun contains(item: ItemStack?, amount: Int): Boolean = inv.contains(item, amount)

	override fun clear(index: Int) = inv.clear(index)

	override fun clear() = inv.clear()

	override fun containsAtLeast(item: ItemStack?, amount: Int): Boolean = inv.containsAtLeast(item, amount)

	override fun getName(): String = _title ?: inv.name

	override fun firstEmpty(): Int = inv.firstEmpty()

	override fun getSize(): Int = inv.size

	override fun getItem(index: Int): ItemStack? = inv.getItem(index)

	override fun addItem(vararg items: ItemStack?): HashMap<Int, ItemStack> = inv.addItem(*items)

	override fun all(materialId: Int): HashMap<Int, out ItemStack> = inv.all(materialId)

	override fun all(material: Material?): HashMap<Int, out ItemStack> = inv.all(material)

	override fun all(item: ItemStack?): HashMap<Int, out ItemStack> = inv.all(item)

	override fun iterator(): MutableListIterator<ItemStack> = inv.iterator()

	override fun iterator(index: Int): MutableListIterator<ItemStack> = inv.iterator(index)

	override fun getContents(): Array<ItemStack> = inv.contents

	override fun setContents(items: Array<out ItemStack>?) {
		inv.contents = items
	}

	override fun getTitle(): String = _title ?: inv.title

	override fun first(materialId: Int): Int = inv.first(materialId)

	override fun first(material: Material?): Int = inv.first(material)

	override fun first(item: ItemStack?): Int = inv.first(item)

	override fun getViewers(): MutableList<HumanEntity> = inv.viewers

	override fun setItem(index: Int, item: ItemStack?) = inv.setItem(index, item)

	override fun removeItem(vararg items: ItemStack?): HashMap<Int, ItemStack> = inv.removeItem(*items)

	override fun getLocation(): Location = inv.location

	override fun getStorageContents(): Array<ItemStack> = inv.storageContents

	override fun remove(materialId: Int) = inv.remove(materialId)

	override fun remove(material: Material?) = inv.remove(material)

	override fun remove(item: ItemStack?) = inv.remove(item)

	override fun getType(): InventoryType = inv.type

	override fun setStorageContents(items: Array<out ItemStack>?) {
		inv.storageContents = items
	}

	override fun setMaxStackSize(size: Int) {
		inv.maxStackSize = size
	}

	override fun getMaxStackSize(): Int = inv.maxStackSize

	override fun getHolder(): InventoryHolder? = inv.holder
}
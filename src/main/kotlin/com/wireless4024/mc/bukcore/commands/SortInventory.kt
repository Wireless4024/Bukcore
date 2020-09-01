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

package com.wireless4024.mc.bukcore.commands

import com.wireless4024.mc.bukcore.api.CommandBase
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.bridge.NBTAPIBridge
import com.wireless4024.mc.bukcore.utils.BlockUtils
import com.wireless4024.mc.bukcore.utils.UniqueSortedArrayList
import de.tr7zw.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.Material.KNOWLEDGE_BOOK
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*
import kotlin.Comparator
import kotlin.math.min

class SortInventory(override val plugin: KotlinPlugin) : CommandBase {
	companion object {

		val COMPARATOR = Comparator<ItemStack?> { o1, o2 ->
			if (o1 === o2) return@Comparator 0
			if (o1 === null) return@Comparator 1
			if (o2 === null) return@Comparator -1

			@Suppress("DEPRECATION")
			val id = o1.typeId - o2.typeId
			if (id != 0) return@Comparator id

			@Suppress("DEPRECATION")
			val data = o1.data.data - o2.data.data
			if (data != 0) return@Comparator data

			if (!NBTAPIBridge.available) return@Comparator o2.amount - o1.amount

			val nbt = NBTItem(o1).toString().compareTo(NBTItem(o2).toString())
			if (nbt != 0) return@Comparator nbt

			return@Comparator o2.amount - o1.amount
		}

		val ITEMID_COMPARATOR = Comparator<ItemStack?> { o1, o2 ->
			if (o1 === o2) return@Comparator 0
			if (o1 === null) return@Comparator 1
			if (o2 === null) return@Comparator -1

			@Suppress("DEPRECATION")
			val id = o1.typeId - o2.typeId
			if (id != 0) return@Comparator id

			@Suppress("DEPRECATION")
			val data = o1.data.data - o2.data.data
			if (data != 0) return@Comparator data

			if (!NBTAPIBridge.available) return@Comparator 0

			return@Comparator NBTItem(o1).toString().compareTo(NBTItem(o2).toString())
		}

		private fun defaultSort(inv: Inventory) {
			inv.contents = inv.contents.apply {
				sortWith(COMPARATOR)
			}
		}

		// player inv sorting
		private fun defaultSort0(inv: Inventory) {
			inv.contents = inv.contents.apply {
				sortWith(COMPARATOR, 0, 9)
				sortWith(COMPARATOR, 9, 36)
			}
		}

		@ExperimentalStdlibApi
		private fun sort(inv: Inventory) {
			val map = TreeMap<ItemStack, Int>(ITEMID_COMPARATOR) // <id, amount>
			for (item: ItemStack? in inv.contents) {
				if (item == null) continue
				if (item in map)
					map[item] = map[item]!! + item.amount
				else
					map[item] = item.amount
			}
			inv.clear()
			map.forEach { it ->
				val item = it.key
				item.amount = it.value
				inv.addItem(item)
			}
		}

		val PADDING_ITEM = ItemStack(KNOWLEDGE_BOOK, KNOWLEDGE_BOOK.maxStackSize)

		// player inv sorting
		private fun sort0(inv: Inventory) {
			val contents = inv.contents
			val hotbar = UniqueSortedArrayList<ItemStack?>(ITEMID_COMPARATOR)
			for (i in 0..8)
				hotbar.add(contents[i])
			val map = TreeMap<ItemStack, Int>(ITEMID_COMPARATOR) // <id, amount>
			for (i in (0 until (min(36, contents.size)))) {
				val item = contents[i] ?: continue
				if (item in map)
					map[item] = map[item]!! + item.amount
				else
					map[item] = item.amount
			}
			/*for (item: ItemStack? in contents) {
				if (item == null) continue
				if (item in map)
					map[item] = map[item]!! + item.amount
				else
					map[item] = item.amount
			}*/
			for (i in 0..35)
				inv.clear(i)
			var succ = 0
			for (item in hotbar) {
				if (item != null) {
					inv.addItem(map.sub(item))
					++succ
				}
			}
			for (i in 0..(8 - succ))
				inv.addItem(PADDING_ITEM)
			map.forEach { it ->
				val item = it.key
				item.amount = it.value
				inv.addItem(item)
			}
			while (inv.contains(KNOWLEDGE_BOOK))
				inv.remove(KNOWLEDGE_BOOK)
		}

		private fun TreeMap<ItemStack, Int>.sub(item: ItemStack): ItemStack {
			val v = this.getOrDefault(item, 0)
			val stackSize = item.maxStackSize
			return if (v > stackSize) {
				this[item] = v - stackSize
				item.clone().apply { this.amount = stackSize }
			} else {
				this.remove(item)
				item.apply { this.amount = v }
			}
		}
	}

	@ExperimentalStdlibApi
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.sortinventory")) {
			var inv: Inventory? = null

			if (args.isNotEmpty()) {
				@Suppress("DEPRECATION")
				inv = sender.server.getPlayer(args.first())?.inventory
			}

			if (inv == null && sender is Player) {
				val ploc = sender.location

				if (args.size >= 1) ploc.x = args[0].let {
					if (it == "~") ploc.x else it.toDoubleOrNull() ?: ploc.x
				}
				if (args.size >= 2) ploc.y = args[1].let {
					if (it == "~") ploc.y else it.toDoubleOrNull() ?: ploc.y
				}
				if (args.size >= 3) ploc.z = args[2].let {
					if (it == "~") ploc.z else it.toDoubleOrNull() ?: ploc.z
				}
				if (args.size >= 4) ploc.world = Bukkit.getWorld(args[3]) ?: ploc.world

				val chest = BlockUtils.isChest(ploc)?.state as? Chest
				            ?: BlockUtils.findChest(sender, plugin["openchest.range"] as Int)?.state as? Chest
				inv = chest?.inventory
			}
			if (inv == null && sender is Player)
				inv = sender.inventory

			if (inv == null) {
				sender.sendMessage("${plugin["message.cant-find"]} ${plugin["message.chest"]}")
				return true
			}
			if (plugin["sortinv.mode"] == "merge" && NBTAPIBridge.available)
				if (inv is PlayerInventory)
					sort0(inv)
				else
					sort(inv)
			else {
				if (inv is PlayerInventory)
					defaultSort0(inv)
				else
					defaultSort(inv)
			}
			sender.sendMessage("inventory has been sorted")
		}
		return true
	}

}
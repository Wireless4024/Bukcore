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

import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.api.PlayerCommandBase
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import com.wireless4024.mc.bukcore.internal.InventoryWrapper
import com.wireless4024.mc.bukcore.utils.BlockUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.command.Command
import org.bukkit.entity.Player

/**
 * open looking chest with long range / different-dimension support
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class OpenChest(override val plugin: KotlinPlugin) : PlayerCommandBase {

	override fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.openchest")) {
			val ploc = sender.location

			if (args.size >= 1) ploc.x = args[0].let {
				if (it == "~") ploc.x else it.toIntOrNull()?.toDouble() ?: ploc.x
			}
			if (args.size >= 2) ploc.y = args[1].let {
				if (it == "~") ploc.y else it.toIntOrNull()?.toDouble() ?: ploc.y
			}
			if (args.size >= 3) ploc.z = args[2].let {
				if (it == "~") ploc.z else it.toIntOrNull()?.toDouble() ?: ploc.z
			}
			if (args.size >= 4) ploc.world = Bukkit.getWorld(args[3]) ?: ploc.world

			val block = BlockUtils.findChest(sender, ploc)
			            ?: BlockUtils.findChest(sender, plugin["openchest.range"] as Int)
			if (block == null) {
				sender.sendMessage("${plugin["message.cant-find"]} ${plugin["message.chest"]}")
				return true
			}
			plugin.info("${plugin["message.open-chest"]} ${block.location}")
			if (plugin["openchest.silent"] as Boolean)
				sender.openInventory(
						InventoryWrapper((block.state as Chest).inventory,
						                 "${plugin["message.chest"]} at %s %s %s".format(block.x, block.y, block.z)))
			else
				sender.openInventory((block.state as Chest).inventory)
			return true
		}
		return true
	}

	override fun onTabComplete(sender: Player,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return when (args.size) {
			1 -> mutableListOf(sender.getTargetBlock(null as Set<Material>?, 80).x.toString())
			2 -> mutableListOf(sender.getTargetBlock(null as Set<Material>?, 80).y.toString())
			3 -> mutableListOf(sender.getTargetBlock(null as Set<Material>?, 80).z.toString())
			4 -> mutableListOf(sender.world.name)
			else -> AlwaysEmptyMutableList.get()
		}
	}
}
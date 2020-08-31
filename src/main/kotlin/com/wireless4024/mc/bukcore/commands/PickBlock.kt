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

import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.api.PlayerCommandBase
import com.wireless4024.mc.bukcore.bridge.NBTAPIBridge
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import com.wireless4024.mc.bukcore.utils.BlockUtils
import de.tr7zw.nbtapi.NBTContainer
import de.tr7zw.nbtapi.NBTItem
import de.tr7zw.nbtapi.NBTReflectionUtil
import de.tr7zw.nbtapi.NBTTileEntity
import de.tr7zw.nbtapi.utils.nmsmappings.ReflectionMethod.COMPOUND_REMOVE_KEY
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.plugin.Plugin

/**
 * pickup block and copy NBT (if PowerNBT available)
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class PickBlock(override val plugin: KotlinPlugin) : PlayerCommandBase {

	override fun onCommand(player: Player, command: Command, label: String, args: Array<String>): Boolean {
		if (player.hasPermission("bukcore.pickblock")) {
			val block = BlockUtils.traceBlock(player, 50)
			if (block == null) {
				player.sendMessage("${ChatColor.RED}${plugin["message.cant-find"]} ${plugin["message.block"]}")
				return true
			}
			var item = block.state.data.toItemStack(1)
			val plugin: Plugin? = NBTAPIBridge.plugin

			if (item.type == SIGN_POST || item.type == WALL_SIGN) {
				item= ItemStack(SIGN)
			}

			val blockState = block.state
			val blockHasNBT = blockState.javaClass.simpleName != "CraftBlockState"
			if (plugin != null && blockState != null && blockHasNBT) {
				val nbt = NBTTileEntity(blockState).compound
				COMPOUND_REMOVE_KEY.run(nbt, "x")
				COMPOUND_REMOVE_KEY.run(nbt, "y")
				COMPOUND_REMOVE_KEY.run(nbt, "z")
				val newNbt = NBTItem(item)
				NBTReflectionUtil.set(newNbt, "BlockEntityTag", nbt)
				newNbt.applyNBT(item)
				item.itemMeta = item.itemMeta.apply {
					lore = mutableListOf("picked")
				}
				Bukcore.getInstance()(1) {
					NBTReflectionUtil.setTileEntityNBTTagCompound(blockState, NBTContainer().compound)
					block.setType(AIR, false)
				}
			} else if (!blockHasNBT)
				block.setType(AIR, false)
			val inv = player.inventory
			inv.itemInMainHand = item
		}
		return true
	}

	override fun onTabComplete(sender: CommandSender,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return AlwaysEmptyMutableList.get()
	}
}
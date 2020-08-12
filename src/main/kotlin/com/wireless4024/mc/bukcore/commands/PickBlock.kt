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
import com.wireless4024.mc.bukcore.api.CommandBase
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.bridge.PowerNBTBridge
import com.wireless4024.mc.bukcore.utils.BlockUtils
import me.dpohvar.powernbt.PowerNBT
import me.dpohvar.powernbt.api.NBTCompound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * pickup block and copy NBT (if PowerNBT available)
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class PickBlock(override val plugin: KotlinPlugin) : CommandBase {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender is Player && sender.hasPermission("bukcore.pickblock")) {
			val block = BlockUtils.traceBlock(sender, 50)
			if (block == null) {
				sender.sendMessage("${ChatColor.RED}${plugin["message.cant-find"]} ${plugin["message.block"]}")
				return true
			}
			val item = block.state.data.toItemStack(1)
			val plugin: Plugin? = PowerNBTBridge.plugin

			if (item.type == SIGN_POST || item.type == WALL_SIGN)
				item.type = SIGN

			if (plugin != null) {
				val nbt = PowerNBT.getApi().read(block)
				nbt.remove("x")
				nbt.remove("y")
				nbt.remove("z")
				if (item.type != SIGN) // this bug :C
					nbt.remove("id")
				val newNbt = NBTCompound()
				newNbt["BlockEntityTag"] = nbt
				PowerNBT.getApi().write(item, newNbt)
				item.itemMeta = item.itemMeta.apply {
					lore = mutableListOf("picked")
				}
				Bukcore.getInstance()(1) {
					PowerNBT.getApi().write(block, NBTCompound())
					block.setType(Material.AIR, false)
				}
			}
			val inv = sender.inventory
			inv.itemInMainHand = item
		}
		if (sender !is Player)
			sender.sendMessage("${plugin["message.need-player"]}${plugin["message.command"]}")

		return true
	}

	override fun onTabComplete(sender: CommandSender,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return mutableListOf()
	}
}
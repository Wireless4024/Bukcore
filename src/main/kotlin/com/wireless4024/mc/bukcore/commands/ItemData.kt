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

import com.google.gson.Gson
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.api.PlayerCommandBase
import com.wireless4024.mc.bukcore.bridge.PowerNBTBridge
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import me.dpohvar.powernbt.PowerNBT
import me.dpohvar.powernbt.api.NBTCompound
import org.bukkit.command.Command
import org.bukkit.entity.Player

/**
 * get and replace item NBT
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class ItemData(override val plugin: KotlinPlugin) : PlayerCommandBase {

	override fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.itemdata")) {
			if (!PowerNBTBridge.available) {
				sender.sendMessage("${PowerNBTBridge.name} ${plugin["message.unavailable"]}")
				return true
			}
			val item = sender.inventory.itemInMainHand
			if (item == null) {
				sender.sendMessage(plugin["message.need-holding-item"] as String)
				return true
			}
			if (args.isNotEmpty()) {
				@Suppress("UNCHECKED_CAST")
				val data = Gson().fromJson(args.joinToString(separator = " ", prefix = "", postfix = ""),
				                           Map::class.java) as Map<out String, *>?
				if (data != null) {
					val nbt = PowerNBT.getApi().read(item) ?: NBTCompound()
					nbt.putAll(data)
					PowerNBT.getApi().write(item, nbt)
				} else {
					sender.sendMessage(plugin["message.json-parse-fail"] as String)
				}
			}
			sender.sendMessage(PowerNBT.getApi().read(item)?.toString() ?: "{}")
		}
		return true
	}

	override fun onTabComplete(sender: Player,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return if (args.isEmpty()) AlwaysEmptyMutableList.get() else mutableListOf(args.last())
	}
}
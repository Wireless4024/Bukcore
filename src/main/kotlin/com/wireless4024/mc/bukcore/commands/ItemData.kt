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
import com.wireless4024.mc.bukcore.bridge.NBTAPIBridge
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import com.wireless4024.mc.bukcore.utils.ReflectionUtils
import com.wireless4024.mc.bukcore.utils.sendMessage
import de.tr7zw.nbtapi.NBTContainer
import de.tr7zw.nbtapi.NBTItem
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

	override fun onCommand(player: Player, command: Command, label: String, args: Array<String>): Boolean {
		if (player.hasPermission("bukcore.itemdata")) {
			if (!NBTAPIBridge.available) {
				player.sendMessage("${NBTAPIBridge.name} ${plugin["message.unavailable"]}")
				return true
			}
			val item = player.inventory.itemInMainHand
			@Suppress("DEPRECATION")
			if (item == null) {
				player.sendMessage(plugin["message.need-holding-item"] as String)
				return true
			}
			try {
				if (args.isNotEmpty()) {
					val arg = if (args.isEmpty()) "{}" else args.joinToString(separator = " ",
					                                                          prefix = "",
					                                                          postfix = "")
					val nbt = NBTItem(item)
					if (arg == "null") {
						ReflectionUtils.getPrivateMethod1(nbt, "setCompound", Object::class.java)?.run {
							isAccessible = true
							invoke(nbt, NBTContainer().compound)
							isAccessible = false
						}
					} else if (arg != "{}") {
						nbt.mergeCompound(NBTContainer(arg))
					}
					nbt.applyNBT(item)
				}
				player.sendMessage(NBTItem(item))
			} catch (t: Throwable) {
				player.sendMessage(plugin["message.need-holding-item"] as String)
			}
		}
		return true
	}

	override fun onTabComplete(player: Player,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return if (args.isEmpty()) AlwaysEmptyMutableList.get() else mutableListOf(args.last())
	}
}
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
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * send message to all online player with rainbow border
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class RainbowChat(override val plugin: KotlinPlugin) : CommandBase {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.rainbowchat") && args.isNotEmpty()) {
			plugin.runAsync {
				val prefix = "§4*§r "
				val messages = mutableListOf<String>()
				for (arg in args.joinToString(separator = " ", prefix = "", postfix = "")
						.replace('&', '§')
						.split("\\n"))
					for (s in arg.chunked(40))
						messages.add(prefix + (if (s.isAlpha()) "§l" else "") + s.trim())
				val msg = arrayOf(
						"§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*"
				) + messages + arrayOf(
						"§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*§9*§d*§4*§c*§6*§e*§2*§a*§b*§3*§1*"
				)
				for (player in sender.server.onlinePlayers) {
					player.sendMessage(msg)
				}
				sender.sendMessage("${plugin["message.message-sent"]} ${sender.server.onlinePlayers.size} ${plugin["message.players"]}")
			}
		}
		return true
	}

	fun String.isAlpha(): Boolean {
		for (c in this)
			if (c !in 'a'..'z' && c !in 'A'..'Z' && c !in '0'..'9' && c != ' ' && c != '§')
				return false
		return true
	}

	override fun onTabComplete(sender: CommandSender,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		if (args.isEmpty()) return mutableListOf()
		return if (args.size == 1) super.getPlayers(sender, args.first()) else mutableListOf(args.last())
	}
}
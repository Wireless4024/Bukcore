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

package com.wireless4024.mc.bukcore.api

import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @since 0.2
 */
interface PlayerCommandBase : CommandBase {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender is Player) return onCommand(sender, command, label, args)
		sender.sendMessage(ChatColor.RED.toString() + (plugin["message.need-player"] as String? ?: "You must be a player to use") + " $label")
		return true
	}

	/**
	 * this will fire when player use command
	 */
	fun onCommand(player: Player, command: Command, label: String, args: Array<String>): Boolean

	override fun onTabComplete(sender: CommandSender,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return if (sender is Player) onTabComplete(sender, command, alias, args) else AlwaysEmptyMutableList.get()
	}

	/**
	 * this will fire when player use tab
	 */
	fun onTabComplete(player: Player,
	                  command: Command,
	                  alias: String,
	                  args: Array<String>): MutableList<String> {
		return super.onTabComplete(player, command, alias, args)
	}
}
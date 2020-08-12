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

import com.wireless4024.mc.bukcore.Bukcore
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * an interface combined with [CommandExecutor] and [TabCompleter]
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
interface CommandBase : CommandExecutor, TabCompleter {

	companion object {

		internal fun getOnlinePlayers(server: Server?, name: String = ""): MutableList<String> {
			return server?.onlinePlayers?.map(Player::getName)?.filter { it.startsWith(name, true) }?.toMutableList()
			       ?: mutableListOf()
		}
	}

	/**
	 * plugin owner
	 */
	val plugin: KotlinPlugin

	/**
	 * @see CommandExecutor.onCommand
	 */
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean

	/**
	 * return players's name when use tab by default
	 * @see TabCompleter.onTabComplete
	 */
	override fun onTabComplete(sender: CommandSender,
	                           command: Command,
	                           alias: String,
	                           args: Array<String>): MutableList<String> {
		return getOnlinePlayers(sender.server, args.lastOrNull()?.toLowerCase() ?: "")
	}

	fun getPlayers(sender: CommandSender, name: String? = null): MutableList<String> {
		return getOnlinePlayers(sender.server, name?.toLowerCase() ?: "")
	}

	/**
	 * register this command to plugin
	 *
	 * @see org.bukkit.command.PluginCommand.setExecutor
	 * @see org.bukkit.command.PluginCommand.setTabCompleter
	 * @param plugin the reference to the plugin
	 * @param name command name or null to use class name
	 */
	fun register(plugin: JavaPlugin? = null, name: String? = null) {
		val p = (plugin ?: this.plugin)
		val n = name ?: this::class.simpleName?.toLowerCase()
		if (p is Bukcore)
			p.info("registering command $n")
		val cm = p.getCommand(n)
		cm.executor = this
		cm.tabCompleter = this
	}
}
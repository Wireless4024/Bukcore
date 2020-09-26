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
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import com.wireless4024.mc.bukcore.utils.i18n.translator
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * suggests that the Java Virtual Machine expend effort toward recycling unused objects
 * in order to make the memory they currently occupy available for quick reuse
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 * @see System.gc
 * @see System.runFinalization
 */
class Gc(override val plugin: KotlinPlugin) : CommandBase {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.gc")) {
			plugin.runAsync {
				val time = System.currentTimeMillis()
				val before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
				System.gc()
				val after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
				val dur = System.currentTimeMillis() - time
				sender.translator {
					sender.sendMessage((-("{freed-memory} ${ChatColor.BLUE}%.3fMib${ChatColor.RESET} {from} ${ChatColor.RED}%.3fMib" +
							"${ChatColor.RESET} {to} ${ChatColor.GREEN}%.3fMib${ChatColor.RESET} {in} ${ChatColor.GREEN}%sms"))
							.format((before - after) / 1048576.0,
									before / 1048576.0, after / 1048576.0, dur))
				}
			}

			return true
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
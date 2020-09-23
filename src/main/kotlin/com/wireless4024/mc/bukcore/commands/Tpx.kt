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
import com.wireless4024.mc.bukcore.utils.i18n.translator
import com.wireless4024.mc.bukcore.utils.player
import com.wireless4024.mc.bukcore.utils.server
import com.wireless4024.mc.bukcore.utils.world
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Tpx(override val plugin: KotlinPlugin) : CommandBase {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (args.isEmpty()) return false
		server {
			sender.translator {
				var player: Player?
				val target = Location(null, 0.0, 0.0, 0.0)
				var pos = 1
				if (sender is Player) {
					player = player(args.first())
					val world: World?
					if (player == null) {
						player = sender
						world = world(args.elementAt(0))
					} else {
						world = world(args.elementAt(1))
						pos = 2
					}
					target.world = world ?: kotlin.run {
						+"{cant-find} {world} ${args.elementAt(pos - 1)}"
						return true
					}
				} else {
					player = player(args.first())
				}
				if (player == null) {
					+"{cant-find} {player} ${args.first()}"
					return true
				}

				val ploc = player.location
				target.x = ploc.x
				target.y = ploc.y
				target.z = ploc.z
				target.yaw = ploc.yaw
				target.pitch = ploc.pitch
				while (pos < args.size) {
					--pos
					target.x = args.elementAtOrNull(++pos)?.run { if (this == "~") target.x else toDoubleOrNull() }
							?: break
					target.y = args.elementAtOrNull(++pos)?.run {
						if (this == "~") target.world.getHighestBlockYAt(target).toDouble() else toDoubleOrNull()
					} ?: break
					target.z = args.elementAtOrNull(++pos)?.run { if (this == "~") target.z else toDoubleOrNull() }
							?: break
					target.pitch = args.elementAtOrNull(++pos)?.run { if (this == "~") target.pitch else toFloatOrNull() }
							?: break
					target.yaw = args.elementAtOrNull(++pos)?.run { if (this == "~") target.yaw else toFloatOrNull() }
							?: break
					break
				}
				player.teleport(target)
			}
		}
		return true
	}

	private val tilde = mutableListOf("~")

	override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String>? {
		return when (args.size) {
			0 -> server { player().mapTo(ArrayList()) { it.name } }
			1 -> {
				server {
					player().mapNotNullTo(ArrayList()) { val name = it.name;if (name.startsWith(args.first(), true)) name else null }
				}
			}
			2 -> {
				server {
					world().mapNotNullTo(ArrayList()) { val name = it.name;if (name.startsWith(args.elementAt(1), true)) name else null }
				}
			}
			3 -> if (sender is Player) mutableListOf(sender.location.blockX.toString()) else tilde
			4 -> if (sender is Player) mutableListOf(sender.location.blockY.toString()) else tilde
			5 -> if (sender is Player) mutableListOf(sender.location.blockZ.toString()) else tilde
			6 -> if (sender is Player) mutableListOf(sender.location.pitch.toString()) else tilde
			7 -> if (sender is Player) mutableListOf(sender.location.yaw.toString()) else tilde
			else -> null
		}
	}
}
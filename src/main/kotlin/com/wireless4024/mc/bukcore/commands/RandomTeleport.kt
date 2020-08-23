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
import com.wireless4024.mc.bukcore.utils.Cooldown
import com.wireless4024.mc.bukcore.utils.blocks.Region3D
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min
import kotlin.random.Random

/**
 * random teleport in the world
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class RandomTeleport(override val plugin: KotlinPlugin) : CommandBase {

	companion object {

		fun random(center: Location, maxRadius: Int, minRadius: Int = 0): Location {
			val rad = maxRadius - minRadius
			val rnd = rad shl 1
			var x = Random.nextInt(rnd + 1) - rad
			if (x < 0) x -= minRadius else x += minRadius
			var z = Random.nextInt(rnd + 1) - rad
			if (z < 0) z -= minRadius else z += minRadius
			return Location(center.world, center.x + x, 0.0, center.z + z, center.yaw, center.pitch)
		}

		private fun isEnable(plugin: JavaPlugin, world: String?): Boolean {
			val k = "rtp.per-world.${world}.enable"
			return if (world != null && plugin.config.isBoolean(k))
				plugin.config.getBoolean(k)
			else
				plugin.config.getBoolean("rtp.enable")
		}

		private fun getInt(plugin: JavaPlugin, world: String?, key: String, default: Int = 1): Int {
			val k = "rtp.per-world.${world}.$key"
			return if (world != null && plugin.config.isInt(k))
				plugin.config.getInt(k, default)
			else
				plugin.config.getInt("rtp.$key", default)
		}
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (sender.hasPermission("bukcore.rtp")) {
			@Suppress("DEPRECATION")
			val player = if (sender !is Player) sender.server.getPlayer(args.firstOrNull() ?: return false) else sender

			@Suppress("NAME_SHADOWING")
			val args = if (sender !is Player) args.copyOfRange(1, args.size) else args

			plugin.runAsync {
				val ploc = player.location
				if (!isEnable(plugin, ploc.world.name)) {
					sender.sendMessage("this world disabled random teleport")
					return@runAsync
				}
				if (!Cooldown[sender.name].availableOrWarn("rtp", getInt(plugin, ploc.world.name, "cooldown"), sender))
					return@runAsync
				val rmax = getInt(plugin, ploc.world.name, "max-radius")
				val radius = if (sender.hasPermission("bukcore.rtp.custom")) args.firstOrNull()?.toIntOrNull()
				                                                             ?: rmax else rmax
				val baseLoc = if (getInt(plugin, ploc.world.name, "max-radius", Int.MIN_VALUE) == Int.MIN_VALUE
				                  || getInt(plugin, ploc.world.name, "min-radius", Int.MIN_VALUE) == Int.MIN_VALUE) ploc
				else Location(ploc.world,
				              getInt(plugin, ploc.world.name, "anchorX").toDouble(),
				              0.0,
				              getInt(plugin, ploc.world.name, "anchorZ").toDouble(),
				              player.location.yaw,
				              player.location.pitch)
				val delay = getInt(plugin, ploc.world.name, "delay", 3)
				for (d in 0L until delay) {
					plugin.runTask(d * 20) {
						sender.sendMessage("${ChatColor.BLUE}${plugin["message.youll-teleport"]} ${plugin["message.in"]} ${delay - d}s")
						ploc.world.spawnParticle(Particle.PORTAL, ploc, (30 + (d * 30)).toInt())
					}
				}
				val loc = random(baseLoc, radius, getInt(plugin, ploc.world.name, "min-radius"))

				// pre load/generate chunks around target location
				Region3D.around(loc, min(16, getInt(plugin, ploc.world.name, "pre-load-chunks-area", delay)))
						.lazyLoadChunk(9, 4) // load 9 chunks every 4 tick

				plugin.info("%s use rtp to %s".format(sender.name, loc))


				plugin.runTask(delay * 20L) {
					if (player.location.block.location != ploc.block.location) {
						sender.sendMessage("${ChatColor.RED}rtp has been cancel")
						return@runTask
					}
					val floc = loc.world.getHighestBlockAt(loc).location
					player.teleport(floc)
					sender.sendMessage("${ChatColor.GREEN}woosh?")
					loc.world.spawnParticle(Particle.PORTAL, floc, 80)
				}

			}
		}
		if (sender !is Player)
			sender.sendMessage("${plugin["message.need-player"]}${plugin["message.command"]}")
		return true
	}
}
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

@file:Suppress("NOTHING_TO_INLINE" /* this will ensure `fun()` is same as `fun{}` */, "DEPRECATION", "HasPlatformType", "unused", "SpellCheckingInspection")

package com.wireless4024.mc.bukcore.utils

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.collections.ArrayList

typealias Worlds = List<World>
typealias Players = Collection<Player>
typealias OffPlayer = OfflinePlayer

inline fun <R> Server.original(block: Server.() -> R) = block(this)
inline fun Server.world() = this.worlds
inline fun Server.world(block: World.() -> Unit) = this.worlds.forEach(block)
inline fun <R> Server.world0(block: World.() -> R) = this.worlds.run { ArrayList<R>(size).also { for (e in this) it.add(block(e)) } }
inline fun <R> Server.world1(block: World.() -> R?) = this.worlds.run { ArrayList<R?>(size).also { for (e in this) it.add(block(e)) } }
inline fun Server.world(name: String): World? = this.getWorld(name)
inline fun <R> Server.world(name: String, block: World.() -> R): R? = this.getWorld(name)?.run(block)
inline fun Server.world(uuid: UUID): World? = this.getWorld(uuid)
inline fun <R> Server.world(uuid: UUID, block: World.() -> R): R? = this.getWorld(uuid)?.run(block)
inline fun Server.player() = this.onlinePlayers
inline fun Server.player(block: Player.() -> Unit) = this.onlinePlayers.forEach(block)
inline fun <R> Server.player0(block: Player.() -> R) = this.onlinePlayers.run { ArrayList<R>(size).also { for (e in this) it.add(block(e)) } }
inline fun <R> Server.player1(block: Player.() -> R?) = this.onlinePlayers.run { ArrayList<R?>(size).also { for (e in this) it.add(block(e)) } }
inline fun Server.player(name: String): Player? = this.getPlayerExact(name)
inline fun <R> Server.player(name: String, block: Player.() -> R): R? = this.getPlayerExact(name)?.run(block)
inline fun Server.player(uuid: UUID): Player? = this.getPlayer(uuid)
inline fun <R> Server.player(uuid: UUID, block: Player.() -> R): R? = this.getPlayer(uuid)?.run(block)
inline fun Server.plugin() = this.pluginManager.plugins
inline fun Server.plugin(block: Plugin.() -> Unit) = plugin().forEach(block)
inline fun <R> Server.plugin0(block: Plugin.() -> R) = plugin().run { ArrayList<R>(size).also { for (e in this) it.add(block(e)) } }
inline fun <R> Server.plugin1(block: Plugin.() -> R?) = plugin().run { ArrayList<R?>(size).also { for (e in this) it.add(block(e)) } }
inline fun Server.plugin(name: String): Plugin? = this.pluginManager.getPlugin(name)
inline fun <R> Server.plugin(name: String, block: Plugin.() -> R): R? = this.plugin(name)?.run(block)
inline fun Server.offPlayer() = this.offlinePlayers
inline fun Server.offPlayer(block: OffPlayer.() -> Unit) = this.offlinePlayers.forEach(block)
inline fun <R> Server.offPlayer0(block: OffPlayer.() -> R) = this.offlinePlayers.run { ArrayList<R>(size).also { for (e in this) it.add(block(e)) } }
inline fun <R> Server.offPlayer1(block: OffPlayer.() -> R?) = this.offlinePlayers.run { ArrayList<R?>(size).also { for (e in this) it.add(block(e)) } }
inline fun <R> Server.offPlayer(name: String, block: OffPlayer.() -> R): R? = this.getOfflinePlayer(name)?.run(block)
inline fun <R> Server.offPlayer(uuid: UUID, block: OffPlayer.() -> R): R? = this.getOfflinePlayer(uuid)?.run(block)
inline fun Server.inventory(owner: InventoryHolder? = null, type: InventoryType = InventoryType.CHEST, name: String = "Chest") = this.createInventory(owner, type, name)
inline fun Server.inventory(owner: InventoryHolder? = null, size: Int = 27, name: String = "Chest", block: Inventory.() -> Unit) = this.createInventory(owner, size, name).apply(block)
inline fun Server.inventory(owner: InventoryHolder? = null, type: InventoryType = InventoryType.CHEST, name: String = "Chest", block: Inventory.() -> Unit) = this.createInventory(owner, type, name).apply(block)
inline fun Server.inventory(owner: InventoryHolder? = null, size: Int = 27, name: String = "Chest") = this.createInventory(owner, size, name)
inline fun <R> Server.inventory(owner: InventoryHolder? = null, type: InventoryType = InventoryType.CHEST, name: String = "Chest", block: Inventory.() -> R) = this.createInventory(owner, type, name).run(block)
inline fun <R> Server.inventory(owner: InventoryHolder? = null, size: Int = 27, name: String = "Chest", block: Inventory.() -> R) = this.createInventory(owner, size, name).run(block)
inline fun Server.bossbar(title: String, color: BarColor = BarColor.PURPLE, style: BarStyle = BarStyle.SOLID, flag: Array<BarFlag> = emptyArray()) = this.createBossBar(title, color, style, *flag)
inline fun Server.bossbar(title: String, color: BarColor = BarColor.PURPLE, style: BarStyle = BarStyle.SOLID, flag: Array<BarFlag> = emptyArray(), block: BossBar.() -> Unit) = this.createBossBar(title, color, style, *flag).apply(block)
inline fun <R> Server.bossbar(title: String, color: BarColor = BarColor.PURPLE, style: BarStyle = BarStyle.SOLID, flag: Array<BarFlag> = emptyArray(), block: BossBar.() -> R) = this.createBossBar(title, color, style, *flag).run(block)
fun Server.entity() = this.worlds.map { it.entities }.reduce { acc, mutableList -> acc + mutableList }
inline fun Server.entity(block: Entity.() -> Unit) = entity().forEach(block)
inline fun <R> Server.entity0(block: Entity.() -> R) = entity().run { ArrayList<R>(size).also { for (e in this) it.add(block(e)) } }
inline var Server.allowList: Boolean
	/**r
	 * @return true if this turn on whitelist
	 * @see [this.hasWhitelist]
	 */
	get() = this.hasWhitelist()
	/**
	 * Sets if the this is whitelisted.
	 * @see [this.setWhitelist]
	 */
	set(value) = this.setWhitelist(value)

inline fun Server.broadcast(message: Any?) = this.broadcastMessage(message.toString())
inline fun Server.command(name: String) = this.getPluginCommand(name)
inline fun Server.register(listner: Listener, plugin: JavaPlugin) = this.pluginManager.registerEvents(listner, plugin)

inline val World.border get() = this.worldBorder
inline val World.type get() = this.worldType
inline val World.folder get() = this.worldFolder
inline var World.strom get() = this.hasStorm(); set(value) = this.setStorm(value)
inline var World.thunder
	get() = this.isThundering;
	set(value) {
		this.isThundering = value
	}

inline fun <T : Entity> Collection<T>.each(block: T.() -> Unit) = this.forEach(block)
inline fun <T : Entity, R> Collection<T>.each(block: T.() -> R): ArrayList<R> {
	val res = ArrayList<R>(this.size)
	for (e in this) res.add(block(e))
	return res
}

inline fun Player.heal() {
	this.health = this.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
	this.foodLevel = 20
	this.saturation = 5f
	this.exhaustion = 0f
	// above value taken from https://minecraft.gamepedia.com/Hunger#Mechanics

	this.removePotionEffect(PotionEffectType.BLINDNESS)
	this.removePotionEffect(PotionEffectType.CONFUSION)
	this.removePotionEffect(PotionEffectType.GLOWING)
	this.removePotionEffect(PotionEffectType.HUNGER)
	this.removePotionEffect(PotionEffectType.LEVITATION)
	this.removePotionEffect(PotionEffectType.POISON)
	this.removePotionEffect(PotionEffectType.UNLUCK)
	this.removePotionEffect(PotionEffectType.SLOW)
	this.removePotionEffect(PotionEffectType.SLOW_DIGGING)
	this.removePotionEffect(PotionEffectType.WEAKNESS)
	this.removePotionEffect(PotionEffectType.WITHER)
}

inline val Player.maxHealth0 get() = this.getAttribute(Attribute.GENERIC_MAX_HEALTH).value
inline fun Player.kill() {
	this.health = 0.0
}
fun Server.selector(selector:String){

}

inline fun <R> server(block: Server.() -> R) = block(Bukkit.getServer())
inline operator fun <R> World.invoke(block: World.() -> R) = block(this)
inline fun <R> world(world: World, block: World.() -> R) = block(world)
inline operator fun <R> Player.invoke(block: Player.() -> R) = block(this)
inline fun <R> player(player: Player, block: Player.() -> R) = block(player)
inline operator fun <T : Entity, R> T.invoke(block: T.() -> R) = block(this)
inline fun <R> entity(entity: Entity, block: Entity.() -> R) = block(entity)
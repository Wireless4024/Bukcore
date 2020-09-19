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


package com.wireless4024.mc.bukcore.utils

import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.utils.i18n.translator
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Cooldown manager
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 * @property cd HashMap<Any, Long>
 */
public class Cooldown private constructor() {

	private val cd = HashMap<Any, Long>()

	/**
	 * set cooldown for a key
	 * @param key   a reference to cooldown
	 * @param duration duration in seconds from now
	 */
	operator fun set(key: Any, duration: Long) {
		cd[key] = System.currentTimeMillis() + duration * 1000
	}

	/**
	 * set cooldown for a key
	 * @param key   a reference to cooldown
	 * @param duration duration in seconds from now
	 */
	operator fun set(key: Any, duration: Int) {
		cd[key] = System.currentTimeMillis() + duration * 1000
	}

	/**
	 * get cooldown remaining for the key in seconds
	 * @param key   a reference to cooldown
	 * @return Long duration in seconds from now
	 */
	operator fun get(key: Any): Long {
		val cd = (cd.getOrDefault(key, 0L) - System.currentTimeMillis()) / 1000
		return if (cd < 1) 0 else cd
	}

	/**
	 * reset all cooldown
	 */
	fun reset() {
		cd.clear()
	}

	/**
	 * check if cooldown available
	 * @param key  a reference to cooldown
	 * @return true if available
	 */
	fun available(key: Any): Boolean {
		return get(key) == 0L
	}

	/**
	 * check if cooldown available and set if available
	 * @param key   a reference to cooldown
	 * @param duration Long duration to set if available
	 * @return true if available
	 */
	fun availableOrSet(key: Any, duration: Long): Boolean {
		if (available(key)) {
			set(key, duration)
			return true
		}
		return false
	}

	/**
	 *
	 * @param key  a reference to cooldown
	 * @param duration Long
	 * @param player CommandSender
	 * @return true if available
	 */
	fun availableOrWarn(key: Any, duration: Long, player: CommandSender): Boolean {
		if (player.isOp) return true
		if (available(key)) {
			set(key, duration)
			return true
		}
		player.sendMessage(ChatColor.RED.toString() + "Cooldown remaining ${get(key)}s")
		return false
	}

	/**
	 *
	 * @param key  a reference to cooldown
	 * @param duration Int
	 * @param player CommandSender
	 * @return true if available
	 */
	fun availableOrWarn(key: Any, duration: Int, player: CommandSender): Boolean {
		if (player.isOp) return true
		if (available(key)) {
			set(key, duration)
			return true
		}
		player.translator {
			+"${ChatColor.RED} {cooldown} {remaining} ${get(key)} {sec}"
		}
		return false
	}

	companion object {

		/**
		 * get cooldown instance for a key
		 * @param key   a reference to cooldown instance (can be [org.bukkit.entity.Player.name] or [org.bukkit.entity.Player.getUniqueId] )
		 * @return Cooldown instance
		 */
		operator fun get(key: Any): Cooldown {
			if (cooldownMap.containsKey(key)) return cooldownMap[key]!!
			val c = Cooldown()
			synchronized(cooldownMap) {
				cooldownMap[key] = c
			}
			return c
		}

		fun resetAll() {
			Bukcore.getInstance().info("all cooldown has been reset!")
			cooldownMap.clear()
		}

		private val cooldownMap = HashMap<Any, Cooldown>()
	}
}
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

// default it's will be BukcoreKt.class
@file:JvmName("Bukcore")

package com.wireless4024.mc.bukcore

import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.commands.*
import com.wireless4024.mc.bukcore.internal.Players
import com.wireless4024.mc.bukcore.utils.Cooldown
import java.io.File

/**
 * Main class for bukcore
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
class Bukcore : KotlinPlugin() {

	/**
	 * reload config
	 */
	fun reload(dry: Boolean = false) {
		if (!dry)
			reloadConfig()
	}

	fun getFile(path: String) = File("plugins/Bukcore/$path")

	override fun onEnable() {
		INSTANCE = this
		Chat(this).register()
		Test(this).register()
		Gc(this).register()
		OpenChest(this).register()
		RainbowChat(this).register()
		PickBlock(this).register()
		ItemData(this).register()
		BukcoreC(this).register(name = "bukcore")
		LoadChunk(this).register()


		if (get("rtp.enable") as Boolean)
			RandomTeleport(this).register()
		saveDefaultConfig()

		if (config.getString("version") < VERSION) {
			logger.info("updating config")
			config["version"] = VERSION

			config.options().copyHeader(true).copyDefaults(true)
			saveConfig()
			logger.info("update config done")
		}
	}

	override fun onDisable() {
		Cooldown.resetAll()
		Players.players.clear()
	}

	companion object {

		const val VERSION = "0.2"

		@JvmSynthetic
		internal var INSTANCE: Bukcore? = null

		@JvmStatic
		fun getInstance() = INSTANCE!!

		fun log(message: Any?) {
			INSTANCE?.info(message)
		}
	}
}

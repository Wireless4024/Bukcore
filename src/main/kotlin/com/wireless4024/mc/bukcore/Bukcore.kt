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

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.commands.*
import com.wireless4024.mc.bukcore.internal.Players
import com.wireless4024.mc.bukcore.utils.Cooldown
import com.wireless4024.mc.bukcore.utils.i18n.Translator
import com.wireless4024.mc.bukcore.utils.plugin
import com.wireless4024.mc.bukcore.utils.server
import java.net.URI
import java.nio.file.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.stream.Stream


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
	fun reload() {
		reloadConfig()
		init()
		_language = config.getString("fallback_lang", "en")
		runAsync {
			// working with filesystem seem very slow so this should not block main thread
			Translator.loadDirectory(getFile("lang"), true)
		}
	}


	private fun init() {
		enableAllCommand()
		for (s in config.getStringList("command.disabled"))
			disableCommand(s)
	}

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
		SortInventory(this).register()
		RandomTeleport(this).register()
		SetLanguage(this).register()
		Tpx(this).register()

		runAsync {
			var fs: FileSystem? = null
			val uri: URI = Bukcore::class.java.classLoader.getResource("bukcore/lang")?.toURI() ?: return@runAsync
			val myPath: Path = if (uri.scheme == "jar") {
				fs = FileSystems.newFileSystem(uri, mutableMapOf<String, Any>())
				fs.getPath("bukcore/lang")
			} else {
				Paths.get(uri)
			}
			val walk: Stream<Path> = Files.walk(myPath, 1)
			val it: Iterator<Path> = walk.iterator()
			while (it.hasNext()) {
				val path = it.next()
				val fname = path.fileName.toString()
				if (!fname.endsWith(".yml", true)) continue
				val lang = Translator.detectFileLanguage(fname)
				if (lang == null) {
					Companion.log("can't detect language for file $path; skipping")
					continue
				} else
					Companion.log("detected language $lang for file $path")

				val stream = path.toUri().toURL().openStream()
				Translator.loadFile(stream, lang)
				stream.close()
			}
			fs?.close()
		}
		//Translator.loadFile(getResource("bukcore/lang/en.yml"), "en")
		//Translator.loadFile(getResource("bukcore/lang/th.yml"), "th")

		server {
			plugin("ProtocolLib") {
				ProtocolLibrary.getProtocolManager().addPacketListener(object : PacketAdapter(this@Bukcore, ListenerPriority.LOWEST, PacketType.Play.Client.SETTINGS) {
					override fun onPacketReceiving(event: PacketEvent) {
						Translator.setLanguage(event.player, event.packet.strings.read(0).substringBefore('_'))
					}
				})
			}
		}
		saveDefaultConfig()

		if (config.getString("version") < VERSION) {
			logger.info("updating config")
			config["version"] = VERSION

			config.options().copyHeader(true).copyDefaults(true)
			saveConfig()
			logger.info("update config done")
		}
		reload()
	}

	override fun onDisable() {
		Cooldown.resetAll()
		Players.players.clear()
		while (shutdownTask.isNotEmpty())
			(shutdownTask.take())()
	}

	companion object {
		private val shutdownTask = LinkedBlockingQueue<() -> Unit>()
		private var _language = "en"
		val language get() = _language

		const val VERSION = "0.3"

		@JvmSynthetic
		private var INSTANCE: Bukcore? = null

		@JvmStatic
		fun getInstance() = INSTANCE!!

		fun log(message: Any?) {
			INSTANCE?.log(message)
		}

		/**
		 * register task to run when Bukcore is disable; a task will run single time
		 */
		fun registerShutdownTask(task: () -> Unit) {
			shutdownTask.put(task)
		}
	}
}

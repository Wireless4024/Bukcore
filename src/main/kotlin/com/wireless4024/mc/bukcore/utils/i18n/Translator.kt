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

package com.wireless4024.mc.bukcore.utils.i18n

import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.utils.UniqueSortedArrayList
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Translator {

	private val replacer = Regex("\\{[^}]+}")
	private val languageFinder = Regex("[a-zA-Z]{2,3}(?=\\.)")
	private val root = YamlConfiguration()
	private val staticPlayerLanguages = HashMap<UUID, String>()
	private val playerLanguages = HashMap<UUID, String>()
	private var fallbackLang: String = ""
	private var fallback: ConfigurationSection = root
	private val bunker = ReentrantLock()

	val languages = ArrayList<String>()

	val keys:ArrayList<String> get() = root.getKeys(false).flatMapTo(UniqueSortedArrayList()) { root.getConfigurationSection(it).getKeys(true) }

	fun detectFileLanguage(fileName: String) = languageFinder.find(fileName)?.value

	init {
		Bukcore.registerShutdownTask {
			Bukcore.getInstance().ymlResource("player-language.yml") {
				for (key in it.getKeys(false)) {
					it.set(key, null)
				}
				staticPlayerLanguages.forEach { t, u ->
					it.set(t.toString(), u)
				}
			}
		}
		Bukcore.getInstance().readYamlFile("player-language.yml").run {
			for (key in getKeys(false)) {
				staticPlayerLanguages[UUID.fromString(key)] = getString(key) ?: continue
			}
		}
	}

	fun setStaticLanguage(player: Player, language: String) {
		val uid = player.uniqueId
		if (uid in staticPlayerLanguages) {
			if (language == "auto") {
				staticPlayerLanguages.remove(uid)
				return
			}
		}
		staticPlayerLanguages[uid] = language
	}

	fun setLanguage(player: Player, language: String) {
		playerLanguages[player.uniqueId] = language
	}

	fun getLanguage(player: Player?): String {
		if (player == null) return Bukcore.language
		val uid = player.uniqueId
		val static = staticPlayerLanguages[uid]
		if (static != null) return static

		val lang = playerLanguages[uid]
		if (lang == null) {
			val fallback = Bukcore.language
			playerLanguages[uid] = fallback
			return fallback
		}
		return lang
	}

	private fun getLanguageSection(language: String): ConfigurationSection? {
		if (language == fallbackLang) return fallback
		return root.getConfigurationSection(language)
	}

	private fun getFallbackLanguage(): ConfigurationSection {
		if (fallbackLang == Bukcore.language) return fallback
		val new = Bukcore.language
		var fallback = root.getConfigurationSection(new)
		if (fallback == null)
			fallback = root.getConfigurationSection("en")

		if (fallback == null)
			return this.fallback
		else {
			this.fallback = fallback
			this.fallbackLang = new
		}
		return fallback

	}

	fun loadFile(stream: InputStream?, language: String, path: String? = null, closeStream: Boolean = true) {
		if (stream == null) return
		if (language.isEmpty()) return
		var yml: ConfigurationSection = YamlConfiguration.loadConfiguration(InputStreamReader(stream, UTF_8))
		if (closeStream)
			stream.close()
		if (path != null) yml = yml.getConfigurationSection(path)
		register(language, yml)
	}

	fun loadFile(reader: Reader?, language: String, path: String? = null, closeReader: Boolean = true) {
		if (reader == null) return
		if (language.isEmpty()) return
		var yml: ConfigurationSection = YamlConfiguration.loadConfiguration(reader)
		if (closeReader)
			reader.close()
		if (path != null) yml = yml.getConfigurationSection(path)
		register(language, yml)
	}

	fun loadFile(file: File?, language: String? = null, path: String? = null) {
		if (file == null || !file.exists() || file.isDirectory) return
		var yml: ConfigurationSection = YamlConfiguration.loadConfiguration(file)
		if (path != null) yml = yml.getConfigurationSection(path)
		val lang = if (language == null || language.isEmpty()) (languageFinder.find(file.name)?.value
				?: return) else language
		register(lang, yml)
	}

	/**
	 * load language files in directory
	 * file name should end with language.yml eg. test-en.yml en.yml
	 * @param directory path to directory
	 */
	fun loadDirectory(directory: File, create: Boolean = false) {
		try {
			if (directory.exists() && directory.isDirectory) {
				for (file in directory.list() ?: return) {
					if (file.endsWith(".yml", true))
						loadFile(directory.resolve(file))
				}
			} else if (create) directory.mkdirs()
		} catch (t: Throwable) {
			t.printStackTrace()
			// ignored
		}
	}

	fun register(language: String, data: ConfigurationSection) {
		/* need this because it can cause [org.bukkit.configuration.MemorySection] to corrupt */
		bunker.lock()
		val lang = language.toLowerCase()
		if (root.contains(lang)) {
			val cfg = root.getConfigurationSection(lang)
			for (key in data.getKeys(false)) cfg.set(key, data.get(key))
		} else {
			root.set(lang, data)
			languages.add(lang)
		}
		bunker.unlock()
	}

	fun translate(language: String, key: String): String {
		return getLanguageSection(language)?.getString(key)
				?: getFallbackLanguage().getString(key) ?: key
	}

	fun translate(locale: Locale, key: String) = translate(locale.language, key)

	fun format(language: String, text: String): String {
		return if ('{' in text)
			replacer.replace(text) { translate(language, it.value.run { substring(1, length - 1) }) }
		else translate(language, text)
	}

	fun format(player: Player, text: String): String {
		val lang = getLanguage(player)
		return if ('{' in text) replacer.replace(text) {
			translate(lang, it.value.run { substring(1, length - 1) })
		} else translate(getLanguage(player), text)
	}

	override fun toString() = root.saveToString()
}

inline class Translation(val language: String) {
	operator fun String.unaryPlus() = Translator.format(language, this)
}

inline class CSTranslation(val sender: CommandSender) {
	operator fun String.unaryPlus() = sender.sendMessage(this.unaryMinus())
	operator fun String.unaryMinus() = Translator.format(Translator.getLanguage(if (sender is Player) sender else null), this)
}

infix fun Locale.translate(key: String) = Translator.translate(this, key)

infix fun Player.translateMessage(text: String) = this.sendMessage(Translator.format(this, text))

inline fun translator(language: String, block: Translation.() -> Unit) {
	block(Translation(language))
}

inline fun CommandSender.translator(block: CSTranslation.() -> Unit) {
	block(CSTranslation(this))
}
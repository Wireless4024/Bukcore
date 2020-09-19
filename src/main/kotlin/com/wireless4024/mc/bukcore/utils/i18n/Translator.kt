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
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import kotlin.collections.HashMap

object Translator {

	private val replacer = Regex("\\{[^}]+}")
	private val languageFinder = Regex("[a-zA-Z]{2,3}(?=\\.)")
	private val root = YamlConfiguration()
	private val playerLanguages = HashMap<UUID, String>()

	fun setLanguage(player: Player, language: String) {
		playerLanguages[player.uniqueId] = language
	}

	fun getLanguage(player: Player?): String {
		if (player == null) return Bukcore.getInstance().config.getString("fallback_lang", "en")
		return playerLanguages[player.uniqueId] ?: Bukcore.getInstance().config.getString("fallback_lang", "en")
	}

	fun loadFile(stream: InputStream?, language: String, path: String? = null) {
		if (stream == null) return
		if (language.isEmpty()) return
		var yml: ConfigurationSection = YamlConfiguration.loadConfiguration(InputStreamReader(stream, UTF_8))
		if (path != null) yml = yml.getConfigurationSection(path)
		register(language, yml)
	}

	fun loadFile(file: File?, language: String? = null, path: String? = null) {
		if (file == null || !file.exists()) return
		var yml: ConfigurationSection = YamlConfiguration.loadConfiguration(file)
		if (path != null) yml = yml.getConfigurationSection(path)
		val lang = if (language == null || language.isEmpty()) (languageFinder.find(file.name)?.value
				?: return) else language
		register(lang, yml)
	}

	fun register(language: String, data: ConfigurationSection) {
		if (root.contains(language)) {
			val cfg = root.getConfigurationSection(language)
			for (key in data.getKeys(false)) cfg.set(key, data.get(key))
		} else
			root.set(language, data)
	}

	fun translate(language: String, key: String): String {
		return (root.getConfigurationSection(language)
				?: root.getConfigurationSection(getLanguage(null)))?.getString(key) ?: key
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
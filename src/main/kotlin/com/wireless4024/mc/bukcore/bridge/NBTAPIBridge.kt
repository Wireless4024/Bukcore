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

package com.wireless4024.mc.bukcore.bridge

import com.wireless4024.mc.bukcore.utils.toJson
import de.tr7zw.nbtapi.NBTCompound
import de.tr7zw.nbtapi.NBTContainer
import de.tr7zw.nbtapi.NBTType.*
import org.bukkit.plugin.Plugin

object NBTAPIBridge : Bridge {

	private var _plugin: Plugin? = null

	override val name: String = "NBTAPI"

	override val plugin: Plugin?
		get() = _plugin
		        ?: (org.bukkit.Bukkit.getServer().pluginManager.getPlugin("NBTAPI"))?.also { _plugin = it }
}

fun NBTCompound.toHashMap(): HashMap<String, Any> {
	val map = HashMap<String, Any>()
	loop@ for (it in this.keys) {
		when (it) {
			"x", "y", "z", "id" -> continue@loop
		}
		@Suppress("NON_EXHAUSTIVE_WHEN")
		when (this.getType(it)) {
			NBTTagString -> map[it] = this.getString(it)
			NBTTagCompound -> map[it] = this.getCompound(it).toHashMap()
			NBTTagByte -> map[it] = this.getByte(it)
			NBTTagShort -> map[it] = this.getShort(it)
			NBTTagInt -> map[it] = this.getInteger(it)
			NBTTagLong -> map[it] = this.getLong(it)
			NBTTagFloat -> map[it] = this.getFloat(it)
			NBTTagDouble -> map[it] = this.getDouble(it)
			NBTTagByteArray -> map[it] = this.getByteArray(it)
			NBTTagIntArray -> map[it] = this.getIntArray(it)
			NBTTagList -> map[it] = this.getCompoundList(it).map(NBTCompound::toHashMap).toTypedArray()
		}
	}
	return map
}

fun Map<String, Any>.toNBTCompound() = NBTContainer(this.toJson())
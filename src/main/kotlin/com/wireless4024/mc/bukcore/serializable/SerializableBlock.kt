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

package com.wireless4024.mc.bukcore.serializable

import com.wireless4024.mc.bukcore.utils.readMap
import com.wireless4024.mc.bukcore.utils.writeJsonMap
import me.dpohvar.powernbt.api.NBTCompound
import me.dpohvar.powernbt.api.NBTManager
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.block.Block
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * a prototype class for any block serialization in Bukcore.
 */
@Suppress("DEPRECATION") class SerializableBlock(baseOffset: Location,
                                                 block: Block,
                                                 private val nbtManager: NBTManager? = null) {

	companion object {

		// ]\u0000!\u0000[
		val PADDING = byteArrayOf(93, 0, 33, 0, 91)
		fun writePadding(output: ObjectOutputStream) {
			output.write(PADDING)
		}

		fun checkPadding(input: ObjectInputStream): Boolean {
			if (input.read() != 93) return false
			if (input.read() != 0) return false
			if (input.read() != 33) return false
			if (input.read() != 0) return false
			if (input.read() != 91) return false
			return true
		}

		fun writeSkip(output: ObjectOutputStream) {
			output.writeByte(-1)
		}

		fun writeBlock(output: ObjectOutputStream, base: Location, block: Block, nbtManager: NBTManager? = null) {
			val bloc = block.location

			if (block.type === AIR) {
				// fail fast due air block
				output.writeByte(0)
				output.writeInt(bloc.blockX - base.blockX)
				output.writeInt(bloc.blockY - base.blockY)
				output.writeInt(bloc.blockZ - base.blockZ)
				return
			}
			output.writeByte(4)

			output.writeInt(bloc.blockX - base.blockX)
			output.writeInt(bloc.blockY - base.blockY)
			output.writeInt(bloc.blockZ - base.blockZ)

			val type = block.type
			@Suppress("DEPRECATION")
			output.writeInt(type.id)
			@Suppress("DEPRECATION")
			output.writeByte(block.data.toInt())

			output.writeJsonMap(if (!type.isBlock) null else nbtManager?.read(block)?.toHashMap()?.apply {
				remove("x")
				remove("y")
				remove("z")
				if (type !== SIGN_POST && type !== WALL_SIGN)
					remove("id")
			})
			output.flush()
		}

		fun readBlock(input: ObjectInputStream, base: Location, nbtManager: NBTManager? = null): Boolean {
			val magic = input.readByte().toInt()
			if (magic == -1) return false // skipped
			if (magic == 0) {
				// fail fast due air block
				base.clone()
						.add(input.readInt().toDouble(), input.readInt().toDouble(), input.readInt().toDouble())
						.block.setType(AIR, false)
				return true
			}

			if (magic != 4) throw UnsupportedOperationException("please implement manually")

			val location = base.clone()
					.add(input.readInt().toDouble(), input.readInt().toDouble(), input.readInt().toDouble())
			val target = location.block

			val type = Material.getMaterial(input.readInt())
			target.setTypeIdAndData(type.id, input.readByte(), false)

			val nbt = input.readMap<Any>()

			if (nbt?.isEmpty() == false && nbtManager != null && type.isBlock) {
				val nbtTag: NBTCompound = nbtManager.read(target)
				nbtTag.putAll(nbt)
				nbtManager.write(target, nbtTag)
			}
			return true
		}
	}

	var offsetX = 0
	var offsetY = 0
	var offsetZ = 0

	var type = Material.AIR
	var nbt: Map<String, Any>? = null

	init {
		val bloc = block.location
		offsetX = bloc.blockX - baseOffset.blockX
		offsetY = bloc.blockY - baseOffset.blockY
		offsetZ = bloc.blockZ - baseOffset.blockZ

		type = block.type

		if (type.isBlock)
			nbt = nbtManager?.read(block)?.toHashMap()?.run {
				remove("x")
				remove("y")
				remove("z")
				if (type !== SIGN_POST && type !== WALL_SIGN)
					remove("id")

				if (this.isEmpty()) null else this
			}
	}

	@Throws(IOException::class)
	fun readObject(input: ObjectInputStream) {
		when (val offetSize = input.readUnsignedByte()) {
			1 -> {
				offsetX = input.readUnsignedByte()
				offsetY = input.readUnsignedByte()
				offsetZ = input.readUnsignedByte()
			}
			2 -> {
				offsetX = input.readUnsignedShort()
				offsetY = input.readUnsignedShort()
				offsetZ = input.readUnsignedShort()
			}
			4 -> {
				offsetX = input.readInt()
				offsetY = input.readInt()
				offsetZ = input.readInt()
			}
			else -> throw UnsupportedOperationException("offset size : $offetSize")
		}
		@Suppress("DEPRECATION") // better serialized size
		this.type = Material.getMaterial(input.readInt())

		this.nbt = input.readMap()
	}

	@Throws(IOException::class)
	fun writeObject(output: ObjectOutputStream) {
		// offset size
		// byte     = 1
		// short    = 2
		// int      = 4
		// long     = 8
		output.writeByte(4)

		output.writeInt(offsetX)
		output.writeInt(offsetY)
		output.writeInt(offsetZ)

		@Suppress("DEPRECATION")
		output.writeInt(type.id)

		output.writeJsonMap(nbt)
	}

	fun toBlock(baseLocation: Location) {
		val location = baseLocation.clone().add(offsetX.toDouble(), offsetY.toDouble(), offsetZ.toDouble())
		val target = location.block
		target.setType(type, false)

		val nbt = this.nbt

		if (nbt != null && nbtManager != null && type.isBlock) {
			val nbtTag: NBTCompound = nbtManager.read(target)
			nbtTag.putAll(nbt)
			nbtManager.write(target, nbtTag)
		}
	}
}
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

import com.wireless4024.mc.bukcore.bridge.NBTAPIBridge
import com.wireless4024.mc.bukcore.bridge.toNBTCompound
import com.wireless4024.mc.bukcore.utils.blocks.BlockNBT.readNBTMap
import de.tr7zw.nbtapi.NBTReflectionUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

object DirectionalOffset {

	fun from(base: Location, target: Location, direction: Byte): DirectionOffset {
		var left = 0.0
		var forward = 0.0
		val up: Double = target.x - base.x

		when (direction) {
			// 213 68 204 - NORTH -> 212 69 201 = 1 3 1
			Direction.NORTH, Direction.BASE -> {
				left = base.x - target.x
				forward = base.z - target.z
			}
			// 213 68 204 - EAST -> 212 69 201 = 3 -1 1
			Direction.EAST -> {
				left = base.z - target.z
				forward = target.x - base.x
			}
			// 213 68 204 - SOUTH -> 212 69 201 = -1 -3 1
			Direction.SOUTH -> {
				left = target.x - base.x
				forward = target.z - base.z
			}
			// 213 68 204 - WEST -> 212 69 201 = -3 1 1
			Direction.WEST -> {
				left = target.z - base.z
				forward = base.x - target.x
			}
		}
		return DirectionOffset(left, forward, up)
	}
}

data class DirectionOffset(val left: Double, val forward: Double, val up: Double) {

	fun toLocation(base: Location, direction: Byte): Location {
		return updateLocation(base.clone(), direction)
	}

	fun toLocation(location: Location) = toLocation(location, Direction.direction4(location))

	fun updateLocation(location: Location, direction: Byte): Location {
		when (direction) {
			Direction.NORTH -> location.add(-left, up, -forward)
			Direction.EAST -> location.add(forward, up, left)
			Direction.SOUTH -> location.add(left, up, forward)
			Direction.WEST -> location.add(-forward, up, -left)
		}
		return location
	}

	fun updateLocation(location: Location) = updateLocation(location, Direction.direction4(location))
}

data class RelativeBlock(val material: Material,
                         val direction: Byte,
                         val offset: DirectionOffset,
                         val nbt: HashMap<String, Any>? = null) {

	fun place(where: Location) {
		val thisDirection = Direction.direction4(where)
		val target = offset.toLocation(where, thisDirection)
		val block = target.block
		block.type = material
		@Suppress("DEPRECATION")
		block.data = Direction.destabilize4(direction, thisDirection)
		if (nbt != null && nbt.isNotEmpty() && NBTAPIBridge.available) {
			try {
				val bs = block.state
				if (bs.javaClass.simpleName == "BlockEntityTag") return
				NBTReflectionUtil.setTileEntityNBTTagCompound(bs, nbt.toNBTCompound().compound)
			} catch (t: Throwable) {
				t.printStackTrace()
			}
		}
	}
}

fun Block.toRelativeBlock(base: Location): RelativeBlock {
	val location = this.location
	val looking = Direction.direction4(location)
	val material: Material = this.type

	@Suppress("DEPRECATION")
	val direction: Byte = Direction.normalize4(this.data, looking)
	val offset: DirectionOffset = DirectionalOffset.from(base, location, looking)
	val nbt: HashMap<String, Any>? = this.readNBTMap()

	return RelativeBlock(material, direction, offset, if (nbt?.isEmpty() != true) nbt else null)
}

fun Location.toRelativeBlock(base: Location): RelativeBlock {
	val block = this.block
	val looking = Direction.direction4(this)
	val material: Material = block.type

	@Suppress("DEPRECATION")
	val direction: Byte = Direction.normalize4(block.data, looking)
	val offset: DirectionOffset = DirectionalOffset.from(base, this, looking)
	val nbt: HashMap<String, Any>? = this.block.readNBTMap()

	return RelativeBlock(material, direction, offset, if (nbt?.isEmpty() != true) nbt else null)
}
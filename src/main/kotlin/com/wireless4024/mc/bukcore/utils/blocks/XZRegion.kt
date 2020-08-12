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

package com.wireless4024.mc.bukcore.utils.blocks

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.min

class XZRegion(world: World, x1: Int, x2: Int, y: Int, z1: Int, z2: Int) : Rectangle(world, x1, x2, y, y, z1, z2) {
	companion object {

		fun around(center: Location, radius: Int): XZRegion {
			return XZRegion(center.world,
			                center.blockX - radius,
			                center.blockX + radius,
			                center.blockY,
			                center.blockZ - radius,
			                center.blockZ + radius)
		}
	}

	override operator fun invoke(task: (Block) -> Unit) {
		val y = y1
		for (x in x1..x2)
			for (z in z1..z2)
				task(world.getBlockAt(x, y, z))
	}

	override operator fun invoke(task: (Block, Int) -> Unit) {
		var i = -1
		val y = y1
		for (x in x1..x2)
			for (z in z1..z2)
				task(world.getBlockAt(x, y, z), ++i)
	}

	override operator fun invoke(array: Array<Block>): Array<Block> {
		var i = -1
		val limit = min(size, array.size)
		val y = y1
		for (x in x1..x2)
			for (z in z1..z2)
				if (++i == limit) break else array[i] = world.getBlockAt(x, y, z)
		return array
	}
}
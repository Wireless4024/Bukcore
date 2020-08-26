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
import com.wireless4024.mc.bukcore.utils.blocks.Region3D
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.BlockIterator
import java.lang.Integer.max
import java.util.*

/**
 * utility functions for blocks
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
object BlockUtils {

	/**
	 * get block from async thread
	 * @param location Location location to get block
	 * @return Block block at location
	 */
	fun getBlockSync(location: Location): Block {
		return Bukcore.getInstance().call { location.block }.get()
	}

	/**
	 * get block from async thread
	 * @param region Location location to get block
	 * @return Block block at location
	 */
	fun getBlockSync(region: Region3D): Array<Block> {
		return Bukcore.getInstance().call { region.toArray() }.get()
	}

	/**
	 * get block from async thread
	 * @param region Location location to get block
	 * @param array target array to store blocks
	 * @return Block block at location
	 */
	fun getBlockSync(region: Region3D, array: Array<Block>): Array<Block> {
		return Bukcore.getInstance().call { region(array) }.get()
	}

	fun find(player: Player, materials: Material): Block? {
		val blocks: Iterator<Block> = BlockIterator(player, 5)
		while (blocks.hasNext()) {
			val blck = blocks.next()
			if (blck.isEmpty) continue
			return if (blck.type == materials) blck else null
		}
		return null
	}

	fun find(player: Player?, vararg materials: Material?): Block? {
		val blocks: Iterator<Block> = BlockIterator(player, 5)
		val mats = materials.clone()
		Arrays.sort(mats)
		while (blocks.hasNext()) {
			val blck = blocks.next()
			if (blck.isEmpty) continue
			return if (Arrays.binarySearch(mats, blck.type) >= 0) blck else null
		}
		return null
	}

	fun findChest(player: Player, distance: Int = 4): Block? {
		val blocks: Iterator<Block> = BlockIterator(player, distance)
		while (blocks.hasNext()) {
			val blck = blocks.next()
			if (blck.type == Material.CHEST || blck.type == Material.TRAPPED_CHEST) return blck
		}
		return null
	}

	fun findChest(p: Player, x: Int, y: Int, z: Int, world: String? = null): Block? {
		val block = world?.run { Bukkit.getWorld(world)?.getBlockAt(x, y, z) } ?: p.world.getBlockAt(x, y, z)
		return if (block.type == Material.CHEST || block.type == Material.CHEST) block else null
	}

	fun isChest(location: Location): Block? {
		val block = location.block
		return if (block.type == Material.CHEST || block.type == Material.CHEST) block else null
	}

	@JvmOverloads
	fun trace(player: Player, distance: Int, allow_pass: Int = 0): Location {
		val blocks: Iterator<Block> = BlockIterator(player, max(distance, 1))
		var blck = blocks.next()
		var last: Block? = blck
		var empty: Block? = null
		var pass = allow_pass
		while (blocks.hasNext()) {
			blck = blocks.next()
			if (last != null && (last.isEmpty || last.isLiquid))
				empty = last
			if (blck.isEmpty || blck.isLiquid) {
				if (pass != allow_pass) {
					return if (blocks.hasNext()) blocks.next().location else blck.location
				}
			} else {
				if (pass < 1) return empty?.location ?: player.location
				--pass
			}
			last = blck
		}
		return if (empty == null)
			if (last == null || !last.isEmpty || !last.isLiquid)
				if (!blck.isEmpty && !blck.isLiquid) player.location
				else blck.location
			else last.location
		else empty.location
	}

	fun traceBlock(player: Player, limit: Int): Block? {
		val block = player.getTargetBlock(null as MutableSet<Material>?, limit)
		return if (block.isEmpty || block.isLiquid) null else block
	}

	@JvmOverloads
	fun getBlockFace(player: Player, distance: Int = 15): BlockFace {
		val target = player.getLastTwoTargetBlocks(null as Set<Material>?, distance)
		if (target.size != 2) return BlockFace.SELF
		return target[1].getFace(target[0])
	}

	fun nearestEntity(location: Location, area: Double = 200.0): Entity? {
		return location.world.getNearbyEntities(location, area, area, area)
				.minBy {
					it.location.distanceSquared(location)
							.run { if (this != 0.0) this else Double.MAX_VALUE }
				}
	}

	fun nearestPlayer(location: Location): Player? {
		val players = location.world.players.stream()
		return players.min { o1, o2 ->
			location.distanceSquared(o1.location)
					.compareTo(location.distanceSquared(o2.location))
					.run { if (this == 0) 1 else this }
		}.orElse(null)
	}
}

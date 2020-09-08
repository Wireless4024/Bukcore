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

import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.internal.AlwaysEmptyMutableList
import com.wireless4024.mc.bukcore.serializable.SerializableBlock
import com.wireless4024.mc.bukcore.utils.io.isReady
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitTask
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StreamCorruptedException
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 *
 * @property world World world of the region
 * @property x1 Int lower bound of x axis
 * @property x2 Int upper bound of x axis
 * @property y1 Int lower bound of y axis
 * @property y2 Int upper bound of y axis
 * @property z1 Int lower bound of z axis
 * @property z2 Int upper bound of z axis
 * @property xDelta Int delta x in the region
 * @property yDelta Int delta y in the region
 * @property zDelta Int delta z in the region
 * @property size Int size in the region
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
open class Region3D(val world: World,
                    x1: Int, x2: Int,
                    y1: Int, y2: Int,
                    z1: Int, z2: Int) {

	val x1: Int
	val x2: Int
	val y1: Int
	val y2: Int
	val z1: Int
	val z2: Int

	init {
		if (x1 < x2) {
			this.x1 = x1
			this.x2 = x2
		} else {
			this.x1 = x2
			this.x2 = x1
		}
		val maxy = world.maxHeight
		if (y1 < y2) {
			this.y1 = max(0, min(maxy, y1))
			this.y2 = max(0, min(maxy, y2))
		} else {
			this.y1 = max(0, min(maxy, y2))
			this.y2 = max(0, min(maxy, y1))
		}
		if (z1 < z2) {
			this.z1 = z1
			this.z2 = z2
		} else {
			this.z1 = z2
			this.z2 = z1
		}
	}

	companion object {

		/**
		 * create [Region3D] from two location
		 * @param location1 Location first location
		 * @param location2 Location second location
		 */
		fun form(location1: Location, location2: Location): Region3D {
			if (location1.world != location2.world) throw IllegalArgumentException("location1.world != location2.world")
			return Region3D(location1.world,
			                location1.blockX, location2.blockX,
			                location1.blockY, location2.blockY,
			                location1.blockZ, location2.blockZ)
		}

		fun around(center: Location, area: Int, up: Int = 1, down: Int = 0): Region3D {
			val areaDiv2 = area shr 1
			return Region3D(center.world,
			                center.blockX - areaDiv2, center.blockX + areaDiv2,
			                (center.blockY - down), center.blockY + up - 1,
			                center.blockZ - areaDiv2, center.blockZ + areaDiv2
			)
		}

		@JvmStatic
		fun square(i: Double): Double {
			return i * i
		}

		@JvmStatic
		fun square(i: Int): Double {
			return 1.0 * i * i
		}

		fun deserialize(input: ObjectInputStream, base: Location) {
			while (input.isReady()) {
				if (SerializableBlock.readBlock(input, base))
					if (!SerializableBlock.checkPadding(input))
						throw StreamCorruptedException("invalid block padding")
			}
		}
	}

	/**
	 * number of blocks in x
	 */
	val xDelta = x2 - x1 + 1

	/**
	 * number of blocks in y
	 */
	val yDelta = y2 - y1 + 1

	/**
	 * number of blocks in z
	 */
	val zDelta = z2 - z1 + 1

	/**
	 * get size of the region
	 */
	val size: Int = xDelta * yDelta * zDelta

	/**
	 * loop through all blocks in this region
	 * @param task a task to process a block
	 */
	open operator fun invoke(task: (Block) -> Unit) {
		for (x in x1..x2)
			for (y in y1..y2)
				for (z in z1..z2)
					task(world.getBlockAt(x, y, z))
	}

	/**
	 * loop through all blocks in this region with indexed
	 * @param task a task to process a block
	 */
	open operator fun invoke(task: (Block, Int) -> Unit) {
		var i = -1
		for (x in x1..x2)
			for (y in y1..y2)
				for (z in z1..z2)
					task(world.getBlockAt(x, y, z), ++i)

	}

	/**
	 * loop through all blocks exclude [exclude] in this region with indexed
	 * @param task a task to process a block
	 */
	fun loopExclude(exclude: Array<Material>, task: (Block) -> Unit) {
		val exc = exclude.sortedArray()

		for (x in x1..x2)
			for (y in y1..y2)
				for (z in z1..z2)
					world.getBlockAt(x, y, z).let {
						if (exc.binarySearch(it.type) < 0)
							task(it)
					}
	}

	/**
	 * loop through all blocks exclude [exclude] in this region with indexed
	 * @param task a task to process a block
	 */
	fun loopExclude(exclude: Array<Material>, task: (Block, Int) -> Unit) {
		val exc = exclude.sortedArray()

		var i = -1
		for (x in x1..x2)
			for (y in y1..y2)
				for (z in z1..z2)
					world.getBlockAt(x, y, z).let {
						if (exc.binarySearch(it.type) < 0)
							task(it, ++i)
					}
	}

	/**
	 * get all blocks inside region into array
	 * @param array Array<Block> target array
	 * @return Array<Block> target array
	 */
	open operator fun invoke(array: Array<Block>): Array<Block> {
		var i = -1
		val limit = min(size, array.size)
		for (x in x1..x2)
			for (y in y1..y2)
				for (z in z1..z2)
					if (++i == limit) break else array[i] = world.getBlockAt(x, y, z)
		return array
	}

	/**
	 * loop through all blocks in ellipsoid region
	 */
	open fun ellipsoid(task: (Block) -> Unit) {
		val xArea = xDelta shr 1
		val yArea = yDelta shr 1
		val zArea = zDelta shr 1
		val xCenter = x1 + xArea
		val yCenter = y1 + yArea
		val zCenter = z1 + zArea
		for (xd in -xArea..xArea)
			for (yd in -yArea..yArea)
				for (zd in -zArea..zArea)
					if (sqrt(square(xd.toDouble() / xDelta) + square(yd.toDouble() / yDelta) + square(zd.toDouble() / zDelta)) <= 0.5)
						task(world.getBlockAt(xCenter + xd, yCenter + yd, zCenter + zd))
	}

	/**
	 * loop through all blocks in ellipsoid region
	 */
	open fun ellipsoid(task: (Block, Int) -> Unit) {
		var i = -1
		val xArea = xDelta shr 1
		val yArea = yDelta shr 1
		val zArea = zDelta shr 1
		val xCenter = x1 + xArea
		val yCenter = y1 + yArea
		val zCenter = z1 + zArea
		for (xd in -xArea..xArea)
			for (yd in -yArea..yArea)
				for (zd in -zArea..zArea)
					if (sqrt(square(xd.toDouble() / xDelta) + square(yd.toDouble() / yDelta) + square(zd.toDouble() / zDelta)) <= 0.5)
						task(world.getBlockAt(xCenter + xd, yCenter + yd, zCenter + zd), ++i)
	}

	/**
	 * get all block in region as array
	 * @return Array<Block> result array
	 */
	fun toArray(): Array<Block> {
		@Suppress("UNCHECKED_CAST") // this will always passed
		return this(java.lang.reflect.Array.newInstance(Block::class.java, size) as Array<Block>)
	}

	fun toArray(range: IntRange): Array<Block> {
		@Suppress("UNCHECKED_CAST") // this will always passed
		val array = java.lang.reflect.Array.newInstance(Block::class.java, range.last - range.first + 1) as Array<Block>
		var index = -1
		for (i in range)
			array[++index] = blockAt(i)
		return array
	}

	/**
	 * loop through all blocks in this region from async thread
	 * Note : this job will not block execution
	 * @param task a task to process a block
	 */
	fun invokeSync(task: (Block) -> Unit) {
		Bukcore.getInstance().runTask { this.invoke(task) }
	}

	/**
	 * get all blocks inside region into array from async thread
	 * Note : this job will not block execution
	 * @param array target array
	 */
	fun invokeSync(array: Array<Block>): Future<Array<Block>> {
		return Bukcore.getInstance().call { this.invoke(array) }
	}

	/**
	 * loop through all blocks in this region from async thread
	 * Note : this job will not block execution
	 * @param delay the ticks to wait before running the task
	 * @param task a task to process a block
	 */
	fun invokeSync(delay: Long, task: (Block) -> Unit) {
		Bukcore.getInstance().runTask(delay) { this.invoke(task) }
	}

	/**
	 * get all block in region as array from async thread
	 * @return Array<Block> result array
	 */
	fun getBlocksSync(): Array<Block> {
		if (Bukkit.isPrimaryThread()) return toArray()
		return Bukcore.getInstance().call(this::toArray).get()
	}

	fun getBlocksSync(range: IntRange): Array<Block> {
		if (Bukkit.isPrimaryThread()) return toArray(range)
		return Bukcore.getInstance().call { toArray(range) }.get()
	}

	/**
	 * get chunk size in this region
	 * @return Int chunk size
	 */
	fun chunkSize(): Int {
		return (((x2 shr 4) - (x1 shr 4)) + 1) * (((z2 shr 4) - (z1 shr 4)) + 1)
	}

	/**
	 * load all chunks in this region
	 * @param generate set to true to generate chunk if chunk is not generated
	 */
	fun loadChunks(generate: Boolean = true) {
		val xcl = x1 shr 4 // low x
		val xch = x2 shr 4 // high x
		val zcl = z1 shr 4 // low z
		val zch = z2 shr 4 // high z

		for (x in xcl..xch)
			for (z in zcl..zch) {
				val chunk = world.getChunkAt(x, z)
				if (!chunk.isLoaded)
					chunk.load(generate)
			}
	}

	/**
	 * get all chunks in this region
	 * @return Array<Chunk>
	 */
	fun chunks(task: (Chunk) -> Unit) {
		val xcl = x1 shr 4 // low x
		val xch = x2 shr 4 // high x
		val zcl = z1 shr 4 // low z
		val zch = z2 shr 4 // high z
		for (x in xcl..xch)
			for (z in zcl..zch)
				task(world.getChunkAt(x, z))
	}

	/**
	 * load and generate all chunks in this region
	 * @param counts amount of chunk to generate in period
	 * @param period delay
	 * @param unload unload chunk after load?
	 */
	fun lazyLoadChunk(counts: Int = 4,
	                  period: Int = 1) {
		val xcl = x1 shr 4 // low  x
		val xch = x2 shr 4 // high x
		val zcl = z1 shr 4 // low  z
		val zch = z2 shr 4 // high z
		var i = 0L

		val core = Bukcore.getInstance()

		@Suppress("UNCHECKED_CAST")  // this will always passed
		val buffer = java.lang.reflect.Array.newInstance(IntPair::class.java, counts) as Array<IntPair>
		var b = -1

		for (x in xcl..xch)
			for (z in zcl..zch) {
				if (!world.isChunkLoaded(x, z)) {
					if (++b < counts) {
						buffer[b] = IntPair(x, z)
					} else {
						val chunks = buffer.copyOfRange(0, b)
						b = -1
						core.runTask(++i * period) {
							var chunk: Chunk
							for (c in chunks) {
								chunk = c.getChunk(world)
								chunk.load(true)
							}
						}
					}
				}
			}
		if (b != -1 && b < buffer.size - 1) {
			val chunks = buffer.copyOfRange(0, b)
			core.runTask(++i * period) {
				var chunk: Chunk
				for (c in chunks) {
					chunk = c.getChunk(world)
					chunk.load(true)
				}
			}
		}
	}

	/**
	 * x, z pair to get chunk
	 */
	private data class IntPair(val a: Int, val b: Int) {

		fun getChunk(world: World): Chunk {
			return world.getChunkAt(a, b)
		}
	}

	/**
	 * get all chunks in the region
	 * @return Array<Chunk>
	 */
	fun chunks(): Array<Chunk> {
		val xcl = x1 shr 4 // low x
		val xch = x2 shr 4 // high x
		val zcl = z1 shr 4 // low z
		val zch = z2 shr 4 // high z
		val size = ((xch - xcl) + 1) * ((zch - zcl) + 1)

		var i = -1

		@Suppress("UNCHECKED_CAST")  // this will always passed
		val chunks = java.lang.reflect.Array.newInstance(Chunk::class.java, size) as Array<Chunk>
		for (x in xcl..xch)
			for (z in zcl..zch)
				chunks[++i] = world.getChunkAt(x, z)

		return chunks
	}

	/**
	 * move x value by [amount]
	 * @param amount Int amount to increase
	 * @return Region3D
	 */
	fun moveX(amount: Int) = Region3D(world, x1 + amount, x2 + amount, y1, y2, z1, z2)

	fun xy(): Array<XYRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(XYRegion::class.java, (z2 - z1 + 1)) as Array<XYRegion>
		var i = -1
		for (z in z1..z2)
			regions[++i] = XYRegion(world, x1, x2, y1, y2, z)
		return regions
	}

	fun xz(): Array<XZRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(XZRegion::class.java, (y2 - y1 + 1)) as Array<XZRegion>
		var i = -1
		for (y in y1..y2)
			regions[++i] = XZRegion(world, x1, x2, y, z1, z2)
		return regions
	}

	fun yz(): Array<YZRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(YZRegion::class.java, (x2 - x1 + 1)) as Array<YZRegion>
		var i = -1
		for (x in x1..x2)
			regions[++i] = YZRegion(world, x, y1, y2, z1, z2)
		return regions
	}

	/**
	 * move y value by [amount]
	 * @param amount Int amount to increase
	 * @return Region3D
	 */
	fun moveY(amount: Int) = Region3D(world, x1, x2, y1 + amount, y2 + amount, z1, z2)

	/**
	 * move z value by [amount]
	 * @param amount Int amount to increase
	 * @return Region3D
	 */
	fun moveZ(amount: Int) = Region3D(world, x1, x2, y1, y2, z1 + amount, z2 + amount)
	fun blockAt(offset: Int): Block {
		// 0 < offset < size
		val x = (offset / (xDelta * zDelta))
		val z = (offset / xDelta) % zDelta
		val y = offset % yDelta

		return world.getBlockAt(x + x1, y + y1, z + z1)
	}

	fun blockAt0(offset: Int): Block {
		// 0 < offset < size
		val y = (offset / (xDelta * yDelta))
		val x = (offset / yDelta) % xDelta
		val z = offset % zDelta

		return world.getBlockAt(x + x1, y + y1, z + z1)
	}

	fun blockAt(offset: Long): Block {
		// 0 < offset < size
		val x = (offset / (xDelta * zDelta))
		val z = (offset / xDelta) % zDelta
		val y = offset % yDelta

		return world.getBlockAt(x.toInt() + x1, y.toInt() + y1, z.toInt() + z1)
	}

	/**
	 * queue a job to update later
	 * @param scale Int number of blocks per interval
	 * @param interval Int interval in tick
	 * @param job Function1<Block, Unit>
	 */
	fun lazyUpdate(scale: Int, interval: Int, job: (Block) -> Unit): MutableList<BukkitTask> {
		val minJobSize = (scale + (scale shr 1))
		val size = size
		when {
			size < minJobSize -> {
				invoke(job)
			}
			else              -> {
				val task = mutableListOf<BukkitTask>()
				var i = 0
				val plugin = Bukcore.getInstance()
				val loops = (size / scale) - 1
				while (true) {
					if (i == loops) break
					val ii = i
					task += plugin.runTask((ii * interval).toLong()) {
						for (j in (ii * scale) until (ii + 1) * scale) {
							job(blockAt(j))
						}
					}
					++i
				}
				// loops = 31 / 10 = 3
				// scale = 10
				// j = 0 ; 0 ..10
				// j = 1 ; 10..20
				// j = 2 ; 20..30
				val ii = i
				task += plugin.runTask((ii * interval).toLong()) {
					for (j in (scale * ii) until size)
						job(blockAt(j))
				}
				return task
			}
		}
		return AlwaysEmptyMutableList.get()
	}

	fun lazyUpdate0(scale: Int, interval: Int, job: (Block) -> Unit): MutableList<BukkitTask> {
		val minJobSize = (scale + (scale shr 1))
		val size = size
		when {
			size < minJobSize -> {
				invoke(job)
			}
			else              -> {
				val task = mutableListOf<BukkitTask>()
				var i = 0
				val plugin = Bukcore.getInstance()
				val loops = (size / scale) - 1
				while (true) {
					if (i == loops) break
					val ii = i
					task += plugin.runTask((ii * interval).toLong()) {
						for (j in (ii * scale) until (ii + 1) * scale) {
							job(blockAt0(j))
						}
					}
					++i
				}
				// loops = 31 / 10 = 3
				// scale = 10
				// j = 0 ; 0 ..10
				// j = 1 ; 10..20
				// j = 2 ; 20..30
				val ii = i
				task += plugin.runTask((ii * interval).toLong()) {
					for (j in (scale * ii) until size)
						job(blockAt0(j))
				}
				return task
			}
		}
		return AlwaysEmptyMutableList.get()
	}

	fun eachBlockPerTick(pos: Int = 0, job: (block: Block, cancel: AtomicBoolean) -> Boolean) {
		val cancel = AtomicBoolean(false)
		if (pos >= size) return
		var now = pos
		while (!cancel.get() && now < size && !job(blockAt0(now), cancel)) {
			if (now - pos > 99) break // if reached 100 block will skip
			++now
		}
		if (++now < size && !cancel.get()) Bukcore.getInstance()() { eachBlockPerTick(now, job) }
	}

	fun lazyAsyncGetBlocks(scale: Int, interval: Int, job: (Array<Block>) -> Unit): MutableList<BukkitTask> {
		val minJobSize = (scale + (scale shr 1))
		val size = size
		when {
			size < minJobSize -> {
				job(getBlocksSync())
			}
			else              -> {
				val task = mutableListOf<BukkitTask>()
				var i = 0
				val plugin = Bukcore.getInstance()
				val loops = (size / scale) - 1
				while (true) {
					if (i == loops) break
					val ii = i
					task += plugin.runAsync((ii * interval).toLong()) {
						job(getBlocksSync((ii * scale) until (ii + 1) * scale))
					}
					++i
				}
				if (size % scale != 0) {
					val ii = i
					task += plugin.runAsync((ii * interval).toLong()) {
						job(getBlocksSync((scale * ii) until size))
					}
				}
				return task
			}
		}
		return AlwaysEmptyMutableList.get()
	}

	override fun hashCode(): Int {
		return world.hashCode() * x1 xor x2 * y1 xor y2 * z1 xor z2
	}

	override fun toString(): String {
		return "Region3D(${world.name}, $x1, $x2, $y1, $y2, $z1 ,$z2)"
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Region3D) return false

		if (world != other.world) return false
		if (x1 != other.x1) return false
		if (x2 != other.x2) return false
		if (y1 != other.y1) return false
		if (y2 != other.y2) return false
		if (z1 != other.z1) return false
		if (z2 != other.z2) return false

		return true
	}

	fun serialize(output: ObjectOutputStream, base: Location) {
		SerializableBlock.writeSkip(output)
		invoke { it ->
			SerializableBlock.writeBlock(output, base, it)
			SerializableBlock.writePadding(output)
		}
		SerializableBlock.writeSkip(output)
		output.flush()
	}
}
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

import org.bukkit.World

/**
 * Rectangle region allow to split region into single line
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
open class Rectangle(world: World, x1: Int, x2: Int, y1: Int, y2: Int, z1: Int, z2: Int) : Region3D(world,
                                                                                                    x1,
                                                                                                    x2,
                                                                                                    y1,
                                                                                                    y2,
                                                                                                    z1,
                                                                                                    z2) {

	fun x(): Array<XRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(XRegion::class.java,
		                                                  (y2 - y1 + 1) * (z2 - z1 + 1)) as Array<XRegion>
		var i = -1
		for (y in y1..y2)
			for (z in z1..z2)
				regions[++i] = XRegion(world, x1, x2, y, z)
		return regions
	}

	fun y(): Array<YRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(YRegion::class.java,
		                                                  (x2 - x1 + 1) * (z2 - z1 + 1)) as Array<YRegion>
		var i = -1
		for (x in x1..x2)
			for (z in z1..z2)
				regions[++i] = YRegion(world, x, y1, y2, z)
		return regions
	}

	fun z(): Array<ZRegion> {
		@Suppress("UNCHECKED_CAST")  // this will always passed
		val regions = java.lang.reflect.Array.newInstance(ZRegion::class.java,
		                                                  (x2 - x1 + 1) * (y2 - y1 + 1)) as Array<ZRegion>
		var i = -1
		for (x in x1..x2)
			for (y in y1..y2)
				regions[++i] = ZRegion(world, x, y, z1, z2)
		return regions
	}
}
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

import com.wireless4024.mc.bukcore.bridge.toHashMap
import com.wireless4024.mc.bukcore.bridge.toNBTCompound
import de.tr7zw.nbtapi.NBTReflectionUtil
import de.tr7zw.nbtapi.NBTTileEntity
import org.bukkit.block.Block

object BlockNBT {

	fun Block.readNBTMap(): HashMap<String, Any>? {
		if (this.state.javaClass.simpleName == "CraftBlockState") return null
		return NBTTileEntity(this.state).toHashMap()
	}

	fun Block.writeNBTMap(nbt: Map<String, Any>?): Boolean {
		val bs = this.state
		if (nbt == null || nbt.isEmpty() || bs.javaClass.simpleName == "CraftBlockState") return false
		return try {
			NBTReflectionUtil.setTileEntityNBTTagCompound(bs, nbt.toNBTCompound().compound)
			true
		} catch (t: Throwable) {
			t.printStackTrace()
			false
		}
	}
}
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


package com.wireless4024.mc.bukcore.commands

import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.api.KotlinPlugin
import com.wireless4024.mc.bukcore.api.PlayerCommandBase
import com.wireless4024.mc.bukcore.utils.RelativeBlock
import org.bukkit.command.Command
import org.bukkit.entity.Player
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * test command
 */
class Test(override val plugin: KotlinPlugin) : PlayerCommandBase {

	val ref = AtomicReference<RelativeBlock>()
	val data = AtomicInteger(0)

	override fun onCommand(player: Player, command: Command, label: String, args: Array<String>): Boolean {
		val l = player.location
		if (args.size > 0)
			Bukcore.getInstance().enableCommand("randomteleport")
		else
			Bukcore.getInstance().disableCommand("randomteleport")
		/*if (args.firstOrNull()?.startsWith('c') == true || ref.get() == null)
			ref.set(DirectionOffset(0.0, 2.0, 0.0).toLocation(l).toRelativeBlock(l))
		else
			ref.get().place(l)*/
		//sender.sendMessage(BlockUtils.nearestEntity(sender.location, 10, sender)?.toString())
		return true
	}
}
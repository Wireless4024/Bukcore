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

import com.wireless4024.mc.bukcore.utils.Utils.Companion.mapToArray
import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * @since 0.2
 */
object ReflectionUtils {

	private val commands = getFieldValue<SimpleCommandMap>(Bukkit.getServer().pluginManager, "commandMap")
	val commandMap get() = commands!!

	private val pluginCommandCon = PluginCommand::class.java.getDeclaredConstructor(
		String::class.java, Plugin::class.java
	)

	fun newPluginCommand(name: String, owner: Plugin) = pluginCommandCon.run {
		isAccessible = true
		val v = newInstance(name, owner)
		isAccessible = false
		return@run v as PluginCommand
	}

	fun getPrivateField(obj: Any?, fieldName: String): Field? {
		return if (obj == null) null else try {
			var clazz: Class<*>? = if (obj is Class<*>) obj else obj.javaClass
			var field: Field? = clazz!!.getDeclaredField(fieldName)
			while (clazz != null && field == null) {
				clazz = clazz.superclass
				field = clazz.getDeclaredField(fieldName)
			}

			field
		} catch (t: Throwable) {
			null
		}
	}

	fun getPrivateMethod(obj: Any?, methodName: String, vararg type: Class<*>): Method? {
		return if (obj == null) null else try {
			if (obj is Class<*>) obj.getDeclaredMethod(methodName, *type)
			else obj.javaClass.getDeclaredMethod(methodName, *type)
		} catch (t: Throwable) {
			null
		}
	}

	fun getPrivateMethod0(obj: Any?, methodName: String): Method? {
		return if (obj == null) null else try {
			if (obj is Class<*>) obj.getDeclaredMethod(methodName)
			else obj.javaClass.getDeclaredMethod(methodName)
		} catch (t: Throwable) {
			null
		}
	}

	fun getPrivateMethod1(obj: Any?, methodName: String, type1: Class<*>): Method? {
		return if (obj == null) null else try {
			if (obj is Class<*>) obj.getDeclaredMethod(methodName, type1)
			else obj.javaClass.getDeclaredMethod(methodName, type1)
		} catch (t: Throwable) {
			null
		}
	}

	fun getPrivateMethod2(obj: Any?, methodName: String, type1: Class<*>, type2: Class<*>): Method? {
		return if (obj == null) null else try {
			if (obj is Class<*>) obj.getDeclaredMethod(methodName, type1, type2)
			else obj.javaClass.getDeclaredMethod(methodName, type1, type2)
		} catch (t: Throwable) {
			null
		}
	}

	fun <T> getFieldValue(obj: Any, fieldName: String, clazz: Class<*>? = null): T? {
		@Suppress("UNCHECKED_CAST") return getPrivateField(clazz ?: obj, fieldName)?.run {
			isAccessible = true
			val value = get(obj)
			isAccessible = false
			value
		} as T?
	}

	fun <T> callMethod(obj: Any, name: String, vararg args: Any): T? {
		@Suppress("UNCHECKED_CAST") return when (args.size) {
			0 -> getPrivateMethod0(obj, name)
			1 -> getPrivateMethod1(obj, name, args[0].javaClass)
			2 -> getPrivateMethod2(obj, name, args[0].javaClass, args[1].javaClass)
			else -> getPrivateMethod(obj, name, *args.mapToArray { it.javaClass })
		}?.run {
			try {
				isAccessible = true
				return if (returnType == Void.TYPE) {
					invoke(obj, *args)
					null
				} else invoke(obj, *args) as? T
			} finally {
				isAccessible = false
			}

		}
	}
}
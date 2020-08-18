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

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wireless4024.mc.bukcore.Bukcore
import com.wireless4024.mc.bukcore.utils.JsonUtils.LimitedInputStream
import java.io.*
import java.util.concurrent.LinkedBlockingQueue

object JsonUtils {

	private val BAOS_CACHE = LinkedBlockingQueue<ByteArrayOutputStream>()

	val GSON_INSTANCE = Gson()

	fun newByteArrayOutputStream(): ByteArrayOutputStream {
		return BAOS_CACHE.poll() ?: ByteArrayOutputStream()
	}

	fun storeByteArrayOutputStream(baos: ByteArrayOutputStream) {
		if (baos.size() < 4096) { // cache any ByteArrayOutputStream if size less than 4096
			baos.reset()
			BAOS_CACHE.add(baos)
		}
	}

	class LimitedInputStream(private val stream: InputStream, private var limit: Int = 2147483647) : InputStream() {

		override fun read(): Int {
			return if (--limit >= 0) stream.read() else -1
		}
	}
}

/**
 * write [map] into [ObjectOutputStream] in JSON format, can be parse using [ObjectInputStream.readMap] or [ObjectInputStream.readJsonMap]
 */
@Throws(IOException::class)
fun <K, V> ObjectOutputStream.writeJsonMap(map: Map<K, V>?) {
	if (map == null || map.isEmpty()) {
		this.writeInt(0) // null
	} else {
		// reuse ByteArrayOutputStream instance
		val baos = JsonUtils.newByteArrayOutputStream()
		val pstream = PrintStream(baos)
		JsonUtils.GSON_INSTANCE.toJson(map, pstream)
		pstream.flush() // ensure all byte have been written to baos

		// length of json
		this.writeInt(baos.size())
		// write bytes to stream
		baos.writeTo(this)
		// send it back to reuse
		JsonUtils.storeByteArrayOutputStream(baos)
	}
}

/**
 * read [Map] from [ObjectOutputStream] that written by [ObjectOutputStream.writeJsonMap].
 * this method will capture generic type and parse it correctly
 */
@Throws(IOException::class)
inline fun <reified K, reified V> ObjectInputStream.readJsonMap(): Map<K, V>? {
	val length = this.readInt()
	if (length == 0) return null
	val type = object : TypeToken<Map<K, V>>() {}.type
	return JsonUtils.GSON_INSTANCE.fromJson(InputStreamReader(LimitedInputStream(this, length)), type)
}

/**
 * read [Map] from [ObjectOutputStream] that written by [ObjectOutputStream.writeJsonMap].
 * if result produce a [ClassCastException] during runtime please use [ObjectInputStream.readJsonMap] instead
 * @return Map<String, V>
 */
@Throws(IOException::class)
@Suppress("UNCHECKED_CAST")
fun <V> ObjectInputStream.readMap(): Map<String, V>? {
	val length = this.readInt()
	if (length == 0) return null
	Bukcore.getInstance().warning(length)
	return JsonUtils.GSON_INSTANCE.fromJson(InputStreamReader(LimitedInputStream(this, length)),
	                                        Map::class.java) as Map<String, V>

}
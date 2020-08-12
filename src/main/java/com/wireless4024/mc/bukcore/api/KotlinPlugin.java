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

package com.wireless4024.mc.bukcore.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * extended {@link JavaPlugin} with kotlin features
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
public abstract class KotlinPlugin extends JavaPlugin {
	/**
	 * log level [{@link java.util.logging.Level#INFO}]
	 *
	 * @param message message to log
	 */
	public void info(Object message) {
		if (((boolean) this.get("debug"))) getLogger().info(String.valueOf(message));
	}

	/**
	 * log level [{@link java.util.logging.Level#WARNING}]
	 *
	 * @param message message to log
	 */
	public void warning(Object message) {
		getLogger().warning(String.valueOf(message));
	}

	/**
	 * get config by path
	 *
	 * @param path path to get config
	 *
	 * @return object
	 */
	public Object get(String path) {
		return getConfig().get(path);
	}

	/**
	 * get config by path
	 *
	 * @param path path to get config
	 * @param def  default value
	 *
	 * @return object
	 */
	public Object get(String path, Object def) {
		return getConfig().get(path, def);
	}

	/**
	 * Calls a method on the main thread and returns a Future object.
	 * This task will be executed by the main server thread.
	 *
	 * @param task Task to be executed
	 * @param <T>  The callable's return type
	 *
	 * @return Future object related to the task
	 *
	 * @see org.bukkit.scheduler.BukkitScheduler#callSyncMethod(Plugin, Callable)
	 */
	public <T> Future<T> call(Callable<T> task) {
		if (Bukkit.isPrimaryThread()) {
			return new Future<T>() {
				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					return false;
				}

				@Override
				public boolean isCancelled() {
					return false;
				}

				@Override
				public boolean isDone() {
					return true;
				}

				@Override
				public T get() throws InterruptedException, ExecutionException {
					try {
						return task.call();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public T get(long timeout, @NotNull TimeUnit unit)
						throws InterruptedException, ExecutionException, TimeoutException {
					try {
						return task.call();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			};
		} return Bukkit.getScheduler().callSyncMethod(this, task);
	}

	/**
	 * Schedules a task in the Bukkit scheduler to run on next tick.
	 *
	 * @param job a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTask(Plugin)
	 */
	public BukkitTask runTask(Runnable job) {
		return Bukkit.getScheduler().runTask(this, job);
	}

	/**
	 * @param job a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see #runTask(Runnable)
	 */
	public BukkitTask invoke(Runnable job) {
		return Bukkit.getScheduler().runTask(this, job);
	}

	/**
	 * Schedules a task to run after the specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task
	 * @param job   a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTaskLater(Plugin, long)
	 */
	public BukkitTask runTask(long delay, Runnable job) {
		return Bukkit.getScheduler().runTaskLater(this, job, delay);
	}


	/**
	 * @return a BukkitTask that contains the id number
	 *
	 * @see #runTask(long, Runnable)
	 */
	public BukkitTask invoke(long delay, Runnable job) {
		return Bukkit.getScheduler().runTaskLater(this, job, delay);
	}

	/**
	 * Schedules a task in the Bukkit scheduler to run asynchronously.
	 *
	 * @param job a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTaskAsynchronously(Plugin)
	 */
	public BukkitTask runAsync(Runnable job) {
		return Bukkit.getScheduler().runTaskAsynchronously(this, job);
	}

	/**
	 * Schedules a task to run asynchronously after the specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task
	 * @param job   a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTaskLaterAsynchronously(Plugin, long)
	 */
	public BukkitTask runAsync(long delay, Runnable job) {
		return Bukkit.getScheduler().runTaskLaterAsynchronously(this, job, delay);
	}

	/**
	 * Schedules a task to repeatedly run until cancelled,
	 * starting after the specified number of server ticks.
	 *
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @param job    a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTaskTimer(Plugin, long, long)
	 */
	public BukkitTask runTimer(long delay, long period, Runnable job) {
		return Bukkit.getScheduler().runTaskTimer(this, job, delay, period);
	}

	/**
	 * Schedules a task to repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 *
	 * @param delay  the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @param job    a task to run
	 *
	 * @return a BukkitTask that contains the id number
	 *
	 * @see org.bukkit.scheduler.BukkitRunnable#runTaskTimerAsynchronously(Plugin, long, long)
	 */
	public BukkitTask runTimerAsync(long delay, long period, Runnable job) {
		return Bukkit.getScheduler().runTaskTimerAsynchronously(this, job, delay, period);
	}
}
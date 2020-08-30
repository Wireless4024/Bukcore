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

import com.wireless4024.mc.bukcore.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * extended {@link JavaPlugin} with kotlin features
 *
 * @author Wireless4024
 * @version 0.1
 * @since 0.1
 */
public abstract class KotlinPlugin extends JavaPlugin {
	private HashMap<String, Command> commands = new HashMap<String, Command>(16, 1f);

	private static String str(Object o) {
		return o instanceof Object[] ?
		       Arrays.deepToString((Object[]) o) :
		       o instanceof String ? (String) o : String.valueOf(o);
	}

	/**
	 * log level [{@link java.util.logging.Level#FINE}]
	 *
	 * @param message message to log
	 */
	public void log(Object message) {
		if (((boolean) this.get("debug"))) getLogger().fine(str(message));
	}

	/**
	 * log level [{@link java.util.logging.Level#INFO}]
	 *
	 * @param message message to log
	 */
	public void info(Object message) {
		getLogger().info(str(message));
	}

	/**
	 * log level [{@link java.util.logging.Level#WARNING}]
	 *
	 * @param message message to log
	 */
	public void warning(Object message) {
		getLogger().warning(str(message));
	}

	/**
	 * get file in config folder
	 *
	 * @param path filename
	 *
	 * @return File object in config folder
	 */
	public File getFile(String path) {
		return new File("plugins" + getName() + path);
	}

	public void enableCommand(String name) {
		name = name.toLowerCase();
		Command cm = commands.get(name);
		if (cm != null) {
			//unregisterCommand(name);
			registerCommand0(name, cm);
		}
	}

	public void disableCommand(String name) {
		name = name.toLowerCase();
		Command cm = commands.get(name);
		if (cm != null) {
			unregisterCommand(name);
			//registerCommand0(name, DisabledCommand.INSTANCE.getInstance());
		}
	}

	public void registerCommand(String name, Command command) {
		name = name.toLowerCase();
		if (command == null) return;
		if (!commands.containsKey(name)) commands.put(name, command);
		if (!command.isRegistered()) {
			SimpleCommandMap c = ReflectionUtils.INSTANCE.getCommandMap();
			command.register(c);
			c.register(getName().toLowerCase(), command);
		}

	}

	private void registerCommand0(String name, Command command) {
		name = name.toLowerCase();
		SimpleCommandMap c = ReflectionUtils.INSTANCE.getCommandMap();
		command.register(c);
		c.register(name, getName().toLowerCase(), command);
	}

	public boolean unregisterCommand(String name) {
		name = name.toLowerCase();
		Command command = commands.get(name);
		if (command == null) return false;
		command.unregister(null);

		final SimpleCommandMap c = ReflectionUtils.INSTANCE.getCommandMap();

		final HashMap<String, Command> kc = ReflectionUtils.INSTANCE.getFieldValue(c, "knownCommands");
		final String n = command.getName(), l = command.getLabel();
		final String prefix = getName().toLowerCase() + ":";
		PluginCommand cmm = null;
		cmm = (PluginCommand) kc.remove(n);
		if (cmm != null && cmm.getPlugin() != this) {
			kc.put(n, cmm);
		}
		kc.remove(l);

		for (String a : command.getAliases()) {
			cmm = (PluginCommand) kc.remove(a);
			if (cmm != null && cmm.getPlugin() != this) kc.put(a, cmm);
			cmm = (PluginCommand) kc.remove(prefix + a);
			if (cmm != null && cmm.getPlugin() != this) kc.put(a, cmm);
		}
		return command.unregister(null);
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
		}
		return Bukkit.getScheduler().callSyncMethod(this, task);
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

/**
 * continue calling a task until return false
 *
 * @param period delay between each call in tick
 * @param job    a task to run
 *//*

	public void lazyRun(final long period, final BooleanCallable job) {
		runTask(period, ()->{
			if (!job.call()) lazyRun(period, job);
		});
	}
	*/
/**
 * continue calling a task asynchronously until return false
 *
 * @param period delay between each call in tick
 * @param job    a task to run
 *//*

	public void lazyRunAsync(final long period, final BooleanCallable job) {
		runAsync(period, ()->{
			if (!job.call()) lazyRunAsync(period, job);
		});
	}
	interface BooleanCallable {
		boolean call();
	}
*/

/*
plugin.lazyRun<Int>(1L, 0) { value, cancel ->
	if (value == 10) cancel.set(true) // run 10 times
	value + 1
}
 */

	/**
	 * continue calling a task until cancel is true
	 *
	 * @param period delay between each call in tick
	 * @param supply initial value
	 * @param job    a task to run
	 */
	public <T> void lazyRun(final long period, final T supply, final BooleanFunction<T> job) {
		AtomicBoolean cancel = new AtomicBoolean(false);
		runTask(period, ()->{
			T value = job.apply(supply, cancel);
			if (!cancel.get()) lazyRun(period, value, job);
		});
	}

	/**
	 * continue calling a task asynchronously until return false
	 *
	 * @param period delay between each call in tick
	 * @param supply initial value
	 * @param job    a task to run
	 */
	public <T> void lazyRunAsync(final long period, final T supply, final BooleanFunction<T> job) {
		AtomicBoolean cancel = new AtomicBoolean(false);
		runAsync(period, ()->{
			T value = job.apply(supply, cancel);
			if (!cancel.get()) lazyRunAsync(period, value, job);
		});
	}

	interface BooleanFunction<T> {
		T apply(T value, AtomicBoolean cancel);
	}
}
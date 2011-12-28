package com.wolvereness.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * @author Wolfe
 *
 */
public final class NameCollection {
	@SuppressWarnings("javadoc")
	public static class OutOfEntriesException extends Exception {
		private static final long serialVersionUID = -5748952116796011173L;
		public OutOfEntriesException() {
		}
		public OutOfEntriesException(
										final String arg0) {
			super(arg0);
		}
		public OutOfEntriesException(
										final String arg0,
										final Throwable arg1) {
			super(arg0, arg1);
		}
		public OutOfEntriesException(
										final Throwable arg0) {
			super(arg0);
		}
	}
	private static YamlConfiguration config = new YamlConfiguration();
	private static HashMap<String, File> plugins = new HashMap<String, File>();
	static {
		config.options().copyDefaults(true);
	}
	private static Configuration getConfig(final File file) {
		if(file.exists())
			return YamlConfiguration.loadConfiguration(file);
		return null;
	}
	/**
	 * This method registers the said name (if needed) and will return a sign-safe length name.
	 * @param name Name to consider
	 * @return Sign-safe name
	 * @throws OutOfEntriesException if there are already 62 players with 16 character names and the same first 14 characters (insanity)
	 */
	public static String getSignName(final String name) throws OutOfEntriesException {
		if(name.length() != 16) return name;
		if(config.isSet(name)) return (String) config.get(name);
		final String truncatedName = name.substring(0, 13);
		char lastChar = '0';
		if(config.isSet(truncatedName)) {
			lastChar = String.valueOf(config.get(truncatedName)).charAt(0);
			if(lastChar == '9') {
				lastChar = 'a';
			} else if (lastChar == 'z') {
				lastChar = 'A';
			} else if (lastChar == 'Z')
				throw new OutOfEntriesException(name);
			else {
				lastChar += 1;
			}
		}
		config.set(truncatedName, Character.toString(lastChar));
		final String signName = truncatedName + '~' + lastChar;
		config.set(name, signName);
		config.set(signName, name);
		saveAll();
		return signName;
	}
	private static void load(final File file) {
		final Configuration pluginConfig = getConfig(file);
		if(pluginConfig != null) {
			final Set<String> keys = pluginConfig.getKeys(false);
			final Map<String, Map<String, String>> nameSets = new HashMap<String, Map<String, String>>();
			for(final String key : keys) {
				if(key.length() != 16) {
					continue;
				}
				final String truncatedName = key.substring(0, 13);
				if(!nameSets.containsKey(truncatedName)) {
					nameSets.put(truncatedName, new HashMap<String,String>());
				}
				nameSets.get(truncatedName).put(key, (String) pluginConfig.get(key));
			}
			for(final Map.Entry<String, Map<String, String>> nameSet : nameSets.entrySet()) {
				char largest = '0';
				for(final Map.Entry<String, String> player : nameSet.getValue().entrySet()) {
					largest = max(largest, player.getValue().charAt(14));
				}
				final char oldLargest = config.isSet(nameSet.getKey()) ? String.valueOf(config.get(nameSet.getKey())).charAt(0) : '0';
				if(max(largest,oldLargest) != oldLargest) {
					config.set(nameSet.getKey(), String.valueOf(largest));
					for(final Map.Entry<String, String> player : nameSet.getValue().entrySet()) {
						config.set(player.getKey(), player.getValue());
						config.set(player.getValue(), player.getKey());
					}
				}
			}
		}
	}
	/**
	 * Checks to see if the two names match (considering extended sign names)
	 * @param signName The name from the sign
	 * @param name The name of the player
	 * @return true if the signName matches said player
	 */
	public static boolean matches(final String signName, final String name) {
		return
			signName.length() == 15
				&& signName.charAt(13) == '~'
			?
				name.equals(config.get(signName))
			:
				signName.equals(name);
	}
	private static char max(final char c1, final char c2) {
		if(c1 <= '9') {
			if(c2 > c1) return c2;
			return c1;
		}
		if(c2 <= '9') return c1;
		if(c1 >= 'a') {
			if (c2 > c1) return c2;
			if (c2 < 'a') return c2;
			return c1;
		}
		if(c2 >= 'a') return c2;
		if(c1 > c2) return c1;
		return c2;
	}
	/**
	 * Registers a plugin with the name collection, creating a file in the plugin's folder
	 * @param plugin Plugin to register with name collection.
	 */
	public static void registerPlugin(final Plugin plugin) {
		final String pluginName = plugin.getDescription().getName();
		final File pluginFile = new File("plugins" + File.separator + pluginName + File.separator + "NameCollection.yml");
		plugins.put(pluginName, pluginFile);
		load(pluginFile);
		saveAll();
	}
	/**
	 * Use this method to reload configurations from all associated plugins.
	 */
	public static void reloadAll() {
		for(final Map.Entry<String, File> plugin : plugins.entrySet()) {
			load(plugin.getValue());
		}
		saveAll();
	}
	private static void save(final File file) {
		try {
			config.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	private static void saveAll() {
		for(final Map.Entry<String, File> entry : plugins.entrySet()) {
			save(entry.getValue());
		}
	}
	/**
	 * @param plugin plugin to unregister
	 */
	public static void unregisterPlugin(final Plugin plugin) {
		final String pluginName = plugin.getDescription().getName();
		if(!plugins.containsKey(pluginName)) throw new IllegalArgumentException(plugin+" not registered.");
		plugins.remove(pluginName);
		if(plugins.isEmpty()) {
			config = new YamlConfiguration();
		}
	}
}

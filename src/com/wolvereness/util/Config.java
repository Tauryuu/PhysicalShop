package com.wolvereness.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Parent class for config implementations
 *
 * @author Wolfe
 *
 */
public abstract class Config {
	private static final Logger logger = Logger.getLogger("Minecraft");
	/**
	 * Makes file f if it does not exist
	 *
	 * @param f file to assure it exists
	 * @return true if the file already existed
	 */
	public static final boolean makeFile(final File f) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (final java.io.IOException ex) {
				ex.printStackTrace();
			}
			return false;
		}
		return true;
	}
	private final FileConfiguration configuration;
	/**
	 * The plugin directory
	 */
	public final String directory = "plugins" + File.separator + getName();
	private final File file;
	/**
	 * Allows for temporary instances of config. Should NOT be called, use standard constructor with null parameter instead.
	 */
	@Deprecated
	protected Config() {
		configuration = null;
		file = null;
	}
	/**
	 * Creates a config in the standard config directory
	 *
	 * @param fileName Name of the file, or null if you wish for an inactive config
	 * @param defaultYaml the stream to create the default yaml from
	 */
	public Config(final String fileName, final InputStream defaultYaml) {
		if (fileName == null) {
			configuration = null;
			file = null;
			return;
		}
		configuration = config(file = new File(directory + File.separator + fileName), defaultYaml);
		defaults();
		save();
	}
	/**
	 * Creates a config in the subdirectory for standard configs.
	 *
	 * @param subDirectory the subdirectory in the plugin folder
	 * @param fileName the filename to attempt to load and save to
	 * @param defaultYaml the stream to create the default yaml from
	 */
	public Config(final String subDirectory, final String fileName, final InputStream defaultYaml){
		configuration = config(file = new File(directory + File.separator + subDirectory + File.separator + fileName),defaultYaml);
		defaults();
		save();
	}

	private final FileConfiguration config(final File f, final InputStream defaultYaml) {
		FileConfiguration configuration = null;
		if(f.exists()) {
			configuration = YamlConfiguration.loadConfiguration(f);
		} else {
			configuration = new YamlConfiguration();
		}
		if(defaultYaml != null) {
			configuration.setDefaults(YamlConfiguration.loadConfiguration(defaultYaml));
			configuration.options().copyDefaults(true);
		}
		return configuration;
	}
	/**
	 * Should set all defaults of the current config. This is called before the object is initialized, so you should not reference any local variables whatsoever.
	 */
	protected abstract void defaults();
	/**
	 * @param section Section to use
	 * @param node Name of the node
	 * @return String.valueOf of whatever is at that node
	 */
	protected String get(final ConfigurationSection section, final String node) {
		return String.valueOf(section.get(node));
	}
	/**
	 * @param section Section to use
	 * @param node Name of the node
	 * @return A parsed byte from that node
	 */
	protected byte getByte(final ConfigurationSection section, final String node) {
		try {
			return Byte.parseByte(get(section,node));
		} catch (final NumberFormatException e) {
			logWarning(section.getCurrentPath() + "." + node + " \"" + get(section,node) + "\" is not valid");
			throw e;
		}
	}
	/**
	 * @return the configuration for this config
	 */
	protected final Configuration getConfig() {
		return configuration;
	}
	/**
	 * @param section Section to use
	 * @param node Name of the node
	 * @return A parsed double from that node
	 */
	protected double getDouble(final ConfigurationSection section, final String node) {
		try {
			return Double.parseDouble(get(section,node));
		} catch (final NumberFormatException e) {
			logWarning(section.getCurrentPath() + "." + node + " \"" + get(section,node) + "\" is not valid");
			throw e;
		}
	}
	/**
	 * @param section Section to use
	 * @param node Name of the node
	 * @return A parsed integer from that node
	 */
	protected int getInt(final ConfigurationSection section, final String node) {
		try {
			return Integer.parseInt(get(section,node));
		} catch (final NumberFormatException e) {
			logWarning(section.getCurrentPath() + "." + node + " \"" + get(section,node) + "\" is not valid");
			throw e;
		}
	}
	/**
	 * @param node the node to get keys of
	 * @return A set of key names for node, or null if invalid or not found
	 */
	protected final Set<String> getKeys(final String node) {
		final ConfigurationSection section = configuration.getConfigurationSection(node);
		if(section == null) return null;
		return section.getKeys(false);
	}

	/**
	 * Simply the name of the folder for the plugin. This is called before the object is initialized, so you should not reference any local variables whatsoever.
	 * @return The name of the plugin.
	 */
	public abstract String getName();
	/**
	 * @param section Section to retrieve pattern from
	 * @param node Node to read from
	 * @return A compiled pattern.
	 */
	protected Pattern getPattern(final ConfigurationSection section, final String node) {
		final Object pattern = section.get(node);
		try {
			if(pattern != null)
				return Pattern.compile(pattern.toString());
			throw new NullPointerException(section.getCurrentPath() + "." + node + " is null");
		} catch (final PatternSyntaxException e) {
			logWarning(section.getCurrentPath() + "." + node + " \"" + pattern + "\" is not valid");
			throw e;
		}
	}
	/**
	 * Sends txt to console, log level INFO
	 *
	 * @param txt message to log
	 */
	public void log(final String txt) {
		logger.log(Level.INFO, String.format("[%s] %s", getName(), txt));
	}
	/**
	 * Sends txt to console, log level SERVERE
	 *
	 * @param txt Message to log
	 */
	public void logSevere(final String txt) {
		logger.log(Level.SEVERE, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Sends txt to console, log level WARNING
	 *
	 * @param txt message to log
	 */
	public void logWarning(final String txt) {
		logger.log(Level.WARNING, String.format("[%s] %s", getName(), txt));
	}
	/**
	 * Saves the configuration to the original file.
	 */
	protected final void save() {
		try {
			configuration.save(file);
		} catch (final IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to write config to " + file + " caused by " + e);
		}
	}
}

package com.wolvereness.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;

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
	 * @return the configuration for this config
	 */
	protected final Configuration getConfig() {
		return configuration;
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

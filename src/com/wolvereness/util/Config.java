package com.wolvereness.util;


import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Parent class for config implementations
 * 
 * @author Wolfe
 * 
 */
public abstract class Config {
	public final String directory = "plugins" + File.separator + getName();
	private final Configuration configuration;

	/**
	 * Creates a config in the subdirectory for standard configs.
	 * 
	 * @param subDirectory
	 * @param fileName
	 */
	public Config(String subDirectory, String fileName){
		new File(directory).mkdir();
		new File(directory + File.separator + subDirectory).mkdir();
		File file = new File(directory + File.separator + subDirectory + File.separator + fileName);
		makeFile(file);
		configuration = YamlConfiguration.loadConfiguration(file);
		defaults();
		try {
			((FileConfiguration) configuration).save(file);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to write config to " + fileName + " caused by " + e);
		}
	}
	/**
	 * Simply the name of the folder for the plugin. This is called before the object is initialized, so you should not reference any local variables whatsoever.
	 * @return The name of the plugin.
	 */
	public abstract String getName();

	/**
	 * Creates a config in the standard config directory
	 * 
	 * @param fileName Name of the file, or null if you wish for an inactive config
	 */
	public Config(String fileName) {
		if (fileName == null) {
			configuration = null;
			return;
		}
		new File(directory).mkdir();
		File file = new File(directory + File.separator + fileName);
		makeFile(file);
		configuration = YamlConfiguration.loadConfiguration(file);
		defaults();
		try {
			((FileConfiguration) configuration).save(file);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to write config to " + fileName + " caused by " + e);
		}
	}

	/**
	 * Allows for temporary instances of config. Should NOT be called, use standard constructor with null parameter instead.
	 */
	@Deprecated
	protected Config() {
		configuration = null;
	}

	/**
	 * Makes file f if it does not exist
	 * 
	 * @param f
	 */
	public static final boolean makeFile(File f) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (java.io.IOException ex) {
				ex.printStackTrace();
			}
			return false;
		}
		return true;
	}
	/**
	 * Should set all defaults of the current config. This is called before the object is initialized, so you should not reference any local variables whatsoever.
	 */
	public abstract void defaults();

	protected final Configuration getConfig() {
		return configuration;
	}
	protected final Set<String> getKeys(String node) {
		ConfigurationSection section = configuration.getConfigurationSection(node);
		if(section == null) return null;
		return section.getKeys(false);
	}
}

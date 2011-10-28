package com.wolvereness.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	private final File file;
	private final FileConfiguration configuration;

	/**
	 * Creates a config in the subdirectory for standard configs.
	 * 
	 * @param subDirectory
	 * @param fileName
	 */
	public Config(String subDirectory, String fileName, InputStream defaultYaml){
		configuration = config(file = new File(directory + File.separator + subDirectory + File.separator + fileName),defaultYaml);
		defaults();
		save();
	}
	/**
	 * Creates a config in the standard config directory
	 * 
	 * @param fileName Name of the file, or null if you wish for an inactive config
	 */
	public Config(String fileName, InputStream defaultYaml) {
		if (fileName == null || defaultYaml == null) {
			configuration = null;
			file = null;
			return;
		}
		configuration = config(file = new File(directory + File.separator + fileName), defaultYaml);
		defaults();
		save();
	}
	private final FileConfiguration config(File f, InputStream defaultYaml) {
		FileConfiguration configuration = null;
		if(f.exists()) {
			configuration = YamlConfiguration.loadConfiguration(f);
		} else {
			configuration = new YamlConfiguration();
		}
		configuration.setDefaults(YamlConfiguration.loadConfiguration(defaultYaml));
		configuration.options().copyDefaults(true);
		return configuration;
	}
	/**
	 * Simply the name of the folder for the plugin. This is called before the object is initialized, so you should not reference any local variables whatsoever.
	 * @return The name of the plugin.
	 */
	public abstract String getName();
	
	/**
	 * Allows for temporary instances of config. Should NOT be called, use standard constructor with null parameter instead.
	 */
	@Deprecated
	protected Config() {
		configuration = null;
		file = null;
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
	 * @return 
	 */
	public abstract void defaults();
	/**
	 * Saves the configuration to the original file.
	 */
	protected final void save() {
		try {
			configuration.save(file);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.WARNING, "Failed to write config to " + file + " caused by " + e);
		}
	}

	protected final Configuration getConfig() {
		return configuration;
	}
	protected final Set<String> getKeys(String node) {
		ConfigurationSection section = configuration.getConfigurationSection(node);
		if(section == null) return null;
		return section.getKeys(false);
	}
}

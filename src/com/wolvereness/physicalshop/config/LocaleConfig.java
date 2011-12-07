package com.wolvereness.physicalshop.config;

import java.io.InputStream;

import com.wolvereness.util.Config;

/**
 * Class controlling the localization.
 *
 * @author Wolfe
 *
 */
public class LocaleConfig extends Config {
	private static final String subDirectory = "Locales";

	private static InputStream getLanguageFile(final String language, final ClassLoader cl) {
		final InputStream lin = cl.getResourceAsStream(subDirectory + '/' + language + ".yml");
		if (lin == null) return cl.getResourceAsStream(subDirectory+"/ENGLISH.yml");
		return lin;
	}

	/**
	 * Takes the specified language and searches for the corresponding file
	 * using the ClassLoader cl.
	 *
	 * @param language language to load
	 * @param cl the loader to get default config
	 */
	public LocaleConfig(final String language, final ClassLoader cl) {
		super(subDirectory, language + ".yml", getLanguageFile(language, cl));
	}

	/**
	 * Accesses all current defaults.
	 */
	@Override
	public void defaults() {
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}

	/**
	 * Returns the phrase associated with message.
	 *
	 * @param message name of the phrase to retrieve
	 * @return a phrase in the language set in the config
	 * @throws NoSuchFieldException if phrase doesn't exist
	 */
	public String getPhrase(final String message) throws NoSuchFieldException {
		final Object s = getConfig().get(message);
		if (s == null)
			throw new NoSuchFieldException(message + " not found.");
		return s.toString();
	}

}

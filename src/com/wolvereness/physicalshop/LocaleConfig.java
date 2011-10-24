package com.wolvereness.physicalshop;

import java.io.InputStream;

import com.wolvereness.util.Config;

/**
 * Class controlling the localization.
 * 
 * @author Wolfe
 * 
 */
// @Deprecated
public class LocaleConfig extends Config {
	private static final String subDirectory = "Locales";

	/**
	 * Takes the specified language and searches for the corresponding file
	 * using the ClassLoader cl.
	 * 
	 * @param language
	 * @param cl
	 */
	public LocaleConfig(String language, ClassLoader cl) {
		super(subDirectory, language + ".yml", getLanguageFile(language, cl));
	}

	/**
	 * Accesses all current defaults.
	 * @return 
	 */
	@Override
	public void defaults() {
	}

	private static InputStream getLanguageFile(String language, ClassLoader cl) {
		InputStream lin = cl.getResourceAsStream(subDirectory + '/' + language + ".yml");
		if (lin == null) {
			return cl.getResourceAsStream(subDirectory+"/ENGLISH.yml");
		} else {
			return lin;
		}
	}

	/**
	 * Returns the phrase associated with message.
	 * 
	 * @param message
	 * @return
	 * @throws NoSuchFieldException
	 */
	public String getPhrase(String message) throws NoSuchFieldException {
		Object s = getConfig().get(message);
		if (s == null)
			throw new NoSuchFieldException(message + " not found.");
		return s.toString();
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}

}

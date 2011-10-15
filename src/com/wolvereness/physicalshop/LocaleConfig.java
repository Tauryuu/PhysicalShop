package com.wolvereness.physicalshop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.wolvereness.util.Config;

/**
 * Class controlling the localization.
 * 
 * @author Wolfe
 * 
 */
// @Deprecated
public class LocaleConfig extends Config {
	private static Map<String, String> defaultPhrases = new HashMap<String, String>(25);
	static {
		defaultPhrases.put("STATUS", "The shop contains %1$d %2$s, %3$d %4$s, and %5$d %6$s");
		defaultPhrases.put("STATUS_ONE_CURRENCY", "The shop contains %1$d %2$s and %3$d %4$s");
		defaultPhrases.put("CANT_DESTROY", "You are not allowed to destroy this shop");
		defaultPhrases.put("CANT_USE", "You are not allowed to use shops");
		defaultPhrases.put("CANT_BUILD", "You are not allowed to build shops");
		defaultPhrases.put("CANT_BUILD_SERVER", "You are not allowed to build server shops");
		defaultPhrases.put("CANT_USE_CHEST", "You are not allowed to use this shop chest");
		defaultPhrases.put("CANT_PLACE_CHEST", "You are not allowed to place this chest");
		defaultPhrases.put("DEPOSIT", "You deposited %1$d %2$s and %3$d %4$s");
		defaultPhrases.put("WITHDRAW", "You withdrew %1$d %2$s and %3$d %4$s");
		defaultPhrases.put("NO_BUY", "You can't buy at this shop");
		defaultPhrases.put("BUY", "You bought %1$d %2$s for %3$d %4$s");
		defaultPhrases.put("BUY_RATE", "You can buy %1$d %2$s for %3$d %4$s");
		defaultPhrases.put("SELL_RATE", "You can sell %1$d %2$s for %3$d %4$s");
		defaultPhrases.put("NO_SELL", "You can't sell at this shop");
		defaultPhrases.put("SELL", "You sold %1$d %2$s for %3$d %4$s");
		defaultPhrases.put("NOT_ENOUGH_PLAYER_MONEY", "You don't have enough %1$s");
		defaultPhrases.put("NOT_ENOUGH_PLAYER_ITEMS", "You don't have enough %1$s");
		defaultPhrases.put("NOT_ENOUGH_SHOP_MONEY", "The shop doesn't have enough %1$s");
		defaultPhrases.put("NOT_ENOUGH_SHOP_ITEMS", "The shop doesn't have enough %1$s");
		defaultPhrases.put("CHEST_INVENTORY_FULL", "The shop is full");
		defaultPhrases.put("PLAYER_INVENTORY_FULL", "Your inventory is full");
		defaultPhrases.put("EXISTING_CHEST", "You can't build a shop over an existing chest");
		defaultPhrases.put("CANT_RELOAD_CONFIG", "You are not allowed to reload the configuration");
		defaultPhrases.put("CONFIG_RELOADED", "The configuration has been reloaded");
	}
	private Map<String, String> phrases;
	// private Field[] fields = this.getClass().getFields();
	private static final String subDirectory = "Locales";

	/**
	 * Takes the specified language and searches for the corresponding file
	 * using the ClassLoader cl.
	 * 
	 * @param language
	 * @param cl
	 */
	public LocaleConfig(String language, ClassLoader cl) {
		super(subDirectory, quickCheck(language.toUpperCase() + ".yml", cl));
	}

	/**
	 * Accesses all current defaults.
	 */
	@Override
	public void defaults() {
		if (phrases == null) phrases = new HashMap<String, String>(defaultPhrases);
		for (Map.Entry<String, String> e : phrases.entrySet()) {
			e.setValue(getConfig().getString(e.getKey(), e.getValue()));
		}
		//getConfig().
		/*
		 * Field[] fields = getClass().getFields(); LocaleConfig empty = new
		 * LocaleConfig(); for(Field f : fields) { try { f.set(this,
		 * getConfig().getString(f.getName(), String.valueOf(f.get(empty)))); }
		 * catch (IllegalArgumentException e) { e.printStackTrace(); } catch
		 * (IllegalAccessException e) { e.printStackTrace(); } } //
		 */
	}

	private static String quickCheck(String language, ClassLoader cl) {
		new File("plugins" + File.separator + "PhysicalShop" + File.separatorChar + subDirectory).mkdir();
		File f = new File("plugins" + File.separator + "PhysicalShop" + File.separatorChar + subDirectory + File.separatorChar + language);
		if (makeFile(f))
			return language;
		try {
			FileOutputStream fout = new FileOutputStream(f);
			int b = 0;
			InputStream lin = cl.getResourceAsStream(subDirectory + '/' + language);
			if (lin == null) {
				PhysicalShop.logSevere("Language file: " + language	+ " not found, file with English values created.");
				return language;
			}
			while ((b = lin.read()) != -1)
				fout.write(b);
			lin.close();
			fout.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return language;
	}

	/**
	 * Returns the phrase associated with message.
	 * 
	 * @param message
	 * @return
	 * @throws NoSuchFieldException
	 */
	public String getPhrase(String message) throws NoSuchFieldException {
		String s = phrases.get(message);
		if (s == null && !phrases.containsKey(message))
			throw new NoSuchFieldException(message + " not found.");
		return s;
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}

}

/**
 * 
 */
package com.wolvereness.physicalshop;


/**
 * Over arching class for messaging players.
 * 
 * @author Wolfe
 * 
 */
@Deprecated
public class Messager {
	public final LocaleConfig lc;

	/**
	 * Constructor, using specified language. ClassLoader should be able to find
	 * default language yml file in the jar file.
	 * 
	 * @param language
	 * @param cl
	 */
	public Messager(String language, ClassLoader cl) {
		lc = new LocaleConfig(language, cl);
	}

	public String getMessage(String message) {
		// return lc.getPhrase(message);
		/*
		 * try { return String.valueOf(lc.getClass().getField(message).get(lc));
		 * } catch (IllegalArgumentException e) { e.printStackTrace(); } catch
		 * (SecurityException e) { e.printStackTrace(); } catch
		 * (IllegalAccessException e) { e.printStackTrace(); } catch
		 * (NoSuchFieldException e) { e.printStackTrace(); } //
		 */
		return "";
	}

	// private static class LocaleConfig extends Config
	// {
	// @SuppressWarnings("unused")
	// public String STATUS = "The shop contains %1$d %2$s and %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String CANT_DESTROY = "You are not allowed to destroy this shop";
	// @SuppressWarnings("unused")
	// public String CANT_USE = "You are not allowed to use shops";
	// @SuppressWarnings("unused")
	// public String CANT_BUILD = "You are not allowed to build shops";
	// @SuppressWarnings("unused")
	// public String CANT_BUILD_SERVER =
	// "You are not allowed to build server shops";
	// @SuppressWarnings("unused")
	// public String CANT_USE_CHEST =
	// "You are not allowed to use this shop chest";
	// @SuppressWarnings("unused")
	// public String CANT_PLACE_CHEST =
	// "You are not allowed to place this chest";
	// @SuppressWarnings("unused")
	// public String DEPOSIT = "You deposited %1$d %2$s and %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String WITHDRAW = "You withdrew %1$d %2$s and %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String NO_BUY = "You can't buy at this shop";
	// @SuppressWarnings("unused")
	// public String BUY = "You bought %1$d %2$s for %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String BUY_RATE = "You can buy %1$d %2$s for %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String SELL_RATE = "You can sell %1$d %2$s for %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String NO_SELL = "You can't sell at this shop";
	// @SuppressWarnings("unused")
	// public String SELL = "You sold %1$d %2$s for %3$d %4$s";
	// @SuppressWarnings("unused")
	// public String NOT_ENOUGH_PLAYER_MONEY = "You don't have enough %1$s";
	// @SuppressWarnings("unused")
	// public String NOT_ENOUGH_PLAYER_ITEMS = "You don't have enough %1$s";
	// @SuppressWarnings("unused")
	// public String NOT_ENOUGH_SHOP_MONEY =
	// "The shop doesn't have enough %1$s";
	// @SuppressWarnings("unused")
	// public String NOT_ENOUGH_SHOP_ITEMS =
	// "The shop doesn't have enough %1$s";
	// @SuppressWarnings("unused")
	// public String CHEST_INVENTORY_FULL = "The shop is full";
	// @SuppressWarnings("unused")
	// public String PLAYER_INVENTORY_FULL = "Your inventory is full";
	// @SuppressWarnings("unused")
	// public String EXISTING_CHEST =
	// "You can't build a shop over an existing chest";
	// //private Field[] fields = this.getClass().getFields();
	// private static final String subDirectory = "Locales";
	//
	// /**
	// * Takes the specified language and searches for the corresponding file
	// using the ClassLoader cl.
	// * @param language
	// * @param cl
	// */
	// public LocaleConfig(String language, ClassLoader cl) {
	// super(subDirectory, quickCheck(language + ".yml", cl));
	// }
	// private LocaleConfig()
	// {
	// super();
	// }
	//
	// /**
	// * Accesses all class defaults.
	// */
	// @Override
	// public void defaults() {
	// Field[] fields = getClass().getFields();
	// LocaleConfig empty = new LocaleConfig();
	// for(Field f : fields)
	// {
	// try {
	// f.set(this, getConfig().getString(f.getName(),
	// String.valueOf(f.get(empty))));
	// } catch (IllegalArgumentException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// private static String quickCheck(String language, ClassLoader cl)
	// {
	// File f = new File(subDirectory + File.separatorChar + language);
	// if(makeFile(f)) return language;
	// try
	// {
	// FileOutputStream fout = new FileOutputStream(f);
	// int b = 0;
	// InputStream lin = cl.getResourceAsStream(subDirectory + '/' + language);
	// while( (b = lin.read()) != -1) fout.write(b);
	// } catch (java.io.IOException e) {
	// e.printStackTrace();
	// }
	// return language;
	// }
	//
	// }
}

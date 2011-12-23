package com.wolvereness.physicalshop.config;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.wolvereness.physicalshop.ShopMaterial;
import com.wolvereness.physicalshop.exception.InvalidSignException;
import com.wolvereness.util.Config;

//"Buy (?=\\d{1,4})|(?<=\\d{1,4}) for (?=\\d{1,4})|(?<= for \\d{1,4})(?=\\D)"
//Inferior
//"(?<=Buy )\\d{1,4}(?= for \\d{1,4}\\D)|(?<=Buy \\d{1,4} for )\\d{1,4}(?=\\D)|(?<=Buy \\d{1,4} for \\d{1,4})\\D"

/**
 *
 */
public class StandardConfig extends Config {
	/**
	 * "config.yml"
	 */
	public static final String fileName = "config.yml";
	private Pattern buyPattern;
	private Pattern materialPattern = null;
	private Pattern sellPattern;

	/**
	 * makes a new standard config, loading up defaults
	 * @param cl Used to get a resource
	 */
	public StandardConfig(final ClassLoader cl) {
		super(fileName,cl.getResourceAsStream(fileName));
	}

	@Override
	protected void defaults() {
		Set<String> currencies = getKeys("currencies");
		if(currencies == null) {
			currencies = new HashSet<String>();
		}
		if (currencies.isEmpty()) {
			getConfig().set("currencies.g", "Gold Ingot");
			currencies.add("g");
		}
		ShopMaterial.resetCurrencies(currencies.size());
		for(final String currency : currencies) {
			ShopMaterial.addCurrency(currency.charAt(0), String.valueOf(getConfig().get("currencies."+currency.charAt(0))));
		}
	}
	/**
	 * Pattern for 'buy-from-shop' (second line on signs).
	 * "buy-pattern"
	 *
	 * @return the pattern splitting the Buy line
	 */
	public Pattern getBuyPattern() {
		return buyPattern == null
			?
				buyPattern = Pattern.compile(getConfig().getString("buy-pattern"))
			:
				buyPattern;
	}

	/**
	 * Reads the current language to be used for LocaleConfig, defaults to "English".
	 * @return the language to be used
	 */
	public String getLanguage() {
		return getConfig().getString("language").toUpperCase();
	}


	/**
	 * Gets the name of the item for the currency
	 * @param c letter representing the currency
	 * @return name of the item
	 * @throws InvalidSignException if the currency isn't found
	 */
	@Deprecated
	public String getMaterialCode(final char c) throws InvalidSignException {
		final Object o = getConfig().get("currencies." + c);
		if(o == null) throw new InvalidSignException();
		final String s = String.valueOf(o);
		return s;
	}

	/**
	 * Pattern for material match (first line on signs)
	 *
	 * @return the pattern matching the Material line
	 */
	public Pattern getMaterialPattern() {
		return materialPattern == null
			?
				materialPattern = Pattern.compile(getConfig().getString("material-pattern"))
			:
				materialPattern;
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}

	/**
	 * Pattern for 'sell-to-shop' (third line on signs).
	 * "sell-pattern"
	 *
	 * @return the pattern splitting the Sell line
	 */
	public Pattern getSellPattern() {
		return sellPattern == null
			?
				sellPattern = Pattern.compile(getConfig().getString("sell-pattern"))
			:
				sellPattern;
	}

	/**
	 * Checks config to get the 'server-shop' name.
	 *
	 * @return the name for server shops
	 */
	public String getServerOwner() {
		return getConfig().getString("server-shop");
	}

	/**
	 * Checks config to get the 'auto-fill-name' setting.
	 *
	 * @return if names should be auto-set
	 */
	public boolean isAutoFillName() {
		return getConfig().getBoolean("auto-fill-name", true);
	}

	/**
	 * Checks config to see if 'protect-existing-chest' is set
	 *
	 * @return true
	 */
	public boolean isExistingChestProtected() {
		return getConfig().getBoolean("protect-existing-chest", true);
	}

	/**
	 * Checks config to see if LogBlock is enabled, with positive checks
	 * LogBlock's status
	 *
	 * @return true if logblock is set to be on, and logblock is enabled, and chestaccess is turned on
	 */
	public boolean isLogBlocked() {
		try {
			if(!getConfig().getBoolean("log-block", false)) return false;
			final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("LogBlock");
			if(plugin == null) return false;
			return ((de.diddiz.LogBlock.LogBlock) plugin).getLBConfig().isLogging(de.diddiz.LogBlock.Logging.CHESTACCESS);
		} catch (final ClassCastException e) {
			e.printStackTrace();
			return false; // sanity check
		}
	}

	/**
	 * Checks config to get the 'protect-break' setting.
	 *
	 * @return the config option for protection chest breaking
	 */
	public boolean isProtectBreak() {
		return getConfig().getBoolean("protect-break", true);
	}

	/**
	 * Checks config to get the 'protect-chest-access' setting.
	 *
	 * @return the config option for protecting chest access
	 */
	public boolean isProtectChestAccess() {
		return getConfig().getBoolean("protect-chest-access", true);
	}

	/**
	 * Checks config to get the 'protect-explode' setting.
	 *
	 * @return the config option for protecting chests from explosions
	 */
	public boolean isProtectExplode() {
		return getConfig().getBoolean("protect-explode", true);
	}

	/**
	 * Checks config to get the 'trigger-redstone' setting.
	 * @return true if redstone should trigger
	 */
	public boolean isRedstoneTriggered() {
		return getConfig().getBoolean("trigger-redstone", false);
	}
}

package com.wolvereness.physicalshop;

import java.util.regex.Pattern;
import org.bukkit.Bukkit;

import com.wolvereness.physicalshop.exception.InvalidSignException;
import com.wolvereness.util.Config;

import de.diddiz.LogBlock.LogBlock;

//"Buy (?=\\d{1,4})|(?<=\\d{1,4}) for (?=\\d{1,4})|(?<= for \\d{1,4})(?=\\D)"
//Inferior
//"(?<=Buy )\\d{1,4}(?= for \\d{1,4}\\D)|(?<=Buy \\d{1,4} for )\\d{1,4}(?=\\D)|(?<=Buy \\d{1,4} for \\d{1,4})\\D"

public class StandardConfig extends Config {
	public static final String fileName = "config.yml";
	private Pattern materialPattern = null;
	private Pattern sellPattern;
	private Pattern buyPattern;
	//private Map<char, String> currencies;

	public StandardConfig() {
		super(fileName);
	}

	@Override
	public void defaults() {
		// getCurrency();
		getConfig().getString("currencies.g", "Gold Ingot");
		getMaterialPattern();
		getBuyPattern();
		getSellPattern();
		getServerOwner();
		getLanguage();
		isAutoFillName();
		isProtectBreak();
		isProtectChestAccess();
		isLogBlocked();
		isProtectExplode();
		isExistingChestProtected();
	}

	/*
	public ShopMaterial getCurrency() 
	{
		final Material material = Material
			.getMaterial(
				configuration
					.getInt(
						"currency",
						Material
							.GOLD_INGOT
							.getId()
						)
				);
		final byte durability = (byte) configuration.getInt("currency-data", 0);
		return new ShopMaterial(material, durability);
	} //*/
	 
	/**
	 * Pattern for material match (first line on signs)
	 * 
	 * @return
	 */
	public Pattern getMaterialPattern() {
		return materialPattern == null
			?
				materialPattern = Pattern.compile(getConfig().getString("material-pattern", "\\[(.+)\\]"))
			:
				materialPattern;
	}

	/**
	 * Pattern for 'sell-to-shop' (third line on signs).
	 * "sell-pattern"
	 * 
	 * @return
	 */
	public Pattern getSellPattern() {
		return sellPattern == null
			?
				sellPattern = Pattern.compile(getConfig().getString("sell-pattern","Sell (?=\\d{1,4} for)|(?<=\\d{1,4}) for (?=\\d{1,4})|(?<= for \\d{1,4})(?=\\D)")) 
			: 
				sellPattern;
	}
	

	/**
	 * Pattern for 'buy-from-shop' (second line on signs).
	 * "buy-pattern"
	 * 
	 * @return
	 */
	public Pattern getBuyPattern() {
		return buyPattern == null 
			? 
				buyPattern = Pattern.compile(getConfig().getString("buy-pattern","Buy (?=\\d{1,4} for)|(?<=\\d{1,4}) for (?=\\d{1,4})|(?<= for \\d{1,4})(?=\\D)")) 
			: 
				buyPattern;
	}

	/**
	 * Checks config to get the 'server-shop' name.
	 * 
	 * @return
	 */
	public String getServerOwner() {
		return getConfig().getString("server-shop", "[Server]");
	}

	/**
	 * Checks config to get the 'auto-fill-name' setting.
	 * 
	 * @return
	 */
	public boolean isAutoFillName() {
		return getConfig().getBoolean("auto-fill-name", true);
	}

	/**
	 * Checks config to get the 'protect-break' setting.
	 * 
	 * @return
	 */
	public boolean isProtectBreak() {
		return getConfig().getBoolean("protect-break", true);
	}

	/**
	 * Checks config to get the 'protect-chest-access' setting.
	 * 
	 * @return
	 */
	public boolean isProtectChestAccess() {
		return getConfig().getBoolean("protect-chest-access", true);
	}

	/**
	 * Checks config to get the 'protect-explode'setting.
	 * 
	 * @return
	 */
	public boolean isProtectExplode() {
		return getConfig().getBoolean("protect-explode", true);
	}

	/**
	 * Checks config to see if LogBlock is enabled, with positive checks
	 * LogBlock's status
	 * 
	 * @return
	 */
	public boolean isLogBlocked() {
		try {
			return 
				getConfig().getBoolean("log-block", false)
			// && Bukkit.getServer().getPluginManager().getPlugin("LogBlock") != null
				&& ((LogBlock) Bukkit.getServer().getPluginManager().getPlugin("LogBlock")).getConfig().logChestAccess;
		} catch (NullPointerException e) {
			return false; // not loaded or none-existent
		} catch (ClassCastException e) {
			e.printStackTrace();
			return false; // sanity check
		}
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
	 * Reads the current language to be used for LocaleConfig, defaults to "English".
	 * @return
	 */
	public String getLanguage() {
		return getConfig().getString("language", "English");
	}

	public String getMaterialCode(char c) throws InvalidSignException {
		String s = getConfig().getString("currencies." + c);
		if(s == null) throw new InvalidSignException();
		return s;
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}
}

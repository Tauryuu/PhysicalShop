package com.wolvereness.physicalshop.config;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.wolvereness.physicalshop.ShopMaterial;
import com.wolvereness.util.Config;

/**
 * @author Wolfe
 *
 */
public class MaterialConfig extends Config {
	/**
	 * Creates a MaterialConfiguration
	 */
	public MaterialConfig() {
		super("Locales", "Items.yml", null);
	}
	@Override
	protected void defaults() {
		final ConfigurationSection aliasSection = getConfig().getConfigurationSection("Aliases");
		if(aliasSection == null) {
			getConfig().set("Aliases.custom_name", "real item name or number");
			ShopMaterial.resetIdentifiers(0);
		} else {
			final Set<String> aliases = aliasSection.getKeys(false);
			if (aliases.size() == 1 && "real item name or number".equals(aliasSection.get("custom_name"))) {
				ShopMaterial.resetIdentifiers(0);
			} else {
				ShopMaterial.resetIdentifiers(aliases.size());
				for(final String alias : aliases) {
					ShopMaterial.addShopMaterialAlias(alias, String.valueOf(aliasSection.get(alias)).replace('|', ':'));
				}
			}
		}
		final ConfigurationSection nameSection = getConfig().getConfigurationSection("Names");
		if(nameSection == null) {
			getConfig().set("Names.real_item_name_or_number|damage_value", "custom item name");
			ShopMaterial.resetNames(0);
		} else {
			final Set<String> names = nameSection.getKeys(false);
			if(names.size() == 1 && "custom item name".equals(nameSection.get("real_item_name_or_number|damage_value"))) {
				ShopMaterial.resetNames(0);
			} else {
				ShopMaterial.resetNames(names.size());
				for(final String name : names) {
					ShopMaterial.setMaterialName(name.replace('|', ':'),String.valueOf(nameSection.get(name)));
				}
			}
		}
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}
}

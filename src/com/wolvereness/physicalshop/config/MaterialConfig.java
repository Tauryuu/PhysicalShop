package com.wolvereness.physicalshop.config;

import java.util.Set;

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
		final Set<String> aliases = getKeys("Aliases");
		if(aliases == null) {
			getConfig().set("Aliases.custom_name", "real item name or number");
			ShopMaterial.resetIdentifiers(0);
		} else if (aliases.size() == 1 && "real item name or number".equals(getConfig().get("Aliases.custom_name"))) {
			ShopMaterial.resetIdentifiers(0);
		} else {
			ShopMaterial.resetIdentifiers(aliases.size());
			for(final String alias : aliases) {
				ShopMaterial.addShopMaterialAlias(alias, String.valueOf(getConfig().get("Aliases." + alias)));
			}
		}
		final Set<String> names = getKeys("Names");
		if (names == null) {
			getConfig().set("Names.real_item_name_or_number|damage_value", "custom item name");
			ShopMaterial.resetNames(0);
		} else if(names.size() == 1 && "custom item name".equals(getConfig().get("Names.real_item_name_or_number|damage_value"))) {
			ShopMaterial.resetNames(0);
		} else {
			ShopMaterial.resetNames(names.size());
			for(final String name : names) {
				ShopMaterial.setMaterialName(name,String.valueOf(getConfig().get("Names."+name)));
			}
		}
	}

	@Override
	public String getName() {
		return "PhysicalShop";
	}
}

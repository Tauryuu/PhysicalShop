package com.wolvereness.physicalshop;

import org.bukkit.inventory.ItemStack;

public class ShopItemStack {

	private final int amount;
	private final ShopMaterial material;

	ShopItemStack(final ItemStack itemStack) {
		material = new ShopMaterial(itemStack);
		amount = itemStack.getAmount();
	}

	public ItemStack getStack() {
		return material.getStack(amount);
	}

}

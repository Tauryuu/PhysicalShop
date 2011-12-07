package com.wolvereness.physicalshop;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a stack of a particular item
 *
 */
public class ShopItemStack {

	private final int amount;
	private final ShopMaterial material;

	ShopItemStack(final ItemStack itemStack) {
		material = new ShopMaterial(itemStack);
		amount = itemStack.getAmount();
	}

	/**
	 * Makes a stack of this item
	 * @return an itemstack for this ShopItemStack
	 */
	public ItemStack getStack() {
		return material.getStack(amount);
	}

}

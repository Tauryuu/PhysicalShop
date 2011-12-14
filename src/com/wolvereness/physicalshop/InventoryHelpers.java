package com.wolvereness.physicalshop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.wolvereness.physicalshop.exception.InvalidExchangeException;

/**
 *
 */
public class InventoryHelpers {
	private static boolean add(final Inventory inventory, final ItemStack stack) {
		int left = stack.getAmount();
		final int maxStackSize = stack.getType().getMaxStackSize();

		loop: for (int pass = 0; pass < 2; ++pass) {
			final ItemStack[] contents = inventory.getContents();

			for (int i = 0; i < contents.length; ++i) {
				if (left == 0) {
					break loop;
				}

				final ItemStack s = contents[i];

				if (s == null) {
					if (pass == 0) {
						continue;
					}
				} else {
					if (	(s.getType() != stack.getType())
							|| (s.getDurability() != stack.getDurability())
							|| (!s.getEnchantments().isEmpty())
							) {
						continue;
					}
				}

				final int size = s == null ? 0 : s.getAmount();
				final int newSize = Math.min(maxStackSize, size + left);

				stack.setAmount(newSize);

				inventory.setItem(i, stack);

				left -= newSize - size;
			}
		}

		return left == 0;
	}

	/**
	 * This will add the addStack, and remove the removeStack from specified inventory. If it fails, it throws an error and resets inventories.
	 * @param inventory inventory to use
	 * @param addStack stack to add
	 * @param removeStack stack to remove
	 * @throws InvalidExchangeException if the inventory cannot support the stack or add, or not enough in inventory to remove the stack
	 */
	public static void exchange(
	        final Inventory inventory,
			final ItemStack addStack,
			final ItemStack removeStack)
			throws InvalidExchangeException {
		final ShopItemStack[] oldItems = InventoryHelpers.getItems(inventory);

		if (removeStack != null) {
			if (!InventoryHelpers.remove(inventory, removeStack)) {
				InventoryHelpers.setItems(inventory, oldItems);
				throw new InvalidExchangeException(
						InvalidExchangeException.Type.REMOVE);
			}

		}

		if (addStack != null) {
			if (!InventoryHelpers.add(inventory, addStack)) {
				InventoryHelpers.setItems(inventory, oldItems);
				throw new InvalidExchangeException(
						InvalidExchangeException.Type.ADD);
			}
		}
	}

	/**
	 * Finds how much of given material is in given inventory
	 * @param inventory the inventory to consider
	 * @param material the material to consider
	 * @return the amount of material in said inventory
	 */
	public static int getCount(final Inventory inventory, final ShopMaterial material) {
		int amount = 0;

		for (final ItemStack i : inventory.getContents()) {
			if (	(i != null)
					&& (i.getType() == material.getMaterial())
					&& (i.getDurability() == material.getDurability())
					&& i.getEnchantments().isEmpty()) {
				amount += i.getAmount();
			}
		}

		return amount;
	}

	/**
	 * Makes a set of shop item stacks to represent this inventory
	 * @param inventory the inventory to consider
	 * @return a set of shop item stacks
	 */
	public static ShopItemStack[] getItems(final Inventory inventory) {
		final ItemStack[] contents = inventory.getContents();
		final ShopItemStack[] items = new ShopItemStack[contents.length];

		for (int i = 0; i < items.length; ++i) {
			final ItemStack stack = contents[i];
			items[i] = stack == null ? null : new ShopItemStack(stack);
		}

		return items;
	}

	private static boolean remove(final Inventory inventory, final ItemStack stack) {
		int left = stack.getAmount();
		final ItemStack[] contents = inventory.getContents();

		for (int i = 0; i < contents.length; ++i) {
			if (left == 0) {
				break;
			}

			final ItemStack s = contents[i];

			if (	(s == null)
					|| (s.getType() != stack.getType())
					|| (s.getDurability() != stack.getDurability())
					|| (!s.getEnchantments().isEmpty())) {
				continue;
			}

			final int size = s.getAmount();
			final int newSize = size - Math.min(size, left);

			if (newSize == 0) {
				inventory.setItem(i, null);
			} else {
				s.setAmount(newSize);
				inventory.setItem(i, s);
			}

			left -= size - newSize;
		}

		return left == 0;
	}

	/**
	 * This changes slot in this inventory to given array
	 * @param inventory the inventory to consider
	 * @param items the items to overwrite the inventory with
	 */
	public static void setItems(final Inventory inventory, final ShopItemStack[] items) {
		for (int i = 0; i < items.length; ++i) {
			final ShopItemStack stack = items[i];
			inventory.setItem(i, stack == null ? null : stack.getStack());
		}
	}

}

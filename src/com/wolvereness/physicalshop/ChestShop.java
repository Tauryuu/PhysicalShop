package com.wolvereness.physicalshop;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.wolvereness.physicalshop.exception.InvalidExchangeException;
import com.wolvereness.physicalshop.exception.InvalidSignException;

public class ChestShop extends Shop {

	private final Chest chest;

	public ChestShop(final Sign sign) throws InvalidSignException {
		super(sign);

		final Block chestBlock = sign.getBlock().getRelative(BlockFace.DOWN);

		if (chestBlock.getType() != Material.CHEST) {
			throw new InvalidSignException();
		}

		chest = (Chest) chestBlock.getState();
	}

	@Override
	protected boolean buy(final Player player) {
		if (!canBuy()) {
			PhysicalShop.sendMessage(player, "NO_BUY");
			return false;
		}
		final ShopItemStack[] items = InventoryHelpers.getItems(chest
				.getInventory());

		try {
			InventoryHelpers.exchange(chest.getInventory(), getBuyCurrency().getStack(getBuyRate().getPrice()), getMaterial().getStack(getBuyRate().getAmount()));
		} catch (final InvalidExchangeException e) {
			switch (e.getType()) {
			case ADD:
				PhysicalShop.sendMessage(player, "CHEST_INVENTORY_FULL");
				break;
			case REMOVE:
				PhysicalShop.sendMessage(player, "NOT_ENOUGH_SHOP_ITEMS",
						getMaterial());
				break;
			}

			return false;
		}

		if (!super.buy(player)) {
			InventoryHelpers.setItems(chest.getInventory(), items);
			return false;
		}
		return true;
	}
	
	@Override
	/**
	 * Gets the current amount of shop's currency in the chest.
	 * @return
	 */
	public int getShopBuyCapital() {
		return InventoryHelpers.getCount(chest.getInventory(), getBuyCurrency());
	}
	
	@Override
	/**
	 * Gets the current amount of shop's currency in the chest.
	 * @return
	 */
	public int getShopSellCapital() {
		return InventoryHelpers.getCount(chest.getInventory(), getSellCurrency());
	}

	@Override
	public int getShopItems() {
		return InventoryHelpers.getCount(chest.getInventory(), getMaterial());
	}

	@Override
	public boolean isOwner(final Player player) {
		return (player != null)
				&& (player.getName().equals(getOwnerName()) || PhysicalShop
						.getPermissions().hasAdmin(player));
	}

	@Override
	public boolean isShopBlock(final Block block) {
		if (super.isShopBlock(block)) {
			return true;
		}

		final Block down = getSign().getBlock().getRelative(BlockFace.DOWN);

		if ((down.getType() == Material.CHEST) && (down.equals(block))) {
			return true;
		}

		return false;
	}

	@Override
	public boolean sell(final Player player) {
		if (!canSell()) {
			PhysicalShop.sendMessage(player, "NO_SELL");
			return false;
		}
		final ShopItemStack[] items = InventoryHelpers.getItems(chest
				.getInventory());

		try {
			InventoryHelpers.exchange(chest.getInventory(), getMaterial()
					.getStack(getSellRate().getAmount()), getSellCurrency().getStack(getSellRate().getPrice()));
		} catch (final InvalidExchangeException e) {
			switch (e.getType()) {
			case ADD:
				PhysicalShop.sendMessage(player, "CHEST_INVENTORY_FULL");
				break;
			case REMOVE:
				PhysicalShop.sendMessage(player, "NOT_ENOUGH_SHOP_MONEY", getSellCurrency());
				break;
			}

			return false;
		}

		if (!super.sell(player)) {
			InventoryHelpers.setItems(chest.getInventory(), items);
			return false;
		}
		return true;
	}

	@Override
	public void status(Player p) {
		if (getBuyCurrency() == null)
		{
			PhysicalShop.sendMessage(p, "STATUS_ONE_CURRENCY", getShopSellCapital(), getSellCurrency(), getShopItems(), getMaterial());
		} else if (getSellCurrency() == null) {
			PhysicalShop.sendMessage(p, "STATUS_ONE_CURRENCY", getShopBuyCapital(), getBuyCurrency(), getShopItems(), getMaterial());
		} else if (getSellCurrency().equals(getBuyCurrency())) {
			PhysicalShop.sendMessage(p, "STATUS_ONE_CURRENCY", getShopBuyCapital(), getBuyCurrency(), getShopItems(), getMaterial());
		} else {
			PhysicalShop.sendMessage(p, "STATUS", getShopBuyCapital(), getBuyCurrency(), getShopSellCapital(), getSellCurrency(), getShopItems(), getMaterial());
		}

		super.status(p);
	}

}

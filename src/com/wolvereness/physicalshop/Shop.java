package com.wolvereness.physicalshop;


import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.wolvereness.physicalshop.exception.InvalidExchangeException;
import com.wolvereness.physicalshop.exception.InvalidMaterialException;
import com.wolvereness.physicalshop.exception.InvalidSignException;
import com.wolvereness.physicalshop.exception.InvalidSignOwnerException;

/**
 *
 */
public class Shop {
	/**
	 * Figures out the current material the shop uses.
	 * @param lines text from sign
	 * @return an associated shop material, or null if failed to decypher
	 */
	public static ShopMaterial getMaterial(final String[] lines) {
		try {
			return ShopMaterial.getShopMaterial(lines[0]);
		} catch (final InvalidMaterialException e) {
			return null;
		}
	}
	/**
	 * Owner is found on fourth line of sign. Making player names longer than 16 characters is on my to-do list.
	 * @param lines The set of lines associated with the sign for a shop.
	 * @return name of the owner of said shop
	 */
	public static String getOwnerName(final String[] lines) {
		return lines[3];
	}
	private static Rate getRate(final String amount, final String price) {
		//final Matcher m = PhysicalShop.getConfig().getBuyPattern().matcher(sign.getLine(line));
		try
		{
			return new Rate(Integer.parseInt(amount), Integer.parseInt(price));
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	private static void updateInventory(final Player player) {
		player.updateInventory();
	}
	private final ShopMaterial buyCurrency;
	private final Rate buyRate;
	private final ShopMaterial material;
	private final String ownerName;
	private final ShopMaterial sellCurrency;
	private final Rate sellRate;
	private Sign sign = null;
	/**
	 * Initializes Shop based off of sign.
	 * @param sign the sign to consider
	 * @throws InvalidSignException If sign does not match correct pattern.
	 */
	public Shop(final Sign sign) throws InvalidSignException {
		this(sign.getLines());
		this.sign = sign;
	}
	/**
	 * Initializes a shop based off the lines from a sign. Used to check validity.
	 * @param lines the text from the sign to consider
	 * @throws InvalidSignException If the sign text does not match correct pattern.
	 */
	public Shop(final String[] lines) throws InvalidSignException {
		//String[] lines = sign.getLines();
		material = Shop.getMaterial(lines);

		if (material == null) throw new InvalidSignException();
		final String[] buySet = PhysicalShop.getPluginConfig().getBuyPattern().split(lines[1]);
		final String[] sellSet = PhysicalShop.getPluginConfig().getSellPattern().split(lines[2]);
		if (buySet.length != 4 && sellSet.length != 4) throw new InvalidSignException();
		Rate buyRate = null, sellRate = null;
		ShopMaterial buyCurrency = null, sellCurrency = null;
		try
		{
			if(buySet.length == 4)
			{
				buyCurrency = ShopMaterial.getCurrency(buySet[3].charAt(0));
				buyRate = getRate(buySet[1],buySet[2]);
			}
		} catch (final InvalidSignException e) {}
		try
		{
			if(sellSet.length == 4)
			{
				sellCurrency = ShopMaterial.getCurrency(sellSet[3].charAt(0));
				sellRate =  getRate(sellSet[1],sellSet[2]);
			}
		} catch (final InvalidSignException e) {}
		if (sellCurrency == null && buyCurrency == null) throw new InvalidSignException();
		this.buyCurrency = buyCurrency;
		this.sellCurrency = sellCurrency;
		this.buyRate = buyRate;
		this.sellRate = sellRate;
		//buyRate = Shop.getRate(sign, 1);
		//sellRate = Shop.getRate(sign, 2);

		//if ((buyRate == null) && (sellRate == null)) {
		//	throw new InvalidSignException();
		//}

		if (((this.ownerName = lines[3]) == null) || ownerName.isEmpty()) throw new InvalidSignOwnerException();
	}

	/**
	 * Invokes the buy routine for player.
	 * @param player
	 * @return
	 */
	protected boolean buy(final Player player) {
		if (!canBuy()) {
			PhysicalShop.sendMessage(player, "NO_BUY");
			return false;
		}

		final Inventory inventory = player.getInventory();

		final int price = getBuyRate().getPrice();
		final int amount = getBuyRate().getAmount();

		try {
			InventoryHelpers.exchange(inventory, material.getStack(amount), getBuyCurrency().getStack(price));
		} catch (final InvalidExchangeException e) {
			switch (e.getType()) {
			case ADD:
				PhysicalShop.sendMessage(player, "PLAYER_INVENTORY_FULL");
				break;
			case REMOVE:
				PhysicalShop.sendMessage(player, "NOT_ENOUGH_PLAYER_MONEY", getBuyCurrency());
				break;
			}

			return false;
		}

		PhysicalShop.sendMessage(player, "BUY", amount, material, price, getBuyCurrency());
		updateInventory(player); //player.updateInventory();

		queryLogBlock(player, false);
		return true;
	}

	/**
	 * @return true if and only if this shop supports buying
	 */
	public boolean canBuy() {
		return buyRate != null;
	}

	/**
	 * This checks player for permission to destroy this shop
	 * @param player player to consider
	 * @return true if and only if player may destroy this shop
	 */
	public boolean canDestroy(final Player player) {
		return (player != null)
				&& PhysicalShop.staticGetPermissionHandler().hasAdmin(player);
	}

	/**
	 * @return true if and only if this shop supports selling
	 */
	public boolean canSell() {
		return sellRate != null;
	}

	/**
	 * @return the currency associated with buying
	 */
	public ShopMaterial getBuyCurrency() {
		return buyCurrency;
	}
	/**
	 * @return the rate associated with buying
	 */
	public Rate getBuyRate() {
		return buyRate;
	}

	/**
	 * @return the material associated with this shop
	 */
	public ShopMaterial getMaterial() {
		return material;
	}

	/**
	 * @return the owner of this shop
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return the currency associated with selling
	 */
	public ShopMaterial getSellCurrency() {
		return sellCurrency;
	}

	/**
	 * @return the rate associated with selling
	 */
	public Rate getSellRate() {
		return sellRate;
	}

	/**
	 * Gets the current amount of shop's buying currency in the chest.
	 *
	 * @return an amount of currency in this shop from purchases
	 */
	public int getShopBuyCapital() {
		return Integer.MAX_VALUE;
	}

	/**
	 * @return the amount of the shop's material currently stored
	 */
	public int getShopItems() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Gets the current amount of shop's selling currency in the chest.
	 *
	 * @return the amount of currency in this shop for selling
	 */
	public int getShopSellCapital() {
		return Integer.MAX_VALUE;
	}

	/**
	 * @return the associated sign
	 */
	public Sign getSign() {
		return sign;
	}
	/**
	 * This method is called when a player right-clicks the sign. It considers the item in player's hand, and will act accordingly.
	 * @param player the player to consider
	 */
	public void interact(final Player player) {
		final ShopMaterial item = new ShopMaterial(player.getItemInHand());

		if (item.equals(getBuyCurrency())) {
			buy(player);
		} else if (item.equals(material)) {
			sell(player);
		}
	}
	/**
	 * An alias for {@link #canDestroy(Player)}, now deprecated because of its ambiguity
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public final boolean isOwner(final Player player) {
		return canDestroy(player);
	}
	/**
	 * @param block block to consider
	 * @return true if said block is the sign for this chest or the sign for this shop is attached to said block
	 */
	public boolean isShopBlock(final Block block) {
		final Block signBlock = sign.getBlock();

		if (block.equals(signBlock)) return true;

		final org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign
				.getData();

		return block.equals(signBlock.getRelative(signData.getAttachedFace()));
	}

	private void queryLogBlock(final Player player, final boolean selling) {
		if (PhysicalShop.getLogBlock() == null)
			return;
		if((selling
			?
				(getSellRate().getPrice() * -1)
			:
				getBuyRate().getPrice()
			) != 0) {
			PhysicalShop
				.getLogBlock()
				.queueChestAccess(
					player
						.getName(),
					sign
						.getBlock()
						.getRelative(BlockFace.DOWN)
						.getLocation(),
					54,
					(short) (
						selling
						?
							getSellCurrency()
						:
							getBuyCurrency()
						)
						.getMaterial()
						.getId(),
					(short) (
						selling
						?
							(getSellRate().getPrice() * -1)
						:
							getBuyRate().getPrice()
						),
					(byte) 0
					);
		}
		if ((selling
			?
				(getSellRate().getAmount())
			:
				(getBuyRate().getAmount() * -1)
			) != 0 ) {
			PhysicalShop
				.getLogBlock()
				.queueChestAccess(
					player
						.getName(),
					sign
						.getBlock()
						.getRelative(BlockFace.DOWN)
						.getLocation(),
					54,
					(short) getMaterial()
						.getMaterial()
						.getId(),
					(short) (
						selling
						?
							getSellRate().getAmount()
						:
							(getBuyRate().getAmount() * -1)
						),
					(byte) 0
					);
		}
	}

	/**
	 * performs sell operation for player
	 * @param player player to sell something to shop
	 * @return true if successful
	 */
	protected boolean sell(final Player player) {
		if (!canSell()) {
			PhysicalShop.sendMessage(player, "NO_SELL");
			return false;
		}

		final Inventory inventory = player.getInventory();

		final int price = getSellRate().getPrice();
		final int amount = getSellRate().getAmount();

		try {
			InventoryHelpers.exchange(inventory, getSellCurrency()
					.getStack(price), material.getStack(amount));
		} catch (final InvalidExchangeException e) {
			switch (e.getType()) {
			case ADD:
				PhysicalShop.sendMessage(player, "PLAYER_INVENTORY_FULL");
				break;
			case REMOVE:
				PhysicalShop.sendMessage(player, "NOT_ENOUGH_PLAYER_ITEMS",
						material);
				break;
			}

			return false;
		}

		updateInventory(player); // player.updateInventory();

		PhysicalShop.sendMessage(player, "SELL", amount, material, price, getSellCurrency());

		queryLogBlock(player, true);
		return true;
	}
	/**
	 * Messages player p the rates for current Shop.
	 * @param p the player to message
	 */
	public void status(final Player p) {
		if (canBuy() && (getShopItems() >= buyRate.getAmount())) {
			PhysicalShop.sendMessage(p, "BUY_RATE", buyRate.getAmount(),
					material, buyRate.getPrice(), getBuyCurrency());
		}

		if (canSell() && (getShopSellCapital() >= sellRate.getPrice())) {
			PhysicalShop.sendMessage(p, "SELL_RATE", sellRate.getAmount(),
					material, sellRate.getPrice(), getSellCurrency());
		}
	}

}

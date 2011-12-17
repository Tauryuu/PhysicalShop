package com.wolvereness.physicalshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.wolvereness.physicalshop.exception.InvalidSignException;

/**
 *
 */
public class ShopHelpers {

	/**
	 * Copy & Pasted from GNU GPL Licensed craftbook
	 * com.sk89q.craftbook.util.SignUtil
	 * @param sign
	 *            treated as sign post if it is such, or else assumed to be a
	 *            wall sign (i.e., if you ask about a stone block, it's
	 *            considered a wall sign).
	 * @return the blank side of the sign opposite the text. In the case of a
	 *         wall sign, the block in this direction is the block to which the
	 *         sign is attached. This is also the direction a player would be
	 *         facing when reading the sign.
	 *
	 */
	public static BlockFace getBack(final Sign sign) {
		if (sign.getType() == Material.SIGN_POST) {
			switch (sign.getRawData()) {
			case 0x0:
				return BlockFace.EAST;
			case 0x1:
			case 0x2:
			case 0x3:
				return BlockFace.SOUTH_EAST;
			case 0x4:
				return BlockFace.SOUTH;
			case 0x5:
			case 0x6:
			case 0x7:
				return BlockFace.SOUTH_WEST;
			case 0x8:
				return BlockFace.WEST;
			case 0x9:
			case 0xA:
			case 0xB:
				return BlockFace.NORTH_WEST;
			case 0xC:
				return BlockFace.NORTH;
			case 0xD:
			case 0xE:
			case 0xF:
				return BlockFace.NORTH_EAST;
			default:
				return BlockFace.SELF;
			}
		}
		switch (sign.getRawData()) {
		case 0x2:
			return BlockFace.WEST;
		case 0x3:
			return BlockFace.EAST;
		case 0x4:
			return BlockFace.SOUTH;
		case 0x5:
			return BlockFace.NORTH;
		default:
			return BlockFace.SELF;
		}
	}
	/**
	 * Copy & Pasted from Bukkit source
	 * Gets the face that this lever or button is attached on
	 * @param data Data of the lever or button
	 * @return BlockFace attached to
	 */
	public static BlockFace getFace(final byte data) {
		switch (data & 0x7) {
		case 0x1:
			return BlockFace.NORTH;
		case 0x2:
			return BlockFace.SOUTH;
		case 0x3:
			return BlockFace.EAST;
		case 0x4:
			return BlockFace.WEST;
		case 0x5:
		case 0x6:
			return BlockFace.DOWN;
		}
		return null;
	}

	/**
	 * Attempts to create a new shop object based on this block
	 * @param block the block to consider
	 * @return null if block is not sign or said sign is invalid, otherwise a new associated {@link Shop} for this block
	 */
	public static Shop getShop(final Block block) {
		if (block == null) return null;

		if ((block.getType() != Material.SIGN_POST)
				&& (block.getType() != Material.WALL_SIGN)) return null;

		final Sign sign = (Sign) block.getState();

		if (sign == null) return null;

		final String ownerName = Shop.getOwnerName(sign.getLines());

		try {
			if (block.getRelative(BlockFace.DOWN).getType() == Material.CHEST)
				return new ChestShop(sign);
			else if (ownerName.equalsIgnoreCase(PhysicalShop.getPluginConfig()
					.getServerOwner())) return new Shop(sign);
			else
				return null;
		} catch (final InvalidSignException e) {
			return null;
		}
	}
	static List<Shop> getShops(final Block block) {
		final ArrayList<Shop> shops = new ArrayList<Shop>();

		final Block[] blocks = new Block[] { block,
				block.getRelative(BlockFace.UP),
				block.getRelative(BlockFace.DOWN),
				block.getRelative(BlockFace.NORTH),
				block.getRelative(BlockFace.EAST),
				block.getRelative(BlockFace.SOUTH),
				block.getRelative(BlockFace.WEST), };

		for (final Block b : blocks) {
			final Shop shop = ShopHelpers.getShop(b);

			if ((shop != null) && shop.isShopBlock(block)) {
				shops.add(shop);
			}
		}

		return shops;
	}

	/**
	 * Checks around block for shops, and checks it against player for ownership
	 * @param block The block being destroyed
	 * @param player The player destroying block, can be null (as in, no destroyer)
	 * @return false if there are shops and player is null or not admin and not owner
	 */
	public static boolean isBlockDestroyable(final Block block,
			final Player player) {
		final List<Shop> shops = ShopHelpers.getShops(block);
		if(player == null && !shops.isEmpty()) return false;
		for (final Shop shop : shops) {
			if (!shop.canDestroy(player)) {
				PhysicalShop.sendMessage(player, "CANT_DESTROY");
				shop.getSign().update();
				return false;
			}
		}

		return true;
	}
    /**
	 * Cuts the name to 15 characters
	 * @param name name to truncate
	 * @return the first 15 characters of the name
	 */
	public static String truncateName(final String name) {
		if(name == null) return null;
		if(name.length()<=15) return name;
		return name.substring(0, 14);
	}
}

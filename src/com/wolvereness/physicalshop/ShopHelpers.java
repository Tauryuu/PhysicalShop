package com.wolvereness.physicalshop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.wolvereness.physicalshop.exception.InvalidSignException;

public class ShopHelpers {

	public static Shop getShop(final Block block) {
		if (block == null) {
			return null;
		}

		if ((block.getType() != Material.SIGN_POST)
				&& (block.getType() != Material.WALL_SIGN)) {
			return null;
		}

		final Sign sign = (Sign) block.getState();

		if (sign == null) {
			return null;
		}

		final String ownerName = Shop.getOwnerName(sign.getLines());

		try {
			if (block.getRelative(BlockFace.DOWN).getType() == Material.CHEST) {
				return new ChestShop(sign);
			} else if (ownerName.equalsIgnoreCase(PhysicalShop.getPluginConfig()
					.getServerOwner())) {
				return new Shop(sign);
			} else {
				return null;
			}
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
			if (!shop.isOwner(player)) {
				PhysicalShop.sendMessage(player, "CANT_DESTROY");
				shop.getSign().update();
				return false;
			}
		}

		return true;
	}

}

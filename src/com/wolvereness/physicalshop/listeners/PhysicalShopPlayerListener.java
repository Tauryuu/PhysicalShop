package com.wolvereness.physicalshop.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.wolvereness.physicalshop.PhysicalShop;
import com.wolvereness.physicalshop.Shop;
import com.wolvereness.physicalshop.ShopHelpers;

public class PhysicalShopPlayerListener extends PlayerListener {

	@Override
	public void onPlayerInteract(final PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}

		// Messaging.save(e.getPlayer());

		final Block block = e.getClickedBlock();

		if (PhysicalShop.getPluginConfig().isProtectChestAccess()
				&& (e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& (block.getType() == Material.CHEST)) {
			final Shop shop = ShopHelpers.getShop(block
					.getRelative(BlockFace.UP));

			if ((shop != null) && shop.isShopBlock(block)
					&& !shop.isOwner(e.getPlayer())) {
				PhysicalShop.sendMessage(e.getPlayer(), "CANT_USE_CHEST");
				e.setCancelled(true);
				return;
			}
		}

		final Shop shop = ShopHelpers.getShop(block);

		if (shop == null) {
			return;
		}

		if (!PhysicalShop.getPermissions().hasUse(e.getPlayer())) {
			PhysicalShop.sendMessage(e.getPlayer(), "CANT_USE");
			return;
		}

		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			shop.status(e.getPlayer());
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			shop.interact(e.getPlayer());
			e.setCancelled(true);
		}
	}

}

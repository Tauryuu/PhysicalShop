package com.wolvereness.physicalshop.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.wolvereness.physicalshop.PhysicalShop;
import com.wolvereness.physicalshop.ShopHelpers;

/**
 *
 */
public class PhysicalShopEntityListener extends EntityListener {

	@Override
	public void onEntityExplode(final EntityExplodeEvent e) {
		if (e.isCancelled()) return;

		// Messaging.save(null);

		if (!PhysicalShop.getPluginConfig().isProtectExplode()) return;

		for (final Block block : e.blockList()) {
			if (!ShopHelpers.isBlockDestroyable(block, null)) {
				e.setCancelled(true);
				return;
			}
		}
	}

}

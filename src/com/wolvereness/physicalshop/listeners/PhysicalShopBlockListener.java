package com.wolvereness.physicalshop.listeners;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.wolvereness.physicalshop.PhysicalShop;
import com.wolvereness.physicalshop.Shop;
import com.wolvereness.physicalshop.ShopHelpers;
import com.wolvereness.physicalshop.exception.InvalidSignException;
import com.wolvereness.physicalshop.exception.InvalidSignOwnerException;

public class PhysicalShopBlockListener extends BlockListener {

	@Override
	public void onBlockBreak(final BlockBreakEvent e) {
		if (e.isCancelled() || ! PhysicalShop.getPluginConfig().isProtectBreak()) {
			return;
		}

		if (!ShopHelpers.isBlockDestroyable(e.getBlock(), e.getPlayer())) {
			//PhysicalShop.sendMessage(e.getPlayer(),"CANT_DESTROY");
			e.setCancelled(true);
		}
	}

	@Override
	public void onBlockBurn(final BlockBurnEvent e) {
		if (e.isCancelled() || ! PhysicalShop.getPluginConfig().isProtectBreak()) {
			return;
		}

		// Messaging.save(e.getPlayer());

		if (!ShopHelpers.isBlockDestroyable(e.getBlock(), null)) {
			e.setCancelled(true);
		}
	}

	@Override
	public void onBlockPlace(final BlockPlaceEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if (!PhysicalShop.getPluginConfig().isProtectChestAccess()) {
			return;
		}

		if (PhysicalShop.getPermissions().hasAdmin(e.getPlayer())) {
			return;
		}

		if (e.getBlock().getType() != Material.CHEST) {
			return;
		}

		// Messaging.save(e.getPlayer());

		final Block block = e.getBlock();

		final Block[] blocks = new Block[] {
				block.getRelative(BlockFace.NORTH),
				block.getRelative(BlockFace.EAST),
				block.getRelative(BlockFace.SOUTH),
				block.getRelative(BlockFace.WEST), };

		for (final Block b : blocks) {
			if (b.getType() == Material.CHEST) {
				final Shop shop = ShopHelpers.getShop(b
						.getRelative(BlockFace.UP));

				if ((shop != null) && shop.isShopBlock(b)) {
					PhysicalShop.sendMessage(e.getPlayer(), "CANT_PLACE_CHEST");
					e.setCancelled(true);
					break;
				}
			}
		}
	}

	@Override
	public void onSignChange(final SignChangeEvent e) {
		if (e.isCancelled()) {
			return;
		}

		// Messaging.save(e.getPlayer());
		Shop shop = null;
		try {
			shop = new Shop(e.getLines());
		} catch (InvalidSignOwnerException ex) {
			if (PhysicalShop.getPluginConfig().isAutoFillName()) {
				e.setLine(3, ShopHelpers.truncateName(e.getPlayer().getName()));
				try {
					shop = new Shop(e.getLines());
				} catch (InvalidSignException ex2) {
					throw new RuntimeException(
						"Logical Falicy on event:" + 
						e +
						", for player:" + 
						e.getPlayer() +
						", for block:" +
						e.getBlock() +
						", for sign text:" +
						Arrays.toString(e.getLines()),
						ex2
						);
				}
			} else {
				return;
			}
		} catch (InvalidSignException ex) {
			return;
		}

		if (!PhysicalShop.getPermissions().hasBuild(e.getPlayer())) {
			PhysicalShop.sendMessage(e.getPlayer(), "CANT_BUILD");
			e.setCancelled(true);
			return;
		}

		if (shop.getOwnerName().equalsIgnoreCase(PhysicalShop.getPluginConfig().getServerOwner())) {
			if (!PhysicalShop.getPermissions().hasAdmin(e.getPlayer())) {
				PhysicalShop.sendMessage(e.getPlayer(), "CANT_BUILD_SERVER");
				e.setCancelled(true);
				return;
			}
		} else {
			if (e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CHEST
					&& PhysicalShop.getPluginConfig().isExistingChestProtected()
					&& !PhysicalShop.getPermissions().hasAdmin(e.getPlayer())
					&& !PhysicalShop.lwcCheck(e.getBlock().getRelative(BlockFace.DOWN), e.getPlayer())
					&& !PhysicalShop.locketteCheck(e.getBlock().getRelative(BlockFace.DOWN), e.getPlayer())) {
				PhysicalShop.sendMessage(e.getPlayer(), "EXISTING_CHEST");
				e.setCancelled(true);
				return;
			}
		}
	}
}

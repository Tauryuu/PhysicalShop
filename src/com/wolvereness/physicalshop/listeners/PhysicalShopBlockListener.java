package com.wolvereness.physicalshop.listeners;

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
import com.wolvereness.util.NameCollection;
import com.wolvereness.util.NameCollection.OutOfEntriesException;

/**
 *
 */
public class PhysicalShopBlockListener extends BlockListener {

	@Override
	public void onBlockBreak(final BlockBreakEvent e) {
		if (e.isCancelled() || ! PhysicalShop.getPluginConfig().isProtectBreak()) return;

		if (!ShopHelpers.isBlockDestroyable(e.getBlock(), e.getPlayer())) {
			//PhysicalShop.sendMessage(e.getPlayer(),"CANT_DESTROY");
			e.setCancelled(true);
		}
	}

	@Override
	public void onBlockBurn(final BlockBurnEvent e) {
		if (e.isCancelled() || ! PhysicalShop.getPluginConfig().isProtectBreak()) return;

		// Messaging.save(e.getPlayer());

		if (!ShopHelpers.isBlockDestroyable(e.getBlock(), null)) {
			e.setCancelled(true);
		}
	}

	@Override
	public void onBlockPlace(final BlockPlaceEvent e) {
		if (e.isCancelled()) return;

		if (!PhysicalShop.getPluginConfig().isProtectChestAccess()) return;

		if (PhysicalShop.staticGetPermissionHandler().hasAdmin(e.getPlayer())) return;

		if (e.getBlock().getType() != Material.CHEST) return;

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
		if (e.isCancelled()) return;

		//final String playerName = ShopHelpers.truncateName(e.getPlayer().getName());
		try {
			new Shop(e.getLines());
		} catch (final InvalidSignOwnerException ex) {
		} catch (final InvalidSignException ex) {
			return;
		}

		if (!PhysicalShop.staticGetPermissionHandler().hasBuild(e.getPlayer())) {
			PhysicalShop.sendMessage(e.getPlayer(), "CANT_BUILD");
			e.setCancelled(true);
			return;
		}

		if (e.getLine(3).equalsIgnoreCase(PhysicalShop.getPluginConfig().getServerOwner())) {
			if (!PhysicalShop.staticGetPermissionHandler().hasAdmin(e.getPlayer())) {
				PhysicalShop.sendMessage(e.getPlayer(), "CANT_BUILD_SERVER");
				e.setCancelled(true);
				return;
			}
		} else {
			if (
					e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CHEST
					&& PhysicalShop.getPluginConfig().isExistingChestProtected()
					&& !PhysicalShop.staticGetPermissionHandler().hasAdmin(e.getPlayer())
					&& !PhysicalShop.lwcCheck(e.getBlock().getRelative(BlockFace.DOWN), e.getPlayer())
					&& !PhysicalShop.locketteCheck(e.getBlock().getRelative(BlockFace.DOWN), e.getPlayer())) {
				PhysicalShop.sendMessage(e.getPlayer(), "EXISTING_CHEST");
				e.setCancelled(true);
				return;
			}
			if (PhysicalShop.getPluginConfig().isAutoFillName()) {
				if(PhysicalShop.getPluginConfig().isExtendedNames()) {
					try {
						e.setLine(3, NameCollection.getSignName(e.getPlayer().getName()));
					} catch (final OutOfEntriesException ex) {
						PhysicalShop.logSevere("Player " + e.getPlayer() + " cannot register extended name!");
						e.getPlayer().sendMessage("Name overflow, notify server administrator!");
						e.setCancelled(true);
					}
				} else {
					e.setLine(3, ShopHelpers.truncateName(e.getPlayer().getName()));
				}
			}
		}
	}
}

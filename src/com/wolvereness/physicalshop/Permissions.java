package com.wolvereness.physicalshop;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.wolvereness.util.PermissionHandler;

/**
 *
 */
public class Permissions extends PermissionHandler {

	/**
	 * Permission node for admin
	 */
	public final String CAN_ADMIN = this.getPermission("admin", PermissionDefault.OP);
	/**
	 * Permission node for build
	 */
	public final String CAN_BUILD = this.getPermission("build", PermissionDefault.TRUE);
	/**
	 * Permission node for use
	 */
	public final String CAN_USE = this.getPermission("use", PermissionDefault.TRUE);
	Permissions(final PhysicalShop plugin) {
		super(plugin);

	}

	/**
	 * Checks to see if player has admin permission
	 * @param p player to consider
	 * @return true if player is a shop admin, default isOp without Permissions(Yeti)
	 */
	public boolean hasAdmin(final Player p) {
		return checkPerm(p, CAN_ADMIN);
	}

	/**
	 * checks to see if player has shop build permission
	 * @param p player to consider
	 * @return true if player may build shops, default true without Permissions(Yeti)
	 */
	public boolean hasBuild(final Player p) {
		return checkPerm(p, CAN_BUILD);
	}

	/**
	 * Checks to see if player has shop use permission
	 * @param p player to consider
	 * @return true if player may use shops, default true without Permissions(Yeti)
	 */
	public boolean hasUse(final Player p) {
		return checkPerm(p, CAN_USE);
	}

}

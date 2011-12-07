package com.wolvereness.physicalshop;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;

/**
 *
 */
public class Permissions {

	private String canAdmin;
	private String canBuild;
	private String canUse;
	private PermissionHandler permissions = null;

	Permissions(final PhysicalShop plugin) {
		final String pluginName = plugin.getDescription().getName();

		final PluginManager manager = plugin.getServer().getPluginManager();
		final Plugin test = manager.getPlugin("Permissions");
		manager.addPermission(new Permission(canBuild = pluginName + ".build",PermissionDefault.TRUE));
		manager.addPermission(new Permission(canAdmin = pluginName + ".admin",PermissionDefault.OP));
		manager.addPermission(new Permission(canUse = pluginName + ".use",PermissionDefault.TRUE));

		if (test != null) {
			permissions = ((com.nijikokun.bukkit.Permissions.Permissions) test)
					.getHandler();
		}
	}

	/**
	 * Checks to see if player has admin permission
	 * @param p player to consider
	 * @return true if player is a shop admin, default isOp without Permissions(Yeti)
	 */
	public boolean hasAdmin(final Player p) {
		if (permissions != null) return permissions.has(p, canAdmin);
		return p.hasPermission(canAdmin);
	}

	/**
	 * checks to see if player has shop build permission
	 * @param p player to consider
	 * @return true if player may build shops, default true without Permissions(Yeti)
	 */
	public boolean hasBuild(final Player p) {
		if (permissions != null) return permissions.has(p, canBuild);
		return p.hasPermission(canBuild);
	}

	/**
	 * Checks to see if player has shop use permission
	 * @param p player to consider
	 * @return true if player may use shops, default true without Permissions(Yeti)
	 */
	public boolean hasUse(final Player p) {
		if (permissions != null) return permissions.has(p, canUse);
		return p.hasPermission(canUse);
	}

}

package com.wolvereness.util;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * @author Wolfe
 *
 */
public class PermissionHandler {
	/**
	 * @author Wolfe
	 * Interface for returning a PermissionHandler
	 */
	public interface Plugin extends org.bukkit.plugin.Plugin {
		/**
		 * Gets the permission handler this plugin is using
		 * @return The permission handler
		 */
		public PermissionHandler getPermissionHandler();
	}
	private final com.nijiko.permissions.PermissionHandler permissions;
	private final Plugin plugin;
	/**
	 * Makes a new PermissionHandler
	 * @param plugin Base plugin to use
	 */
	public PermissionHandler(final Plugin plugin) {
		this.plugin = plugin;
		final org.bukkit.plugin.Plugin test = plugin.getServer().getPluginManager().getPlugin("Permissions");
		if(test != null) {
			permissions = ((com.nijikokun.bukkit.Permissions.Permissions) test).getHandler();
		} else {
			permissions = null;
		}
	}
	/**
	 * Checks player for given permission
	 * @param otherPlayer player to check
	 * @param perm permission to check
	 * @return true if the specified player has specified permission
	 */
	public boolean checkPerm(final Player otherPlayer, final String perm) {
		if(otherPlayer == null || perm == null || perm.isEmpty()) throw new IllegalArgumentException();
		return permissions == null ? otherPlayer.hasPermission(perm) : permissions.has(otherPlayer, perm);
	}
	/**
	 * Creates a permission based on the name and the registered plugin with default value
	 * @param permissionName Name to use for the permission
	 * @param def Default value
	 * @return Full name for the permission
	 */
	public String getPermission(final String permissionName, final PermissionDefault def) {
		final String permission = String.format("%1$s.%2$s",plugin.getDescription().getName(),permissionName);
		if(plugin.getServer().getPluginManager().getPermission(permission) == null) {
			plugin.getServer().getPluginManager().addPermission(new Permission(permission,def));
		} else {
			plugin.getServer().getPluginManager().getPermission(permission).setDefault(def);
		}
		return permission;
	}
}

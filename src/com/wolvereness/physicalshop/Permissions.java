package com.wolvereness.physicalshop;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;

public class Permissions {

	private PermissionHandler permissions = null;
	private Permission canBuild;
	private Permission canUse;
	private Permission canAdmin;
	private final String pluginName;

	Permissions(final PhysicalShop plugin) {
		pluginName = plugin.getDescription().getName();

		final Plugin test = plugin.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (test != null) {
			permissions = ((com.nijikokun.bukkit.Permissions.Permissions) test)
					.getHandler();
		} else {
			canBuild = new Permission(pluginName + ".build",PermissionDefault.TRUE);
			canAdmin = new Permission(pluginName + ".admin",PermissionDefault.OP);
			canUse = new Permission(pluginName + ".use",PermissionDefault.TRUE);
		}
	}

	public boolean hasAdmin(final Player p) {
		if (permissions != null) {
			return permissions.has(p, pluginName + ".admin");
		} else {
			return p.hasPermission(canAdmin);
		}
	}

	public boolean hasBuild(final Player p) {
		if (permissions != null) {
			return permissions.has(p, pluginName + ".build");
		} else {
			return p.hasPermission(canBuild);
		}
	}

	public boolean hasUse(final Player p) {
		if (permissions != null) {
			return permissions.has(p, pluginName + ".use");
		} else {
			return p.hasPermission(canUse);
		}
	}

}

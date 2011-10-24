package com.wolvereness.physicalshop;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;

public class Permissions {

	private PermissionHandler permissions = null;
	private String canBuild;
	private String canUse;
	private String canAdmin;

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

	public boolean hasAdmin(final Player p) {
		if (permissions != null) {
			return permissions.has(p, canAdmin);
		} else {
			return p.hasPermission(canAdmin);
		}
	}

	public boolean hasBuild(final Player p) {
		if (permissions != null) {
			return permissions.has(p, canBuild);
		} else {
			return p.hasPermission(canBuild);
		}
	}

	public boolean hasUse(final Player p) {
		if (permissions != null) {
			return permissions.has(p, canUse);
		} else {
			return p.hasPermission(canUse);
		}
	}

}

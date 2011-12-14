package com.wolvereness.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.wolvereness.util.CommandHandler.Reset.Resettable;
import com.wolvereness.util.CommandHandler.Verbose.Verbosable;
import com.wolvereness.util.PermissionHandler.Plugin;

/**
 * @author Wolfe
 *
 */
public interface CommandHandler {
	/**
	 * @author Wolfe
	 * Used to indicate a plugin will accept all CommandHandler commands
	 */
	public interface Commandable extends Resettable, Verbosable {

	}
	/**
	 * @author Wolfe
	 *
	 */
	public class Reload extends ShortCommand {
		private final Plugin plugin;
		/**
		 * @see com.wolvereness.util.CommandHandler
		 */
		@SuppressWarnings("javadoc")
		public Reload(final Plugin plugin) {
			super(plugin.getPermissionHandler(), "reload", PermissionDefault.OP);
			this.plugin = plugin;
		}
		@Override
		public boolean go(final CommandSender sender) {
			plugin.reloadConfig();
			sender.sendMessage(plugin.getDescription().getFullName() + " reloaded.");
			return true;
		}
	}
	/**
	 * @author Wolfe
	 * Command handler to Reset the current status of a {@link Resettable} plugin.
	 */
	public class Reset extends ShortCommand {
		/**
		 * @author Wolfe
		 * Implemented by plugins that are able to be reset.
		 */
		public interface Resettable extends Plugin {
			/**
			 * Should reset the implementing plugin
			 */
			public void commandReset();
		}
		private final Resettable plugin;
		/**
		 * Makes a new reset command for specified plugin
		 * @param plugin plugin that can be reset later
		 */
		public Reset(final Resettable plugin) {
			super(plugin.getPermissionHandler(), "reset", PermissionDefault.OP);
			this.plugin = plugin;
		}
		@Override
		public boolean go(final CommandSender sender) {
			plugin.commandReset();
			sender.sendMessage(plugin.getDescription().getFullName() + " has been reset.");
			return true;
		}

	}
	/**
	 * @author Wolfe
	 * A short command that should need no further arguments
	 */
	public abstract class ShortCommand implements CommandHandler {
		private static boolean permissionMessage(final CommandSender sender, final boolean checkPerm) {
			if(!checkPerm) {
				sender.sendMessage("You don't have permission to do that.");
			}
			return !checkPerm;
		}
		private final String PERMISSION;
		/**
		 * Super constructor for a ShortCommand
		 * @param permissionHandler Permission Handler to use
		 * @param permission Permission to assign
		 * @param def default status of permission
		 */
		protected ShortCommand(final PermissionHandler permissionHandler, final String permission, final PermissionDefault def) {
			PERMISSION = permissionHandler.getPermission(permission, def);
		}
		/**
		 * Executes the command assuming permission has been checked
		 * @param sender whoever sent the command
		 * @return true if valid command, false otherwise
		 */
		public abstract boolean go(final CommandSender sender);
		@Override
		public boolean onCommand(final CommandSender sender, final String[] args, final PermissionHandler permissionHandler) {
			return sender instanceof Player ? (permissionMessage(sender,permissionHandler.checkPerm((Player) sender, PERMISSION)) || go(sender)) : go(sender);
		}
	}
	/**
	 * @author Wolfe
	 * Command handler to print a readout of current information for a {@link Verbosable} plugin.
	 */
	public class Verbose extends ShortCommand {
		/**
		 * @author Wolfe
		 * Used to indicate something able to print verbose
		 */
		public interface Verbosable extends Plugin{
			/**
			 * Prints out any useful information
			 * @param sender person to send verbose to
			 */
			public void verbose(CommandSender sender);
		}
		private final Verbosable plugin;

		/**
		 * Makes a new verbose command for provided verbosable plugin
		 * @param plugin plugin to use to verbose
		 */
		public Verbose(final Verbosable plugin) {
			super(plugin.getPermissionHandler(), "verbose", PermissionDefault.OP);
			this.plugin = plugin;
		}
		@Override
		public boolean go(final CommandSender sender) {
			plugin.verbose(sender);
			return true;
		}
	}
	/**
	 * @author Wolfe
	 *
	 */
	public class Version implements CommandHandler {
		private final String MESSAGE;
		/**
		 * Defaults the message format to {@code "%2$s version %1$s by Wolvereness, an original Minefire.net plugin"}
		 * @see Version#Version(Plugin,String)
		 */
		@SuppressWarnings("javadoc")
		public Version(final Plugin plugin) {
			this(plugin,"%2$s version %1$s by Wolvereness, an original Minefire.net plugin");
		}
		/**
		 * Makes a new Version command handler
		 * @param plugin Plugin used for version
		 * @param formatBase The string format to use for the message
		 */
		public Version(final Plugin plugin, final String formatBase) {
			MESSAGE = String.format(formatBase,plugin.getDescription().getVersion(),plugin.getDescription().getName());
		}
		@Override
		public boolean onCommand(final CommandSender sender, final String[] args,final PermissionHandler permissionHandler) {
			sender.sendMessage(MESSAGE);
			return true;
		}
	}
	/**
	 * Executes command from specified sender
	 * @param sender the command sender
	 * @param args the arguments used
	 * @param permissionHandler the permission handler to use
	 * @return true if valid command, false otherwise
	 */
	public boolean onCommand(CommandSender sender, String[] args, PermissionHandler permissionHandler);
}

package com.wolvereness.physicalshop;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.wolvereness.physicalshop.config.LocaleConfig;
import com.wolvereness.physicalshop.config.MaterialConfig;
import com.wolvereness.physicalshop.config.StandardConfig;
import com.wolvereness.physicalshop.listeners.PhysicalShopBlockListener;
import com.wolvereness.physicalshop.listeners.PhysicalShopEntityListener;
import com.wolvereness.physicalshop.listeners.PhysicalShopPlayerListener;
import com.wolvereness.util.CommandHandler;
import com.wolvereness.util.CommandHandler.Reload;
import com.wolvereness.util.CommandHandler.Verbose;
import com.wolvereness.util.CommandHandler.Verbose.Verbosable;
import com.wolvereness.util.CommandHandler.Version;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

/**
 *
 */
public class PhysicalShop extends JavaPlugin implements Verbosable {

	private static StandardConfig configuration;
	private static Consumer consumer = null;
	private static LocaleConfig locale;
	private static Lockette lockette = null;
	private static boolean logblockChecked = false;
	/**
	 * logger that this plugin uses
	 */
	protected final static Logger logger = Logger.getLogger("Minecraft");
	private static LWCPlugin lwc = null;
	private static Permissions permissions;
	/**
	 * Command to reload PhysicalShop
	 */
	public static final String RELOAD_COMMAND = "RELOAD";
	/**
	 * Command to print verbose
	 */
	public static final String VERBOSE_COMMAND = "VERBOSE";
	/**
	 * Command to get version
	 */
	public static final String VERSION_COMMAND = "VERSION";
	/**
	 * This function checks for LogBlock if not already found after plugin
	 * enabled
	 *
	 * @return LogBlock consumer
	 */
	public static Consumer getLogBlock() {
		try {
			if (!logblockChecked) {
				logblockChecked = true;
				if (configuration.isLogBlocked()) {
					final Plugin logblockPlugin = Bukkit.getServer().getPluginManager().getPlugin("LogBlock");
					if (logblockPlugin == null) {
						logWarning("Failed to find LogBlock");
						return null;
					}
					consumer = ((LogBlock) logblockPlugin).getConsumer();
					if (consumer == null) {
						logWarning("Error getting LogBlock consumerco");
					} else {
						log("Sucessfully hooked into LogBlock");
					}
				} else {
					log("Did not hook into LogBlock");
				}
			}
			return consumer;
		} catch (final ClassCastException e) {
			logWarning("Error classtyping LogBlock!"); // This is a bit of a sanity check...
			return null;
		} catch (final NullPointerException e) {
			logSevere("Logblock not found");
			return null;
		}
	}
	/**
	 * Returns name of plugin.
	 *
	 * @return "PhysicalShop"
	 */
	public static String getName() {
		return "PhysicalShop";
	}
	/**
	 * Grabs the current StandardConfig being used.
	 *
	 * @return the configuration being used
	 */
	public static StandardConfig getPluginConfig() {
		return PhysicalShop.configuration;
	}
	/**
	 * Method used to hook into lockette
	 * @param relative the block to consider
	 * @param player player to consider
	 * @return true if and only if lockette is enabled and player owns said block
	 */
	public static boolean locketteCheck(final Block relative, final Player player) {
		return lockette != null && player.getName().equals(Lockette.getProtectedOwner(relative));
	}
	/**
	 * Sends txt to console, log level INFO
	 *
	 * @param txt text to send
	 */
	public static void log(final String txt) {
		logger.log(Level.INFO, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Sends txt to console, log level SERVERE
	 *
	 * @param txt text to send
	 */
	public static void logSevere(final String txt) {
		logger.log(Level.SEVERE, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Sends txt to console, log level WARNING
	 *
	 * @param txt text to send
	 */
	public static void logWarning(final String txt) {
		logger.log(Level.WARNING, String.format("[%s] %s", getName(), txt));
	}
	/**
	 * This function checks for LWC, thus letting player create shop over
	 * existing chest
	 *
	 * @param block
	 *            Block representing chest
	 * @param player
	 *            Player creating sign
	 * @return Returns true if not protect-existing-chest or if LWC enabled and protection exists and
	 *         player is admin of chest
	 */
	public static boolean lwcCheck(final Block block, final Player player) {
		if (lwc == null) return false;
		//LWC lwc = ((LWCPlugin) lwcPlugin).getLWC();
		final Protection protection = lwc.getLWC().findProtection(block);
		if (protection == null)	return false; // no protection here
		return lwc.getLWC().canAdminProtection(player, protection);
	}

	/**
	 * Sends message to player cs
	 *
	 * @param cs player to consider
	 * @param message message to send
	 */
	public static void sendMessage(final CommandSender cs, final String message) {
		try {
			cs.sendMessage(locale.getPhrase(message));
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		} catch (final NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends messsage, formatted from args, to player cs
	 *
	 * @param cs player to consider
	 * @param message message name to send
	 * @param args stuff to format into message
	 */
	public static void sendMessage(final CommandSender cs, final String message, final Object... args)
	{
		try {
			cs.sendMessage(String.format(locale.getPhrase(message), args));
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		} catch (final NullPointerException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Returns previously obtained permission set.
	 * @return the permission handler being used
	 */
	public static Permissions staticGetPermissionHandler() {
		return PhysicalShop.permissions;
	}
	private final PhysicalShopBlockListener blockListener = new PhysicalShopBlockListener();
	private final HashMap<String,CommandHandler> commands = new HashMap<String,CommandHandler>();
	private final PhysicalShopEntityListener entityListener = new PhysicalShopEntityListener();
	private final PhysicalShopPlayerListener playerListener = new PhysicalShopPlayerListener();
	@Override
	public Permissions getPermissionHandler() {
		return PhysicalShop.permissions;
	}
	/**
	 * This will capture the only command, /physicalshop. It will send version information to the sender, and it checks permissions and reloads config if there is proper permission to.
	 * @param sender Player / Console sending command
	 * @param command ignored
	 * @param label ignored
	 * @param args ignored
	 * @return true, errors are handled manually.
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		final String subCommand = args.length == 0 ? VERSION_COMMAND : args[0].toUpperCase();
		if(commands.containsKey(subCommand))
			return commands.get(subCommand).onCommand(sender, args, staticGetPermissionHandler());
		return false;
	}

	/**
	 * Does nothing as of yet.
	 */
	@Override
	public void onDisable() {
	}

	/**
	 * Initialization routine
	 */
	@Override
	public void onEnable() {
		try
		{
			reloadConfig();
			PhysicalShop.permissions = new Permissions(this);
			//Events
			final PluginManager pm = getServer().getPluginManager();
			pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
			pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
			pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
			//Commands
			commands.put(RELOAD_COMMAND, new Reload(this));
			commands.put(VERSION_COMMAND, new Version(this,"%2$s version %1$s by Wolvereness, original by yli"));
			commands.put(VERBOSE_COMMAND, new Verbose(this));
			//Hooks
			Plugin temp = getServer().getPluginManager().getPlugin("LWC");
			if(temp != null && temp instanceof LWCPlugin) {
				lwc = (LWCPlugin) temp;
			}
			temp = getServer().getPluginManager().getPlugin("Lockette");
			if(temp != null && temp instanceof Lockette) {
				lockette = (Lockette) temp;
			}
			log(String.format("version %s enabled", getDescription().getVersion()));
		} catch (final RuntimeException t) {
			t.printStackTrace();
			throw t;
		}
	}
	@Override
	public void onLoad() {
	}
	@Override
	public void reloadConfig() {
		configuration = new StandardConfig(this.getClassLoader());
		locale = new LocaleConfig(configuration.getLanguage(), this.getClassLoader());
		new MaterialConfig();
		logblockChecked = false;
		super.reloadConfig();
	}
	public void verbose(final CommandSender sender) {
		ShopMaterial.verbose(sender);
	}

}

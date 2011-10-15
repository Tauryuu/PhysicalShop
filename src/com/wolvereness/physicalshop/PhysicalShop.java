package com.wolvereness.physicalshop;

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
import com.wolvereness.physicalshop.Permissions;
import com.wolvereness.physicalshop.ShopMaterial;
import com.wolvereness.physicalshop.listeners.PhysicalShopBlockListener;
import com.wolvereness.physicalshop.listeners.PhysicalShopEntityListener;
import com.wolvereness.physicalshop.listeners.PhysicalShopPlayerListener;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

public class PhysicalShop extends JavaPlugin {

	private static StandardConfig configuration;
	private static ShopMaterial currency;
	private static Permissions permissions;
	private static Consumer consumer = null;
	private static boolean logblockChecked = false;
	private static Lockette lockette = null;
	private static LocaleConfig locale;
	private static LWCPlugin lwc = null;

	private final PhysicalShopBlockListener blockListener = new PhysicalShopBlockListener();
	private final PhysicalShopEntityListener entityListener = new PhysicalShopEntityListener();
	private final PhysicalShopPlayerListener playerListener = new PhysicalShopPlayerListener();

	/**
	 * Initialization routine
	 */
	@Override
	public void onEnable() {
		try
		{
			loadConfig(getClassLoader());

			PhysicalShop.permissions = new Permissions(this);
			final PluginManager pm = getServer().getPluginManager();
			pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.BLOCK_BURN, blockListener, Priority.Normal, this);
			pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
			pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
			pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
			Plugin temp = getServer().getPluginManager().getPlugin("LWC");
			if(temp != null && temp instanceof LWCPlugin)
				lwc = (LWCPlugin) temp;
			temp = getServer().getPluginManager().getPlugin("Lockette");
			if(temp != null && temp instanceof Lockette)
				lockette = (Lockette) temp;
			log(String.format("version %s enabled", getDescription().getVersion()));
			logblockChecked = false;
		} catch (RuntimeException t) {
			t.printStackTrace();
			throw t;
		}
	}

	private static void loadConfig(ClassLoader classLoader) {
		PhysicalShop.configuration = new StandardConfig();
//		log("Loaded config");
		// PhysicalShop.currency = PhysicalShop.configuration.getCurrency();
//		log("Loaded permissions");
//		try
//		{
			locale = new LocaleConfig(configuration.getLanguage(), classLoader);
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
//		log("Loaded locales");
		
	}

	/**
	 * Does nothing as of yet.
	 */
	@Override
	public void onDisable() {
		/*
		 * try { configuration.save(); } catch (Throwable t) {
		 * t.printStackTrace(); }//
		 */
	}

	@Override
	public void onLoad() {
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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(String.format("PhysicalShop version %1$s by Wolvereness, original by yli",getDescription().getVersion()));
		if(sender instanceof Player && !permissions.hasAdmin((Player) sender)) {
			sendMessage(sender,"CANT_RELOAD_CONFIG");
			return true;
		}
		loadConfig(getClassLoader());
		sendMessage(sender,"CONFIG_RELOADED");
		return true;
	}

	/**
	 * Sends message to player cs
	 * 
	 * @param cs
	 * @param message
	 */
	public static void sendMessage(CommandSender cs, String message) {
		try {
			cs.sendMessage(locale.getPhrase(message));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends messsage, formatted from args, to player cs
	 * 
	 * @param cs
	 * @param message
	 * @param args
	 */
	public static void sendMessage(CommandSender cs, final String message, final Object... args)
	{
		try {
			cs.sendMessage(String.format(locale.getPhrase(message), args));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Grabs the current StandardConfig being used.
	 * 
	 * @return
	 */
	public static StandardConfig getPluginConfig() {
		return PhysicalShop.configuration;
	}

	/**
	 * Currency is now per-shop.
	 * 
	 * @return
	 */
	@Deprecated
	public static ShopMaterial getCurrency() {
		return PhysicalShop.currency;
	}
	
	/**
	 * Returns previously obtained permission set.
	 * @return
	 */
	public static Permissions getPermissions() {
		return PhysicalShop.permissions;
	}

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
					consumer = ((LogBlock) Bukkit.getServer().getPluginManager().getPlugin("LogBlock")).getConsumer();
					if (consumer == null) {
						logWarning("Failed to hook into LogBlock");
					} else {
						log("Sucessfully hooked into LogBlock");
					}
				} else {
					log("Did not hook into LogBlock");
				}
			}
			return consumer;
		} catch (ClassCastException e) {
			logWarning("Error classtyping LogBlock!"); // This is a bit of a sanity check...
			return null;
		} catch (NullPointerException e) {
			logSevere("Logblock not found");
			return null;
		}
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
	public static boolean lwcCheck(Block block, Player player) {
		if (lwc == null) return false;
		//LWC lwc = ((LWCPlugin) lwcPlugin).getLWC();
		Protection protection = lwc.getLWC().findProtection(block);
		if (protection == null)	return false; // no protection here
		return lwc.getLWC().canAdminProtection(player, protection);
	}

	protected final static Logger logger = Logger.getLogger("Minecraft");

	/**
	 * Sends txt to console, log level INFO
	 * 
	 * @param txt
	 */
	public static void log(String txt) {
		logger.log(Level.INFO, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Sends txt to console, log level SERVERE
	 * 
	 * @param txt
	 */
	public static void logSevere(String txt) {
		logger.log(Level.SEVERE, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Sends txt to console, log level WARNING
	 * 
	 * @param txt
	 */
	public static void logWarning(String txt) {
		logger.log(Level.WARNING, String.format("[%s] %s", getName(), txt));
	}

	/**
	 * Returns name of plugin.
	 * 
	 * @return
	 */
	public static String getName() {
		return "PhysicalShop";
	}

	public static boolean locketteCheck(Block relative, Player player) {
		return lockette != null && player.getName().equals(Lockette.getProtectedOwner(relative));
	}

}

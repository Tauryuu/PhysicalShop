package com.wolvereness.physicalshop;

import org.bukkit.command.CommandSender;

@Deprecated
public class Messaging {

	public static String BUY;
	public static String BUY_RATE;
	public static String CANT_BUILD;
	public static String CANT_BUILD_SERVER;
	public static String CANT_DESTROY;
	public static String CANT_PLACE_CHEST;
	public static String CANT_USE;
	public static String CANT_USE_CHEST;
	public static String CHEST_INVENTORY_FULL;
	public static String DEPOSIT;
	public static String EXISTING_CHEST;
	public static String NO_BUY;
	public static String NO_SELL;
	public static String NOT_ENOUGH_PLAYER_ITEMS;
	public static String NOT_ENOUGH_PLAYER_MONEY;
	public static String NOT_ENOUGH_SHOP_ITEMS;
	public static String NOT_ENOUGH_SHOP_MONEY;
	public static String PLAYER_INVENTORY_FULL;
	public static String SELL;
	public static String SELL_RATE;
	public static String STATUS;
	public static String WITHDRAW;
	private static CommandSender sender;

	@Deprecated
	public static void save(final CommandSender sender) {
		Messaging.sender = sender;
	}

	@Deprecated
	public static void send(final String string) {
		if (Messaging.sender != null) {
			Messaging.sender.sendMessage(string);
		}
	}

	@Deprecated
	public static void send(final String string, final Object... args) {
		if (Messaging.sender != null) {
			Messaging.send(String.format(string, args));
		}
	}

}

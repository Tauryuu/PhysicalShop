package com.wolvereness.physicalshop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;
import org.bukkit.material.Leaves;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.Wool;

import com.wolvereness.physicalshop.exception.InvalidMaterialException;
import com.wolvereness.physicalshop.exception.InvalidSignException;
/**
 *
 */
public class ShopMaterial {

	private static HashMap<Character, ShopMaterial> currencies = null;
	private static HashMap<String, ShopMaterial> identifiers = null;
	private static final Pattern junkCharacters = Pattern.compile("[^A-Za-z0-9:_]");

	private static HashMap<ShopMaterial, String> names = null;

	private static final Pattern spaces = Pattern.compile("\\s+");

	/**
	 * Adds currency represented by item.
	 * @param currencyIdentifier character to use as a reference
	 * @param item name of the item to reference
	 */
	public static void addCurrency(final char currencyIdentifier, final String item) {
		try {
			currencies.put(Character.valueOf(currencyIdentifier), new ShopMaterial(junkCharacters.matcher(spaces.matcher(item).replaceAll("_")).replaceAll("").toUpperCase()));
		} catch (final InvalidMaterialException e) {
			PhysicalShop.logSevere("Configuration error for shop currency:'"+currencyIdentifier+"' for item:"+item);
		}
	}

	/**
	 * Adds an alias to use to reference a shop material
	 * @param alias string to use as reference
	 * @param item name of the item to reference
	 */
	public static void addShopMaterialAlias(String alias, final String item) {
		alias = junkCharacters.matcher(spaces.matcher(alias).replaceAll("_")).replaceAll("").toUpperCase();
		try {
			identifiers.put(alias,getShopMaterial(item, false));
		} catch (final InvalidMaterialException e) {
			PhysicalShop.logWarning("Configuration error for material alias: "+alias+" mapping to: "+item);
			//e.printStackTrace();
		}
	}

	private static String checkPattern(final String string) throws InvalidMaterialException {
		final Matcher m = PhysicalShop.getPluginConfig().getMaterialPattern().matcher(string);

		if (!m.find()) throw new InvalidMaterialException();


		return m.group(1);
	}
	/**
	 * Searches for ShopMaterial associated with currency character.
	 * @param currencyIdentifier The character the shop will be associated with.
	 * @return ShopMaterial Associated with the currencyIdentifier
	 * @throws InvalidSignException If the currency isn't listed.
	 */
	public static ShopMaterial getCurrency(final char currencyIdentifier) throws InvalidSignException {
		final Character searchCharacter = Character.valueOf(currencyIdentifier);
		if(!currencies.containsKey(searchCharacter)) throw new InvalidSignException();
		return currencies.get(searchCharacter);
	}

	/**
	 * Retrieves the material based on a name.
	 * @param name Name to search / interpret
	 * @return ShopMaterial that should be associated with the name.
	 * @throws InvalidMaterialException is name is invalid
	 */
	public static ShopMaterial getShopMaterial(final String name) throws InvalidMaterialException {
		return getShopMaterial(name,true);
	}

	/**
	 * Retrieves the material based on a name.
	 * @param name Name to search / interpret
	 * @param checkPattern Checks to see if name matches pattern
	 * @return ShopMaterial that should be associated with the name.
	 * @throws InvalidMaterialException if name is invalid
	 */
	public static ShopMaterial getShopMaterial(String name, final boolean checkPattern) throws InvalidMaterialException {
		name = junkCharacters.matcher(spaces.matcher(checkPattern ? checkPattern(name) : name).replaceAll("_")).replaceAll("").toUpperCase();
		if(identifiers.containsKey(name))
			return identifiers.get(name);
		return new ShopMaterial(name);
	}
	@Deprecated
	private static Short parseDurability(final String string,final Material material) {
		try {
			return Short.parseShort(string);
		} catch (final NumberFormatException e) {
		}

		final String s = string.replace(" ", "_").toUpperCase();
		MaterialData data = null;

		try {
			switch (material) {
			case COAL:
				data = new Coal(CoalType.valueOf(s));
				break;
			case LOG:
				data = new Tree(TreeSpecies.valueOf(s));
				break;
			case LEAVES:
				data = new Leaves(TreeSpecies.valueOf(s));
				break;
			case STEP:
			case DOUBLE_STEP:
				data = new Step(Material.valueOf(s));
				break;
			case INK_SACK:
				data = new Dye();
				((Dye) data).setColor(DyeColor.valueOf(s));
				break;
			case WOOL:
				data = new Wool(DyeColor.valueOf(s));
				break;
			}
		} catch (final IllegalArgumentException e) {
		}

		return data == null ? 0 : (short) data.getData();
	}//*/

	/**
	 * Blanks the current currencies
	 * @param load initial size of the hashmap
	 */
	public static void resetCurrencies(final int load) {
		currencies = new HashMap<Character,ShopMaterial>(load);
	}

	/**
	 * Blanks the current identifiers
	 * @param load initial size of the hashmap
	 */
	public static void resetIdentifiers(final int load) {
		identifiers = new HashMap<String,ShopMaterial>(load);
	}

	/**
	 * Blanks the cur rent names
	 * @param load initial size of the hashmap
	 */
	public static void resetNames(final int load) {
		names = new HashMap<ShopMaterial,String>(load);
	}

	/**
	 * Sets the name that a shop material should display
	 * @param material material name to reference
	 * @param name the name to give the material
	 */
	public static void setMaterialName(final String material, final String name) {
		try {
			names.put(getShopMaterial(material, false), name);
		} catch (final InvalidMaterialException e) {
			PhysicalShop.logWarning("Configuration error for material name: "+name+" mapping from: "+material);
		}
	}

	private static String toHumanReadableString(final Object object) {
		final StringBuilder sb = new StringBuilder();

		for (final String word : object.toString().split("_")) {
			sb.append(word.substring(0, 1).toUpperCase()
					+ word.substring(1).toLowerCase() + " ");
		}

		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
	/**
	 * Prints a large amount of output for debugging purposes
	 * @param sender The person to send the output to
	 */
	public static void verbose(final CommandSender sender) {
		for(final Map.Entry<Character, ShopMaterial> currency : currencies.entrySet()) {
			sender.sendMessage(currency.getValue().toStringDefault(new StringBuilder().append(currency.getKey()).append(" represents ")).toString());
		}
		for(final Entry<String, ShopMaterial> identifier : identifiers.entrySet()) {
			sender.sendMessage(identifier.getValue().toStringDefault(new StringBuilder().append(identifier.getKey()).append(" can be used for ")).toString());
		}
		for(final Entry<ShopMaterial, String> name : names.entrySet()) {
			sender.sendMessage(name.getKey().toStringDefault(new StringBuilder()).append(" is printed as ").append(name.getValue()).toString());
		}
	}
	private final short durability;
	private final Map<Enchantment, Integer> enchantment;

	private final Material material;
	/**
	 *
	 * @param c character representing shop currency
	 * @throws InvalidSignException thrown if character doesn't exist
	 */
	@Deprecated
	public ShopMaterial(final char c) throws InvalidSignException
	{
		final String materialString[] = PhysicalShop.getPluginConfig().getMaterialCode(c).split(":");
		material = Material.matchMaterial(materialString[0]);
		enchantment = null;
		if(material == null)
		{
			PhysicalShop.logSevere("Configuration error for currency:"+c);
			throw new InvalidSignException();
		}
		if(materialString.length > 1)
		{
			durability = Short.parseShort(materialString[1]);
		} else {
			durability = 0;
		}
	}
	/**
	 * @param itemStack items to derive this material from
	 */
	public ShopMaterial(final ItemStack itemStack) {
		this(itemStack.getType(), itemStack.getDurability(), itemStack.getEnchantments());
	}
	/*
	 * Deprecated because of enchantment
	 * @param material bukkit material to reference
	 * @param durability durability to reference
	 *
	@Deprecated
	public ShopMaterial(final Material material, final byte durability) {
		this(material, durability, null);
	}//*/
	/**
	 * @param material bukkit material to reference
	 * @param durability durability to reference
	 * @param enchantment enchantment to reference
	 */
	public ShopMaterial(final Material material, final short durability, final Map<Enchantment,Integer> enchantment) {
		this.material = material;
		this.durability = durability;
		this.enchantment = enchantment == null ? null : enchantment.isEmpty() ? null : enchantment;
	}
	private ShopMaterial(final String string) throws InvalidMaterialException {
		enchantment = null;
		final String[] strings = string.split(":");

		if (strings.length == 2) {
			material = Material.matchMaterial(strings[0]);
			durability = Short.parseShort(strings[1]);
			return;
		}

		Material material = null;

		for (int i = 0; i < string.length(); ++i) {
			if ((i == 0) || (string.charAt(i) == ' ')) {
				material = Material.matchMaterial(string.substring(i).trim());

				if (material != null) {
					this.material = material;
					durability = ShopMaterial.parseDurability(string.substring(0, i).trim(), material);
					return;
				}
			}
		}

		throw new InvalidMaterialException();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ShopMaterial)) return false;
		final ShopMaterial other = (ShopMaterial) obj;
		if (durability != other.durability) return false;
		if (material != other.material) return false;
		if (enchantment == other.enchantment) return true;
		if (enchantment == null) return false;
		if (!enchantment.equals(other.enchantment)) return false;
		return true;
	}

	/**
	 * @return the durability for this material
	 */
	public short getDurability() {
		return durability;
	}

	/**
	 * @return the bukkit material for this material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param amount size to set the stack to
	 * @return an item stack representing this material
	 */
	public ItemStack getStack(final int amount) {
		if(amount == 0) return null;
		final ItemStack stack = new ItemStack(getMaterial(), amount,getDurability());
		if(enchantment == null) return stack;
		stack.addUnsafeEnchantments(enchantment);
		return stack;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Integer(material.getId()).hashCode() ^ new Short(durability).hashCode();
	}
	@Override
	public String toString() {

		if(names.containsKey(this)) return names.get(this);
		return ShopMaterial.toHumanReadableString(toStringDefault(new StringBuilder()).toString());
	}

	private StringBuilder toStringDefault(final StringBuilder sb) {
		switch (material) {
		case COAL:
			sb.append(new Coal(material, (byte) durability).getType().toString());
			return sb;
		case LOG:
			sb.append(new Tree(material, (byte) durability).getSpecies().toString()).append('_');
			break;
		case LEAVES:
			sb.append(new Leaves(material, (byte) durability).getSpecies().toString()).append('_');
			break;
		case STEP:
		case DOUBLE_STEP:
			sb.append(new Step(material, (byte) durability).getMaterial().toString()).append('_');
			break;
		case INK_SACK:
			sb.append(new Dye(material, (byte) durability).getColor().toString()).append('_');
			break;
		case WOOL:
			sb.append(new Wool(material, (byte) durability).getColor().toString()).append('_');
			break;
		}
		return sb.append(material.toString());
	}
}

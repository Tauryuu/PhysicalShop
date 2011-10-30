package com.wolvereness.physicalshop;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
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

public class ShopMaterial {

	private static HashMap<Character, ShopMaterial> currencies = null;
	private static HashMap<String, ShopMaterial> identifiers = null;
	private static HashMap<ShopMaterial, String> names = null;
	
	private static byte parseDurability(final String string,final Material material) {
		try {
			return Byte.parseByte(string);
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

		return data == null ? 0 : data.getData();
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (material.getId() << 8) | durability;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ShopMaterial)) {
			return false;
		}
		final ShopMaterial other = (ShopMaterial) obj;
		if (durability != other.durability) {
			return false;
		}
		if (material != other.material) {
			return false;
		}
		return true;
	}

	private final byte durability;
	private final Material material;
	
	public ShopMaterial(final ItemStack itemStack) {
		material = itemStack.getType();
		durability = (byte) itemStack.getDurability();
	}

	public ShopMaterial(final Material material, final byte durability) {
		this.material = material;
		this.durability = durability;
	}
	
	@Deprecated
	public ShopMaterial(final char c) throws InvalidSignException
	{
		String materialString[] = PhysicalShop.getPluginConfig().getMaterialCode(c).split(":");
		material = Material.matchMaterial(materialString[0]);
		if(material == null)
		{
			PhysicalShop.logSevere("Configuration error for currency:"+c);
			throw new InvalidSignException();
		}
		if(materialString.length > 1)
		{
			durability = Byte.parseByte(materialString[1]);
		} else {
			durability = 0;
		}
	}

	private ShopMaterial(final String string) throws InvalidMaterialException {
		final String[] strings = string.split(":");

		if (strings.length == 2) {
			material = Material.matchMaterial(strings[0]);
			durability = ShopMaterial.parseDurability(strings[1], material);
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

	public short getDurability() {
		return durability;
	}

	public Material getMaterial() {
		return material;
	}

	public ItemStack getStack(final int amount) {
		return amount != 0 ? new ItemStack(getMaterial(), amount,
				getDurability()) : null;
	}

	@Override
	public String toString() {
		
		if(names.containsKey(this)) {
			return names.get(this);
		}
		final StringBuilder sb = new StringBuilder();
		final MaterialData data = material.getNewData(durability);

		switch (material) {
		case COAL:
			sb.append(((Coal) data).getType().toString());
			break;
		case LOG:
			sb.append(((Tree) data).getSpecies().toString() + "_"
					+ material.toString());
			break;
		case LEAVES:
			sb.append(((Leaves) data).getSpecies().toString() + "_"
					+ material.toString());
			break;
		case STEP:
		case DOUBLE_STEP:
			sb.append(((Step) data).getMaterial().toString() + "_"
					+ material.toString());
			break;
		case INK_SACK:
			sb.append(((Dye) data).getColor().toString() + "_"
					+ material.toString());
			break;
		case WOOL:
			sb.append(((Wool) data).getColor().toString() + "_"
					+ material.toString());
			break;
		default:
			sb.append(material.toString());
			break;
		}

		return ShopMaterial.toHumanReadableString(sb.toString());
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
	 * Adds currency represented by item.
	 * @param currencyIdentifier
	 * @param item
	 */
	public static void addCurrency(final char currencyIdentifier, String item) {
		try {
			currencies.put(Character.valueOf(currencyIdentifier), new ShopMaterial(junkCharacters.matcher(spaces.matcher(item).replaceAll("_")).replaceAll("").toUpperCase()));
		} catch (InvalidMaterialException e) {
			PhysicalShop.logSevere("Configuration error for shop currency:'"+currencyIdentifier+"' for item:"+item);
		}
	}

	private static final Pattern spaces = Pattern.compile("\\s+");
	private static final Pattern junkCharacters = Pattern.compile("[^A-Za-z0-9:_]");
	/**
	 * Adds an alias to use to reference a shop material
	 * @param alias
	 * @param item
	 */
	public static void addShopMaterialAlias(String alias, String item) {
		alias = junkCharacters.matcher(spaces.matcher(alias).replaceAll("_").replace('|',':')).replaceAll("").toUpperCase();
		try {
			identifiers.put(alias,getShopMaterial(item, false));
		} catch (InvalidMaterialException e) {
			PhysicalShop.logWarning("Configuration error for material alias: "+alias+" mapping to: "+item);
			//e.printStackTrace();
		}
	}
	/**
	 * Sets the name that a shop material should display
	 * @param material
	 * @param name
	 */
	public static void setMaterialName(String material, String name) {
		try {
			names.put(getShopMaterial(material, false), name);
		} catch (InvalidMaterialException e) {
			PhysicalShop.logWarning("Configuration error for material name: "+name+" mapping from: "+material);
		}
	}
	/**
	 * Retrieves the material based on a name.
	 * @param name Name to search / interpret
	 * @return ShopMaterial that should be associated with the name.
	 * @throws InvalidMaterialException
	 */
	public static ShopMaterial getShopMaterial(String name) throws InvalidMaterialException {
		return getShopMaterial(name,true);
	}

	/**
	 * Retrieves the material based on a name.
	 * @param name Name to search / interpret
	 * @param checkPattern Checks to see if name matches pattern
	 * @return ShopMaterial that should be associated with the name.
	 * @throws InvalidMaterialException
	 */
	public static ShopMaterial getShopMaterial(String name, boolean checkPattern) throws InvalidMaterialException {
		name = junkCharacters.matcher(spaces.matcher(checkPattern ? checkPattern(name) : name).replaceAll("_")).replaceAll("").toUpperCase();
		if(identifiers.containsKey(name)) 
			return identifiers.get(name);
		return new ShopMaterial(name);
	}
	
	/**
	 * Blanks the current names
	 * @param load
	 */
	public static void resetNames(int load) {
		names = new HashMap<ShopMaterial,String>(load);
	}
	
	/**
	 * Blanks the current identifiers
	 * @param load
	 */
	public static void resetIdentifiers(int load) {
		identifiers = new HashMap<String,ShopMaterial>(load);
	}
	
	/**
	 * Blanks the current currencies
	 * @param load
	 */
	public static void resetCurrencies(int load) {
		currencies = new HashMap<Character,ShopMaterial>(load);
	}
	private static String checkPattern(String string) throws InvalidMaterialException {
		final Matcher m = PhysicalShop.getPluginConfig().getMaterialPattern().matcher(string);

		if (!m.find()) {
			throw new InvalidMaterialException();
		}


		return m.group(1);
	}
}

package com.wolvereness.physicalshop;

import java.util.regex.Matcher;

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

	private static byte parseDurability(final String string,
			final Material material) {
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
	
	public ShopMaterial(final char c) throws InvalidSignException
	{
		String materialString[] = PhysicalShop.getConfig().getMaterialCode(c).split(":");
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

	public ShopMaterial(final String string) throws InvalidMaterialException {
		final Matcher m = PhysicalShop.getConfig().getMaterialPattern()
				.matcher(string);

		if (!m.find()) {
			throw new InvalidMaterialException();
		}

		final String matchString = m.group(1);
		final String[] strings = matchString.split(":");

		if (strings.length == 2) {
			material = Material.matchMaterial(strings[0]);
			durability = ShopMaterial.parseDurability(strings[1], material);
			return;
		}

		Material material = null;

		for (int i = 0; i < matchString.length(); ++i) {
			if ((i == 0) || (matchString.charAt(i) == ' ')) {
				material = Material.matchMaterial(matchString.substring(i)
						.trim());

				if (material != null) {
					this.material = material;
					durability = ShopMaterial.parseDurability(matchString
							.substring(0, i).trim(), material);
					return;
				}
			}
		}

		throw new InvalidMaterialException();
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ShopMaterial)) {
			return false;
		}

		final ShopMaterial item = (ShopMaterial) object;

		return (getMaterial() == item.getMaterial())
				&& (getDurability() == item.getDurability());
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
}

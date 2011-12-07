package com.wolvereness.physicalshop;

/**
 *
 */
public class Rate {
	private final int amount;
	private final int price;
	/**
	 * Creates a Rate to represent amount of material and price measured in currency.
	 * @param amount
	 * @param price
	 */
	Rate(final int amount, final int price) {
		this.amount = amount;
		this.price = price;
	}
	/**
	 * Returns the amount of shop material associated with this Rate.
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}
	/**
	 * Returns the amount of shop material associated with this Rate that can be purchased with quantity of currency.
	 * @param currency amount of currency to consider
	 * @return the figured amount
	 */
	public int getAmount(final int currency) {
		return price != 0 ? currency / price * amount : 0;
	}
	/**
	 * Returns the price from the currency associated with this Rate.
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	/**
	 * Returns the price from the currency associated with this Rate of how much said items would cost.
	 * @param items the number of items to consider
	 * @return the figured price
	 */
	public int getPrice(final int items) {
		return amount != 0 ? items / amount * price : 0;
	}

}

package com.wolvereness.physicalshop.exception;

/**
 *
 */
public class InvalidExchangeException extends Exception {

	/**
	 * Used to represent why an exchange failed
	 */
	public enum Type {
		/**
		 * Failed to add
		 */
		ADD,
		/**
		 * Failed to remove
		 */
		REMOVE
	}

	private static final long serialVersionUID = -728948381002934316L;
	private final Type type;

	@SuppressWarnings("javadoc")
	public InvalidExchangeException(final Type type) {
		this.type = type;
	}

	/**
	 * @return gives the reason for this failure
	 */
	public Type getType() {
		return type;
	}

}

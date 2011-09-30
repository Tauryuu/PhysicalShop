package com.wolvereness.physicalshop.exception;

public class InvalidExchangeException extends Exception {

	public enum Type {
		ADD, REMOVE
	}

	private static final long serialVersionUID = -728948381002934316L;
	private final Type type;

	public InvalidExchangeException(final Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

}

package com.norcode.bukkit.subdivision.flag;

public class StringFlag extends Flag<String> {

	protected StringFlag(String name) {
		super(name);
	}

	protected StringFlag(String name, String description) {
		super(name, description);
	}

	@Override
	public String parseValue(String input) throws IllegalArgumentException {
		return input;
	}

	@Override
	public String serializeValue(Object value) {
		return (String) value;
	}

	@Override
	public String getValue(Object value) {
		return (String) value;
	}
}

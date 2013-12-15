package com.norcode.bukkit.subdivision.flag;


public class BooleanFlag extends Flag<Boolean> {
	@Override
	public Boolean parseValue(String input) throws IllegalArgumentException {
		return Boolean.parseBoolean(input);
	}

	@Override
	public String serializeValue(Object value) {
		return Boolean.toString((Boolean) value);
	}

	@Override
	public Boolean getValue(Object value) {
		return (Boolean) value;
	}
}

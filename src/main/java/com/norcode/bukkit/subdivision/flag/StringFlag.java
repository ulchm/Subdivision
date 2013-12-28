package com.norcode.bukkit.subdivision.flag;

import com.norcode.bukkit.subdivision.region.Region;

public class StringFlag extends Flag<String> {

	protected StringFlag(String name) {
		super(name);
	}

	protected StringFlag(String name, String description) {
		super(name, description);
	}

	@Override
	public String get(Region r) {
		String val = (String) r.getFlag(this);
		return val;
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

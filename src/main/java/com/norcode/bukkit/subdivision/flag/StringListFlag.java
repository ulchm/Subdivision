package com.norcode.bukkit.subdivision.flag;

import java.util.List;

public class StringListFlag extends Flag<List<String>> {

	protected StringListFlag(String name) {
		super(name);
	}

	protected StringListFlag(String name, String description) {
		super(name, description);
	}

	@Override
	public List<String> parseValue(String input) throws IllegalArgumentException {
		List results = gson.fromJson(input, List.class);
		return (List<String>) results;
	}

	@Override
	public String serializeValue(Object value) {
		return gson.toJson(value);
	}

	@Override
	public List<String> getValue(Object value) {
		return (List<String>) value;
	}
}

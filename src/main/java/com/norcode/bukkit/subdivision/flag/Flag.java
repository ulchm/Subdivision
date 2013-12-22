package com.norcode.bukkit.subdivision.flag;

import com.google.gson.Gson;
import com.norcode.bukkit.subdivision.SubdivisionPlugin;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class Flag<T> {

	private static LinkedHashMap<String, Flag> registry = new LinkedHashMap<String, Flag>();
	protected static Gson gson = new Gson();

	protected String name;
	protected String description;

	public abstract T parseValue(String input) throws IllegalArgumentException;
	public abstract String serializeValue(Object value);
	public abstract T getValue(Object value);

	protected Flag(String name) {
		this.name = name;
	}

	protected Flag(String name, String description) {
		this(name);
		this.description = description;
	}

	public static boolean register(Flag flag) {
		if (registry.containsKey(flag.getName())) {
			return false;
		}
		registry.put(flag.getName().toLowerCase(), flag);
		return true;
	}

	public String getName() {
		return name;
	}

	public static Flag fromKey(String flagKey) {
		return registry.get(flagKey.toLowerCase());
	}


	public static void setupFlags(SubdivisionPlugin subdivisionPlugin) {
		for (Flag f: registry.values()) {
			f.onEnable(subdivisionPlugin);
		}
	}

	protected void onEnable(SubdivisionPlugin plugin) {
	}

	protected void onDisable() {}
}

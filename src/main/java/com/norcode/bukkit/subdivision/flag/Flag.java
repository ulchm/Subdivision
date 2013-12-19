package com.norcode.bukkit.subdivision.flag;

import com.google.gson.Gson;

import java.util.HashMap;

public abstract class Flag<T> {

	private static HashMap<String, Flag> registry = new HashMap<String, Flag>();
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

	public static void register(Flag flag) throws AlreadyRegisteredException {
		if (registry.containsKey(flag.getName())) {
			throw new AlreadyRegisteredException("A flag is already registered under the name '" + flag.getName() + '"');
		}
		registry.put(flag.getName().toLowerCase(), flag);
	}

	public String getName() {
		return name;
	}

	public static Flag fromKey(String flagKey) {
		return registry.get(flagKey.toLowerCase());
	}

	public static class AlreadyRegisteredException extends Exception {
		public AlreadyRegisteredException() {
		}

		public AlreadyRegisteredException(String message) {
			super(message);
		}

		public AlreadyRegisteredException(String message, Throwable cause) {
			super(message, cause);
		}

		public AlreadyRegisteredException(Throwable cause) {
			super(cause);
		}

		public AlreadyRegisteredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}
	}
}

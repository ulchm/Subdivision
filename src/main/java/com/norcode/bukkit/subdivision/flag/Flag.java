package com.norcode.bukkit.subdivision.flag;

import com.google.gson.Gson;

import java.util.HashMap;

public abstract class Flag<T> {

	private static HashMap<String, Class<? extends Flag>> registry = new HashMap<String, Class<? extends Flag>>();

	protected static Gson gson = new Gson();

	public abstract T parseValue(String input) throws IllegalArgumentException;
	public abstract String serializeValue(Object value);
	public abstract T getValue(Object value);

}

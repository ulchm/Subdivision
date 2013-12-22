package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.flag.Flag;

public class PermissionFlag extends Flag<RegionPermissionState> {

	protected PermissionFlag(String name, String desc) {
		super(name, desc);
	}

	@Override
	public RegionPermissionState parseValue(String input) throws IllegalArgumentException {
		return RegionPermissionState.valueOf(input.toUpperCase());
	}

	@Override
	public String serializeValue(Object value) {
		return ((RegionPermissionState) value).name();
	}

	@Override
	public RegionPermissionState getValue(Object value) {
		return (RegionPermissionState) value;
	}


}

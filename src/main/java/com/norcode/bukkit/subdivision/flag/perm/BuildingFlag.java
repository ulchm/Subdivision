package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.Flag;

public class BuildingFlag extends PermissionFlag {

	public static BuildingFlag flag = new BuildingFlag();

	private BuildingFlag() {
		super("building", "Building");
		Flag.register(flag);
	}
}

package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.flag.Flag;
import org.bukkit.Material;

import java.util.EnumSet;

public class FarmingFlag extends PermissionFlag {

	public static FarmingFlag flag = new FarmingFlag();

	private FarmingFlag() {
		super("farming", "Farming");
	}
}

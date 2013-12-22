package com.norcode.bukkit.subdivision.flag;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;

public class MobSpawningFlag extends BooleanFlag {

	public static MobSpawningFlag flag = new MobSpawningFlag();

	private MobSpawningFlag() {
		super("mobspawning", "Mob spawning");
			Flag.register(flag);
	}

}
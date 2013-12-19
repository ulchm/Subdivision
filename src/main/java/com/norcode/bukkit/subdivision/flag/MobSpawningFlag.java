package com.norcode.bukkit.subdivision.flag;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;

public class MobSpawningFlag extends BooleanFlag {

	public static MobSpawningFlag flag = new MobSpawningFlag();

	private MobSpawningFlag() {
		super("mobspawning", "Mob spawning");
		try {
			Flag.register(flag);
		} catch (AlreadyRegisteredException e) {
			SubdivisionPlugin.debug("Failed to register: " + flag.getName() + " (Already registered)");
		}
	}

}
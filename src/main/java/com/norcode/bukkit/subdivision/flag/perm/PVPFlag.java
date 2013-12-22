package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.Flag;

public class PVPFlag extends PermissionFlag {

	public static PVPFlag flag = new PVPFlag();

	private PVPFlag() {
		super("pvp", "PVP");
		Flag.register(flag);
	}
}

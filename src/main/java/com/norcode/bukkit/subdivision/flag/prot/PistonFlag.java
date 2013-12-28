package com.norcode.bukkit.subdivision.flag.prot;

import com.norcode.bukkit.subdivision.flag.BooleanFlag;

public class PistonFlag extends ProtectionFlag {
	public static PistonFlag flag = new PistonFlag();
	private PistonFlag() {
		super("pistons", "Pistons");
	}
}

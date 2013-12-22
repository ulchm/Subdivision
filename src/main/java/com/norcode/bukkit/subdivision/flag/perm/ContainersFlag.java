package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.Flag;

public class ContainersFlag extends PermissionFlag {

	public static ContainersFlag flag = new ContainersFlag();

	private ContainersFlag() {
		super("containers", "Containers");
		Flag.register(flag);
	}
}

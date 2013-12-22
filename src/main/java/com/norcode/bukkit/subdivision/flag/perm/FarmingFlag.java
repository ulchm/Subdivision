package com.norcode.bukkit.subdivision.flag.perm;

import com.norcode.bukkit.subdivision.flag.Flag;
import org.bukkit.Material;

import java.util.EnumSet;

public class FarmingFlag extends PermissionFlag {

	public static FarmingFlag flag = new FarmingFlag();

	public static EnumSet<Material> FARMABLE_BLOCKS = EnumSet.of(
			Material.CROPS, Material.SUGAR_CANE_BLOCK, Material.RED_ROSE,
			Material.YELLOW_FLOWER, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM,
			Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.LONG_GRASS,
			Material.COCOA, Material.MELON_BLOCK, Material.PUMPKIN);

	private FarmingFlag() {
		super("farming", "Farming");
		Flag.register(flag);
	}
}

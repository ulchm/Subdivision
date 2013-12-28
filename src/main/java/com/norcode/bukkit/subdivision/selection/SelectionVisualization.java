package com.norcode.bukkit.subdivision.selection;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.region.CuboidSelection;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.norcode.bukkit.subdivision.rtree.Point3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashSet;

public class SelectionVisualization extends BoundsWireframe {

	private MaterialData blockType;
	private SubdivisionPlugin plugin;
	private Player player;
	private World world;

	private int drawStep = 0;

	public SelectionVisualization(SubdivisionPlugin plugin, Player player, World world, Bounds bounds) {
		super(bounds);
		this.plugin = plugin;
		this.player = player;
		this.world = world;

	}

	public boolean draw() {
		Point3D blk = step(drawStep++);
		if (blk == null) {
			drawStep --;
			return false;
		}
		if (drawStep <= 8) {
			this.player.sendBlockChange(new Location(world, (int) blk.getX(), (int) blk.getY(), (int) blk.getZ()), Material.GLOWSTONE, (byte) 0);
		} else {
			this.player.sendBlockChange(new Location(world, (int) blk.getX(), (int) blk.getY(), (int) blk.getZ()), Material.STAINED_GLASS, (byte) 4);
		}
		return true;
	}

	public boolean undraw() {
		Point3D blk = step(--drawStep);
		if (blk == null) {
			drawStep ++;
			return false;
		}
		Location l = new Location(world, (int) blk.getX(), (int) blk.getY(), (int) blk.getZ());
		Block b = world.getBlockAt(l);
		this.player.sendBlockChange(l, b.getType(), b.getData());
		return true;
	}

}

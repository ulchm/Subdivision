package com.norcode.bukkit.subdivision.region;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.rtree.Bounded;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CuboidSelection implements Bounded {

	Location p1;
	Location p2;

	@Override
	public Bounds getBounds() {
		if (!isValid()) return null;
		Location min = getMin();
		Location max = getMax();
		return new Bounds(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
	}

	public CuboidSelection(Location p1, Location p2) {
		this(p1);
		this.p2 = p2;
	}

	public CuboidSelection(Location p1) {
		this();
		this.p1 = p1;
	}

	public CuboidSelection() {}

	public Location getP1() {
		return p1;
	}

	public void setP1(Location p1) {
		this.p1 = p1;
	}

	public Location getP2() {
		return p2;
	}

	public void setP2(Location p2) {
		this.p2 = p2;
	}

	public Location getMin() {
		if (!isValid()) {
			return null;
		}
		return new Location(p1.getWorld(),
				Math.min(p1.getBlockX(), p2.getBlockX()),
				Math.min(p1.getBlockY(), p2.getBlockY()),
				Math.min(p1.getBlockZ(), p2.getBlockZ()));
	}

	public Location getMax() {
		if (!isValid()) return null;
		return new Location(p1.getWorld(),
				Math.max(p1.getBlockX(), p2.getBlockX()),
				Math.max(p1.getBlockY(), p2.getBlockY()),
				Math.max(p1.getBlockZ(), p2.getBlockZ()));
	}

	public int getVolume() {
		if (!isValid()) return -1;
		return getArea() * (getHeight());
	}

	public World getWorld() {
		if (p1 != null) {
			return p1.getWorld();
		} else if (p2 != null) {
			return p2.getWorld();
		}
		return null;
	}

	public int getArea() {
		if (!isValid()) return -1;
		return getWidth() * getLength() * getHeight();
	}

	public int getLength() {
		if (!isValid()) return -1;
		return 1 + (getMax().getBlockZ() - getMin().getBlockZ());
	}

	public int getWidth() {
		if (!isValid()) return -1;
		return 1 + (getMax().getBlockX() - getMin().getBlockX());
	}

	public int getHeight() {
		if (!isValid()) return -1;
		return 1 + (getMax().getBlockY() - getMin().getBlockY());
	}

	public boolean isValid() {
		return (p1 != null && p2 != null && p1.getWorld().getUID().equals(p2.getWorld().getUID()));
	}

	public static CuboidSelection fromWorldEdit(SubdivisionPlugin plugin, Player player) {
		WorldEditPlugin we = plugin.getWorldEdit();
		if (we != null) {
			Selection sel = we.getSelection(player);
			if (sel instanceof com.sk89q.worldedit.bukkit.selections.CuboidSelection) {
				CuboidSelection cs = new CuboidSelection();
				return new CuboidSelection(
					sel.getMinimumPoint(),
					sel.getMaximumPoint()
				);
			}
		}
		return null;
	}
}

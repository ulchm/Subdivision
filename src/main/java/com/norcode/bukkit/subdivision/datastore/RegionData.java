package com.norcode.bukkit.subdivision.datastore;

import com.norcode.bukkit.subdivision.rtree.Bounds;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RegionData {

	private final int minX;
	private final int minY;
	private final int minZ;

	private final int maxX;
	private final int maxY;
	private final int maxZ;

	private final UUID id;
	private final UUID parentId;
	private final UUID worldId;

	private final int priority;
	private final Set<UUID> owners;
	private final Set<UUID> members;
	private final Map<String, String> flags;

	public RegionData(
			int minX, int minY, int minZ,
			int maxX, int maxY, int maxZ,
			UUID id,
			UUID parentId,
			UUID worldId,
			int priority,
			Set<UUID> owners,
			Set<UUID> members, Map<String, String> flags) {

		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.id = id;
		this.parentId = parentId;
		this.worldId = worldId;
		this.priority = priority;
		this.owners = owners;
		this.members = members;
		this.flags = flags;
	}

	public RegionData(World world, Bounds bounds) {
		this.id = UUID.randomUUID();
		this.worldId = world.getUID();
		this.minX = bounds.getX1();
		this.minY = bounds.getY1();
		this.minZ = bounds.getZ1();
		this.maxX = bounds.getX2();
		this.maxY = bounds.getY2();
		this.maxZ = bounds.getZ2();
		this.parentId = null;
		this.flags = new HashMap<String, String>();
		this.owners = new HashSet<UUID>();
		this.members = new HashSet<UUID>();
		this.priority = 0;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}

	public UUID getId() {
		return id;
	}

	public UUID getParentId() {
		return parentId;
	}

	public UUID getWorldId() {
		return worldId;
	}

	public int getPriority() {
		return priority;
	}

	public Set<UUID> getOwners() {
		return owners;
	}

	public Set<UUID> getMembers() {
		return members;
	}

	public Map<String, String> getFlags() {
		return flags;
	}

	public Bounds getBounds() {
		return new Bounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
}

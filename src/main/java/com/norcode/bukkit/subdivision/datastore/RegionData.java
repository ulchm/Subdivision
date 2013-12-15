package com.norcode.bukkit.subdivision.datastore;

import com.norcode.bukkit.subdivision.flag.Flag;

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
	private final Map<String, String> flags;

	public RegionData(
			int minX, int minY, int minZ,
			int maxX, int maxY, int maxZ,
			UUID id,
			UUID parentId,
			UUID worldId,
			int priority,
			Set<UUID> owners,
			Map<String, String> flags) {

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
		this.flags = flags;
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

	public Map<String, String> getFlags() {
		return flags;
	}
}

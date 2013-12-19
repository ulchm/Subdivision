package com.norcode.bukkit.subdivision.region;


import com.norcode.bukkit.subdivision.rtree.Bounded;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.norcode.bukkit.subdivision.datastore.RegionData;
import com.norcode.bukkit.subdivision.flag.Flag;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Region implements Bounded {
	private int minX;
	private int minY;
	private int minZ;
	private int maxX;
	private int maxY;
	private int maxZ;
	private int priority;
	private UUID id;
	private UUID worldId;
	private UUID parentId;
	private Set<UUID> owners;
	private Map<Flag, Object> flags = new HashMap<Flag, Object>();

	public Region(RegionData data) {
		minX = data.getMinX();
		minY = data.getMinY();
		minZ = data.getMinZ();
		maxX = data.getMaxX();
		maxY = data.getMaxY();
		maxZ = data.getMaxZ();
		id = data.getId();
		parentId = data.getParentId();
		priority = data.getPriority();
		worldId = data.getWorldId();
		owners = new HashSet<UUID>(data.getOwners());
		for (String flagKey: data.getFlags().keySet()) {
			Flag flag = Flag.fromKey(flagKey);
			if (flag != null) {
				flags.put(flag, flag.parseValue(data.getFlags().get(flagKey)));
			}
		}
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

	public UUID getWorldId() {
		return worldId;
	}

	public UUID getParentId() {
		return parentId;
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public boolean equals(Region other) {
		if (other instanceof Region) {
			Region otherRegion = (Region) other;
			return otherRegion.id == this.id;
		}
		return false;
	}

	public void addOwner(Player player) {
		owners.add(player.getUniqueId());
	}

	public RegionData getRegionData() {
		HashMap<String, String> serializedFlags = new HashMap<String, String>();
		for (Flag f: flags.keySet()) {
			serializedFlags.put(f.getName(), f.serializeValue(flags.get(f)));
		}
		return new RegionData(minX, minY, minZ, maxX, maxY, maxZ, id, parentId, worldId, priority, owners, serializedFlags);
	}
}
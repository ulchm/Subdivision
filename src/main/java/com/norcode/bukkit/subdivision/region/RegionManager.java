package com.norcode.bukkit.subdivision.region;

import com.norcode.bukkit.subdivision.rtree.Node;
import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.datastore.RegionData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RegionManager {

	private SubdivisionPlugin plugin;
	private HashMap<UUID, Node> worldTrees = new HashMap<UUID, Node>();
	private HashMap<UUID, Region> regionMap = new HashMap<UUID, Region>();
	public RegionManager(SubdivisionPlugin plugin) {
		this.plugin = plugin;
	}

	public void initialize(List<RegionData> data) {
		Region region;
		for (RegionData rdata: data) {
			region = new Region(rdata);
			Node node = getWorldNode(region);
			regionMap.put(region.getId(), region);
			node.insert(region);
		}
	}

	public void add(Region region) {
		Node  node = getWorldNode(region);
		regionMap.put(region.getId(), region);
		node.insert(region);
	}

	public Region remove(Region region) {
		Node node = getWorldNode(region);
		regionMap.remove(region.getId());
		return (Region) node.remove(region);
	}

	public Region getById(UUID uuid) {
		return regionMap.get(uuid);
	}

	public Collection<Region> getAll() {
		return regionMap.values();
	}

	private Node getWorldNode(Region region) {
		Node node = this.worldTrees.get(region.getWorldId());
		if (node == null) {
			node = Node.create();
			this.worldTrees.put(region.getWorldId(), node);
		}
		return node;
	}

	public HashMap<UUID, Node> getWorldTrees() {
		return worldTrees;
	}
}



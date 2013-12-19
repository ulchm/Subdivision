package com.norcode.bukkit.subdivision.region;

import java.util.LinkedList;
import java.util.List;

public class RegionSet {
	List<Region> regions;

	public RegionSet(LinkedList<Region> regions) {
		this.regions = regions;
	}

	public List<Region> getRegions() {
		return regions;
	}
}

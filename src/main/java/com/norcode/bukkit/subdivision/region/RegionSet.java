package com.norcode.bukkit.subdivision.region;

import com.norcode.bukkit.subdivision.flag.perm.PermissionFlag;
import com.norcode.bukkit.subdivision.flag.perm.RegionPermissionState;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RegionSet {

	Region parent = null;
	Region region;

	public RegionSet(LinkedList<Region> regions) {
		if (regions.size() == 1) {
			region = regions.get(0);
		} else {
			if (regions.get(0).getParentId() == null) {
				parent = regions.get(0);
				region = regions.get(1);

			} else {
				parent = regions.get(1);
				region = regions.get(0);
			}
			assert region.getParentId().equals(parent.getId());
		}
	}

	public boolean hasParent() {
		return parent != null;
	}

	public Region getParent() {
		return parent;
	}

	public Region getRegion() {
		return region;
	}

	public RegionPermissionState getPermissionFlag(PermissionFlag flag) {
		RegionPermissionState state = region.getPermissionFlag(flag);
		if (state ==  null && parent != null) {
			return parent.getPermissionFlag(flag);
		}
		return state;
	}
}

package com.norcode.bukkit.subdivision.region;

import com.norcode.bukkit.subdivision.flag.perm.BuildingFlag;
import com.norcode.bukkit.subdivision.flag.perm.PermissionFlag;
import com.norcode.bukkit.subdivision.flag.perm.RegionPermissionState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RegionSet {

	List<Region> regions;

	HashMap<PermissionFlag, RegionPermissionState> cachedPerms = new HashMap<PermissionFlag, RegionPermissionState>();

	public RegionSet(LinkedList<Region> regions) {
		this.regions = regions;
	}

	public List<Region> getRegions() {
		return regions;
	}

	public RegionPermissionState getPermissionFlag(PermissionFlag flag) {
		if (!cachedPerms.containsKey(flag)) {
			RegionPermissionState state = null;
			Region region = null;
			// Figure out what the current state of the perm flag is
			for (Region r: this.regions) {
				if (state == null) {
					state = r.getPermissionFlag(flag);
					region = r;
					continue;
				}
				// TODO: Actually support more than 1 region :)
			}
			// cache it
			cachedPerms.put(flag, state);
		}
		return cachedPerms.get(flag);
	}
}

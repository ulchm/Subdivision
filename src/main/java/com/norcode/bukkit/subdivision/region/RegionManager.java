package com.norcode.bukkit.subdivision.region;

import com.norcode.bukkit.subdivision.flag.Flag;
import com.norcode.bukkit.subdivision.flag.perm.PermissionFlag;
import com.norcode.bukkit.subdivision.flag.perm.RegionPermissionState;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.norcode.bukkit.subdivision.rtree.Node;
import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.datastore.RegionData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionManager {

	private SubdivisionPlugin plugin;
	private HashMap<UUID, GlobalRegion> globalRegions = new HashMap<UUID, GlobalRegion>();
	private HashMap<UUID, Region> regionMap = new HashMap<UUID, Region>();

	HashMap<Flag, Object> universalFlagDefaults = new HashMap<Flag, Object>();

	private LinkedHashMap<Location, Region> cache;
	private int cacheSize = 10;

	public RegionManager(SubdivisionPlugin plugin) {
		this.plugin = plugin;
		this.configure(plugin.getConfig());
		cache = new LinkedHashMap(cacheSize, .75F, true) {
			public boolean removeEldestEntry(Map.Entry eldest) {
				return size() > cacheSize;
			}
		};

	}

	private void configure(FileConfiguration config) {
		cacheSize = config.getInt("cache-size", 10);
		ConfigurationSection flagCfg = config.getConfigurationSection("default-permission-flags");
		if (flagCfg == null) {
			flagCfg = config.createSection("flags");
		}
		ConfigurationSection universal = flagCfg.getConfigurationSection("universal-defaults");
		if (universal == null) {
			universal = flagCfg.createSection("universal-defaults");
		}
		for (Flag f: Flag.getAllFlags()) {
			if (f instanceof PermissionFlag) {
				RegionPermissionState state = ((PermissionFlag) f).parseValue(universal.getString(f.getName()));
				universalFlagDefaults.put(f, state);
			}
		}
		ConfigurationSection global = flagCfg.getConfigurationSection("global-defaults");
		if (global == null) {
			global = flagCfg.createSection("global-defaults");
		}
		for (String key: global.getKeys(false)) {
			World world = plugin.getServer().getWorld(key);
			if (world == null) {
				plugin.getLogger().warning("Unknown world: " + key);
				continue;
			}
			GlobalRegion gr = new GlobalRegion(plugin, world.getUID());
		}
	}

	public void initialize(List<RegionData> data) {
		Region region;
		for (RegionData rdata: data) {
			region = new Region(plugin, rdata);
			getGlobalRegion(region.getWorldId()).addRegion(region);
			regionMap.put(region.getId(), region);
		}
	}

	public void add(Region region) {
		getGlobalRegion(region.getWorldId()).addRegion(region);
		regionMap.put(region.getId(), region);
		invalidateCache(region);
	}

	public void invalidateCache(Region region) {
		Iterator<Map.Entry<Location, Region>> iter = cache.entrySet().iterator();
		Map.Entry<Location, Region> entry;
		Region activeRegion;
		while (iter.hasNext()) {
			entry = iter.next();
			if (region.contains(entry.getKey())) {
				iter.remove();
			}
		}
	}

	public Region remove(Region region) {
		GlobalRegion gr = getGlobalRegion(region.getWorldId());
		regionMap.remove(region.getId());
		invalidateCache(region);
		return (Region) gr.removeRegion(region);
	}

	public Region getById(UUID uuid) {
		return regionMap.get(uuid);
	}

	public Collection<Region> getAll() {
		return regionMap.values();
	}

	public GlobalRegion getGlobalRegion(UUID worldId) {
		if (!globalRegions.containsKey(worldId)) {
			globalRegions.put(worldId, new GlobalRegion(plugin, worldId));
		}
		return globalRegions.get(worldId);
	}

	public Region getRegion(Location loc) {
		if (!cache.containsKey(loc)) {
			GlobalRegion gr = getGlobalRegion(loc.getWorld().getUID());
			List<Region> regions = gr.search(new Bounds(loc.getBlockX(), loc.getBlockY(),
					loc.getBlockZ(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
			if (regions.size() == 0) {
				cache.put(loc, gr);
			} else if (regions.size() == 1) {
				cache.put(loc, regions.get(0));
			} else {
				if (regions.get(0).hasParent()) {
					assert regions.get(1).getId().equals(regions.get(0).getParentId());
					cache.put(loc, regions.get(0));
				} else {
					assert regions.get(0).getId().equals(regions.get(1).getParentId());
					cache.put(loc, regions.get(1));
				}
			}
		}
		return cache.get(loc);
	}

	public RegionPermissionState getUniversalPermissionFlag(Flag f) {
		return ((PermissionFlag) f).getValue(universalFlagDefaults.get(f));
	}

	public int getGlobalRegionCount() {
		return globalRegions.size();
	}
}



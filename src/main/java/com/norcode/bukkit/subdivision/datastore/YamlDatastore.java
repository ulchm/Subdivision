package com.norcode.bukkit.subdivision.datastore;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class YamlDatastore extends Datastore {

	private String fileName;

	public YamlDatastore(SubdivisionPlugin plugin) {
		super(plugin);

	}

	@Override
	protected void onDisable() throws DatastoreException {
		saveConfig();
	}

	@Override
	protected boolean onEnable() throws DatastoreException {
		if (plugin == null)
			throw new IllegalArgumentException("plugin cannot be null");
		if (!plugin.isInitialized())
			throw new IllegalArgumentException("plugin must be initialized");
		this.fileName = "data/regions.yml";
		reloadConfig();
		return true;
	}

	@Override
	protected List<RegionData> loadRegions() throws DatastoreException {

		List<RegionData> results = new LinkedList<RegionData>();
		UUID worldId;
		UUID id;
		UUID parentId;
		Set<UUID> owners;
		Set<UUID> members;
		Map<String, String> flags;
		int minX, minY, minZ, maxX, maxY, maxZ;

		ConfigurationSection cfg;
		ConfigurationSection flagCfg;
		for (String worldIdString: getConfig().getKeys(false)) {
			// top level keys are world UUID's
			worldId = UUID.fromString(worldIdString);

			if (plugin.getServer().getWorld(worldId) == null) {
				// Skip the section if we can't find the world
				plugin.debug("Skipping worldId: " + worldIdString + " (World not found)");
				continue;
			}

			// each the world's regions gets a ConfigurationSection
			for (String regionId: getConfig().getConfigurationSection(worldIdString).getKeys(false)) {
				id = UUID.fromString(regionId);
				cfg = getConfig().getConfigurationSection(worldIdString + "." + regionId);
				List<Integer> bounds = cfg.getIntegerList("bounds");
				minX = bounds.get(0);
				minY = bounds.get(1);
				minZ = bounds.get(2);
				maxX = bounds.get(3);
				maxY = bounds.get(4);
				maxZ = bounds.get(5);
				if (!cfg.contains("parent")) {
					parentId = null;
				} else {
					parentId = UUID.fromString(cfg.getString("parent"));
				}
				owners = new HashSet<UUID>();
				for (String uuidS: cfg.getStringList("owners")) {
					owners.add(UUID.fromString(uuidS));
				}
				members = new HashSet<UUID>();
				for (String uuidS: cfg.getStringList("members")) {
					members.add(UUID.fromString(uuidS));
				}
				flags = new HashMap<String, String>();
				flagCfg = cfg.getConfigurationSection("flags");
				if (flagCfg != null) {
					for (String key: flagCfg.getKeys(false)) {
						flags.put(key.toLowerCase(), flagCfg.getString(key));
					}
				}
				results.add(new RegionData(minX, minY, minZ, maxX, maxY, maxZ, id, parentId, worldId, owners, members, flags));
			}
		}
		return results;
	}

	@Override
	public void saveRegions(List<RegionData> regions) throws DatastoreException {
		ConfigurationSection cfg;
		ConfigurationSection flagCfg;
		ConfigurationSection wcfg;
		List<String> ownerIds;

		for (RegionData data: regions) {
			saveRegionData(data);
		}
		saveConfig();
	}

	private ConfigurationSection saveRegionData(RegionData data) {
		ConfigurationSection cfg = getConfig().createSection(data.getWorldId().toString() + "." + data.getId().toString());
		cfg.set("bounds", Arrays.asList(new Integer[] {data.getMinX(), data.getMinY(), data.getMinZ(), data.getMaxX(), data.getMaxY(), data.getMaxZ()}));
		if (data.getParentId() != null) {
			cfg.set("parent", data.getParentId().toString());
		}
		// owners
		List<String> uids = new ArrayList<String>();
		for (UUID ownerId: data.getOwners()) {
			uids.add(ownerId.toString());
		}
		cfg.set("owners", uids);
		// members
		uids = new ArrayList<String>();
		for (UUID memberId: data.getMembers()) {
			uids.add(memberId.toString());
		}
		cfg.set("members", uids);
		// flags
		if (data.getFlags().size() > 0) {
			ConfigurationSection flagCfg = cfg.createSection("flags");
			for (Map.Entry<String, String> flag: data.getFlags().entrySet()) {
				flagCfg.set(flag.getKey(), flag.getValue());
			}
		}
		return cfg;
	}

	@Override
	public void saveRegion(RegionData region) throws DatastoreException {
		saveRegionData(region);
		saveConfig();
	}

	@Override
	public void deleteRegion(RegionData region) throws DatastoreException {
		String key = region.getWorldId().toString() + "." + region.getId().toString();
		getConfig().set(key, null);
		saveConfig();
	}

	private File configFile;
	private FileConfiguration fileConfiguration;

	private void reloadConfig() {
		if (configFile == null) {
			File dataFolder = plugin.getDataFolder();
			if (dataFolder == null)
				throw new IllegalStateException();
			configFile = new File(dataFolder, fileName);
		}
		fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource(fileName);
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			fileConfiguration.setDefaults(defConfig);
		}
	}

	private FileConfiguration getConfig() {
		if (fileConfiguration == null) {
			this.reloadConfig();
		}
		return fileConfiguration;
	}

	private void saveConfig() {
		if (fileConfiguration == null || configFile == null) {
			return;
		} else {
			try {
				getConfig().save(configFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
			}
		}
	}

	private void saveDefaultConfig() {
		if (!configFile.exists()) {
			this.plugin.saveResource(fileName, false);
		}
	}
}

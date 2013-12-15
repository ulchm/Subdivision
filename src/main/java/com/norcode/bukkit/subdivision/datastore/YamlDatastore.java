package com.norcode.bukkit.subdivision.datastore;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

	YamlDatastore(SubdivisionPlugin plugin) {
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
		Map<String, String> flags;
		int minX, minY, minZ, maxX, maxY, maxZ, priority;

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
				minX = cfg.getInt("min-x");
				minY = cfg.getInt("min-y");
				minZ = cfg.getInt("min-z");
				maxX = cfg.getInt("max-x");
				maxY = cfg.getInt("max-y");
				maxZ = cfg.getInt("max-z");
				if (!cfg.contains("parent")) {
					parentId = null;
				} else {
					parentId = UUID.fromString(cfg.getString("parent"));
				}
				priority = cfg.getInt("priority", 0);
				owners = new HashSet<UUID>();
				for (String uuidS: cfg.getStringList("owners")) {
					owners.add(UUID.fromString(uuidS));
				}
				flags = new HashMap<String, String>();
				flagCfg = cfg.getConfigurationSection("flags");
				for (String key: flagCfg.getKeys(false)) {
					flags.put(key.toLowerCase(), flagCfg.getString(key));
				}
				results.add(new RegionData(minX, minY, minZ, maxX, maxY, maxZ, id, parentId, worldId, priority, owners, flags));
			}
		}
		return results;
	}

	@Override
	protected void saveRegions(List<RegionData> regions) throws DatastoreException {
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
		cfg.set("min-x", data.getMinX());
		cfg.set("min-y", data.getMinY());
		cfg.set("min-z", data.getMinZ());
		cfg.set("max-x", data.getMaxX());
		cfg.set("max-y", data.getMaxY());
		cfg.set("max-z", data.getMaxZ());
		cfg.set("priority", data.getPriority());
		if (data.getParentId() != null) {
			cfg.set("parent", data.getParentId().toString());
		}
		List<String> ownerIds = new ArrayList<String>();
		for (UUID ownerId: data.getOwners()) {
			ownerIds.add(ownerId.toString());
		}
		cfg.set("owners", ownerIds);
		if (data.getFlags().size() > 0) {
			ConfigurationSection flagCfg = cfg.createSection("flags");
			for (Map.Entry<String, String> flag: data.getFlags().entrySet()) {
				flagCfg.set(flag.getKey(), flag.getValue());
			}
		}
		return cfg;
	}

	@Override
	protected void saveRegion(RegionData region) throws DatastoreException {
		saveRegionData(region);
		saveConfig();
	}

	@Override
	protected void deleteRegion(RegionData region) throws DatastoreException {
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

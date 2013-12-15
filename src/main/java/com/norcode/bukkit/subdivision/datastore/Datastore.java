package com.norcode.bukkit.subdivision.datastore;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public abstract class Datastore {

	private static HashMap<String, Class<? extends Datastore>> registry;

	protected static Class<? extends Datastore> getImplementation(String type) throws DatastoreException {
		if (!registry.containsKey(type.toLowerCase())) {
			throw new DatastoreException("No implementation found for datastore.type=" + type.toLowerCase());
		}
		return registry.get(type.toLowerCase());
	}

	protected static void register(String string, Class<? extends Datastore> clazz) throws DatastoreException {
		if (registry.containsKey(string.toLowerCase())) {
			throw new DatastoreException("There is already a datastore implementation registered as " + string.toLowerCase());
		}
	}

	protected SubdivisionPlugin plugin;

	Datastore(SubdivisionPlugin plugin) {
		this.plugin = plugin;
	}

	public final boolean enable() {
		try {
			return this.onEnable();
		} catch (DatastoreException e) {
			e.printStackTrace();
			return false;
		}
	}

	public final void disable() {
		try {
			this.onDisable();
		} catch (DatastoreException e) {
			e.printStackTrace();
		}
	}

	public static Datastore create(SubdivisionPlugin subdivisionPlugin) throws DatastoreException {
		ConfigurationSection cfg = subdivisionPlugin.getConfig().getConfigurationSection("datastore");
		Class<? extends Datastore> impl = getImplementation(cfg.getString("type"));
		Datastore store = null;
		try {
			store = impl.getConstructor(SubdivisionPlugin.class).newInstance(subdivisionPlugin);
		} catch (NoSuchMethodException e) {
			throw new DatastoreException(e);
		} catch (InvocationTargetException e) {
			throw new DatastoreException(e);
		} catch (InstantiationException e) {
			throw new DatastoreException(e);
		} catch (IllegalAccessException e) {
			throw new DatastoreException(e);
		}
		return store;
	}


	protected abstract void onDisable() throws DatastoreException;
	protected abstract boolean onEnable() throws DatastoreException;
	protected abstract List<RegionData> loadRegions() throws DatastoreException;
	protected abstract void saveRegions(List<RegionData> regions) throws DatastoreException;
	protected abstract void saveRegion(RegionData region) throws DatastoreException;
	protected abstract void deleteRegion(RegionData region) throws DatastoreException;

}

package com.norcode.bukkit.subdivision;

import com.norcode.bukkit.subdivision.datastore.Datastore;
import com.norcode.bukkit.subdivision.datastore.DatastoreException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.kerberos.KerberosTicket;

public class SubdivisionPlugin extends JavaPlugin {

	private Datastore datastore;
	private static boolean debugMode = false;

	@Override
	public void onEnable() {
		if (!loadConfig()) {
			this.getServer().getPluginManager().disablePlugin(this);
		} else if (!setupDatastore()) {
			this.getServer().getPluginManager().disablePlugin(this);
		}

	}

	private boolean loadConfig() {
		saveDefaultConfig();
		debugMode = getConfig().getBoolean("debug-mode", false);
		return true;
	}

	private boolean setupDatastore() {
		try {
			datastore = Datastore.create(this);
		} catch (DatastoreException e) {
			e.printStackTrace();
			return false;
		}
		return datastore.enable();
	}

	@Override
	public void onDisable() {
		datastore.disable();
	}

	public static void debug(String s) {
		if (debugMode) {
			Bukkit.getServer().getLogger().info(s);
		}
	}
}

package com.norcode.bukkit.subdivision;

import com.norcode.bukkit.subdivision.command.DebugCommand;
import com.norcode.bukkit.subdivision.command.RegionCommand;
import com.norcode.bukkit.subdivision.datastore.Datastore;
import com.norcode.bukkit.subdivision.datastore.DatastoreException;
import com.norcode.bukkit.subdivision.flag.Flag;
import com.norcode.bukkit.subdivision.listener.PlayerListener;
import com.norcode.bukkit.subdivision.region.CuboidSelection;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.region.RegionManager;
import com.norcode.bukkit.subdivision.region.RegionSet;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.kerberos.KerberosTicket;

public class SubdivisionPlugin extends JavaPlugin {

	private Datastore datastore;
	private RegionManager regionManager;

	private static boolean debugMode = false;

	// Commands
	private DebugCommand debugCommand;
	private RegionCommand regionCommand;

	// Event Listeners
	private PlayerListener playerListener;

	@Override
	public void onEnable() {

		regionManager = new RegionManager(this);

		if (!loadConfig()) {
			this.getServer().getPluginManager().disablePlugin(this);
		} else if (!setupDatastore()) {
			this.getServer().getPluginManager().disablePlugin(this);
		}
		setupCommands();
		setupEvents();

	}

	private void setupEvents() {
		this.playerListener = new PlayerListener(this);
		Flag.setupFlags(this);
	}

	private void setupCommands() {
		this.debugCommand = new DebugCommand(this);
		this.regionCommand = new RegionCommand(this);
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

	public RegionSet getActiveRegionSet(Player player) {
		return (RegionSet) player.getMetadata("subdivisions-active-regionset").get(0).value();
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public WorldEditPlugin getWorldEdit() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin))
			return null;
		return (WorldEditPlugin) plugin;
	}

	public CuboidSelection getPlayerSelection(Player player) {
		if (player.hasMetadata("subdivisions-selection")) {
			return (CuboidSelection) player.getMetadata("subdivisions-selection").get(0).value();
		}
		return null;
	}

	public Datastore getDatastore() {
		return datastore;
	}
}


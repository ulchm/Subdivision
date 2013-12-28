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
import com.norcode.bukkit.subdivision.selection.RenderManager;
import com.norcode.bukkit.subdivision.selection.Renderer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SubdivisionPlugin extends JavaPlugin {

	private Datastore datastore;
	private RegionManager regionManager;

	private static boolean debugMode = false;

	// Commands
	private DebugCommand debugCommand;
	private RegionCommand regionCommand;

	// Event Listeners
	private PlayerListener playerListener;
	private HashMap<Flag, Object> universalFlagDefaults;
	private RenderManager renderManager;
	private BukkitTask renderTask;

	@Override
	public void onEnable() {

		regionManager = new RegionManager(this);

		if (!loadConfig()) {
			this.getServer().getPluginManager().disablePlugin(this);
		}
		setupEvents();
		if (!setupDatastore()) {
			this.getServer().getPluginManager().disablePlugin(this);
		}
		setupCommands();


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
		renderManager = new RenderManager(this);
		renderTask = getServer().getScheduler().runTaskTimer(this, renderManager, 1, 1);
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
		if (renderTask != null) {
			renderTask.cancel();
		}
	}

	public static void debug(String s) {
		if (debugMode) {
			Bukkit.getServer().getLogger().info(s);
		}
	}

	public Region getRegion(Player player) {
		return (Region) player.getMetadata("subdivisions-active-region").get(0).value();
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
		CuboidSelection sel = new CuboidSelection();
		player.setMetadata("subdivisions-selection", new FixedMetadataValue(this, sel));
		return sel;
	}

	public Datastore getDatastore() {
		return datastore;
	}

	public RenderManager getRenderManager() {
		return renderManager;
	}
}


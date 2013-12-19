package com.norcode.bukkit.subdivision.listener;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.region.RegionSet;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.norcode.bukkit.subdivision.rtree.Node;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class PlayerListener implements Listener {

	SubdivisionPlugin plugin;

	public PlayerListener(SubdivisionPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final String playerName = event.getPlayer().getName();
		event.getPlayer().setMetadata("subdivisions-active-regionset", new LazyMetadataValue(plugin, new Callable<Object>() {
			@Override
			public RegionSet call() throws Exception {
				Player player = plugin.getServer().getPlayerExact(playerName);
				Node tree = plugin.getRegionManager().getWorldTrees().get(player.getWorld().getUID());
				if (tree == null) {
					return new RegionSet(new LinkedList<Region>());
				}
				int px,py,pz;
				px = player.getLocation().getBlockX();
				py = player.getLocation().getBlockY();
				pz = player.getLocation().getBlockZ();
				return new RegionSet(tree.search(new Bounds(px,py,pz, px, py, pz)));
			}
		}));
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		Location from = event.getFrom();
		if (from.getBlockX() != to.getBlockX()
				|| from.getBlockZ() != to.getBlockZ()
				|| from.getBlockY() != to.getBlockY()
				|| !from.getWorld().getUID().equals(to.getWorld().getUID())) {
			event.getPlayer().getMetadata("subdivisions-active-regionset").get(0).invalidate();
		}
	}
}
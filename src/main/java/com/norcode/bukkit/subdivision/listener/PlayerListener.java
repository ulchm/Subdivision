package com.norcode.bukkit.subdivision.listener;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.perm.BuildingFlag;
import com.norcode.bukkit.subdivision.flag.perm.ContainersFlag;
import com.norcode.bukkit.subdivision.flag.perm.FarmingFlag;
import com.norcode.bukkit.subdivision.flag.perm.PVPFlag;
import com.norcode.bukkit.subdivision.region.CuboidSelection;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.selection.SelectionVisualization;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.EnumSet;
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
		event.getPlayer().setMetadata("subdivisions-active-region", new LazyMetadataValue(plugin, new Callable<Object>() {
			@Override
			public Region call() throws Exception {
				Player player = plugin.getServer().getPlayerExact(playerName);
				return plugin.getRegionManager().getRegion(player.getLocation());
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
			event.getPlayer().getMetadata("subdivisions-active-region").get(0).invalidate();
		}
	}


	@EventHandler
	public void onPlayerChangeToFromWand(PlayerItemHeldEvent event) {

		ItemStack leaving = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
		ItemStack goingTo = event.getPlayer().getInventory().getItem(event.getNewSlot());

		if (isWandItem(leaving) && !isWandItem(goingTo)) {
			plugin.getRenderManager().getRenderer(event.getPlayer()).clear();
		} else if (isWandItem(goingTo)) {
			CuboidSelection sel = plugin.getPlayerSelection(event.getPlayer());
			if (sel.isValid()) {
				SelectionVisualization viz = new SelectionVisualization(plugin, event.getPlayer(), event.getPlayer().getWorld(), sel.getBounds());
				plugin.getRenderManager().getRenderer(event.getPlayer()).draw(viz);
			}
		}

	}

	@EventHandler
	public void onPlayerClickWand(PlayerInteractEvent event) {
		if (isWandItem(event.getItem())) {
			Player p = event.getPlayer();
			CuboidSelection selection = plugin.getPlayerSelection(p);
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				event.getPlayer().sendMessage("Set Pt.2 to " + event.getClickedBlock().getLocation());
				selection.setP2(event.getClickedBlock().getLocation());
			} else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				event.getPlayer().sendMessage("Set Pt.1 to " + event.getClickedBlock().getLocation());
				selection.setP1(event.getClickedBlock().getLocation());
			} else {
				return;
			}
			event.setCancelled(true);
			if (selection.isValid()) {
				SelectionVisualization viz = new SelectionVisualization(plugin, p, p.getWorld(), selection.getBounds());
				plugin.getRenderManager().getRenderer(p).draw(viz);
			}
		}
	}


	private boolean isWandItem(ItemStack item) {
		return item != null && item.getType().equals(Material.GOLD_SPADE);
	}



}
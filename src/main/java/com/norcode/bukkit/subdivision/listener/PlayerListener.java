package com.norcode.bukkit.subdivision.listener;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.perm.BuildingFlag;
import com.norcode.bukkit.subdivision.flag.perm.ContainersFlag;
import com.norcode.bukkit.subdivision.flag.perm.FarmingFlag;
import com.norcode.bukkit.subdivision.flag.perm.PVPFlag;
import com.norcode.bukkit.subdivision.flag.perm.RegionPermissionState;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import com.norcode.bukkit.subdivision.rtree.Node;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.EnumSet;
import java.util.LinkedList;
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

	@EventHandler(ignoreCancelled=true)
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Material blockType = event.getBlock().getType();
		Region r = plugin.getRegionManager().getRegion(event.getBlock().getLocation());
		if (r.allows(BuildingFlag.flag, p) ||
				(r.allows(FarmingFlag.flag, p) && isBreakingCrop(blockType))) {
			return;
		} else {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You don't have permission to break blocks here.");
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Material blockType = event.getBlock().getType();
		Region r = plugin.getRegionManager().getRegion(event.getBlock().getLocation());
		if (r.allows(BuildingFlag.flag, p) ||
				(r.allows(FarmingFlag.flag, p) && isPlacingCrop(blockType))) {
			return;
		} else {
			event.setCancelled(true);
			p.sendMessage(ChatColor.RED + "You don't have permission to place blocks here.");
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerOpenInventory(InventoryOpenEvent event) {
		if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof BlockState) {
			Location loc = ((BlockState) event.getInventory().getHolder()).getLocation();
			Region rs = plugin.getRegionManager().getRegion(loc);
			if (!rs.allows(ContainersFlag.flag, (Player) event.getPlayer())) {
				event.setCancelled(true);
				((Player)event.getPlayer()).sendMessage(ChatColor.RED +
						"You do not have permission to open containers here.");
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = null;
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
					player = (Player) ((Projectile) event.getDamager()).getShooter();
				}
			}
			Region r = plugin.getRegionManager().getRegion(event.getEntity().getLocation());
			if (!r.allows(PVPFlag.flag, player)) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You do not have PVP permissions here!");
			}
		}
	}

	public static EnumSet<Material> ALLOWED_FARMING_BLOCKBREAKS = EnumSet.of(
		Material.COCOA, Material.MELON_BLOCK, Material.MELON_STEM, Material.PUMPKIN_STEM, Material.PUMPKIN,
		Material.LONG_GRASS, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.HUGE_MUSHROOM_2,
		Material.HUGE_MUSHROOM_1, Material.SUGAR_CANE_BLOCK, Material.CROPS, Material.CACTUS, Material.POTATO,
		Material.CARROT, Material.WHEAT, Material.NETHER_STALK
	);

	public static EnumSet<Material> ALLOWED_FARMING_BLOCKPLACES = ALLOWED_FARMING_BLOCKBREAKS.clone();
	static {
		ALLOWED_FARMING_BLOCKPLACES.remove(Material.PUMPKIN);
		ALLOWED_FARMING_BLOCKPLACES.remove(Material.MELON_BLOCK);
	}

	private boolean isBreakingCrop(Material blockType) {
		return ALLOWED_FARMING_BLOCKBREAKS.contains(blockType);
	}

	public boolean isPlacingCrop(Material blockType) {
		return ALLOWED_FARMING_BLOCKPLACES.contains(blockType);
	}
}
package com.norcode.bukkit.subdivision.listener;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.flag.prot.ExplosionFlag;
import com.norcode.bukkit.subdivision.flag.prot.PistonFlag;
import com.norcode.bukkit.subdivision.flag.perm.BuildingFlag;
import com.norcode.bukkit.subdivision.flag.perm.ContainersFlag;
import com.norcode.bukkit.subdivision.flag.perm.FarmingFlag;
import com.norcode.bukkit.subdivision.flag.perm.PVPFlag;
import com.norcode.bukkit.subdivision.flag.prot.RegionProtectionState;
import com.norcode.bukkit.subdivision.region.GlobalRegion;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.rtree.Bounds;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ProtectionListener implements Listener {
	private SubdivisionPlugin plugin;

	public ProtectionListener(SubdivisionPlugin plugin) {
		this.plugin = plugin;
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

	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent event) {
		Location loc1 = event.getBlocks().get(0).getLocation();
		Location loc2 = event.getBlocks().get(event.getBlocks().size()-1).getLocation();
		Bounds blockBounds = new Bounds(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(),
										loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
		List<Region> regions = plugin.getRegionManager().getGlobalRegion(event.getBlock().getWorld().getUID()).search(blockBounds);
		if (regions.size() > 0) {
			Region pistonRegion = plugin.getRegionManager().getRegion(event.getBlock().getLocation());
			for (Region r: regions) {
				if (PistonFlag.flag.get(r) == RegionProtectionState.DENY) {
					if (!r.equals(pistonRegion)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}


	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent event) {
		Region blockRegion = plugin.getRegionManager().getRegion(event.getBlock().getLocation());
		Region retractRegion = plugin.getRegionManager().getRegion(event.getRetractLocation());
		if (!(blockRegion instanceof GlobalRegion)) {
			if (!retractRegion.equals(blockRegion)) {
				if (PistonFlag.flag.get(blockRegion) == RegionProtectionState.DENY) {
					event.setCancelled(true);
				}
			}
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
			if (player != null) {
				Region r = plugin.getRegionManager().getRegion(event.getEntity().getLocation());
				if (!r.allows(PVPFlag.flag, player)) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "You do not have PVP permissions here!");
				}
			}
		}
	}

	@EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block b: event.blockList()) {
			Region r = plugin.getRegionManager().getRegion(b.getLocation());
			if (ExplosionFlag.flag.get(r) == RegionProtectionState.DENY) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		Entity remover = null;
		Region region = plugin.getRegionManager().getRegion(event.getEntity().getLocation());
		if (event instanceof HangingBreakByEntityEvent) {
			HangingBreakByEntityEvent e = (HangingBreakByEntityEvent) event;
			remover = e.getRemover();
			if (remover instanceof Projectile) {
				remover = ((Projectile) remover).getShooter();
			}
			if (remover instanceof Player) {
				Player player = (Player) remover;
				if (!region.allows(BuildingFlag.flag, player)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {
		Block hangingSpace = event.getEntity().getLocation().getBlock();
		if (event.getEntity() instanceof Painting) {
			int h = ((Painting) event.getEntity()).getArt().getBlockHeight();
			int w = ((Painting) event.getEntity()).getArt().getBlockWidth();
			List<Location> locs = new ArrayList<Location>();
			for (int y=hangingSpace.getY(); y < hangingSpace.getY()+h; y++) {
				switch (event.getBlockFace()) {
				case NORTH:
				case SOUTH:
					int x1 = Math.min(hangingSpace.getX(), hangingSpace.getX()+w);
					int x2 = Math.max(hangingSpace.getX(), hangingSpace.getX()+w);
					for (int x=x1;x<x2;x++) {
						locs.add(new Location(hangingSpace.getWorld(), x, y, hangingSpace.getZ()));
					}
					break;
				case EAST:
				case WEST:
					int z1 = Math.min(hangingSpace.getZ(), hangingSpace.getZ() + w);
					int z2 = Math.max(hangingSpace.getZ(), hangingSpace.getZ() + w);
					for (int z=z1;z<z2;z++) {
						locs.add(new Location(hangingSpace.getWorld(), hangingSpace.getX(), y, z));
					}
					break;
				}
				for (Location l: locs) {
					Region r = plugin.getRegionManager().getRegion(l);
					if (!r.allows(BuildingFlag.flag, event.getPlayer())) {
						event.setCancelled(true);
					}
				}
            }
		}
	}
}
package com.norcode.bukkit.subdivision.command;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.datastore.DatastoreException;
import com.norcode.bukkit.subdivision.datastore.RegionData;
import com.norcode.bukkit.subdivision.region.CuboidSelection;
import com.norcode.bukkit.subdivision.region.GlobalRegion;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.rtree.Node;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;


public class RegionCreateCommand extends BaseCommand {

	public RegionCreateCommand(SubdivisionPlugin plugin) {
		super(plugin,"create", new String[] {"new", "define"}, "subdivisions.command.region.create", new String[] {"Region Create Help"});
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		if (!(commandSender instanceof Player))
			throw new CommandError("This command is only available to players.");

		Player player = (Player) commandSender;
		CuboidSelection selection = null;

		selection = CuboidSelection.fromWorldEdit(plugin, player);
		if (selection == null) {
			throw new CommandError("You have not made a valid selection.");
		}
		RegionData data = new RegionData(selection.getWorld(), selection.getBounds());
		GlobalRegion gr = plugin.getRegionManager().getGlobalRegion(selection.getWorld().getUID());
		List<Region> overlapping = gr.search(data.getBounds());
		if (!overlapping.isEmpty()) {
			// might be in an illegal spot
			if (overlapping.size() > 1 ||
				overlapping.get(0).getBounds().insersects(data.getBounds())) {
				// can't cross borders.
				throw new CommandError("Your selection crosses an existing border");
			}
			// there is only one, and HOPEFULLY were fully contained in it
			Region parent = overlapping.get(0);
			if (!parent.getBounds().contains(data.getBounds())) {
				throw new CommandError("ERROR! Tell metalhedd that intersects is broken!");
			}
			if (!parent.isOwner(player)) {
				throw new CommandError("Your selection is within a region you do not own.");
			}
		}
		Region region = new Region(plugin, data);
		region.addOwner(player);
		plugin.getRegionManager().add(region);
		try {
			plugin.getDatastore().saveRegion(region.getRegionData());
		} catch (DatastoreException e) {
			e.printStackTrace();
		}
	}
}

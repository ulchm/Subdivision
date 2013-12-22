package com.norcode.bukkit.subdivision.command;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.datastore.DatastoreException;
import com.norcode.bukkit.subdivision.datastore.RegionData;
import com.norcode.bukkit.subdivision.region.CuboidSelection;
import com.norcode.bukkit.subdivision.region.Region;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;


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

		Region region = new Region(new RegionData(selection.getWorld(), selection.getBounds()));
		region.addOwner(player);
		plugin.getRegionManager().add(region);
		try {
			plugin.getDatastore().saveRegion(region.getRegionData());
		} catch (DatastoreException e) {
			e.printStackTrace();
		}
	}
}

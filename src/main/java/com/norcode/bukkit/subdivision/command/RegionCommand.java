package com.norcode.bukkit.subdivision.command;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;

public class RegionCommand extends BaseCommand {

	public RegionCommand(SubdivisionPlugin plugin) {
		super(plugin, "region", new String[0], null, new String[] { "Region Help" });
		registerSubcommand(new RegionCreateCommand(plugin));
		plugin.getServer().getPluginCommand("region").setExecutor(this);
	}
}

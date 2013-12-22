package com.norcode.bukkit.subdivision.command;

import com.norcode.bukkit.subdivision.SubdivisionPlugin;
import com.norcode.bukkit.subdivision.region.Region;
import com.norcode.bukkit.subdivision.region.RegionSet;
import com.norcode.bukkit.subdivision.util.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class DebugCommand extends BaseCommand {

	public DebugCommand(SubdivisionPlugin plugin) {
		super(plugin, "debug", new String[] {"dbg"}, "subdivisions.debug", new String[] {""});
		plugin.getServer().getPluginCommand("debug").setExecutor(this);
		registerSubcommand(new ShowRegionSetCommand());
	}

	private class ShowRegionSetCommand extends BaseCommand {
		public ShowRegionSetCommand() {
			super(DebugCommand.this.plugin, "rs", new String[]{},  null, null);
		}

		@Override
		protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
			Player player = (Player) commandSender;
			RegionSet set = (RegionSet) player.getMetadata("subdivisions-active-regionset").get(0).value();
			player.sendMessage(ChatColor.BOLD + "Region: " + set.getRegion());
			if (set.hasParent()) {
				player.sendMessage(ChatColor.BOLD + "Parent: " + set.getParent());
			}
		}
	}
}

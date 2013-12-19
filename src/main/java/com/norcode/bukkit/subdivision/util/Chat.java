package com.norcode.bukkit.subdivision.util;

import org.bukkit.command.CommandSender;

public class Chat {
	public static void sendMessage(CommandSender player, String... message) {
		player.sendMessage(message);
	}
}

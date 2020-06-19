package com.bluemarien.everythingplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * This class represents the feed command.
 * 
 * @author Anthony Farina
 * @version 2020.06.18
 */
public class Feed implements CommandExecutor {

	/**
	 * This method is run when a player runs the feed command.
	 * 
	 * @param sender
	 * 			The entity running the command.
	 * @param command
	 * 			The command object of this command located in plugin.yml.
	 * @param commandLabel
	 * 			The String that is succeeds the "/" symbol in the command.
	 * @param args
	 * 			An array of arguments as Strings passed to the command. Does not include the command label.
	 * 
	 * @return Returns true if the command was run successfully, false otherwise.
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

		// Check that the command being run is "/feed".
		if (commandLabel.equals("feed")) {
			// Check if the entity running the command is a player.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return true;
			}
			
			Player commandPlayer = (Player)sender;
			
			// The player typed "/feed".
			if (args.length == 0) {
				// Feed the player running the command.
				feedPlayer(commandPlayer);
				return true;
			}
			// The player typed "/feed (something)".
			else if (args.length == 1) {
				Player receiver = Bukkit.getServer().getPlayer(args[0]);
				
				// Check if the receiving player is on the server.
				if (receiver == null) {
					commandPlayer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not on this server!");
					return true;
				}
				
				// Feed the receiving player.
				feedPlayer(receiver);
				return true;
			}
			
			// The player gave too many parameters.
			commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
			commandPlayer.sendMessage(ChatColor.RED + "/feed [player]");
			return true;
		}

		// The command was not handled.
		return false;
	}
	
	/**
	 * This method feeds the provided player by removing the hunger potion effect, setting their food level to 20
	 * (the default maximum), and setting their saturation to 14 (the default maximum).
	 * 
	 * @param player
	 * 			The player to feed.
	 */
	private static void feedPlayer(Player player) {
		player.removePotionEffect(PotionEffectType.HUNGER);
		player.setFoodLevel(20);
		player.setSaturation(14f);
	}
}

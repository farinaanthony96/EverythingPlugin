package com.bluemarien.everythingplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * This class represents the heal command. This command is used to heal a player
 * to full health.
 * 
 * @author Anthony Farina
 * @version 2020.06.19
 */
public class Heal implements CommandExecutor {

	/**
	 * This method is run when a player runs the heal command.
	 * 
	 * @param sender       The entity running the command.
	 * @param command      The command object of this command located in plugin.yml.
	 * @param commandLabel The String that is succeeds the "/" symbol in the
	 *                     command.
	 * @param args         An array of arguments as Strings passed to the command.
	 *                     Does not include the command label.
	 * 
	 * @return Returns true if the command was handled successfully, false
	 *         otherwise.
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

		// Check if the command being run is "/heal".
		if (commandLabel.equals("heal")) {
			// Check if the entity running the command is a player.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return true;
			}

			Player commandPlayer = (Player) sender;

			// Check if the player typed "/heal".
			if (args.length == 0) {
				// Heal the player running the command.
				healPlayer(commandPlayer);
				return true;
			}
			// Check if the player typed "/heal [player]".
			else if (args.length == 1) {
				// Get the receiving player.
				Player receiver = Bukkit.getServer().getPlayer(args[0]);

				// Check if the receiving player is on the server.
				if (receiver == null) {
					commandPlayer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not on this server!");
					return true;
				}

				// Heal the receiving player.
				healPlayer(receiver);
				return true;
			}

			// The player gave too many parameters.
			commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
			commandPlayer.sendMessage(ChatColor.RED + "/heal [player]");
			return true;
		}

		// The command was not handled.
		return false;
	}

	/**
	 * This method heals the provided player by removing fire damage, removing the
	 * harm, poison, and wither effects, and setting their health to 20 (the default
	 * maximum health for a player).
	 * 
	 * @param player The player to heal.
	 */
	private static void healPlayer(Player player) {
		player.setFireTicks(0);
		player.removePotionEffect(PotionEffectType.HARM);
		player.removePotionEffect(PotionEffectType.POISON);
		player.removePotionEffect(PotionEffectType.WITHER);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
	}
}

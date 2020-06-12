package com.bluemarien.everythingplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class represents the exp command.
 * 
 * @author Anthony Farina
 * @version 2020.06.11
 */
public class Exp implements CommandExecutor {

	/**
	 * This method is run when a player runs the exp command.
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

		// Check that the command being run is "/exp".
		if (commandLabel.equals("exp")) {
			// Check if the entity running the command is a player.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use the command /exp!");
				return true;
			}
			
			Player commandPlayer = (Player)sender;

			// The player only typed "/exp".
			if (args.length == 0) {
				commandPlayer.sendMessage(ChatColor.RED + "Incorrect syntax! Proper syntax is:");
				commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player1> [player2]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
				return true;
			}
			// The player typed "/exp (something)".
			else if (args.length == 1) {
				// Clear the player's experience level.
				if (args[0].equals("clear")) {
					commandPlayer.setExp(0f);
					commandPlayer.setLevel(0);
					return true;
				}
				
				// The player gave an invalid command.
				commandPlayer.sendMessage(ChatColor.RED + "Too few arguments! Proper syntax is:");
				commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player1> [player2]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
				return true;
			}
			// The player typed "/exp (something) (something)".
			else if (args.length == 2) {
				if (args[0].equals("set")) {
					int level = 0;
					
					// Try to get a valid experience level from the player.
					try {
						level = Integer.parseInt(args[1]);
						
						// Experience level cannot be negative.
						if (level < 0) {
							throw new NumberFormatException();
						}
					}
					// The experience level given was not a non-negative integer.
					catch (NumberFormatException e) {
						commandPlayer.sendMessage(ChatColor.RED + "Exp level must be a non-negative number!");
						return true;
					}
					
					// Set this player's experience level.
					commandPlayer.setLevel(level);
					return true;
				}
				else if (args[0].equals("clear")) {
					// Get the receiving player for the command.
					Player receiver = Bukkit.getServer().getPlayer(args[1]);
					
					// Check if the receiving player is on the server.
					if (receiver == null ) {
						commandPlayer.sendMessage(ChatColor.RED + "The player " + args[1] + " is not on this server!");
						return true;
					}
					
					// Set the receiving player's experience level to 0.
					receiver.setLevel(0);
					receiver.setExp(0f);
					return true;
				}
				
				// The player gave an invalid command.
				commandPlayer.sendMessage(ChatColor.RED + "Incorrect syntax! Proper syntax is:");
				commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player1> [player2]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
				return true;
			}
			// The player typed "/exp (something) (something) (something)".
			else if (args.length == 3) {
				int level = 0;
				
				// Try to get a valid experience level from the player.
				try {
					level = Integer.parseInt(args[1]);
					
					if (level < 0) {
						throw new NumberFormatException();
					}
				}
				// The experience level given was not a non-negative integer.
				catch (NumberFormatException e) {
					commandPlayer.sendMessage(ChatColor.RED + "Exp level must be a non-negative number!");
					return true;
				}
				
				// Get the receiving player for the command.
				Player receiver = Bukkit.getServer().getPlayer(args[2]);
				
				// Check if the receiving player is on the server.
				if (receiver == null ) {
					commandPlayer.sendMessage(ChatColor.RED + "The player " + args[2] + " is not on this server!");
					return true;
				}
				
				// Check if the player wants to give their experience levels to another player.
				if (args[0].equals("give")) {
					// Check if the giving player has enough experience levels to give to the receiving player.
					if (commandPlayer.getLevel() < level) {
						commandPlayer.sendMessage(ChatColor.RED + "You don't have enough experience levels!");
						return true;
					}
					
					// Give the experience levels to the receiving player.
					commandPlayer.setLevel(commandPlayer.getLevel() - level);
					receiver.setLevel(receiver.getLevel() + level);
					return true;
				}
				// Check if the player wants to set another player's experience level.
				else if (args[0].equals("set")) {
					receiver.setLevel(level);
					return true;
				}
				
				// The player gave an invalid command.
				commandPlayer.sendMessage(ChatColor.RED + "Incorrect syntax! Proper syntax is:");
				commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player1> [player2]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
				return true;
			}
			// The player typed "/exp (something) (something) (something) (something)".
			else if (args.length == 4) {
				if (args[1].equals("give")) {
					int level = 0;
					
					// Try to get a valid experience level from the player.
					try {
						level = Integer.parseInt(args[1]);
						
						if (level < 0) {
							throw new NumberFormatException();
						}
					}
					// The experience level given was not a non-negative integer.
					catch (NumberFormatException e) {
						commandPlayer.sendMessage(ChatColor.RED + "Exp level must be a non-negative number!");
						return true;
					}
					
					// Get the receiving player for the command.
					Player giver = Bukkit.getServer().getPlayer(args[2]);
					Player receiver = Bukkit.getServer().getPlayer(args[3]);
					
					// Check if the giving player is on the server.
					if (giver == null ) {
						commandPlayer.sendMessage(ChatColor.RED + "The player " + args[2] + " is not on this server!");
						return true;
					}
					// Check if the receiving player is on the server.
					else if (receiver == null) {
						commandPlayer.sendMessage(ChatColor.RED + "The player " + args[3] + " is not on this server!");
						return true;
					}
					
					// Check if the giving player has enough experience levels to give to the receiving player.
					if (giver.getLevel() < level) {
						commandPlayer.sendMessage(ChatColor.RED + giver.getName() + " doesn't have enough experience levels!");
						return true;
					}
					
					// Transfer experience levels from the giving player to the receiving player.
					giver.setLevel(giver.getLevel() - level);
					receiver.setLevel(receiver.getLevel() + level);
					return true;
				}
			}
			// The player gave too many arguments.
			else if (args.length >= 5) {
				commandPlayer.sendMessage(ChatColor.RED + "Too many arguments! Proper syntax is:");
				commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player1> [player2]");
				commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
				return true;
			}
		}

		// The command was not handled.
		return false;
	}
}

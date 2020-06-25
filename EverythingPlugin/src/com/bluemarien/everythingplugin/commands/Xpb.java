package com.bluemarien.everythingplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.resources.XpBankDatabase;

/**
 * This class represents the xpbank command. This command is used as a virtual
 * bank for a player's experience levels. A player can check their balance,
 * check the balance of the top 10 highest experience banks, deposit, and
 * withdraw experience levels.
 * 
 * @author Anthony Farina
 * @version 2020.06.25
 */
public class Xpb implements CommandExecutor {

	/**
	 * This method is run when a player runs the xpbank command.
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

		// Check if the command being run is "/xpbank".
		if (commandLabel.equals("xpbank")) {
			// Check if the entity running the command is a player.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
				return true;
			}

			// The entity running the command is a player.
			Player commandPlayer = (Player) sender;
			XpBankDatabase xpBank = EverythingPlugin.getXpBankDatabase();

			// Check if the player only typed "/xpbank".
			if (args.length == 0) {
				// Return the amount of levels stored in the player's xp bank.
				commandPlayer.sendMessage(
						ChatColor.GOLD + "XP Bank Balance: " + xpBank.getXPBankBalance(commandPlayer));
				return true;
			}
			// Check if the player typed "/xpbank (something)".
			else if (args.length == 1) {
				// Check if the player typed "/xpbank create".
				if (args[0].equals("create")) {
					// Create a new record in the xp bank database, if possible.
					xpBank.createRecord(commandPlayer);
					return true;
				}
				// Check if the player typed "/xpbank top10".
				else if (args[0].equals("top10")) {
					// Show the top 10 highest xpbanks.
					// TODO
					return true;
				}
			}
			// Check if the player typed "/xpbank (something) (something)".
			else if (args.length == 2) {
				// Check if the player typed "/xpbank deposit <levels>".
				if (args[0].equals("deposit")) {
					// Check if the levels the player provided is an integer.
					int levelsToDeposit = 0;

					try {
						levelsToDeposit = Integer.parseInt(args[1]);
					}
					// The experience level given was not a non-negative integer.
					catch (NumberFormatException e) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}

					// Check if the player provided a positive integer level.
					if (levelsToDeposit < 1) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}

					// Check if the player is not trying to deposit more levels than they have.
					if (levelsToDeposit > commandPlayer.getLevel()) {
						commandPlayer.sendMessage(ChatColor.RED + "You don't have that many levels to deposit!");
						return true;
					}

					// Deposit levels into the player's xp bank.
					commandPlayer.setLevel(commandPlayer.getLevel() - levelsToDeposit);
					xpBank.modifyXPBankBalance(commandPlayer, XpBankDatabase.BankAction.DEPOSIT, levelsToDeposit);
					commandPlayer
							.sendMessage(ChatColor.GOLD + "Successfully deposited " + levelsToDeposit + " levels.");
					commandPlayer.sendMessage(
							ChatColor.GOLD + "XP Bank Balance: " + xpBank.getXPBankBalance(commandPlayer));
					return true;
				}
				// Check if the player typed "/xpbank withdraw <levels>".
				else if (args[0].equals("withdraw")) {
					// Check if the levels the player provided is an integer.
					int levelsToWithdraw = 0;

					try {
						levelsToWithdraw = Integer.parseInt(args[1]);
					}
					// The experience level given was not a non-negative integer.
					catch (NumberFormatException e) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}

					// Check if the player provided a positive integer level.
					if (levelsToWithdraw < 1) {
						commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer level!");
						return true;
					}

					// Check if the player is not trying to withdraw more levels than they have in
					// their bank.
					if (levelsToWithdraw > xpBank.getXPBankBalance(commandPlayer)) {
						commandPlayer.sendMessage(ChatColor.RED + "You don't have that many levels to withdraw!");
						return true;
					}

					// Withdraw levels from the player's xp bank.
					xpBank.modifyXPBankBalance(commandPlayer, XpBankDatabase.BankAction.WITHDRAW,
							levelsToWithdraw);
					commandPlayer.setLevel(commandPlayer.getLevel() + levelsToWithdraw);

					commandPlayer
							.sendMessage(ChatColor.GOLD + "Successfully withdrew " + levelsToWithdraw + " levels.");
					commandPlayer.sendMessage(
							ChatColor.GOLD + "XP Bank Balance: " + xpBank.getXPBankBalance(commandPlayer));
					return true;
				}
			}

			// The player gave too many parameters.
			commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
			commandPlayer.sendMessage(ChatColor.RED + "/xpbank | /xpbank top10");
			commandPlayer.sendMessage(ChatColor.RED + "/xpbank <deposit|withdrawal> <levels>");
			return true;

		}

		// The command was not handled.
		return false;
	}
}

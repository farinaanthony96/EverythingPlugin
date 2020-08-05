package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.XpBankDatabase;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the xpbank command. This command is used as a virtual bank for a player's
 * experience levels. A player can check their balance, check the balance of the top 10 highest
 * experience banks, deposit, and withdraw experience levels.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class Xpbank implements CommandExecutor {

    /**
     * Executes the given command, returning its success.
     *
     * If false is returned, then the "usage" plugin.yml entry for this command (if defined) will be
     * sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     *
     * @return True if a valid command, otherwise false
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if the command being run is "/xpbank".
        if (!command.getName().equals("xpbank")) {
            // The command was not handled properly.
            return false;
        }

        // Check if the entity running the command is a player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
            return true;
        }

        // The entity running the command is a player.
        Player commandPlayer = (Player) sender;
        Permission perms = EverythingPlugin.getPermissions();
        XpBankDatabase xpBank = EverythingPlugin.getXpBankDatabase();

        // Check if the player has the permission to run this command.
        if (!perms.has(commandPlayer, "everythingplugin.xpbank")) {
            commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that " +
                    "command.");
            return true;
        }

        // Check if the player only typed "/xpbank".
        if (args.length == 0) {
            // Show the player the usage of "/xpbank".
            commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/xpbank\":");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank [help | ?]");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank top10");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <balance | b>");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <deposit | d> <levels>");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <withdraw | w> <levels>");
            return true;
        }
        // Check if the player typed "/xpbank (something)".
        else if (args.length == 1) {
            // Check if the player typed "/xpbank balance" or "/xpbank b".
            if (args[0].equals("balance") || args[0].equals("b")) {
                // Return the amount of levels stored in the xp bank database for the player.
                commandPlayer.sendMessage(ChatColor.GOLD + "XP Bank Balance: "
                        + xpBank.getXPBankBalance(commandPlayer));
                return true;
            }
            // Check if the player typed "/xpbank top10".
            else if (args[0].equals("top10")) {
                // TODO: Show the top 10 highest xpbanks.
                return true;
            }
            // Check if the player typed "/xpbank help" or "/xpbank ?".
            else if (args[0].equals("help") || args[0].equals("?")) {
                // Show the player the usage of "/xpbank".
                commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/xpbank\":");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank [help | ?]");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank top10");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <balance | b>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <deposit | d> <levels>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpbank <withdraw | w> <levels>");
                return true;
            }

            // The player typed an invalid subcommand.
            commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank top10");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <balance | b>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <deposit | d> <levels>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <withdraw | w> <levels>");
            return true;
        }
        // Check if the player typed "/xpbank (something) (something)".
        else if (args.length == 2) {
            // Check if the player typed "/xpbank deposit <levels>" or "/xpbank d <levels>".
            if (args[0].equals("deposit") || args[0].equals("d")) {
                // Try to get a numbered experience level from the first command parameter.
                int levelsToDeposit = 0;

                try {
                    levelsToDeposit = Integer.parseInt(args[1]);
                }
                // The experience level given was not a number.
                catch (NumberFormatException e) {
                    commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive " +
                            "integer level!");
                    return true;
                }

                // Check if the player provided a positive integer level.
                if (levelsToDeposit < 1) {
                    commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive " +
                            "integer level!");
                    return true;
                }

                // Check if the player is trying to deposit more levels than they have.
                if (levelsToDeposit > commandPlayer.getLevel()) {
                    commandPlayer.sendMessage(ChatColor.RED + "You don't have that many levels to" +
                            " deposit!");
                    return true;
                }

                // Deposit levels into the xp bank database for the player.
                commandPlayer.setLevel(commandPlayer.getLevel() - levelsToDeposit);
                xpBank.modifyXPBankBalance(commandPlayer, XpBankDatabase.BankAction.DEPOSIT,
                        levelsToDeposit);
                commandPlayer.sendMessage(ChatColor.GOLD + "Successfully deposited "
                        + levelsToDeposit + " levels.");
                commandPlayer.sendMessage(ChatColor.GOLD + "XP Bank Balance: "
                        + xpBank.getXPBankBalance(commandPlayer));
                return true;
            }
            // Check if the player typed "/xpbank withdraw <levels>" or "/xpbank w <levels>".
            else if (args[0].equals("withdraw") || args[0].equals("w")) {
                // Try to get a numbered experience level from the first command parameter.
                int levelsToWithdraw;

                try {
                    levelsToWithdraw = Integer.parseInt(args[1]);
                }
                // The experience level given was not a number.
                catch (NumberFormatException e) {
                    commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive " +
                            "integer level!");
                    return true;
                }

                // Check if the player provided a positive integer level.
                if (levelsToWithdraw < 1) {
                    commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive " +
                            "integer level!");
                    return true;
                }

                // Check if the player is trying to withdraw more levels than they have in their
                // xp bank.
                if (levelsToWithdraw > xpBank.getXPBankBalance(commandPlayer)) {
                    commandPlayer.sendMessage(ChatColor.RED + "You don't have that many levels to" +
                            " withdraw!");
                    return true;
                }

                // Withdraw levels from the xp bank database for the player.
                xpBank.modifyXPBankBalance(commandPlayer, XpBankDatabase.BankAction.WITHDRAW,
                        levelsToWithdraw);
                commandPlayer.setLevel(commandPlayer.getLevel() + levelsToWithdraw);
                commandPlayer.sendMessage(ChatColor.GOLD + "Successfully withdrew "
                        + levelsToWithdraw + " levels.");
                commandPlayer.sendMessage(ChatColor.GOLD + "XP Bank Balance: "
                        + xpBank.getXPBankBalance(commandPlayer));
                return true;
            }

            // The player typed an invalid subcommand.
            commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank top10");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <balance | b>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <deposit | d> <levels>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <withdraw | w> <levels>");
            return true;
        }
        // The player typed more than 3 arguments to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank top10");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <balance | b>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <deposit | d> <levels>");
            commandPlayer.sendMessage(ChatColor.RED + "/xpbank <withdraw | w> <levels>");
            return true;
        }
    }
}

package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class represents the exp command. The exp command can be used to give,
 * set, or clear other player's (or your own) experience levels.
 *
 * @author Anthony Farina
 * @version 2020.06.25
 */
public class Exp implements CommandExecutor {

    /**
     * This method is run when a player runs the exp command.
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

        // Check if the command being run is "/exp".
        if (commandLabel.equals("exp")) {
            // Check if the entity running the command is a player.
            if (!(sender instanceof Player)) {
                // The entity running the command is not a player.
                sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
                return true;
            }

            // The entity running the command is a player.
            Player commandPlayer = (Player) sender;
            Permission perms = EverythingPlugin.getPermissions();

            // Check if the player typed "/exp".
            if (args.length == 0) {
                // Show the player the usage of "/exp".
                commandPlayer.sendMessage(ChatColor.RED + "Usage of \"/exp\":");
                commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                return true;
            }
            // Check if the player typed "/exp (something)".
            else if (args.length == 1) {
                // Check if the player typed "/exp clear".
                if (args[0].equals("clear")) {
                    // Check if the player has the permission to run this command.
                    if (!perms.has(commandPlayer, "everythingplugin.exp.clear")) {
                        commandPlayer.sendMessage(ChatColor.DARK_RED + "You do not have permission to run that command.");
                        return true;
                    }

                    // Clear the player's experience levels.
                    commandPlayer.setExp(0f);
                    commandPlayer.setLevel(0);
                    return true;
                }
                // Check if the player typed "/exp help" or "/exp ?".
                else if (args[0].equals("help") || args[0].equals("?")) {
                    // Show the player the usage of "/exp".
                    commandPlayer.sendMessage(ChatColor.RED + "Usage of \"/exp\":");
                    commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                    commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                    commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                    return true;
                }

                // The player typed an invalid subcommand.
                commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
                commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                return true;
            }
            // Check if the player typed "/exp (something) (something)".
            else if (args.length == 2) {
                // Check if the player typed "/exp set <level>".
                if (args[0].equals("set")) {
                    // Check if the player has the permission to run this command.
                    if (!perms.has(commandPlayer, "everythingplugin.exp.set")) {
                        commandPlayer.sendMessage(ChatColor.DARK_RED + "You do not have permission to run that command.");
                        return true;
                    }

                    int level = 0;

                    // Try to get a valid experience level from the player.
                    try {
                        level = Integer.parseInt(args[1]);

                        // Check if the experience level is negative.
                        if (level < 0) {
                            throw new NumberFormatException();
                        }
                    }
                    // The experience level given was not a non-negative integer.
                    catch (NumberFormatException e) {
                        commandPlayer.sendMessage(ChatColor.RED + "Experience level must be a non-negative integer!");
                        return true;
                    }

                    // Set the player's experience level.
                    commandPlayer.setLevel(level);
                    return true;
                }
                // Check if the player typed "/exp clear [player]".
                else if (args[0].equals("clear")) {
                    // Check if the player has the permission to run this command.
                    if (!perms.has(commandPlayer, "everythingplugin.exp.clear.others")) {
                        commandPlayer.sendMessage(ChatColor.DARK_RED + "You do not have permission to run that command.");
                        return true;
                    }

                    // Get the receiving player from the second "/exp" parameter.
                    Player receiver = Bukkit.getServer().getPlayer(args[1]);

                    // Check if the receiving player is on the server.
                    if (receiver == null) {
                        commandPlayer.sendMessage(ChatColor.RED + "The player " + args[1] + " is not on this server!");
                        return true;
                    }

                    // Clear the receiving player's experience levels.
                    receiver.setLevel(0);
                    receiver.setExp(0f);
                    return true;
                }

                // The player gave an invalid subcommand.
                commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
                commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                return true;
            }
            // Check if the player typed "/exp (something) (something) (something)".
            else if (args.length == 3) {
                int level = 0;

                // Try to get a valid experience level from the second "/exp" parameter.
                try {
                    level = Integer.parseInt(args[1]);

                    // Check if the experience level is negative.
                    if (level < 0) {
                        throw new NumberFormatException();
                    }
                }
                // The experience level given was not a non-negative integer.
                catch (NumberFormatException e) {
                    commandPlayer.sendMessage(ChatColor.RED + "Experience level must be a non-negative integer!");
                    return true;
                }

                // Get the receiving player from the third "/exp" parameter.
                Player receiver = Bukkit.getServer().getPlayer(args[2]);

                // Check if the receiving player is on the server.
                if (receiver == null) {
                    commandPlayer.sendMessage(ChatColor.RED + "The player " + args[2] + " is not on this server!");
                    return true;
                }

                // Check if the player typed "/exp give <level> <player>".
                if (args[0].equals("give")) {
                    // Check if the player has the permission to run this command.
                    if (!perms.has(commandPlayer, "everythingplugin.exp.give")) {
                        commandPlayer.sendMessage(ChatColor.DARK_RED + "You do not have permission to run that command.");
                        return true;
                    }

                    // Check if the player has enough experience levels to give to the receiving
                    // player.
                    if (commandPlayer.getLevel() < level) {
                        commandPlayer.sendMessage(ChatColor.RED + "You don't have enough experience levels!");
                        return true;
                    }

                    // Give the player's experience levels to the receiving player.
                    commandPlayer.setLevel(commandPlayer.getLevel() - level);
                    receiver.setLevel(receiver.getLevel() + level);
                    return true;
                }
                // Check if the player typed "/exp set <level> [player]".
                else if (args[0].equals("set")) {
                    // Check if the player has the permission to run this command.
                    if (!perms.has(commandPlayer, "everythingplugin.exp.set.others")) {
                        commandPlayer.sendMessage(ChatColor.DARK_RED + "You do not have permission to run that command.");
                        return true;
                    }

                    // Set the receiving player's experience level.
                    receiver.setLevel(level);
                    return true;
                }

                // The player gave an invalid subcommand.
                commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
                commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                return true;
            }
            // The player typed more than 3 arguments.
            else {
                // The player gave too many arguments.
                commandPlayer.sendMessage(ChatColor.RED + "Too many arguments! Proper syntax is:");
                commandPlayer.sendMessage(ChatColor.RED + "/exp clear [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp set <level> [player]");
                commandPlayer.sendMessage(ChatColor.RED + "/exp give <level> <player>");
                return true;
            }
        }

        // The command was not handled.
        return false;
    }
}

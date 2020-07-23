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
 * This class represents the xpshare command. The xpshare command is used to give xp levels to other
 * players on the server.
 *
 * @author Anthony Farina
 * @version 2020.07.22
 */
public class Xpshare implements CommandExecutor {

    /**
     * This method is run when a player runs the xpshare command.
     *
     * @param sender       The entity running the command.
     * @param command      The command object of this command located in plugin.yml.
     * @param commandLabel The String that succeeds the "/" symbol in the command.
     * @param args         An array of arguments as Strings passed to the command. Does not include
     *                     the command label.
     *
     * @return Returns true if the command was handled successfully, false otherwise.
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel,
                             String[] args) {

        // Check if the command being run is "/xpshare".
        if (!commandLabel.equals("xpshare")) {
            // The command was not handled properly.
            return false;
        }

        // Check if the entity running the command is a player.
        if (!(sender instanceof Player)) {
            // The entity running the command is not a player.
            sender.sendMessage(ChatColor.RED + "You must be a player to use that command!");
            return true;
        }

        // The entity running the command is a player.
        Player commandPlayer = (Player) sender;
        Permission perms = EverythingPlugin.getPermissions();

        // Check if the player has the permission to run this command.
        if (!perms.has(commandPlayer, "everythingplugin.xpshare")) {
            commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that " +
                    "command.");
            return true;
        }

        // Check if the player typed "/xpshare".
        if (args.length == 0) {
            // Show the player the usage of "/xpshare".
            commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/xpshare\":");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpshare [help | ?]");
            commandPlayer.sendMessage(ChatColor.GOLD + "/xpshare <level> <player>");
            return true;
        }
        // Check if the player typed "/xpshare (something)".
        else if (args.length == 1) {
            // Check if the player typed "/xpshare help" or "/xpshare ?".
            if (args[0].equals("help") || args[0].equals("?")) {
                // Show the player the usage of "/xpshare".
                commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/xpshare\":");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpshare [help | ?]");
                commandPlayer.sendMessage(ChatColor.GOLD + "/xpshare <level> <player>");
                return true;
            }

            // The player typed an invalid subcommand.
            commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/xpshare [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/xpshare <level> <player>");
            return true;
        }
        // Check if the player typed "/xpshare (something) (something)".
        else if (args.length == 2) {
            int level;

            // Try to get a numbered experience level from the first command parameter.
            try {
                level = Integer.parseInt(args[0]);
            }
            // The experience level given was not a number.
            catch (NumberFormatException e) {
                commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer " +
                        "level!");
                return true;
            }

            // Check if the player provided a positive integer level.
            if (level < 1) {
                commandPlayer.sendMessage(ChatColor.RED + "You must provide a positive integer " +
                        "level!");
                return true;
            }

            // Get the receiving player from the second command parameter.
            Player receiver = Bukkit.getServer().getPlayer(args[1]);

            // Check if the receiving player is on the server.
            if (receiver == null) {
                commandPlayer.sendMessage(ChatColor.RED + "The player " + args[1] + " is not on " +
                        "this server!");
                return true;
            }

            // Check if the player has enough experience levels to give to the receiving player.
            if (commandPlayer.getLevel() < level) {
                commandPlayer.sendMessage(ChatColor.RED + "You do not have enough experience " +
                        "levels to give to " + receiver.getName() + "!");
                return true;
            }

            // Transfer the experience levels from the player to the receiving player.
            commandPlayer.setLevel(commandPlayer.getLevel() - level);
            receiver.setLevel(receiver.getLevel() + level);
            commandPlayer.sendMessage(ChatColor.GOLD + "You have given " + level + " experience " +
                    "levels to " + receiver.getName() + ".");
            receiver.sendMessage(ChatColor.GOLD + "You have received " + level + " experience " +
                    "levels from " + commandPlayer.getName() + ".");
            return true;
        }
        // The player typed more than 2 arguments to the command.
        else {
            // The player gave too many arguments.
            commandPlayer.sendMessage(ChatColor.RED + "Too many arguments! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/xpshare [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/xpshare <level> <player>");
            return true;
        }
    }
}

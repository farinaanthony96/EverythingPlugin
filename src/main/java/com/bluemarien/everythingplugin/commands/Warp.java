package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.WarpDatabase;

import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class represents the warp command. This command is used to create, delete, list, and
 * teleport to existing warps set on the server.
 *
 * @author Anthony Farina
 * @version 2020.07.22
 */
public class Warp implements CommandExecutor {

    /**
     * This method is run when a player runs the feed command.
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

        // Check if the command being run is "/warp".
        if (!commandLabel.equals("warp")) {
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
        WarpDatabase warpDB = EverythingPlugin.getWarpDatabase();

        // Check if the player has permission to run this command.
        if (!perms.has(commandPlayer, "everythingplugin.warp")) {
            commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that " +
                    "command.");
            return true;
        }

        // Check if the player typed "/warp".
        if (args.length == 0) {
            // Show the player the usage of "/warp".
            commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/warp\":");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp [help | ?]");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp <list | l>");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp <warp name>");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp <create | c> <warp name>");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp <delete | d> <warp name>");
            return true;
        }
        // Check if the player typed "/warp (something)".
        else if (args.length == 1) {
            // Check if the player typed "/warp help" or "/warp ?".
            if (args[0].equals("help") || args[0].equals("?")) {
                // Show the player the usage of "/warp".
                commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/warp\":");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp [help | ?]");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <list | l>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <warp name>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <create | c> <warp name>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <delete | d> <warp name>");
                return true;
            }
            // Check if the player typed "/warp list" or "/warp l".
            else if (args[0].equals("list") || args[0].equals("l")) {
                // Get the list of all warp names in the database and prepare the warp list message.
                Set<String> warpNames = warpDB.listWarps();
                StringBuilder warpMessage = new StringBuilder(ChatColor.GOLD + "Warps: ");

                // Put all the warp names in the warp list message.
                for (String warp : warpNames) {
                    warpMessage.append(warp);
                    warpMessage.append(" ");
                }

                // Send the player the warp list.
                commandPlayer.sendMessage(ChatColor.GOLD + warpMessage.toString());
                return true;
            }
            // The player intends to teleport to a warp.
            else {
                // Get the location of the warp.
                Location warp = warpDB.getWarp(args[0]);

                // Check if the warp exists in the database.
                if (warp == null) {
                    commandPlayer.sendMessage(ChatColor.RED + "The warp " + args[0] + " does not " +
                            "exist!");
                    return true;
                }

                // Teleport the player to the warp location.
                commandPlayer.teleport(warp);
                commandPlayer.sendMessage(ChatColor.GOLD + "Warped to " + args[0] + ".");
                return true;
            }
        }
        // Check if the player typed "/warp (something) (something)".
        else if (args.length == 2) {
            // Check if the player typed "/warp create <warp name>" or "/warp c <warp name>".
            if (args[0].equals("create") || args[0].equals("c")) {
                // Check if the player is naming a warp after a command.
                if (args[1].equals("create") || args[1].equals("c") || args[1].equals("delete") || args[1].equals("d") || args[1].equals("list") || args[1].equals("l") || args[1].equals("help") || args[1].equals("?")) {
                    commandPlayer.sendMessage(ChatColor.RED + "You cannot name a warp after a " +
                            "warp command!");
                    return true;
                }

                // Insert the new warp in the warp database.
                warpDB.insertWarp(commandPlayer.getLocation(), args[1]);
                commandPlayer.sendMessage(ChatColor.GOLD + "Warp " + args[1] + " created.");
                return true;
            }
            // Check if the player typed "/warp delete <warp name>" or "/warp d <warp name>".
            else if (args[0].equals("delete") || args[0].equals("d")) {
                // Check if the warp exists in the database.
                if (!warpDB.removeWarp(args[1])) {
                    commandPlayer.sendMessage(ChatColor.RED + "The warp " + args[1] + " does not " +
                            "exist!");
                    return true;
                }

                // The warp was successfully removed from the database.
                commandPlayer.sendMessage(ChatColor.GOLD + "Warp " + args[1] + " deleted.");
                return true;
            }

            // The player typed an invalid subcommand.
            commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/warp [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <list | l>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <create | c> <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <delete | d> <warp name>");
            return true;
        }
        // The player typed more than 2 arguments to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/warp [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <list | l>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <create | c> <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <delete | d> <warp name>");
            return true;
        }
    }
}

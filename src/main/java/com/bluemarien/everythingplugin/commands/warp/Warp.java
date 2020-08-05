package com.bluemarien.everythingplugin.commands.warp;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.WarpDatabase;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the warp command. This command teleports the player to a specific warp
 * location stored in the warp database.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class Warp implements CommandExecutor {

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

        // Check if the command being run is "/warp".
        if (!label.equals("warp")) {
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
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp [warp name]");
            return true;
        }
        // Check if the player typed "/warp (something)".
        else if (args.length == 1) {
            // Get the location of the warp from the warp database.
            Location warp = warpDB.getWarp(args[0]);

            // Check if the warp exists in the warp database.
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
        // The player typed more than 1 argument to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/warp [warp name]");
            return true;
        }
    }
}

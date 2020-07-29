package com.bluemarien.everythingplugin.commands.warp;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.WarpDatabase;

import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the listwarps command. This command will list all warps in the warp
 * database to the player.
 *
 * @author Anthony Farina
 * @version 2020.07.29
 */
public class Listwarps implements CommandExecutor {

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
     * @return True if a valid command, otherwise false.
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Check if the command being run is "/listwarps".
        if (!label.equals("listwarps")) {
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

        // Check if the player typed "/listwarps".
        if (args.length == 0) {
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
        // The player gave an argument to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/listwarps");
            return true;
        }
    }
}

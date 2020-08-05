package com.bluemarien.everythingplugin.commands.multihome;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.MultihomeDatabase;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the delhome command. This command deletes a player's home specified with a
 * home label.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class Delhome implements CommandExecutor {

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

        // Check if the command being run is "/delhome".
        if (!label.equals("delhome")) {
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
        MultihomeDatabase multihomeDB = EverythingPlugin.getMultihomeDatabase();

        // Check if the player has permission to run this command.
        if (!perms.has(commandPlayer, "everythingplugin.multihome")) {
            commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that " +
                    "command.");
            return true;
        }

        // Check if the player typed "/delhome".
        if (args.length == 0) {
            commandPlayer.sendMessage(ChatColor.RED + "Too few parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/delhome <home name>");
            return true;
        }
        // Check if the player typed "/delhome (something)".
        else if (args.length == 1) {
            // Check if the home exists in the database.
            if (!multihomeDB.removeHome(commandPlayer.getUniqueId().toString(), args[0])) {
                commandPlayer.sendMessage(ChatColor.RED + "The home " + args[0] + " does not " +
                        "exist!");
                return true;
            }

            // The home was successfully deleted from the multihome database.
            commandPlayer.sendMessage(ChatColor.GOLD + "The home " + args[0] + " was successfully" +
                    " deleted.");
            return true;
        }
        // The player typed more than 1 argument to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/delhome <home name>");
            return true;
        }
    }
}

package com.bluemarien.everythingplugin.commands.multihome;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.MultihomeDatabase;

import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the listhomes command. This command will list all the home names for the
 * player.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class Listhomes implements CommandExecutor {

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

        // Check if the command being run is "/listhomes".
        if (!label.equals("listhomes")) {
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

        // Check if the player typed "/listhomes".
        if (args.length == 0) {
            // Get the list of all the player's home names from the multihome database and prepare
            // the home list message.
            Set<String> homeNames = multihomeDB.listHomes(commandPlayer.getUniqueId().toString());
            StringBuilder homeMessage = new StringBuilder(ChatColor.GOLD + "Homes: ");

            // Put all the home names in the home list message.
            for (String home : homeNames) {
                homeMessage.append(home);
                homeMessage.append(" ");
            }

            // Send the player the home list.
            commandPlayer.sendMessage(ChatColor.GOLD + homeMessage.toString());
            return true;
        }
        // The player gave an argument to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/listhomes");
            return true;
        }
    }
}

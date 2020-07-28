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
 * @version 2020.07.28
 */
public class Delhome implements CommandExecutor {

    /**
     * This method is run when a player runs the home command.
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

        // Check if the command being run is "/delhome".
        if (!commandLabel.equals("delhome")) {
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

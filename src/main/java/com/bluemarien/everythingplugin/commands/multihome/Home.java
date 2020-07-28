package com.bluemarien.everythingplugin.commands.multihome;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.MultihomeDatabase;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * This class represents the home command. The "/home" command will teleport a player to their
 * default home location, which is set with the "/sethome" command. It will teleport a player to a
 * specific home location if the home name is provided.
 *
 * @author Anthony Farina
 * @version 2020.07.28
 */
public class Home implements CommandExecutor {

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

        // Check if the command being run is "/home".
        if (!commandLabel.equals("home")) {
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

        // Check if the player typed "/home".
        if (args.length == 0) {
            // Get the default home location of the player.
            Location defaultHome = multihomeDB.getHome(commandPlayer.getUniqueId().toString(),
                    "[default]");

            // Check if the home exists in the database.
            if (defaultHome == null) {
                commandPlayer.sendMessage(ChatColor.RED + "Your default home does not exist!");
                commandPlayer.sendMessage(ChatColor.RED + "Use \"/sethome\" to set a default home" +
                        ".");
                return true;
            }

            // Teleport the player to their default home location.
            commandPlayer.teleport(defaultHome);
            commandPlayer.sendMessage(ChatColor.GOLD + "Teleported to your default home.");
            return true;
        }
        // Check if the player typed "/home (something)".
        else if (args.length == 1) {
            // Get the specific home for the player.
            Location home = multihomeDB.getHome(commandPlayer.getUniqueId().toString(), args[0]);

            // Check if the home exists.
            if (home == null) {
                commandPlayer.sendMessage(ChatColor.RED + "The home " + args[0] + " doesn't " +
                        "exist!");
                return true;
            }

            // Teleport the player to the home location.
            commandPlayer.teleport(home);
            commandPlayer.sendMessage(ChatColor.GOLD + "Teleported to " + args[0] + ".");
            return true;
        }
        // The player typed more than 1 argument to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/home [home name]");
            return true;
        }
    }
}

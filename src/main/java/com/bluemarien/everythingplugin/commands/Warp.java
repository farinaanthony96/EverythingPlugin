package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;
import com.bluemarien.everythingplugin.backend.WarpDatabase;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class
 *
 * @author Anthony Farina
 * @version 2020.06.29
 */
public class Warp implements CommandExecutor {

    private WarpDatabase warpDB = null;

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
    @Override
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
        warpDB = new WarpDatabase();

        // Check if the player typed "/warp".
        if (args.length == 0) {
            // Show the player the usage of "/warp".
            commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/warp\":");
            commandPlayer.sendMessage(ChatColor.GOLD + "/warp [help | ?]");
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
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <warp name>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <create | c> <warp name>");
                commandPlayer.sendMessage(ChatColor.GOLD + "/warp <delete | d> <warp name>");
                return true;
            } else {
                // Check if the warp exists in the database.
                if (warpDB.getWarpDatabase().get("warps." + args[0]) == null) {
                    commandPlayer.sendMessage(ChatColor.RED + "The warp " + args[0] + " does not " +
                            "exist!");
                    return true;
                }

                // Teleport the player to the warp location.
                commandPlayer.teleport(new Location(
                        Bukkit.getWorld(
                        warpDB.getWarpDatabase().getString("warps." + args[0] + ".world")),
                        warpDB.getWarpDatabase().getDouble("warps." + args[0] + ".x"),
                        warpDB.getWarpDatabase().getDouble("warps." + args[0] + ".y"),
                        warpDB.getWarpDatabase().getDouble("warps." + args[0] + ".z"),
                        Float.parseFloat(warpDB.getWarpDatabase().getString("warps." + args[0] + ".yaw")),
                        Float.parseFloat(warpDB.getWarpDatabase().getString("warps." + args[0] + ".pitch"))));
                commandPlayer.sendMessage(ChatColor.GOLD + "Warped to " + args[0] + ".");
                return false;
            }
        }
        // Check if the player typed "/warp (something) (something)".
        else if (args.length == 2) {
            // Check if the player typed "/warp create <warp name>" or "/warp c <warp name>".
            if (args[0].equals("create") || args[0].equals("c")) {
                // Create the new warp in the warp database.
                warpDB.getWarpDatabase().set("warps." + args[1] + ".world",
                        commandPlayer.getLocation().getWorld().getName());
                warpDB.getWarpDatabase().set("warps." + args[1] + ".x",
                        commandPlayer.getLocation().getX());
                warpDB.getWarpDatabase().set("warps." + args[1] + ".y",
                        commandPlayer.getLocation().getY());
                warpDB.getWarpDatabase().set("warps." + args[1] + ".z",
                        commandPlayer.getLocation().getZ());
                warpDB.getWarpDatabase().set("warps." + args[1] + ".yaw",
                        commandPlayer.getLocation().getYaw());
                warpDB.getWarpDatabase().set("warps." + args[1] + ".pitch",
                        commandPlayer.getLocation().getPitch());
                warpDB.saveWarpDatabase();

                commandPlayer.sendMessage(ChatColor.GOLD + "Warp " + args[1] + " created.");
                return true;
            }
            // Check if the player typed "/warp delete <warp name>" or "/warp d <warp name>".
            else if (args[0].equals("delete") || args[0].equals("d")) {
                // Check if the warp exists in the database.
                if (warpDB.getWarpDatabase().getConfigurationSection("warps." + args[1]) == null) {
                    commandPlayer.sendMessage(ChatColor.RED + "The warp " + args[1] + " does not " +
                            "exist!");
                    return true;
                }

                // Delete the warp from the warp database.
                warpDB.getWarpDatabase().set("warps." + args[1], null);
                warpDB.saveWarpDatabase();

                commandPlayer.sendMessage(ChatColor.GOLD + "Warp " + args[1] + " deleted.");
                return true;
            }

            // The player typed an invalid subcommand.
            commandPlayer.sendMessage(ChatColor.RED + "Unknown subcommand! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/warp [help | ?]");
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
            commandPlayer.sendMessage(ChatColor.RED + "/warp <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <create | c> <warp name>");
            commandPlayer.sendMessage(ChatColor.RED + "/warp <delete | d> <warp name>");
            return true;
        }
    }
}

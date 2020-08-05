package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


/**
 * This class represents the feed command. The feed command is used to fill a player's hunger bar.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class Feed implements CommandExecutor {

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

        // Check if the command being run is "/feed".
        if (!label.equals("feed")) {
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

        // Check if the player typed "/feed".
        if (args.length == 0) {
            // Check if the player has the permission to run this command.
            if (!perms.has(commandPlayer, "everythingplugin.feed")) {
                commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that" +
                        " command.");
                return true;
            }

            // Feed the player.
            feedPlayer(commandPlayer);
            commandPlayer.sendMessage(ChatColor.GOLD + "You have been fed.");
            return true;
        }
        // Check if the player typed "/feed [player]".
        else if (args.length == 1) {
            // Check if the player has the permission to run this command.
            if (!perms.has(commandPlayer, "everythingplugin.feed.others")) {
                commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that" +
                        " command.");
                return true;
            }

            // Get the receiving player.
            Player receiver = Bukkit.getServer().getPlayer(args[0]);

            // Check if the receiving player is on the server.
            if (receiver == null) {
                commandPlayer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not on " +
                        "this server!");
                return true;
            }

            // Feed the receiving player.
            feedPlayer(receiver);
            receiver.sendMessage(ChatColor.GOLD + "You have been fed by " + commandPlayer.getName() + ".");
            commandPlayer.sendMessage(ChatColor.GOLD + "You have fed " + receiver.getName() + ".");
            return true;
        }
        // The player typed more than 2 arguments to the command.
        else {
            // The player gave too many parameters.
            commandPlayer.sendMessage(ChatColor.RED + "Too many parameters! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/feed [player]");
            return true;
        }
    }

    /**
     * This method feeds the provided player by removing the hunger potion effect, setting their
     * food level to 20 (the default maximum for players), and setting their saturation to 14 (the
     * default maximum for players).
     *
     * @param player The player to feed.
     */
    private static void feedPlayer(Player player) {
        player.removePotionEffect(PotionEffectType.HUNGER);
        player.setFoodLevel(20);
        player.setSaturation(14f);
    }
}

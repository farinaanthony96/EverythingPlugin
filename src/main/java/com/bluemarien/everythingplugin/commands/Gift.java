package com.bluemarien.everythingplugin.commands;

import com.bluemarien.everythingplugin.EverythingPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * This class represents the gift command. This command allows players to send an item (or items) in their main hand to other players on the server.
 *
 * @author Anthony Farina
 * @version 2020.07.24
 */
public class Gift implements CommandExecutor {

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

        // Check if the command being run is "/gift".
        if (!commandLabel.equals("gift")) {
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
        if (!perms.has(commandPlayer, "everythingplugin.gift")) {
            commandPlayer.sendMessage(ChatColor.RED + "You do not have permission to run that" +
                    " command.");
            return true;
        }

        // Check if the player typed "/gift".
        if (args.length == 0) {
            // Show the player the usage of "/gift".
            commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/gift\":");
            commandPlayer.sendMessage(ChatColor.GOLD + "/gift [help | ?]");
            commandPlayer.sendMessage(ChatColor.GOLD + "/gift <player> [amount]");
            return true;
        }
        // Check if the player typed "/gift (something)".
        else if (args.length == 1) {
            if (args[0].equals("help") || args[0].equals("?")) {
                // Show the player the usage of "/gift".
                commandPlayer.sendMessage(ChatColor.GOLD + "Usage of \"/gift\":");
                commandPlayer.sendMessage(ChatColor.GOLD + "/gift [help | ?]");
                commandPlayer.sendMessage(ChatColor.GOLD + "/gift <player> [amount]");
                return true;
            }

            // Check if the player is trying to gift nothing.
            if (commandPlayer.getInventory().getItemInMainHand().getAmount() == 0) {
                commandPlayer.sendMessage(ChatColor.RED + "You cannot gift nothing!");
            }

            // Get the receiving player from the first command parameter.
            Player receiver = Bukkit.getServer().getPlayer(args[0]);

            // Check if the receiving player is on the server.
            if (receiver == null) {
                commandPlayer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not on " +
                        "this server!");
                return true;
            }

            // Check if the player is trying to gift themselves.
            if (receiver.getName().equals(commandPlayer.getName())) {
                commandPlayer.sendMessage(ChatColor.RED + "You cannot gift yourself!");
                return true;
            }

            // Gift the receiving player the contents of the sender's main hand.
            ItemStack gift = commandPlayer.getInventory().getItemInMainHand();
            commandPlayer.getInventory().setItemInMainHand(null);
            receiver.getInventory().addItem(gift);
            commandPlayer.sendMessage(ChatColor.GOLD + "You have gifted " + gift.getAmount() + " " + gift.getType().toString() + " to " + receiver.getName() + ".");
            receiver.sendMessage(ChatColor.GOLD + "You have been gifted " + gift.getAmount() + " " + gift.getType().toString() + " from " + commandPlayer.getName() + ".");
            return true;
        }
        // Check if the player typed "/gift (something) (something)".
        else if (args.length == 2) {
            // Check if the player is trying to gift nothing.
            if (commandPlayer.getInventory().getItemInMainHand().getAmount() == 0) {
                commandPlayer.sendMessage(ChatColor.RED + "You cannot gift nothing!");
            }

            // Get the receiving player from the first command parameter.
            Player receiver = Bukkit.getServer().getPlayer(args[0]);

            // Check if the receiving player is on the server.
            if (receiver == null) {
                commandPlayer.sendMessage(ChatColor.RED + "The player " + args[0] + " is not on " +
                        "this server!");
                return true;
            }

            // Check if the player is trying to gift themselves.
            if (receiver.getName().equals(commandPlayer.getName())) {
                commandPlayer.sendMessage(ChatColor.RED + "You cannot gift yourself!");
                return true;
            }

            // Try to get an integer from the second command parameter.
            int amount = 0;

            try {
                amount = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException ignored) {
                commandPlayer.sendMessage(ChatColor.RED + "You must provide an amount from 1 to 64!");
                return true;
            }

            // Check if the amount is not between 1 and 64.
            if (!(amount >= 1 && amount <= 64)) {
                commandPlayer.sendMessage(ChatColor.RED + "You must provide an amount from 1 to 64!");
                return true;
            }

            // Check if the player has enough items to gift.
            if (commandPlayer.getInventory().getItemInMainHand().getAmount() < amount) {
                commandPlayer.sendMessage(ChatColor.RED + "You don't have that amount to gift!");
                return true;
            }

            // Gift the receiving player the contents of the sender's main hand.
            int originalAmount = commandPlayer.getInventory().getItemInMainHand().getAmount();
            ItemStack gift = commandPlayer.getInventory().getItemInMainHand();
            gift.setAmount(amount);
            commandPlayer.getInventory().getItemInMainHand().setAmount(originalAmount - amount);
            receiver.getInventory().addItem(gift);
            commandPlayer.sendMessage(ChatColor.GOLD + "You have gifted " + gift.getAmount() + " " + gift.getType().toString() + " to " + receiver.getName() + ".");
            receiver.sendMessage(ChatColor.GOLD + "You have been gifted " + gift.getAmount() + " " + gift.getType().toString() + " from " + commandPlayer.getName() + ".");
            return true;
        }
        // The player typed more than 2 arguments to the command.
        else {
            // The player gave too many arguments.
            commandPlayer.sendMessage(ChatColor.RED + "Too many arguments! Proper syntax is:");
            commandPlayer.sendMessage(ChatColor.RED + "/gift [help | ?]");
            commandPlayer.sendMessage(ChatColor.RED + "/gift <player> [amount]");
            return true;
        }
    }
}

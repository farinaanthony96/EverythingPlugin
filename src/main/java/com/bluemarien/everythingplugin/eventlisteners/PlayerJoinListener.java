package com.bluemarien.everythingplugin.eventlisteners;

import com.bluemarien.everythingplugin.backend.XpBankDatabase;
import com.bluemarien.everythingplugin.EverythingPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


/**
 * This class contains event listeners that listen for the PlayerJoinEvent.
 *
 * @author Anthony Farina
 * @version 2020.07.21
 */
public class PlayerJoinListener implements Listener {

    /**
     * This listener checks if a player is not already in the xp bank database. If they aren't then
     * they will be automatically added to the database.
     *
     * @param event The PlayerJoinEvent to listen for when adding a new player to the xp bank
     *              database.
     */
    @EventHandler
    public void registerNewXpBankPlayer(PlayerJoinEvent event) {
        // Declare and initialize references to the xp bank database and the joining player.
        XpBankDatabase xpBankDB = EverythingPlugin.getXpBankDatabase();
        Player player = event.getPlayer();

        // Check if the joining player is not in the xp bank database.
        if (!xpBankDB.isInXpBankDatabase(player)) {
            // Add the joining player to the xp bank database.
            xpBankDB.insertPlayer(player);
        }
    }
}

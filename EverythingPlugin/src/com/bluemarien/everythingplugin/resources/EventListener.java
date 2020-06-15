package com.bluemarien.everythingplugin.resources;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class will listen for events that occur on the Spigot server.
 * 
 * @author Anthony Farina
 * @version 2020.06.15
 */
public class EventListener implements Listener {

	/**
	 * 
	 * 
	 * @param event
	 */
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
        if (!p.hasPlayedBefore()) {
            
        }
    }
}

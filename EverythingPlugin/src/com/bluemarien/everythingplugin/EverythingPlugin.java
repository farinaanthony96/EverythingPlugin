package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.commands.Exp;
import com.bluemarien.everythingplugin.commands.Feed;
import com.bluemarien.everythingplugin.commands.Heal;
import com.bluemarien.everythingplugin.resources.SQLite;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents the EverythingPlugin running on a Spigot server.
 * 
 * @author Anthony Farina
 * @version 2020.06.11
 */
public class EverythingPlugin extends JavaPlugin {
	
	public SQLite db;

	/**
	 * Properly enable the plugin.
	 */
	public void onEnable() {
		PluginDescriptionFile pdFile = getDescription();
		Logger logger = getLogger();
		
		logger.info("Loading commands...");
		
		getCommands();
		
		logger.info("Commands loaded!");
		
		// Check if EveryPlugin directory exists.
		File file = new File("./plugins/EverythingPlugin");
		
		if (!file.isDirectory()) {
			file.mkdir();
		}
		
		// Check if the database exists.
		if (!Files.exists(Paths.get("./plugins/EverythingPlugin/test.db"))) {
			// Make a new database.
			db = new SQLite();
		}
		
		// Connect to database.
		
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully enabled!");	
	}
	
	/**
	 * Properly disable the plugin.
	 */
	public void onDisable() {
		PluginDescriptionFile pdFile = getDescription();
		Logger logger = getLogger();
		
		
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully disabled!");
	}
	
	
	/**
	 * Register the plugin's commands.
	 */
	public void getCommands() {
		getCommand("heal").setExecutor(new Heal());
		getCommand("feed").setExecutor(new Feed());
		getCommand("exp").setExecutor(new Exp());
	}
}

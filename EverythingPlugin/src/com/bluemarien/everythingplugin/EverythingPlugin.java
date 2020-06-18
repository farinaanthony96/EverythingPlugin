package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.commands.Exp;
import com.bluemarien.everythingplugin.commands.Feed;
import com.bluemarien.everythingplugin.commands.Heal;
import com.bluemarien.everythingplugin.resources.SQLite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents the EverythingPlugin running on a Spigot server.
 * 
 * @author Anthony Farina
 * @version 2020.06.15
 */
public class EverythingPlugin extends JavaPlugin {
	
	// Declare reference to the plugin's description file "plugin.yml".
	private PluginDescriptionFile pdFile;
	
	/**
	 * 
	 */
	public static Logger logger;
	public static SQLite expBankDB;
	public static final String pluginFolderPath = "./plugins/EverythingPlugin";
	public static final String databaseName = "xpBankDatabase.db";

	/**
	 * Properly enable the plugin.
	 */
	public void onEnable() {
		// Load plugin information and initialize logger.
		pdFile = getDescription();
		logger = getLogger();
		
		// Load the plugin commands.
		logger.info("Loading commands...");
		loadCommands();
		logger.info("Commands loaded!");
		
		// Check if the EveryPlugin directory exists in the "plugins" directory.
		if (!Files.isDirectory(Paths.get(pluginFolderPath))) {
			// Try to make the EverythingPlugin directory in the "plugins" directory.
			try {
				Files.createDirectory(Paths.get(pluginFolderPath));
			}
			// Something went wrong creating the EverythingPlugin directory.
			catch (IOException e) {
				e.printStackTrace();
				logger.info("An error occurred while creating the EverythingPlugin folder! Disabling plugin...");
				onDisable();
			}
		}
		
		// Connect to expBank database.
		expBankDB = new SQLite();
		
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully enabled!");	
	}
	
	/**
	 * Properly disable the plugin.
	 */
	public void onDisable() {
		// Close the SQLite database properly.
		expBankDB.closeDatabase();
		
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully disabled!");
	}
	
	
	/**
	 * Register the plugin's commands.
	 */
	public void loadCommands() {
		getCommand("heal").setExecutor(new Heal());
		getCommand("feed").setExecutor(new Feed());
		getCommand("exp").setExecutor(new Exp());
	}
}

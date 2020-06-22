package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.commands.Exp;
import com.bluemarien.everythingplugin.commands.Feed;
import com.bluemarien.everythingplugin.commands.Heal;
import com.bluemarien.everythingplugin.commands.Xpb;
import com.bluemarien.everythingplugin.resources.XpBankDatabase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class represents the EverythingPlugin plugin running on a Spigot server.
 * The plugin's description file is named "plugin.yml".
 * 
 * @author Anthony Farina
 * @version 2020.06.19
 */
public class EverythingPlugin extends JavaPlugin {

	/**
	 * Make sure other classes have static access to the logger, XP Bank database,
	 * and paths to the plugin's directory.
	 */
	public static Logger logger;
	public static XpBankDatabase xpBankDB;
	public static final String pluginFolderPath = "./plugins/EverythingPlugin";
	public static final String xpBankDBName = "XPBankDatabase.db";

	// Declare reference to the plugin's description file "plugin.yml".
	private PluginDescriptionFile pdFile;

	/**
	 * Properly enable the plugin.
	 */
	public void onEnable() {
		// Load plugin description file (plugin.yml) and initialize the logger.
		pdFile = getDescription();
		logger = getLogger();

		// Load the plugin commands.
		logger.info("Loading commands...");
		loadCommands();
		logger.info("Commands loaded!");

		// Check if the EverythingPlugin directory exists in the "plugins" directory.
		if (!Files.isDirectory(Paths.get(pluginFolderPath))) {
			// Try to make the EverythingPlugin directory in the "plugins" directory.
			try {
				Files.createDirectory(Paths.get(pluginFolderPath));
			}
			// Something went wrong creating the EverythingPlugin directory.
			catch (IOException e) {
				logger.info("An error occurred while creating the EverythingPlugin directory! Disabling plugin...");
				logger.info(e.getMessage());
				onDisable();
				return;
			}
		}

		// Connect to or create a new XP bank database.
		xpBankDB = new XpBankDatabase();

		// We enabled the plugin successfully.
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully enabled!");
	}

	/**
	 * Properly disable the plugin.
	 */
	public void onDisable() {
		// Close the XP bank database properly.
		xpBankDB.closeXPBankDatabase();

		// We disabled the plugin successfully.
		logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully disabled!");
		return;
	}

	/**
	 * Register the plugin's commands with Spigot. Don't forget to add them to the
	 * plugin.yml file after adding them here!
	 */
	private void loadCommands() {
		// Set the executor for each command in the plugin description file.
		getCommand("heal").setExecutor(new Heal());
		getCommand("feed").setExecutor(new Feed());
		getCommand("exp").setExecutor(new Exp());
		getCommand("xpb").setExecutor(new Xpb());
	}
}

package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.backend.*;
import com.bluemarien.everythingplugin.commands.*;
import com.bluemarien.everythingplugin.eventlisteners.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * This class represents the EverythingPlugin plugin running on a Spigot server. The plugin's
 * description file is named "plugin.yml".
 *
 * Features to add:
 *   - Multihome
 *   - Gifting
 *   - Sign modding / coloring
 *   - Backpacks
 *   - Mob catching
 *   - Distant farms
 *   - Treecapitator
 *
 * Bugs to fix:
 *
 * @author Anthony Farina
 * @version 2020.07.24
 */
public final class EverythingPlugin extends JavaPlugin {

    /**
     * Declare plugin's logger, description file "plugin.yml", and folder path.
     */
    private static Logger logger = null;
    private PluginDescriptionFile pdFile = null;
    private static final String pluginFolderPath = "./plugins/EverythingPlugin";

    /**
     * Declare xp bank database fields.
     */
    private static XpBankDatabase xpBankDB = null;
    private static final String xpBankDBName = "XPBankDatabase.db";

    /**
     * Declare warp database fields.
     */
    private static WarpDatabase warpDB = null;
    private static final String warpDBName = "warps.yml";

    /**
     * Declare event listeners.
     */
    private PlayerJoinListener playerJoinListener = null;

    /**
     * Declare a reference to the permissions manager for the plugin.
     */
    private static Permission perms = null;


    /**
     * Properly enable the plugin.
     */
    @Override
    public void onEnable() {
        // Get the plugin's description file (plugin.yml) and logger.
        pdFile = this.getDescription();
        logger = this.getLogger();

        // Check if an error occurred setting up the EverythingPlugin directory.
        if (!setupPluginDirectory()) {
            logger.info("An error occurred while creating the EverythingPlugin directory! " +
                    "Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load the plugin's commands.
        this.loadCommands();

        // Connect to or create a new XP bank database.
        xpBankDB = new XpBankDatabase();

        // Connect to or create a new warp database.
        warpDB = new WarpDatabase();

        // Register event listeners.
        playerJoinListener = new PlayerJoinListener();
        this.getServer().getPluginManager().registerEvents(playerJoinListener, this);

        // Check if an error occurred setting up the plugin's permission system.
        if (!setupPermissions()) {
            logger.info("An error occurred while setting up the permission system! Disabling " +
                    "plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // We enabled the plugin successfully.
        logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully " +
                "enabled!");
    }

    /**
     * Properly disable the plugin.
     */
    @Override
    public void onDisable() {
        // Close the XP bank database properly.
        if (xpBankDB != null) {
            xpBankDB.closeXPBankDatabase();
        }

        // Save the warp database.
        if (warpDB != null) {
            logger.info("Saving the warp database...");
            warpDB.saveWarpDatabase();
            logger.info("Saved the warp database successfully!");
        }

        // Unregister event listeners.
        if (playerJoinListener != null) {
            HandlerList.unregisterAll(playerJoinListener);
        }

        // Disabled the plugin successfully.
        logger.info(pdFile.getName() + " v" + pdFile.getVersion() + " has been successfully " +
                "disabled!");
    }

    /**
     * Sets up the EverythingPlugin directory.
     *
     * @return True if the EverythingPlugin directory was set up successfully, false otherwise.
     */
    private boolean setupPluginDirectory() {
        // Check if the EverythingPlugin directory exists in the "plugins" directory.
        if (!Files.isDirectory(Paths.get(pluginFolderPath))) {
            // Try to make the EverythingPlugin directory in the "plugins" directory.
            try {
                Files.createDirectory(Paths.get(pluginFolderPath));
            }
            // Something went wrong creating the EverythingPlugin directory.
            catch (IOException e) {
                logger.info(e.getMessage());
                return false;
            }

            // The EverythingPlugin directory was created successfully.
            return true;
        }

        // The EverythingPlugin directory already exists.
        return true;
    }

    /**
     * Register the plugin's commands with Spigot. Don't forget to add them to the plugin.yml file
     * after adding them here!
     */
    private void loadCommands() {
        // Set the executor for each command in the plugin description file.
        logger.info("Loading commands...");

        this.getCommand("heal").setExecutor(new Heal());
        this.getCommand("feed").setExecutor(new Feed());
        this.getCommand("xpshare").setExecutor(new Xpshare());
        this.getCommand("xpbank").setExecutor(new Xpbank());
        this.getCommand("warp").setExecutor(new Warp());

        logger.info("Commands loaded successfully!");
    }

    /**
     * Sets up the permissions system via Vault.
     *
     * @return True if permissions were set up successfully, false otherwise.
     */
    private boolean setupPermissions() {
        // Get the service provider registration for permissions.
        RegisteredServiceProvider<Permission> rsp =
                this.getServer().getServicesManager().getRegistration(Permission.class);

        // Check if the permissions registration was successful.
        if (rsp == null) {
            // An error occurred registering the permissions registration.
            return false;
        }

        // Get the permissions provider.
        perms = rsp.getProvider();

        // Check if the permissions provider exists.
        if (perms == null) {
            // The permissions provider doesn't exist.
            return false;
        }

        // Check if Vault is installed on the server.
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            // Vault is not installed on the server.
            return false;
        }

        // Permissions were set up successfully.
        return true;
    }

    /**
     * Returns this plugin's logger object for output to the server console.
     *
     * @return This plugin's logger object.
     */
    public static Logger getEPLogger() {
        return logger;
    }

    /**
     * Returns the plugin's folder path in the "plugins" directory.
     *
     * @return The plugin's folder path in the "plugins" directory.
     */
    public static String getPluginFolderPath() {
        return pluginFolderPath;
    }

    /**
     * Returns the xp bank database object.
     *
     * @return The xp bank database object.
     */
    public static XpBankDatabase getXpBankDatabase() {
        return xpBankDB;
    }

    /**
     * Returns the name of the xp bank database.
     *
     * @return The name of the xp bank database.
     */
    public static String getXpBankDBName() {
        return xpBankDBName;
    }

    /**
     * Returns the warp database object.
     *
     * @return The warp database object.
     */
    public static WarpDatabase getWarpDatabase() {
        return warpDB;
    }

    /**
     * Returns the name of the warp database.
     *
     * @return The name of the warp database.
     */
    public static String getWarpDBName() {
        return warpDBName;
    }

    /**
     * Returns the permission object to check for player permissions.
     *
     * @return The permission object.
     */
    public static Permission getPermissions() {
        return perms;
    }
}

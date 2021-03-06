package com.bluemarien.everythingplugin;

import com.bluemarien.everythingplugin.backend.*;
import com.bluemarien.everythingplugin.commands.*;
import com.bluemarien.everythingplugin.commands.multihome.*;
import com.bluemarien.everythingplugin.commands.warp.*;
import com.bluemarien.everythingplugin.eventlisteners.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * This class represents the EverythingPlugin plugin running on a Spigot server. This registers the
 * plugin's commands, event listeners, and sets up database objects. The plugin's description file
 * is named "plugin.yml".
 *
 * Features to add:
 *   - Multihome (admin commands to modify / teleport to other player's homes)
 *   - Seen command
 *   - Sign modifying / coloring
 *   - Backpacks
 *   - Mob catching
 *   - Distant farms
 *   - Treecapitator
 *   - Tab complete for all commands
 *   - Teleport commands for a "/back" command (including /tpa)
 *   - Repair command
 *   - Economy
 *   - Kits
 *   - Mob Spawning
 *   - Mail
 *   - Configuration file
 *
 * Bugs to fix:
 *   - Trying to add repair cost to the enchantment extraction is buggy
 *
 *
 * @author Anthony Farina
 * @version 2020.09.26
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
     * Declare multihome database fields.
     */
    private static MultihomeDatabase multihomeDB = null;
    private static final String multihomeDBName = "multihomes.yml";

    /**
     * Declare event listeners.
     */
    private PlayerJoinListener playerJoinListener = null;
    private PrepareAnvilListener prepareAnvilListener = null;
    private InventoryClickListener inventoryClickListener = null;

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

        // Connect to or create a new multihome database.
        multihomeDB = new MultihomeDatabase();

        // Register the event listeners.
        playerJoinListener = new PlayerJoinListener();
        this.getServer().getPluginManager().registerEvents(playerJoinListener, this);
        prepareAnvilListener = new PrepareAnvilListener();
        this.getServer().getPluginManager().registerEvents(prepareAnvilListener, this);
        inventoryClickListener = new InventoryClickListener();
        this.getServer().getPluginManager().registerEvents(inventoryClickListener, this);


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

        // Save the multihome database.
        if (multihomeDB != null) {
            logger.info("Saving the multihome database...");
            multihomeDB.saveMultihomeDatabase();
            logger.info("Saved the multihome database successfully!");
        }

        // Unregister event listeners.
        if (playerJoinListener != null) {
            HandlerList.unregisterAll(playerJoinListener);
        }
        if (prepareAnvilListener != null) {
            HandlerList.unregisterAll(prepareAnvilListener);
        }
        if (inventoryClickListener != null) {
            HandlerList.unregisterAll(inventoryClickListener);
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
            // An error occurred creating the EverythingPlugin directory.
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
        logger.info("Loading commands...");

        // Register commands with the server by setting the executor for each command in the
        // plugin description file.
        try {
            Objects.requireNonNull(this.getCommand("heal")).setExecutor(new Heal());
            Objects.requireNonNull(this.getCommand("feed")).setExecutor(new Feed());
            Objects.requireNonNull(this.getCommand("xpshare")).setExecutor(new Xpshare());
            Objects.requireNonNull(this.getCommand("xpbank")).setExecutor(new Xpbank());
            Objects.requireNonNull(this.getCommand("warp")).setExecutor(new Warp());
            Objects.requireNonNull(this.getCommand("setwarp")).setExecutor(new Setwarp());
            Objects.requireNonNull(this.getCommand("delwarp")).setExecutor(new Delwarp());
            Objects.requireNonNull(this.getCommand("listwarps")).setExecutor(new Listwarps());
            Objects.requireNonNull(this.getCommand("gift")).setExecutor(new Gift());
            Objects.requireNonNull(this.getCommand("home")).setExecutor(new Home());
            Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new Sethome());
            Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new Delhome());
            Objects.requireNonNull(this.getCommand("listhomes")).setExecutor(new Listhomes());
        }
        // An error occurred registering the commands.
        catch (NullPointerException e) {
            logger.severe(e.getMessage());
            logger.severe("An error occurred while loading the commands! Disabling plugin...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

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

        // Set the permissions provider variable for the plugin.
        perms = rsp.getProvider();

        // Check if Vault is installed on the server and return true since permissions have been
        // set up successfully, or false if Vault isn't installed.
        return getServer().getPluginManager().getPlugin("Vault") != null;
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
     * Returns the multihome database object.
     *
     * @return The multihome database object.
     */
    public static MultihomeDatabase getMultihomeDatabase() {
        return multihomeDB;
    }

    /**
     * Returns the name of the multihome database.
     *
     * @return The name of the multihome database.
     */
    public static String getMultihomeDBName() {
        return multihomeDBName;
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

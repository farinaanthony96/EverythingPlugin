package com.bluemarien.everythingplugin.backend;

import com.bluemarien.everythingplugin.EverythingPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 * This class represents a multihome database that this plugin uses to store data about the homes
 * for players on the server. It uses the YAML configuration object from the Spigot API to handle
 * database functionality.
 *
 * @author Anthony Farina
 * @version 2020.08.05
 */
public class MultihomeDatabase {

    /**
     * Declare and initialize multihome database path and file objects.
     */
    private final String multihomeDatabasePath =
            EverythingPlugin.getPluginFolderPath() + "/" + EverythingPlugin.getMultihomeDBName();
    private FileConfiguration multihomeDatabase = null;
    private File multihomeDatabaseFile = null;

    /**
     * Loads an existing multihome database or, if necessary, creates a new database if one doesn't
     * exist.
     */
    public MultihomeDatabase() {
        // Make the file object to reference the multihome database.
        multihomeDatabaseFile = new File(multihomeDatabasePath);

        // Check if the multihome database exists.
        if (!multihomeDatabaseExists()) {
            // Create and initialize a new multihome database.
            EverythingPlugin.getEPLogger().info("No multihome database detected! Creating a new multihome database...");
            createMultihomeDatabase();
        }
        // The multihome database exists.
        else {
            // Load the existing multihome database.
            EverythingPlugin.getEPLogger().info("Connecting to the existing multihome database...");
            multihomeDatabase = YamlConfiguration.loadConfiguration(multihomeDatabaseFile);
            EverythingPlugin.getEPLogger().info("Connected to the existing multihome database successfully!");
        }
    }

    /**
     * Inserts a new home location for the provided player UUID with the provided home name and
     * location into the multihome database.
     *
     * @param playerUUID The UUID of the player to save a home for.
     * @param home       The location of the home to save.
     * @param homeName   The name of the home.
     */
    public void insertHome(String playerUUID, Location home, String homeName) {
        // Insert the home location into the multihome database labeled with the provided home
        // name for the provided player UUID.
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".world", Objects.requireNonNull(home.getWorld()).getName());
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".x", home.getX());
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".y", home.getY());
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".z", home.getZ());
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".yaw", home.getYaw());
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName + ".pitch", home.getPitch());

        // Save the home to the multihome database.
        saveMultihomeDatabase();
    }

    /**
     * Gets the location of a stored home if it exists in the database.
     *
     * @param playerUUID The UUID of the player to return a home for.
     * @param homeName   The name of the home to return the location for.
     *
     * @return The location of a stored home or null if the home doesn't exist in the multihome database.
     */
    public Location getHome(String playerUUID, String homeName) {
        // Check if the home exists in the multihome database.
        if (multihomeDatabase.get("multihomes." + playerUUID + "." + homeName) == null) {
            // Return null since the home doesn't exist in the multihome database.
            return null;
        }

        // Return the location of the home from the multihome database.
        return new Location(
                Bukkit.getWorld(Objects.requireNonNull(multihomeDatabase.getString("multihomes." + playerUUID + "." + homeName + ".world"))),
                multihomeDatabase.getDouble("multihomes." + playerUUID + "." + homeName + ".x"),
                multihomeDatabase.getDouble("multihomes." + playerUUID + "." + homeName + ".y"),
                multihomeDatabase.getDouble("multihomes." + playerUUID + "." + homeName + ".z"),
                Float.parseFloat(Objects.requireNonNull(multihomeDatabase.getString("multihomes." + playerUUID + "." + homeName + ".yaw"))),
                Float.parseFloat(Objects.requireNonNull(multihomeDatabase.getString("multihomes." + playerUUID + "." + homeName + ".pitch"))));
    }

    /**
     * Removes a home from the multihome database for the provided player UUID. Returns true if the
     * home was removed successfully, or false if the home doesn't exist.
     *
     * @param playerUUID The UUID of the player to remove a home for.
     * @param homeName   The home to remove from the database.
     *
     * @return True if the home was removed successfully, false if the home doesn't exist in the
     * multihome database.
     */
    public boolean removeHome(String playerUUID, String homeName) {
        // Check if the home exists in the multihome database.
        if (multihomeDatabase.get("multihomes." + playerUUID + "." + homeName) == null) {
            return false;
        }

        // Delete the home from the multihome database.
        multihomeDatabase.set("multihomes." + playerUUID + "." + homeName, null);
        saveMultihomeDatabase();
        return true;
    }

    /**
     * Returns a set of Strings containing all the home names stored in the database for the
     * provided player UUID.
     *
     * @param playerUUID The UUID of the player to list homes for.
     *
     * @return A set of Strings containing all the home names stored in the database for the
     * provided player UUID.
     */
    public Set<String> listHomes(String playerUUID) {
        // Get the section of homes from the multihome database for the player.
        ConfigurationSection homes = multihomeDatabase.getConfigurationSection("multihomes." + playerUUID);

        // Return the empty set if there are no homes in the multihome database for the provided player UUID, otherwise return the set of all homes for the provided player UUID in the multihome database.
        return homes == null ? Collections.emptySet() : homes.getKeys(false);
    }

    /**
     * Saves the multihome database.
     *
     * @return True if the multihome database was saved successfully, false otherwise.
     */
    public boolean saveMultihomeDatabase() {
        // Try to save the multihome database.
        try {
            multihomeDatabase.save(multihomeDatabaseFile);
        }
        // An error occurred while trying to save the multihome database.
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred saving the multihome database file!");
            return false;
        }

        // The multihome database was saved successfully.
        return true;
    }

    /**
     * Reloads the multihome database.
     *
     * @return True if the multihome database was reloaded successfully, false otherwise.
     */
    public boolean reloadMultihomeDatabase() {
        // Try to reload the multihome database.
        EverythingPlugin.getEPLogger().info("Reloading the multihome database...");

        try {
            multihomeDatabase = YamlConfiguration.loadConfiguration(multihomeDatabaseFile);
        }
        // An error occurred while trying to reload the multihome database.
        catch (IllegalArgumentException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred reloading the multihome database file!");
            return false;
        }

        // The multihome database was reloaded successfully.
        EverythingPlugin.getEPLogger().info("Reloaded the multihome database successfully!");
        return true;
    }

    /**
     * Creates a new multihome database.
     *
     * @return True if a new multihome database was created successfully, false otherwise.
     */
    private boolean createMultihomeDatabase() {
        // Try to create a new multihome database file.
        try {
            multihomeDatabaseFile.createNewFile();
        }
        // An error occurred while creating a new multihome database file.
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred creating the multihome database!");
            return false;
        }

        // A new multihome database file was created successfully. Load and configure the file.
        multihomeDatabase = YamlConfiguration.loadConfiguration(multihomeDatabaseFile);
        multihomeDatabase.createSection("multihomes");
        EverythingPlugin.getEPLogger().info("Created and connected to the new multihome database successfully!");
        return true;
    }

    /**
     * Checks if a multihome database already exists.
     *
     * @return True if the multihome database already exists, false otherwise.
     */
    private boolean multihomeDatabaseExists() { return Files.exists(Paths.get(multihomeDatabasePath)); }
}

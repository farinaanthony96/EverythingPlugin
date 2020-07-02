package com.bluemarien.everythingplugin.backend;

import com.bluemarien.everythingplugin.EverythingPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class
 *
 * @author Anthony Farina
 * @version 2020.06.28
 */
public class WarpDatabase {

    /**
     *
     */
    private final String warpDatabasePath = EverythingPlugin.getPluginFolderPath() + "/" + EverythingPlugin.getWarpDBName();
    private FileConfiguration warpDatabase = null;
    private File warpDatabaseFile = null;

    /**
     *
     */
    public WarpDatabase() {
        // Make the file object to reference the warp database.
        warpDatabaseFile = new File(warpDatabasePath);

        // Check if the warp database exists.
        if (!warpDatabaseExists()) {
            // Create and initialize a new warp database.
            createWarpDatabase();
        }

        // Load the warp database.
        warpDatabase = YamlConfiguration.loadConfiguration(warpDatabaseFile);
    }

    /**
     *
     *
     * @return
     */
    public FileConfiguration getWarpDatabase() {
        return warpDatabase;
    }

    /**
     * Inserts the provided warp location with the provide warp name into the warp database.
     *
     * @param warp The location of the warp to save.
     * @param warpName The name of the warp.
     */
    public void insertWarp(Location warp, String warpName) {
        // Insert the warp location into the warp database labeled with the provided warp name.
        warpDatabase.set("warps." + warpName + ".world", warp.getWorld().toString());
        warpDatabase.set("warps." + warpName + ".x", warp.getX());
        warpDatabase.set("warps." + warpName + ".y", warp.getY());
        warpDatabase.set("warps." + warpName + ".z", warp.getZ());
        warpDatabase.set("warps." + warpName + ".yaw", warp.getYaw());
        warpDatabase.set("warps." + warpName + ".pitch", warp.getPitch());
        saveWarpDatabase();
    }

    /**
     *
     *
     * @param warpName
     * @return
     */
    public Location getWarp(String warpName) {
        // Check if the warp exists in the database.
        if (warpDatabase.get("warps." + warpName) == null) {
            return null;
        }

        // Return the location of the warp.
        return new Location(
                Bukkit.getWorld(warpDatabase.getString("warps." + warpName + ".world")),
                warpDatabase.getDouble("warps." + warpName + ".x"),
                warpDatabase.getDouble("warps." + warpName + ".y"),
                warpDatabase.getDouble("warps." + warpName + ".z"),
                Float.parseFloat(warpDatabase.getString("warps." + warpName + ".yaw")),
                Float.parseFloat(warpDatabase.getString("warps." + warpName + ".pitch")));
    }

    /**
     *
     *
     * @param warpName
     * @return
     */
    public boolean removeWarp(String warpName) {
        // Check if the warp exists in the database.
        if (warpDatabase.get("warps." + warpName) == null) {
            return false;
        }

        // Delete the warp from the warp database.
        warpDatabase.set("warps." + warpName, null);
        saveWarpDatabase();
        return true;
    }

    // TODO: list?

    /**
     *
     *
     * @return
     */
    public boolean saveWarpDatabase() {
        try {
            warpDatabase.save(warpDatabaseFile);
        }
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred saving the warp database file!");
            return false;
        }

        return true;
    }

    /**
     *
     *
     * @return
     */
    public boolean reloadWarpDatabase() {
        try {
            warpDatabase = YamlConfiguration.loadConfiguration(warpDatabaseFile);
        }
        catch (IllegalArgumentException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred reloading the warp database file!");
            return false;
        }

        return true;
    }

    /**
     *
     *
     * @return
     */
    private boolean warpDatabaseExists() { return Files.exists(Paths.get(warpDatabasePath)); }


    /**
     *
     *
     * @return
     */
    private boolean createWarpDatabase() {
        try {
            warpDatabaseFile.createNewFile();
        }
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred creating the warp database!");
            return false;
        }

        return true;
    }
}

package com.bluemarien.everythingplugin.backend;

import com.bluemarien.everythingplugin.EverythingPlugin;
import org.bukkit.ChatColor;
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
    private final String databasePath = EverythingPlugin.getPluginFolderPath() + "/" + EverythingPlugin.getWarpDBName();
    private FileConfiguration warpDatabase = null;
    private File warpDatabaseFile = null;

    /**
     *
     */
    public WarpDatabase() {
        warpDatabaseFile = new File(databasePath);

        // Check if the warp database exists.
        if (!warpDatabaseExists()) {
            // Create and initialize a new warp database.
            createWarpDatabase();
        }

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
     *
     */
    public void saveWarpDatabase() {
        try {
            warpDatabase.save(warpDatabaseFile);
        }
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred saving the warp database file!");
        }
    }

    public void reloadWarpDatabase() {
        warpDatabase = YamlConfiguration.loadConfiguration(warpDatabaseFile);
    }

    /**
     *
     *
     * @return
     */
    private boolean warpDatabaseExists() { return Files.exists(Paths.get(databasePath)); }

    /**
     *
     */
    private void createWarpDatabase() {
        try {
            warpDatabaseFile.createNewFile();
        }
        catch (IOException e) {
            EverythingPlugin.getEPLogger().info(ChatColor.RED + "An error occurred creating the warp database!");
        }
    }
}

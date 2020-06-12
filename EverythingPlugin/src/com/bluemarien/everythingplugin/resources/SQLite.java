package com.bluemarien.everythingplugin.resources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.DriverManager;  
import java.sql.SQLException;

import com.bluemarien.everythingplugin.EverythingPlugin;

/**
 * This class represents an SQLite database that this plugin uses to store data
 * about players on the server this plugin is installed with.
 * 
 * @author Anthony Farina
 * @version 2020.06.12
 */
public class SQLite {

	/**
	 * Initialize database path and declare connection to database.
	 */
	private final String databasePath = EverythingPlugin.pluginFolderPath + "/"
									  + EverythingPlugin.databaseName;
	private Connection conn;
	private final String localPathURL = "jdbc:sqlite:" + databasePath;
	
	/**
	 * Connects to an existing expBank database or, if necessary, creates a
	 * database if one doesn't exist.
	 */
	public SQLite() {
		// Create and initialize a new database if one doesn't exist.
		if (!databaseExists()) {
			createDatabase();
		}
		// Connect to the existing database.
		else {
			try {
				conn = DriverManager.getConnection(localPathURL);
			}
			// An error occurred connecting to the database.
			catch (SQLException e) {
				EverythingPlugin.logger.info(e.getMessage());
			}
		}
	}
	
	/**
	 * Insert a record into the database.
	 */
	public void insert() {
		// TODO
	}
	
	/**
	 * Remove a record from the database.
	 */
	public void remove() {
		// TODO
	}
	
	/**
	 * Modify an existing record in the database.
	 */
	public void modify() {
		// TODO
	}
	
	/**
	 * Closes the connection to the database.
	 */
	public void closeDatabase() {
		// Try to close the connection to the database.
		try {
			conn.close();
		}
		// An error occurred closing the connection.
		catch (SQLException e) {
			EverythingPlugin.logger.info(e.getMessage());
		}
	}
	
	/**
	 * Check if the database exists.
	 * 
	 * @return True if the database exists, false otherwise.
	 */
	private boolean databaseExists() {
		return Files.exists(Paths.get(databasePath));
	}
	
	/**
	 * Creates a new database in the EverythingPlugin directory.
	 */
	private void createDatabase() {
		String localPath = "jdbc:sqlite:" + databasePath;
		
		// Try to create a database.
		try {
			// Create the database.
			conn = DriverManager.getConnection(localPath);
			
			// TODO: Initialize the database.
			
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                EverythingPlugin.logger.info("The driver name is " + meta.getDriverName());
                EverythingPlugin.logger.info("A new database has been created.");
            }
        }
		// An error occurred creating the database.
		catch (SQLException e) {
			EverythingPlugin.logger.info(e.getMessage());
        }
	}
}

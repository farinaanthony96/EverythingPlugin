package com.bluemarien.everythingplugin.resources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import com.bluemarien.everythingplugin.EverythingPlugin;

/**
 * This class represents an SQLite database that this plugin uses to store data
 * about players on the server this plugin is installed with.
 * 
 * @author Anthony Farina
 * @version 2020.06.15
 */
public class SQLite {

	/**
	 * Initialize database path and declare connection to database.
	 */
	private final String databasePath = EverythingPlugin.pluginFolderPath + "/"
									  + EverythingPlugin.databaseName;
	private Connection conn;
	private final String localPathURL = "jdbc:sqlite:" + databasePath;
	private final String tableName = "xpBankTable";
	
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
	public void insert(Player p) {
		String uuid = p.getUniqueId().toString();
		String statement = "INSERT INTO " + tableName + "(UUID,XP) VALUES(?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(statement)) {
            pstmt.setString(1, uuid);
            pstmt.setInt(2, 0);
            if (pstmt.executeUpdate() > 0)
            	EverythingPlugin.logger.info("I've added " + p.getName() + " to " + tableName + "!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
	 * Creates a new database in the EverythingPlugin directory and creates a
	 * table in the new database.
	 */
	private void createDatabase() {
		String localPath = "jdbc:sqlite:" + databasePath;
		
		// Try to create a database.
		try {
			// Create the database.
			conn = DriverManager.getConnection(localPath);
			
			// SQL statement for creating a new table.
	        String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
	                + "	UUID text,\n"
	                + "	XP integer\n"
	                + ");";

	        try (Statement stmt = conn.createStatement()) {
	        	// Create a new table in the database.
	            stmt.execute(statement);
	        } catch (SQLException e) {
	        	EverythingPlugin.logger.info(e.getMessage());
	        }
			
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

package com.bluemarien.everythingplugin.resources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import com.bluemarien.everythingplugin.EverythingPlugin;

/**
 * This class represents an SQLite database that this plugin uses to store data
 * about players on the server this plugin is installed with.
 * 
 * @author Anthony Farina
 * @version 2020.06.19
 */
public class SQLite {
	
	// Make sure other classes can access the DEPOSIT and WITHDRAWAL enumerations.
	public enum BankAction {DEPOSIT, WITHDRAWAL}

	/**
	 * Initialize database paths/name and declare connection to database.
	 */
	private final String databasePath = EverythingPlugin.pluginFolderPath + "/"
									  + EverythingPlugin.databaseName;
	private final String localPathURL = "jdbc:sqlite:" + databasePath;
	private final String tableName = "xpBankTable";
	private Connection conn;
	
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
	public void insert(Player player) {
		String uuid = player.getUniqueId().toString();
		String query = "INSERT INTO " + tableName + "(UUID,XP) VALUES(?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, uuid);
            pstmt.setInt(2, 1);
            
            if (pstmt.executeUpdate() > 0) {
            	EverythingPlugin.logger.info("I've added " + player.getName() + " to " + tableName + "!");
            }
        }
        catch (SQLException e) {
        	EverythingPlugin.logger.info(e.getMessage());
        }
	}
	
	/**
	 * Gets a record from the database.
	 * 
	 * @param player
	 * 			The player to get the record for.
	 * 
	 * @return Returns the levels that the given player has in their xpbank.
	 */
	public int get(Player player) {
		String uuid = player.getUniqueId().toString();
		String query = "SELECT UUID, XP\n"
				     + "FROM " + tableName + "\n"
				     + "WHERE UUID LIKE '" + uuid + "';";
		int levelsInBank = 0;
		
		// 
		try (Statement statement = conn.createStatement();
			 ResultSet result = statement.executeQuery(query)) {
			result.next();
			levelsInBank = result.getInt("XP");
		}
		catch (SQLException e) {
			EverythingPlugin.logger.info(e.getMessage());
		}
		
		return levelsInBank;
	}
	
	/**
	 * Modify an existing record in the database.
	 */
	public void modify(Player player, BankAction action) {
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
	                + "	UUID text UNIQUE,\n"
	                + "	XP integer CHECK(XP >= 0)\n"
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

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
 * This class represents an xp bank database that this plugin uses to store data
 * about players on the server this plugin is installed with. It uses SQLite to
 * handle database functionality.
 * 
 * @author Anthony Farina
 * @version 2020.06.25
 */
public class XpBankDatabase {

	// Make sure other classes can access the DEPOSIT and WITHDRAW enumerations.
	public enum BankAction {
		DEPOSIT, WITHDRAW
	}

	/**
	 * Initialize database paths/name and declare the connection to database.
	 */
	private final String databasePath = EverythingPlugin.getPluginFolderPath() + "/" + EverythingPlugin.getXpBankDatabaseName();
	private final String localPathURL = "jdbc:sqlite:" + databasePath;
	private final String tableName = "xpBankTable";
	private Connection conn = null;

	/**
	 * Connects to an existing expBank database or, if necessary, creates a database
	 * if one doesn't exist.
	 */
	public XpBankDatabase() {
		// Check if a database already exists.
		if (!xpBankDatabaseExists()) {
			// Create and initialize a new database.
			createXPBankDatabase();
		}
		// Connect to the existing database.
		else {
			// Attempt to connect to the existing database.
			try {
				conn = DriverManager.getConnection(localPathURL);
			}
			// An error occurred connecting to the database.
			catch (SQLException e) {
				EverythingPlugin.getEPLogger().info("Error connecting to the xp bank database! Disabling plugin.");
				EverythingPlugin.getEPLogger().info(e.getMessage());
				// Bukkit.getServer().getPluginManager().disablePlugin(EverythingPlugin);
			}
		}
	}

	/**
	 * Insert a record into the database.
	 * 
	 * @param player The player to insert into the database.
	 */
	public void createRecord(Player player) {
		// Initialize the UUID of the player and the query for the database.
		String uuid = player.getUniqueId().toString();
		String query = "INSERT INTO " + tableName + "(UUID,XP) VALUES(?,?)";

		// Try to prepare and execute a query for the database.
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			// Set the parameters for the "VALUES".
			pstmt.setString(1, uuid);
			pstmt.setInt(2, 0);

			// Make sure the query was successful.
			if (pstmt.executeUpdate() > 0) {
				EverythingPlugin.getEPLogger()
						.info("The player " + player.getName() + " has been added to the " + tableName + " database!");
			}
		}
		// An error occurred executing the query to the database.
		catch (SQLException e) {
			EverythingPlugin.getEPLogger().info("An error occurred adding " + player.getName() + " to the xp bank database!");
			EverythingPlugin.getEPLogger().info(e.getMessage());
		}
	}

	/**
	 * Gets the balance of the given player's xp bank.
	 * 
	 * @param player The player to get the xp bank balance for.
	 * 
	 * @return Returns the balance that the given player has in their xp bank.
	 */
	public int getXPBankBalance(Player player) {
		// Initialize the UUID of the player, the query for the database, and the level
		// variable.
		String uuid = player.getUniqueId().toString();
		String query = "SELECT UUID, XP\n" + "FROM " + tableName + "\n" + "WHERE UUID LIKE '" + uuid + "';";
		int levelsInBank = 0;

		// Try to execute the query and get the player's xp bank balance.
		try (Statement statement = conn.createStatement(); ResultSet result = statement.executeQuery(query)) {
			// Get the player's record in the database and get the player's xp bank balance.
			result.next();
			levelsInBank = result.getInt("XP");
		}
		// An error occurred executing the query to the database, or while getting the
		// player's xp bank balance.
		catch (SQLException e) {
			EverythingPlugin.getEPLogger().info("An error occurred accessing " + player.getName() + "'s xp bank from the database!");
			EverythingPlugin.getEPLogger().info(e.getMessage());
		}

		return levelsInBank;
	}

	/**
	 * Modify an a player's xp bank balance by either depositing or withdrawing more
	 * levels.
	 * 
	 * @param player The player to modify the xp bank balance for.
	 * @param action Whether to deposit or withdraw from the xp bank.
	 * @param levels The levels to deposit or withdraw from the xp bank.
	 */
	public void modifyXPBankBalance(Player player, BankAction action, int levels) {
		// Initialize the UUID of the player and the query for the database.
		String uuid = player.getUniqueId().toString();
		String query = "";

		// Check if a deposit is requested.
		if (action == BankAction.DEPOSIT) {
			query = "UPDATE " + tableName + " SET XP = XP + ? " + "WHERE UUID LIKE ?";
		}
		// Check if a withdrawal is requested.
		else if (action == BankAction.WITHDRAW) {
			query = "UPDATE " + tableName + " SET XP = XP - ? " + "WHERE UUID LIKE ?";
		}

		// Try to prepare and execute a query for the database.
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			// Set the parameters for "SET" and "LIKE".
			pstmt.setInt(1, levels);
			pstmt.setString(2, uuid);

			// Execute the query.
			pstmt.executeUpdate();
		}
		// An error occurred while preparing or executing the query to the database.
		catch (SQLException e) {
			EverythingPlugin.getEPLogger().info("An error occurred updating " + player.getName() + "'s xp bank from the database!");
			EverythingPlugin.getEPLogger().info(e.getMessage());
		}
	}

	/**
	 * Properly closes the connection to the xp bank database.
	 */
	public void closeXPBankDatabase() {
		// Try to close the connection to the database.
		try {
			conn.close();
		}
		// An error occurred closing the connection to the database.
		catch (SQLException e) {
			EverythingPlugin.getEPLogger().info("An error occurred closing the xp bank database!");
			EverythingPlugin.getEPLogger().info(e.getMessage());
		}
	}

	/**
	 * Check if the database exists.
	 * 
	 * @return True if the database exists, false otherwise.
	 */
	private boolean xpBankDatabaseExists() {
		return Files.exists(Paths.get(databasePath));
	}

	/**
	 * Creates a new xp bank database in the EverythingPlugin directory and creates
	 * a table in the xp bank database.
	 */
	private void createXPBankDatabase() {
		// Initialize a string containing the relative path to the xp bank database.
		String localPath = "jdbc:sqlite:" + databasePath;

		// Try to create the database.
		try {
			// Create the database.
			conn = DriverManager.getConnection(localPath);

			// Initialize the query for creating a new table.
			String statement = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n" + "	UUID text UNIQUE,\n"
					+ "	XP integer CHECK(XP >= 0)\n" + ");";

			// Try to execute the query to the database.
			try (Statement stmt = conn.createStatement()) {
				// Create a new table in the database.
				stmt.execute(statement);
			}
			// An error occurred executing the query to the database.
			catch (SQLException e) {
				EverythingPlugin.getEPLogger().info("An error occurred creating the table for the xp bank database! Disabling plugin.");
				EverythingPlugin.getEPLogger().info(e.getMessage());
				// Bukkit.getServer().getPluginManager().disablePlugin(EverythingPlugin);
			}

			// Check if the creation and connection to the new database was successful.
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				EverythingPlugin.getEPLogger().info("The driver name is " + meta.getDriverName());
				EverythingPlugin.getEPLogger().info("A new database has been created.");
			}
		}
		// An error occurred creating the database.
		catch (SQLException e) {
			EverythingPlugin.getEPLogger().info("An error occurred creating a new xp bank database! Disabling plugin.");
			EverythingPlugin.getEPLogger().info(e.getMessage());
			// Bukkit.getServer().getPluginManager().disablePlugin(EverythingPlugin);
		}
	}
}

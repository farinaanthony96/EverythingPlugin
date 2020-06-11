package com.bluemarien.everythingplugin.resources;

import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.DriverManager;  
import java.sql.SQLException;

/**
 * This class...
 * 
 * @author AnthonyFarina
 * @version 2020.06.11
 */
public class SQLite {

	/**
	 * 
	 */
	Connection conn;
	// Need more stuff?
	
	public SQLite() {
		// Initialize fields.
		createDatabase("test.db");
	}
	
	public void checkDataBase() {
		// Check if database exists. If it doesn't, create one.
	}
	
	public void insert() {
		// Insert a record into the database.
	}
	
	public void remove() {
		// Remove a record from the database.
	}
	
	public void modify() {
		// Modify an entry in the database.
	}
	
	private void createDatabase(String databaseName) {
		String url = "jdbc:sqlite:./plugins/EverythingPlugin/" + databaseName;
		
		try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        }
		catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
}

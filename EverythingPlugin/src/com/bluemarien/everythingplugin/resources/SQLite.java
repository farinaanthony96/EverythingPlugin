package com.bluemarien.everythingplugin.resources;

import java.sql.Connection;  
import java.sql.DatabaseMetaData;  
import java.sql.DriverManager;  
import java.sql.SQLException;

/**
 * This class...
 * 
 * @author AnthonyFarina
 * @version 2020.05.07
 */
public class SQLite {

	/**
	 * 
	 */
	Connection conn;
	// Need more stuff?
	
	public SQLite() {
		// Initialize fields.
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
	
	private void createDatabase() {
		// Create a new database.
	}
}

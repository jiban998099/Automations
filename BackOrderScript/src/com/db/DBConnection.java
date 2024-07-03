package com.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	private static String STR_DB_CONNECTION = "";
	private static String STR_DB_USER = "";
	private static String STR_DB_PASSWORD = "";
	
	private static String ZPROD_DB_CONNECTION = "";
	private static String ZPROD_DB_USER = "";
	private static String ZPROD_DB_PASSWORD = "";
	
	
	public static Connection getSterlingDRConnection() {
		init();
		Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(STR_DB_CONNECTION, STR_DB_USER,
					STR_DB_PASSWORD);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	
	public static Connection getZProdDBConnection() {
		init();
		Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(ZPROD_DB_CONNECTION, ZPROD_DB_USER,
					ZPROD_DB_PASSWORD);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	
	private static void init() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\BackOrderScript\\src\\ProductionDB.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		STR_DB_CONNECTION = properties.getProperty("DBCONNPRD");
		STR_DB_USER = properties.getProperty("DBIDPRD");
		STR_DB_PASSWORD = properties.getProperty("DBPSWDPRD");
		
		ZPROD_DB_CONNECTION = properties.getProperty("ZPRODDBCONN");
		ZPROD_DB_USER = properties.getProperty("ZPRODDBID");
		ZPROD_DB_PASSWORD = properties.getProperty("ZPRODDBPSWD");

	}
}


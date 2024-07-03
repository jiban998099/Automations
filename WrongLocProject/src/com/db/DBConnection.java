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
	
	
	private static String ZPROD_DB_CONNECTION = "";
	private static String ZPROD_DB_USER = "";
	private static String ZPROD_DB_PASSWORD = "";

	
	
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
			return dbConnection;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	

	private static void init() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\WrongLocProject\\Connection.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		ZPROD_DB_CONNECTION = properties.getProperty("ZPRODDBCONN");
		ZPROD_DB_USER = properties.getProperty("ZPRODDBID");
		ZPROD_DB_PASSWORD = properties.getProperty("ZPRODDBPSWD");
		

	}
}

/**
 * 
 */
package com.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Jiban
 */
public class DBConnection {

	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	private static String REP_DB_CONNECTION = "";
	private static String REP_DB_USER = "";
	private static String REP_DB_PASSWORD = "";
	
	
	public static Connection getRepDBConnection() {
		init();
		Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(REP_DB_CONNECTION, REP_DB_USER,
					REP_DB_PASSWORD);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	private static void init() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Properties\\ProductionDB.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		REP_DB_CONNECTION = properties.getProperty("RPDBCONN");
		REP_DB_USER = properties.getProperty("RPDBID");
		REP_DB_PASSWORD = properties.getProperty("RPDBPSWD");

	}
}


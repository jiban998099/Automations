package db;

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
	
	private static String PPRD_CONNECTION = "";
	private static String PPRD_USER = "";
	private static String PPRD_PASSWORD = "";
	

	private static String CLE_DB_CONNECTION = "";
	private static String CLE_DB_USER = "";
	private static String CLE_DB_PASSWORD = "";

	
	public static Connection getSterlingDBConnection() {
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
			System.out.println("Connected to Z-PROD Support");

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
			System.out.println("Connected to ZPROD");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	
	public static Connection getPProdDBConnection() {
		init();
		Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(PPRD_CONNECTION, PPRD_USER,
					PPRD_PASSWORD);
			System.out.println("Connected to PPROD");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	
	public static Connection getCLEDBConnection() {
		init();
		Connection dbConnection = null;

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(CLE_DB_CONNECTION, CLE_DB_USER,
					CLE_DB_PASSWORD);
			System.out.println("Connected to CLE DB");


		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		}

		return dbConnection;

	}

	private static void init() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\pickTicket\\Connection.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		STR_DB_CONNECTION = properties.getProperty("REPDBCONN");
		STR_DB_USER = properties.getProperty("REPDBID");
		STR_DB_PASSWORD = properties.getProperty("REPDBPSWD");
		
		
		ZPROD_DB_CONNECTION = properties.getProperty("ZPRODDBCONN");
		ZPROD_DB_USER = properties.getProperty("ZPRODDBID");
		ZPROD_DB_PASSWORD = properties.getProperty("ZPRODDBPSWD");
		
		
		
		CLE_DB_CONNECTION = properties.getProperty("ERRORDBCONN");
		CLE_DB_USER = properties.getProperty("ERRORDBID");
		CLE_DB_PASSWORD = properties.getProperty("ERRORDBPSWD");
		//System.out.println(CLE_DB_CONNECTION+"--"+CLE_DB_USER+"--"+CLE_DB_PASSWORD);
		
		PPRD_CONNECTION=properties.getProperty("PPDBCONN");
		PPRD_PASSWORD=properties.getProperty("PPDBID");
		PPRD_USER=properties.getProperty("PPDBPSWD");

	}
}

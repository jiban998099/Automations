package com.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import com.constants.Constants;

import com.db.DBConnection;


/** 
 * Author  			: Somnath Pal (592961)
 * Program Name 	: InventoryWrongLocLast.java
 * Description      : Inventory snapshot WrongLoc Inputs created temp tables and creates .csv file for mailing
 * Date - 03/09/2017
 */


public class InventoryWrongLocLast {

	private final static Logger logger = Logger.getLogger(InventoryWrongLocLast.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Connection strConnection = null;
		strConnection = DBConnection.getZProdDBConnection();
		ResultSet rs = null;
		PreparedStatement prepareStmt = null;
		Statement stmt = null;

		try {
			
			CreateTempTables(strConnection);
			SterlingWrongLocDetails2(rs, strConnection);		
		    SterlingWrongLocDetails3(rs, strConnection);
			SterlingWrongLocDetails4(rs, strConnection);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				
				strConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	
	
	private static void CreateTempTables(Connection strConnection) throws SQLException {
		
		Statement stmt = strConnection.createStatement();
		logger.info("Creating Table: " + Constants.HELD_TRANSFER_TEMP_CHECK);
		stmt.executeQuery(Constants.HELD_TRANSFER_TEMP_CHECK);
		
				
		logger.info("Creating Table: " + Constants.WSI_PO_QTY_STER_RMS_1);
		stmt.executeQuery(Constants.TRANSFER_RESERVE_TEMP_CHECK);
				
		
		logger.info("Creating Table: " + Constants.held_t_2);
		stmt.executeQuery(Constants.held_t_2);
		
		
		logger.info("Creating Table: " + Constants.tr_t_2);
		stmt.executeQuery(Constants.tr_t_2);
		
		
	}



	private static void SterlingWrongLocDetails2(ResultSet rs,
			Connection strConnection) throws SQLException {
		Statement strStmt = strConnection.createStatement();
		System.out.println( "Query to execute: " + Constants.STR_WRONG_LOC_DATA_2);
		rs = strStmt
				.executeQuery(Constants.STR_WRONG_LOC_DATA_2);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1));
			sBuilder.append(rs.getString(1));
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of WrongLoc 2: "
				+ count);
		writeToFile(sBuilder.toString(), "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\WrongLocProject\\output\\Wrong_LOC_2.xml");

	}
	
	private static void SterlingWrongLocDetails3(ResultSet rs,
			Connection strConnection) throws SQLException {
		Statement strStmt = strConnection.createStatement();
		System.out.println( "Query to execute: " + Constants.STR_WRONG_LOC_DATA_3);
		rs = strStmt
				.executeQuery(Constants.STR_WRONG_LOC_DATA_3);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1));
			sBuilder.append(rs.getString(1));
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of WrongLoc Count for Dashboard: "
				+ count);
		writeToFile(sBuilder.toString(), "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\WrongLocProject\\output\\Wrong_LOC_3.xml");

	}
	
	private static void SterlingWrongLocDetails4(ResultSet rs, Connection strConnection)
			throws SQLException {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt
				.executeQuery(Constants.STR_WRONG_LOC_DATA_4);
		StringBuilder sBuilder = new StringBuilder(
				"ITEM_ID, ORGANIZATION_CODE, SHIPNODE_KEY, SUPPLY_REFERENCE, QUANTITY\n");
		while (rs.next()) {
			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5));
			sBuilder.append(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5)+ "\n");
		}

		writeToFile(sBuilder.toString(), "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\WrongLocProject\\output\\Wrong_LOC_Negative_Qty.csv");
		logger.info("\n Wrong Loc Completed!!");
	}


	private static void writeToFile(String content, String fileName) {
		try {

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}

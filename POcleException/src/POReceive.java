import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import constants.Constants;
import db.DBConnection;

public class POReceive {

	private final static Logger logger = Logger.getLogger(POReceive.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection cleConnection = null;
		Connection strConnection = null;
		cleConnection = DBConnection.getCLEDBConnection();
		strConnection = DBConnection.getSterlingDBConnection();
		Statement cleStmt = null;
		ResultSet rs = null;

		try {
			cleStmt = cleConnection.createStatement();
			getExceptionSummary(rs, cleStmt);
			getReceiveExceptionDetails(rs, cleStmt, strConnection);
			getSterlingDetails(rs, strConnection);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				cleStmt.close();
				cleConnection.close();
				strConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Done!");
	}

	private static void getSterlingDetails(ResultSet rs,
			Connection strConnection) throws SQLException {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt.executeQuery(Constants.STR_PO_DATA_4_RECEIVE);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , "
					+ rs.getString(5) + " , " + rs.getString(6) + " , "
					+ rs.getString(7) + " , " + rs.getString(8) + " , "
					+ rs.getString(9) + " , " + rs.getString(10) + " , "
					+ rs.getString(11) + " , " + rs.getString(12));
			sBuilder.append(rs.getString(12));
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of Order Need to be Received/Reprocessed In Sterling: "
				+ count);
		writeToFile(sBuilder.toString(), "POReceive.xml");

	}

	private static void writeToFile(String content, String fileName) {
		try {

			File file = new File(fileName);
			System.out.println(fileName);
			// if file doesn't exist, then create it
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

	private static void getReceiveExceptionDetails(ResultSet rs,
			Statement cleStmt, Connection strConnection) throws SQLException {
		rs = cleStmt.executeQuery(Constants.CLE_RECEIVE_EXCEPTION);
		PreparedStatement strPrepStmt = strConnection
				.prepareStatement("TRUNCATE TABLE PO_EXP_REPROCESS_RECEIVE_1");
		strPrepStmt.execute();
		strPrepStmt = strConnection
				.prepareStatement("INSERT INTO PO_EXP_REPROCESS_RECEIVE_1 VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		int count = 0;
		while (rs.next()) {

			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , "
					+ rs.getString(5) + " , " + rs.getString(6) + " , "
					+ rs.getString(7) + " , " + rs.getString(8) + " , "
					+ rs.getString(9) + " , " + rs.getString(10) + " , "
					+ rs.getString(11) + " , " + rs.getString(12) + " , "
					+ rs.getString(13));

			strPrepStmt.setString(1, rs.getString(3));
			strPrepStmt.setString(2, rs.getString(4));
			strPrepStmt.setString(3, rs.getString(5));
			strPrepStmt.setString(4, rs.getString(6));
			strPrepStmt.setString(5, rs.getString(7));
			strPrepStmt.setString(6, rs.getString(8));
			strPrepStmt.setString(7, rs.getString(9));
			strPrepStmt.setString(8, rs.getString(10));
			strPrepStmt.setString(9, rs.getString(11));
			strPrepStmt.setString(10, rs.getString(12));
			strPrepStmt.setCharacterStream(11,
					new StringReader(rs.getString(13)), rs.getString(13)
							.length());
			strPrepStmt.addBatch();
			strPrepStmt.clearParameters();
			count++;
		}
		logger.info("Total Number of PO Receive Exception in CLE: " + count);
		strPrepStmt.executeBatch();
	}

	private static void getExceptionSummary(ResultSet rs, Statement cleStmt)
			throws SQLException {
		rs = cleStmt.executeQuery(Constants.CLE_COUNT_EXCEPTION);
		StringBuilder sBuilder = new StringBuilder("InterfaceName, ExceptionCode, Msg, Count\n");
		while (rs.next()) {
			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4));
			sBuilder.append(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4)+"\n");
		}
		
		writeToFile(sBuilder.toString(), "ExceptionSummary.csv");
	}

}

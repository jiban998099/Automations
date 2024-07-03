package com.main;
import com.db.DBConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BY_ECDD_TIBCOlog {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {

    	//BY_ECDD_TIBCOlog
    	String bY_ECDD_TIBCOlog = "SELECT /*+PARALLEL (4)*/ to_char(a.audit_timestamp, 'YYYY-MM-DD HH24:MI:SS') AS timestamp, " +
    			"a.audit_timestamp AS responsets_ts, " +
    			"b.audit_timestamp AS requestts_ts " +
    			//"a.transactionid " +
    		    "FROM cle_owner.app_audit a, cle_owner.app_audit b " +
    		    "WHERE b.transactionid = a.transactionid " +
    		    "AND a.msg ='Received New Sourcing Service Response Payload' " +
    		    "AND b.msg ='ECOM REQUEST SENT TO NEW BY SOURCING SERVICE' " +
    		    "AND a.interfaceid = '825' " +
    		    "AND A.Audit_Timestamp > TRUNC(SYSDATE) - INTERVAL '2' DAY + TO_DSINTERVAL('0 23:59:59.000000000') " +
    		    "AND A.Audit_Timestamp < TRUNC(SYSDATE) - INTERVAL '1' DAY + TO_DSINTERVAL('0 23:59:59.000000000') " +
    		    "AND B.Audit_Timestamp > TRUNC(SYSDATE) - INTERVAL '2' DAY + TO_DSINTERVAL('0 23:59:59.000000000') " +
    		    "AND B.Audit_Timestamp < TRUNC(SYSDATE) - INTERVAL '1' DAY + TO_DSINTERVAL('0 23:59:59.000000000') " +
    		    "AND b.interfaceid = '825'";

    	

        // Obtain a database connection
        Connection connection = DBConnection.getCleDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("BY_ECDD_TIBCOlog");
                executeQuery(connection, "BY_ECDD_TIBCOlog", bY_ECDD_TIBCOlog, sheet1);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\BY_ECDD_TIBCOlog.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to BY_ECDD_TIBCOlog.xlsx");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to obtain database connection.");
        }
    }

    private static void executeQuery(Connection connection, String heading, String sqlQuery, Sheet sheet) throws SQLException {
        System.out.println("The query execution will take time so please keep patience..... :)");
        System.out.println("Executing query for: " + heading);
        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sqlQuery);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            Row headingRow = sheet.createRow(0);
            Cell headingCell = headingRow.createCell(0);
            headingCell.setCellValue(heading);

            Row headerRow = sheet.createRow(1);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = headerRow.createCell(i - 1);
                cell.setCellValue(metaData.getColumnName(i));
            }

            int rowNum = 2;
            int rowCount = 0; // Initialize row count
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
                    cell.setCellValue(resultSet.getString(i));
                }
                rowCount++; 
            }

            resultSet.close();
            System.out.println("Query execution for " + heading + " successful. Fetched " + rowCount + " records.");

        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
            throw e; 
        }
    }
}


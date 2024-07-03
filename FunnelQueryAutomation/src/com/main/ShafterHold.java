/**
 * 
 */
package com.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.db.DBConnection;

/**
 * @author Jiban
 */
public class ShafterHold {

    public static void main(String[] args) {

    	//ShafterHold
    	String shafterHold = "select oht.hold_type,count(yoh.ordeR_no) from YANTRA_OWNER.yfs_order_header yoh, " +
    			"YANTRA_OWNER.yfs_order_hold_type oht where oht.ordeR_headeR_key = yoh.order_header_key " +
    			"and oht.hold_type in ('SHAFTER_HOLD','THRESHOLD_REVIEW','FAILED_AUTH','FAILED_CHARGE','REDEMPTION_FAILURE','REVIEW_FRAUD','FAIL_CHARGE_CC_B2B','FAIL_CHARGE_CC_NOB2B') " +
    			"and oht.order_header_key > to_char(sysdate-1,'YYYYMMDD') and oht.order_header_key < to_char(sysdate,'YYYYMMDD') " +
    			"and oht.status='1100' group by oht.hold_type ";
    	

        // Obtain a database connection
        Connection connection = DBConnection.getRepDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("ShafterHold");
                executeQuery(connection, "ShafterHold", shafterHold, sheet1);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\FunnelViewReport\\ShafterHold.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to ShafterHold.xlsx");
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
        Statement statement = connection.createStatement();

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
        while (resultSet.next()) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = row.createCell(i - 1);
                cell.setCellValue(resultSet.getString(i));
            }
        }

        resultSet.close();
        statement.close();
        System.out.println("Query execution for " + heading + " successful....");
    }
}

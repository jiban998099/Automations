package com.dbr;
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

public class POReceiptQry {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {

    	//POReceiptPreWK
    	String pOReceiptPreWK = "select oh.ENTERPRISE_KEY,count(*), sum (ors.status_quantity) total_quantity " +
    			"from yantra_owner.yfs_order_header oh, Yantra_owner.yfs_order_line ol, " +
    			"yantra_owner.yfs_order_release_status ors where oh.order_header_key = ol.order_header_key " +
    			"and ol.order_line_key = ors.order_line_key and oh.document_type='0005' " +
    			"and ors.order_release_status_key > to_char(sysdate-9,'YYYYMMDD') " +
    			"and ors.order_release_status_key < to_char(sysdate-8,'YYYYMMDD') " +
    			"and ors.status_quantity>0 and ors.status in ('3900','3950') " +
    			"and oh.order_type='2' group by oh.ENTERPRISE_KEY order by oh.ENTERPRISE_KEY ";
    			
        //POReceiptRepDy
    	String pOReceiptRepDy = "select oh.ENTERPRISE_KEY,count(*), sum (ors.status_quantity) total_quantity " +
    			"from yantra_owner.yfs_order_header oh, Yantra_owner.yfs_order_line ol, " +
    			"yantra_owner.yfs_order_release_status ors where oh.order_header_key = ol.order_header_key " +
    			"and ol.order_line_key = ors.order_line_key and oh.document_type='0005' " +
    			"and ors.order_release_status_key > to_char(sysdate-2,'YYYYMMDD') " +
    			"and ors.order_release_status_key < to_char(sysdate-1,'YYYYMMDD') " +
    			"and ors.status_quantity>0 and ors.status in ('3900','3950') " +
    			"and oh.order_type='2' group by oh.ENTERPRISE_KEY order by oh.ENTERPRISE_KEY ";
    	

        // Obtain a database connection
        Connection connection = DBConnection.getReportingDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("POReceiptPreWK");
                executeQuery(connection, "POReceiptPreWK", pOReceiptPreWK, sheet1);
                
                Sheet sheet2 = workbook.createSheet("POReceiptRepDy");
                executeQuery(connection, "POReceiptRepDy", pOReceiptRepDy, sheet2);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DBRQueryOutput\\POReceiptQry.xlsx")) {
                    workbook.write(outputStream);
                    workbook.close();
                }

                connection.close();

                System.out.println("Query results have been written to POReceiptQry.xlsx");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to obtain database connection.");
        }
    }

    private static void executeQuery(Connection connection, String heading, String sqlQuery, Sheet sheet) throws SQLException {
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


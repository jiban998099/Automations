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

public class OrderLineCount {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {
    	
    	//Previous Week Order Line Count
    	String previousWeekOrderLineCount = "SELECT /*+PARALLEL (3)*/ l.fulfillment_type,count(l.order_line_key) as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, " +
    			"YANTRA_OWNER.yfs_order_line l, YANTRA_OWNER.YFS_STATUS YS " +
    			"WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
    			"and s.status in ('1600') AND l.KIT_CODE <> 'BUNDLE' " +
    			"AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT' group by l.fulfillment_type ";
    	//Reporting Day Order Line Count
    	String reportingDayOrderLineCount = "SELECT /*+PARALLEL (3)*/ l.fulfillment_type,count(l.order_line_key) as linekey " +
    	    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, " +
    	    			"YANTRA_OWNER.yfs_order_line l, YANTRA_OWNER.YFS_STATUS YS " +
    	    			"WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    	    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    	    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    	    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    	    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    	    			"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
    	    			"and s.status in ('1600') AND l.KIT_CODE <> 'BUNDLE' " +
    	    			"AND l.item_id NOT LIKE 'DeliveryItem%' " +
    	    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    	    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT' group by l.fulfillment_type ";

        // Obtain a database connection
        Connection connection = DBConnection.getReportingDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("Previous Week Order Line Count");
                executeQuery(connection, "Previous Week Order Line Count", previousWeekOrderLineCount, sheet1);

                Sheet sheet2 = workbook.createSheet("Reporting Day Order Line Count");
                executeQuery(connection, "Reporting Day Order Line Count", reportingDayOrderLineCount, sheet2);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DBRQueryOutput\\OrderLineCount.xlsx")) {
                    workbook.write(outputStream);
                    workbook.close();
                }

                connection.close();

                System.out.println("Query results have been written to OrderLineCount.xlsx");
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


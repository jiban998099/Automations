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

public class ShipmentQueries {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {
    	
    	//Previous Week UPSN/TRK Orders
        String previousWeekUPSNTRKOrders = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
        		"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
        		"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
        		"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
        		"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
        		"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
        		"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
        		"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
        		"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
        		"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    	
    	//Sum of Previous Week UPSN/TRK Orders
        String sumOfPreviousWeekUPSNTRKOrders = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
        		"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
        		"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
        		"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
        		"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
        		"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
        		"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
        		"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
        		"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
        		"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
        		"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";
        
        //Reporting Day UPS/TRK Orders
        String reportingDayUPSNTRKOrders = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
        		"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
        		"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
        		"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
        		"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
        		"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
        		"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
        		"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
        		"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
        		"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
        
        //Sum of Reporting Day UPSN/TRK Orders
        String sumOfReportingDayUPSNTRKOrders = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
        		"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
        		"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
        		"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
        		"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
        		"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
        		"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
        		"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
        		"AND YS.STATUS = S.STATUS AND s.status > '1000' " +
        		"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
        		"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";
        
        // Obtain a database connection
        Connection connection = DBConnection.getReportingDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                // Create sheets for previous week and reporting day orders
                Sheet sheet1 = workbook.createSheet("Previous_Week");
                int nextRow1 = executeQuery(connection, "Previous Week UPSN_TRK Orders", previousWeekUPSNTRKOrders, sheet1);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders", sumOfPreviousWeekUPSNTRKOrders, sheet1, nextRow1 + 1);

                Sheet sheet2 = workbook.createSheet("Reporting_Day");
                int nextRow2 = executeQuery(connection, "Reporting Day UPSN_TRK Orders", reportingDayUPSNTRKOrders, sheet2);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders", sumOfReportingDayUPSNTRKOrders, sheet2, nextRow2 + 1);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DBRQueryOutput\\ShipmentQueries.xlsx")) {
                    workbook.write(outputStream);
                    workbook.close();
                }

                connection.close();

                System.out.println("Query results have been written to ShipmentQueries.xlsx");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to obtain database connection.");
        }
    }

    private static int executeQuery(Connection connection, String heading, String sqlQuery, Sheet sheet) throws SQLException {
        System.out.println("Executing query for: " + heading);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Create heading row
        Row headingRow = sheet.createRow(0);
        Cell headingCell = headingRow.createCell(0);
        headingCell.setCellValue(heading);

        // Create header row
        Row headerRow = sheet.createRow(1);
        for (int i = 1; i <= columnCount; i++) {
            Cell cell = headerRow.createCell(i-1);
            cell.setCellValue(metaData.getColumnName(i));
        }

        // Populate data rows
        int rowIndex = 2; // Start populating data from row 2
        while (resultSet.next()) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = row.createCell(i-1);
                cell.setCellValue(resultSet.getString(i));
            }
        }

        resultSet.close();
        statement.close();
        System.out.println("Query execution for " + heading + " successful.");

        // Return the next row index
        return rowIndex;
    }

    private static void executeSummaryQuery(Connection connection, String heading, String sqlQuery, Sheet sheet, int startRow) throws SQLException {
        System.out.println("Executing summary query for: " + heading);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Create heading row for summary
        Row headingRow = sheet.createRow(startRow);
        Cell headingCell = headingRow.createCell(0);
        headingCell.setCellValue(heading);

        // Create header row for summary
        Row headerRow = sheet.createRow(startRow + 1);
        for (int i = 1; i <= columnCount; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(metaData.getColumnName(i));
        }

        // Populate data row for summary
        if (resultSet.next()) {
            Row dataRow = sheet.createRow(startRow + 2);
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = dataRow.createCell(i);
                cell.setCellValue(resultSet.getString(i));
            }
        }

        resultSet.close();
        statement.close();
        System.out.println("Summary query execution for " + heading + " successful.");
    }
}




package lowPzOrderCount;

import db.DBConnection;

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

/**
 * @author Jiban
 */
public class LowPZOrderCount {

    public static void main(String[] args) {
	
    	//LowPZOrderCountUSA
    	String lowPZOrderCountUSA = "select oh.order_no, oh.order_header_key, ors.ORDER_LINE_KEY " +
    	"from YANTRA_OWNER.wsi_vas_data vd,yantra_owner.yfs_order_line ol,YANTRA_OWNER.yfs_order_header oh, " +
    	"YANTRA_OWNER.yfs_order_release_status ors where oh.order_header_key=ol.order_header_key " +
    	"and ol.order_line_key=vd.extn_order_line_key and ors.order_header_key=ol.order_header_key " +
    	"and ors.order_line_key=vd.extn_order_line_key and oh.draft_order_flag='N' " +
    	"and ol.LEVEL_OF_SERVICE in ('MONOPZ') and ol.carrier_service_code in ('REGULAR_SS','REGULAR_CMO') " +
    	"and oh.document_type='0001' and ors.ORDER_RELEASE_KEY>to_char(sysdate -(1 / 24), 'YYYYMMDDHH24') " +
    	"and ors.status>'3200' and oh.extn_market_code='USA' and ors.status_quantity>0 ";

    	//LowPZOrderCountCAN
    	String lowPZOrderCountCAN = "select oh.order_no, oh.order_header_key, ors.ORDER_LINE_KEY " +
    	"from YANTRA_OWNER.wsi_vas_data vd,yantra_owner.yfs_order_line ol,YANTRA_OWNER.yfs_order_header oh, " +
    	"YANTRA_OWNER.yfs_order_release_status ors where oh.order_header_key=ol.order_header_key " +
    	"and ol.order_line_key=vd.extn_order_line_key and ors.order_header_key=ol.order_header_key " +
    	"and ors.order_line_key=vd.extn_order_line_key and oh.draft_order_flag='N' " +
    	"and ol.LEVEL_OF_SERVICE in ('MONOPZ') and ol.carrier_service_code in ('REGULAR_SS','REGULAR_CMO') " +
    	"and oh.document_type='0001' and ors.ORDER_RELEASE_KEY>to_char(sysdate -(1 / 24), 'YYYYMMDDHH24') " +
    	"and ors.status>'3200' and ors.status_quantity>0and oh.extn_market_code='CAN' ";
        

        // Obtain a database connection
        Connection connection = DBConnection.getSterlingDRConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("LowPZOrderCountUSA");
                executeQuery(connection, "LowPZOrderCountUSA", lowPZOrderCountUSA, sheet1);

                Sheet sheet2 = workbook.createSheet("LowPZOrderCountCAN");
                executeQuery(connection, "LowPZOrderCountCAN", lowPZOrderCountCAN, sheet2);	

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LowPZOrderCount.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to LowPZOrderCount.xlsx");
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
        
        int rowCount = 0;

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
            rowCount++;
        }

        resultSet.close();
        statement.close();
        System.out.println("Query execution for " + heading + " successful....");
        System.out.println("Total rows fetched for " + heading + ": " + rowCount);
    }
}

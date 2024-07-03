package taskQTable;

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
public class TaskQTable {

    public static void main(String[] args) {
	
		//ScheduleTaskQ
		String scheduleTaskQ = "SELECT distinct e.task_q_key,a.order_no,a.order_header_key " +
				"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
				"yantra_owner.yfs_order_line c, yantra_owner.yfs_task_q e " +
				"WHERE b.order_header_key =a.order_header_key AND b.order_line_key =c.order_line_key " +
				"AND c.order_header_key =a.order_header_key and e.data_key=a.order_header_key " +
				"AND a.document_type ='0001' and c.shipnode_key like '%DTC%' " +
				"and e.transaction_key like 'SCHEDULE.0001%' and e.hold_flag='N' and e.lockid<'1' " +
				"and e.task_q_key>TO_CHAR(sysdate-1,'YYYYMMDD') and e.available_date>sysdate-1/48 " +
				"and b.status_quantity>'0' and b.status<'1300' " +
				"and b.status>'1099' and c.kit_Code<>'BUNDLE' " +
				"and a.payment_status='AUTHORIZED' AND a.order_header_key >TO_CHAR(sysdate-1,'YYYYMMDD')";
		
		//ScheduleTaskQCAN
		String scheduleTaskQCAN = "SELECT distinct e.task_q_key,a.order_no,a.order_header_key " +
				"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
				"yantra_owner.yfs_order_line c, yantra_owner.yfs_task_q e " +
				"WHERE b.order_header_key =a.order_header_key AND b.order_line_key =c.order_line_key " +
				"AND c.order_header_key =a.order_header_key and e.data_key=a.order_header_key " +
				"AND a.document_type ='0001' and a.extn_market_code='CAN' " +
				"and c.shipnode_key like '%DTC%' and e.transaction_key like 'SCHEDULE.0001%' and e.hold_flag='N' and e.lockid<'1' " +
				"and e.task_q_key>TO_CHAR(sysdate-1,'YYYYMMDD') and e.available_date>sysdate-1/48 " +
				"and b.status_quantity>'0' and b.status<'1300' and b.status>'1099' and c.kit_Code<>'BUNDLE' " +
				"and a.payment_status='AUTHORIZED' AND a.order_header_key >TO_CHAR(sysdate-1,'YYYYMMDD') ";
		
		//ReleaseTaskQ
		String releaseTaskQ = "SELECT distinct e.task_q_key,a.order_no,a.order_header_key " +
				"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
				"yantra_owner.yfs_order_line c, yantra_owner.yfs_task_q e " +
				"WHERE b.order_header_key =a.order_header_key AND b.order_line_key =c.order_line_key " +
				"AND c.order_header_key =a.order_header_key and e.data_key=a.order_header_key " +
				"AND a.document_type ='0001' and c.shipnode_key like '%DTC%' " +
				"and e.transaction_key like 'RELEASE.0001%' and e.hold_flag='N' and e.lockid<'1' " +
				"and e.task_q_key>TO_CHAR(sysdate-1,'YYYYMMDD') and e.available_date>sysdate-1/48 " +
				"and b.status_quantity>'0' and b.status<'1500.102' and b.status>'1500.100' " +
				"and c.kit_Code<>'BUNDLE' and a.payment_status='AUTHORIZED' AND a.order_header_key >TO_CHAR(sysdate-1,'YYYYMMDD') ";
		
		//ReleaseTaskQCAN
		String releaseTaskQCAN = "SELECT distinct e.task_q_key,a.order_no,a.order_header_key " +
				"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
				"yantra_owner.yfs_order_line c, yantra_owner.yfs_task_q e " +
				"WHERE b.order_header_key =a.order_header_key AND b.order_line_key =c.order_line_key " +
				"AND c.order_header_key =a.order_header_key and e.data_key=a.order_header_key " +
				"AND a.document_type ='0001' and a.extn_market_code='CAN' and c.shipnode_key like '%DTC%' " +
				"and e.transaction_key like 'RELEASE.0001%' and e.hold_flag='N' and e.lockid<'1' " +
				"and e.task_q_key>TO_CHAR(sysdate-1,'YYYYMMDD') and e.available_date>sysdate-1/48 " +
				"and b.status_quantity>'0' and b.status<'1500.102' and b.status>'1500.100' " +
				"and c.kit_Code<>'BUNDLE' and a.payment_status='AUTHORIZED' " +
				"AND a.order_header_key >TO_CHAR(sysdate-1,'YYYYMMDD') ";
        

        // Obtain a database connection
        Connection connection = DBConnection.getSterlingDRConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("ScheduleTaskQ");
                executeQuery(connection, "ScheduleTaskQ", scheduleTaskQ, sheet1);

                Sheet sheet2 = workbook.createSheet("ScheduleTaskQCAN");
                executeQuery(connection, "ScheduleTaskQCAN", scheduleTaskQCAN, sheet2);

                Sheet sheet3 = workbook.createSheet("ReleaseTaskQ");
                executeQuery(connection, "ReleaseTaskQ", releaseTaskQ, sheet3);

                Sheet sheet4 = workbook.createSheet("ReleaseTaskQCAN");
                executeQuery(connection, "ReleaseTaskQCAN", releaseTaskQCAN, sheet4);	

                // Write the workbook content to a file
     
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\TaskQTable.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to TaskQTable.xlsx");
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
        System.out.println("Current Backlog for " + heading + ": " + rowCount + " records");
    }
}

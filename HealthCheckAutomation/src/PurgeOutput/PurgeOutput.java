package PurgeOutput;
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
public class PurgeOutput {

    public static void main(String[] args) {
    	
    	//SalesOrderPurge
        String salesOrderPurge = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='SalesOrderPurge' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name='NumOrdersPurged' " +
        		"group by  substr(statistics_detail_key,1,8),server_name ";
        //POPurgerAgentServer
        String pOPurgerAgentServer = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='POPurgerAgentServer' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name='NumOrdersPurged' " +
        		"group by  substr(statistics_detail_key,1,8),server_name ";
        
        //ShipmentPurge
        String shipmentPurge = "SELECT substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"FROM   yantra_owner.yfs_statistics_detail " +
        		"WHERE  server_name in ('SHIPMENTPRGPBPK','SHIPMENTPRG') " +
        		"AND statistic_name = 'NumShipmentsPurged' " +
        		"AND statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') " +
        		"group by  substr(statistics_detail_key,1,8),server_name " +
        		"order by substr(statistics_detail_key,1,8),server_name ";
        
        
        //AlertPurge
        String alertPurge = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='AlertPurgeServer' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name='NumInboxesPurged_Live' " +
        		"group by substr(statistics_detail_key,1,8),server_name ";
        
        //InboxPurge
        String inboxPurge = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='InboxPurgeServer' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name='NumInboxesPurged_Live' " +
        		"group by substr(statistics_detail_key,1,8),server_name ";
        
        //ReturnShpPurge
        String returnShpPurge = "SELECT substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"FROM   yantra_owner.yfs_statistics_detail " +
        		"WHERE  server_name in ('RETURNSHPPRG') " +
        		"AND statistic_name = 'NumShipmentsPurged' " +
        		"AND statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') " +
        		"group by  substr(statistics_detail_key,1,8),server_name " +
        		"order by substr(statistics_detail_key,1,8),server_name ";
        
        //WorkOrderPurgeAgent
        String workOrderPurgeAgent = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='WorkOrderPurgeAgent' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name='NumWorkOrdersPurged' " +
        		"group by  substr(statistics_detail_key,1,8),server_name ";
        
        //InventoryPurgeServer
        String inventoryPurgeServer = "select substr(statistics_detail_key,1,8),server_name, sum (statistic_value) " +
        		"from YANTRA_OWNER.yfs_statistics_detail where server_name='InventoryPurgeServer' " +
        		"and statistics_detail_key > to_char(sysdate-1,'YYYYMMDD') and statistic_name in ('NumInventoryAuditsPurged_Live','NumInventorySupplyTempsPurged','NumItemAuditRecordsPurged') " +
        		"group by  substr(statistics_detail_key,1,8),server_name ";

        // Obtain a database connection
        Connection connection = DBConnection.getSterlingDRConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("SalesOrderPurge");
                executeQuery(connection, "SalesOrderPurge", salesOrderPurge, sheet1);
        		
        		Sheet sheet2 = workbook.createSheet("POPurgerAgentServer");
                executeQuery(connection, "POPurgerAgentServer", pOPurgerAgentServer, sheet2);
                		
        		Sheet sheet3 = workbook.createSheet("ShipmentPurge");
                executeQuery(connection, "ShipmentPurge", shipmentPurge, sheet3);	

        		Sheet sheet4 = workbook.createSheet("AlertPurge");
                executeQuery(connection, "AlertPurge", alertPurge, sheet4);	
        				
        		Sheet sheet5 = workbook.createSheet("InboxPurge");
                executeQuery(connection, "InboxPurge", inboxPurge, sheet5);	
        		
        		Sheet sheet6 = workbook.createSheet("ReturnShpPurge");
                executeQuery(connection, "ReturnShpPurge", returnShpPurge, sheet6);	
        		
        		Sheet sheet7 = workbook.createSheet("WorkOrderPurgeAgent");
                executeQuery(connection, "WorkOrderPurgeAgent", workOrderPurgeAgent, sheet7);	
                
        		Sheet sheet8 = workbook.createSheet("InventoryPurgeServer");
                executeQuery(connection, "InventoryPurgeServer", inventoryPurgeServer, sheet8);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\PurgeOutput.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to PurgeOutput.xlsx");
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

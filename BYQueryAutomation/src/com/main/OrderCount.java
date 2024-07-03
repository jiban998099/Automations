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

public class OrderCount {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {

    	//OrderCreateMG
    	String orderCreateMG = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' " +
    	"and d.status=b.status and a.enterprise_key='MG' and d.process_type_key='ORDER_FULFILLMENT' " +
    	"AND b.order_line_key =c.order_line_key AND c.order_header_key =a.order_header_key and e.item_id=c.item_id " +
    	"and a.document_type='0001' and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' " +
    	"AND substr(c.extn_zip_code,1,5) <='99499') ) and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropMG
    	String orderDropMG = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='MG' and d.process_type_key='ORDER_FULFILLMENT' " +
    	"AND b.order_line_key =c.order_line_key AND c.order_header_key =a.order_header_key " +
    	"and e.item_id=c.item_id and a.document_type='0001' and b.status_quantity>'0' " +
    	"and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreateWE
    	String orderCreateWE = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='WE' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropWE
    	String orderDropWE = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='WE' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreatePB
    	String orderCreatePB = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PB' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropPB
    	String orderDropPB = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PB' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreatePK
    	String orderCreatePK = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PK' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropPK
    	String orderDropPK = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PK' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreatePT
    	String orderCreatePT = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PT' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropPT
    	String orderDropPT = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='PT' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') ) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreateWS
    	String orderCreateWS = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='WS' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) )>='00001' AND substr(c.extn_zip_code,1,5) <='99499') " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropWS
    	String orderDropWS = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='WS' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0'and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499')) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreateGR
    	String orderCreateGR = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='GR' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD')) group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropGR
    	String orderDropGR = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='GR' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499') " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD')) group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderCreateRJ
    	String orderCreateRJ = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='RJ' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499')) " +
    	"and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";

    	//OrderDropRJ
    	String orderDropRJ = "SELECT count(distinct(a.order_no)),b.status,d.description,a.enterprise_key " +
    	"FROM yantra_owner.yfs_order_header a, yantra_owner.yfs_order_release_status b , " +
    	"yantra_owner.yfs_order_line c, yantra_owner.yfs_status d, yantra_owner.yfs_item e " +
    	"WHERE b.order_header_key =c.order_header_key and c.kit_Code<>'BUNDLE' and d.status=b.status " +
    	"and a.enterprise_key='RJ' and d.process_type_key='ORDER_FULFILLMENT' AND b.order_line_key =c.order_line_key " +
    	"AND c.order_header_key =a.order_header_key and e.item_id=c.item_id and a.document_type='0001' " +
    	"and b.status_quantity>'0' and ((substr(c.extn_zip_code,1,5) >='00001' AND substr(c.extn_zip_code,1,5) <='99499')) " +
    	"and b.status>'2099' and b.status>'1099' AND a.order_header_key >to_char(sysdate-1,'YYYYMMDD') " +
    	"AND a.order_header_key <to_char(sysdate,'YYYYMMDD') group by b.status,d.description,a.enterprise_key order by b.status ";
 
    	

        // Obtain a database connection
        Connection connection = DBConnection.getSterlingDRConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("OrderCreateMG");
                executeQuery(connection, "OrderCreateMG", orderCreateMG, sheet1);

                Sheet sheet2 = workbook.createSheet("OrderDropMG");
                executeQuery(connection, "OrderDropMG", orderDropMG, sheet2);

                Sheet sheet3 = workbook.createSheet("OrderCreateWE");
                executeQuery(connection, "OrderCreateWE", orderCreateWE, sheet3);

                Sheet sheet4 = workbook.createSheet("OrderDropWE");
                executeQuery(connection, "OrderDropWE", orderDropWE, sheet4);

                Sheet sheet5 = workbook.createSheet("OrderCreatePB");
                executeQuery(connection, "OrderCreatePB", orderCreatePB, sheet5);

                Sheet sheet6 = workbook.createSheet("OrderDropPB");
                executeQuery(connection, "OrderDropPB", orderDropPB, sheet6);

                Sheet sheet7 = workbook.createSheet("OrderCreatePK");
                executeQuery(connection, "OrderCreatePK", orderCreatePK, sheet7);

                Sheet sheet8 = workbook.createSheet("OrderDropPK");
                executeQuery(connection, "OrderDropPK", orderDropPK, sheet8);

                Sheet sheet9 = workbook.createSheet("OrderCreatePT");
                executeQuery(connection, "OrderCreatePT", orderCreatePT, sheet9);

                Sheet sheet10 = workbook.createSheet("OrderDropPT");
                executeQuery(connection, "OrderDropPT", orderDropPT, sheet10);

                Sheet sheet11 = workbook.createSheet("OrderCreateWS");
                executeQuery(connection, "OrderCreateWS", orderCreateWS, sheet11);

                Sheet sheet12 = workbook.createSheet("OrderDropWS");
                executeQuery(connection, "OrderDropWS", orderDropWS, sheet12);

                Sheet sheet13 = workbook.createSheet("OrderCreateGR");
                executeQuery(connection, "OrderCreateGR", orderCreateGR, sheet13);

                Sheet sheet14 = workbook.createSheet("OrderDropGR");
                executeQuery(connection, "OrderDropGR", orderDropGR, sheet14);

                Sheet sheet15 = workbook.createSheet("OrderCreateRJ");
                executeQuery(connection, "OrderCreateRJ", orderCreateRJ, sheet15);

                Sheet sheet16 = workbook.createSheet("OrderDropRJ");
                executeQuery(connection, "OrderDropRJ", orderDropRJ, sheet16);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\OrderCount.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();

                System.out.println("Query results have been written to OrderCount.xlsx");
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


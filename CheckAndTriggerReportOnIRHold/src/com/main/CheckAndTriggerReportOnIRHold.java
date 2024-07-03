package com.main;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.db.DBConnection;

/**
 * @author Jiban
 */

public class CheckAndTriggerReportOnIRHold {

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getSterlingDRConnection();
			System.out.println("Starting CheckAndTriggerReportOnIRHold Operation...");
            System.out.println("Database connection established....");

            String query = "SELECT DISTINCT h.order_no, h.order_type, h.entry_type, h.order_date, l.extn_config_id, oh.hold_type, l.extn_is_mto, " +
            		"h.draft_order_flag, h.payment_status, yi.extn_item_type, h.createts " +
            		"FROM yantra_owner.yfs_order_header h JOIN yantra_owner.yfs_order_line l on h.order_header_key = l.order_header_key " +
            		"JOIN YANTRA_OWNER.yfs_order_release_status ors on l.order_line_key = ors.order_line_key " +
            		"JOIN yantra_owner.yfs_order_hold_type oh on h.order_header_key = oh.order_header_key " +
            		"JOIN yantra_owner.yfs_item yi on l.item_id = yi.item_id WHERE h.document_type = '0001' " +
            		"AND h.enterprise_key = 'RJ' AND ors.status_quantity > '0' AND ors.status = '1000' AND oh.HOLD_TYPE = 'ITEM_REALIZATION_HLD' " +
            		"AND oh.status in ('1100') AND oh.createts < SYSDATE - 2 AND yi.extn_item_type = 'CONFIGURABLE' " +
            		"UNION SELECT DISTINCT h.order_no, h.order_type, h.entry_type, h.order_date, l.extn_config_id, oh.hold_type, " +
            		"l.extn_is_mto, h.draft_order_flag, h.payment_status, yi.extn_item_type, h.createts " +
            		"FROM yantra_owner.yfs_order_header h JOIN yantra_owner.yfs_order_line l on h.order_header_key = l.order_header_key " +
            		"JOIN YANTRA_OWNER.yfs_order_release_status ors on l.order_line_key = ors.order_line_key " +
            		"JOIN yantra_owner.yfs_order_hold_type oh on h.order_header_key = oh.order_header_key " +
            		"JOIN yantra_owner.yfs_item yi on l.item_id = yi.item_id WHERE h.document_type = '0001' AND h.enterprise_key = 'RJ' " +
            		"AND ors.status_quantity > '0' AND ors.status = '1100' AND oh.HOLD_TYPE = 'ITEM_REALIZATION_HLD' AND oh.status in ('1100') " +
            		"AND yi.extn_item_type = 'CONFIGURABLE' UNION SELECT DISTINCT h.order_no, h.order_type, h.entry_type, h.order_date, " +
            		"l.extn_config_id, oh.hold_type, l.extn_is_mto, h.draft_order_flag, h.payment_status, yi.extn_item_type, h.createts " +
            		"FROM yantra_owner.yfs_order_header h JOIN yantra_owner.yfs_order_line l on h.order_header_key = l.order_header_key " +
            		"JOIN YANTRA_OWNER.yfs_order_release_status ors on l.order_line_key = ors.order_line_key " +
            		"JOIN yantra_owner.yfs_order_hold_type oh on h.order_header_key = oh.order_header_key " +
            		"JOIN yantra_owner.yfs_item yi on l.item_id = yi.item_id WHERE h.document_type = '0001' AND h.enterprise_key = 'RJ' " +
            		"AND ors.status_quantity > '0' AND oh.HOLD_TYPE = 'ITEM_REALIZATION_HLD' AND oh.status in ('1100') AND yi.extn_item_type = 'CONFIGURABLE' ";

            preparedStatement = connection.prepareStatement(query);
            System.out.println("Executing query: " + query);
            resultSet = preparedStatement.executeQuery();
            System.out.println("Query executed successfully....."); 
			Thread.sleep(30000);			
            String outputFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\OrdersOnIRHold.csv";
            int rowCount = writeResultSetToCSV(resultSet, outputFilePath);
            System.out.println("Total rows fetched: " + rowCount);
            System.out.println("Query results have been written to OrdersOnIRHold.csv.....");

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            triggerP2();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
                System.out.println("Database connection closed...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void triggerP2() {
        System.out.println("Raising P2");
        //ProcessBuilder pb = new ProcessBuilder("/app/sqlworkbench/scripts/CTS/CheckAndTriggerReportOnIRHold/triggerP2.sh");
        try {
            //Process p = pb.start();
            System.out.println("P2 triggered.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int writeResultSetToCSV(ResultSet resultSet, String filename) throws SQLException {
        int rowCount = 0;
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        	        	
            writer.println("ORDER_NO,ORDER_TYPE,ENTRY_TYPE,ORDER_DATE,CONFIG_ID,HOLD_TYPE,EXTN_IS_MTO,DRAFT_ORDER_FLAG,PAYMENT_STATUS,EXTN_ITEM_TYPE,CREATETS");
            while (resultSet.next()) {
                String orderNo = resultSet.getString("order_no");
                String orderType = resultSet.getString("order_type");
                String entryType = resultSet.getString("entry_type");
                String orderDate = resultSet.getString("order_date");
                String configId = resultSet.getString("extn_config_id");
                String holdType = resultSet.getString("hold_type");
                String isMTO = resultSet.getString("extn_is_mto");
                String draftOrderFlag = resultSet.getString("draft_order_flag");
                String paymentStatus = resultSet.getString("payment_status");
                String itemType = resultSet.getString("extn_item_type");
                String createts = resultSet.getString("createts");
                                
                writer.println(orderNo + "," + orderType + "," + entryType + "," + orderDate + "," + configId + "," + holdType + "," + isMTO + "," + draftOrderFlag + "," + paymentStatus + "," + itemType + "," + createts);
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowCount;
    }
}
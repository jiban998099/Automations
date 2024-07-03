package com.dbr;

import com.db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PendingInvoiceCount {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {
	
    	//Pending Invoice Hold For Vendor
    	String pendingInvoiceHoldVendor = "SELECT /*+PARALLEL (3)*/ l.fulfillment_type AS fulfilltype, h.order_no,hth.hold_type, h.enterprise_key AS brand, " +
    			"ys.description AS dsstatusflow, l.order_line_key AS linekey FROM yantra_owner.yfs_order_release_status s, " +
    			"yantra_owner.yfs_order_header h, yantra_owner.yfs_order_hold_type hth, yantra_owner.yfs_order_line l, " +
    			"yantra_owner.yfs_status ys WHERE s.order_release_status_key > to_char(sysdate-1, 'YYYYMMDD') " +
    			"AND h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') AND s.order_release_status_key < to_char(sysdate-1, 'YYYYMMDD') || '19' " +
    			"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key and hth.order_line_key = l.order_line_key " +
    			"AND l.item_group_code != 'DS' AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key " +
    			"AND ys.status = s.status AND l.shipnode_key LIKE '%VDR_%' AND s.status > '1000' and hth.status='1100' " +
    			"and h.hold_flag='Y' and hth.hold_type='PENDING_INVOICE_HOLD' " +
    			"and not exists (select 1 from yantra_owner.yfs_order_hold_type where hold_type<>'PENDING_INVOICE_HOLD' and status='1100' " +
    			"and order_header_key=h.order_header_key) AND s.status_quantity > '0' AND s.status < '3700.00.03' " +
    			"and l.fulfillment_type in ('FT_ONHAND_DS','FT_G_CA_DS') AND l.kit_code <> 'BUNDLE' " +
    			"AND l.item_id NOT LIKE 'DeliveryItem%' AND h.order_type NOT IN ('RARETAIL', 'REPLACEMENT', 'PART_REPLACEMENT') " +
    			"AND ys.process_type_key = 'ORDER_FULFILLMENT' ";

    	//Pending Invoice Hold For Sutter
    	String pendingInvoiceHoldSutter = "SELECT /*+PARALLEL (3)*/ l.fulfillment_type AS fulfilltype, h.order_no,hth.hold_type, h.enterprise_key AS brand, " +
    			"ys.description AS dsstatusflow, l.order_line_key AS linekey FROM yantra_owner.yfs_order_release_status s, " +
    			"yantra_owner.yfs_order_header h, yantra_owner.yfs_order_hold_type hth, yantra_owner.yfs_order_line l, " +
    			"yantra_owner.yfs_status ys WHERE s.order_release_status_key > to_char(sysdate-1, 'YYYYMMDD') " +
    			"AND h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') AND s.order_release_status_key < to_char(sysdate-1, 'YYYYMMDD') || '19' " +
    			"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key and hth.order_line_key = l.order_line_key " +
    			"AND l.item_group_code != 'DS' AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key " +
    			"AND ys.status = s.status AND l.shipnode_key = 'VDR_5579' AND s.status > '1000' and hth.status='1100' " +
    			"and h.hold_flag='Y' and hth.hold_type='PENDING_INVOICE_HOLD' " +
    			"and not exists (select 1 from yantra_owner.yfs_order_hold_type where hold_type<>'PENDING_INVOICE_HOLD' and status='1100' " +
    			"and order_header_key=h.order_header_key) AND s.status_quantity > '0' AND s.status < '3700.00.03' " +
    			"and l.fulfillment_type in ('FT_ONHAND_DS','FT_G_CA_DS') AND l.kit_code <> 'BUNDLE' " +
    			"AND l.item_id NOT LIKE 'DeliveryItem%' AND h.order_type NOT IN ('RARETAIL', 'REPLACEMENT', 'PART_REPLACEMENT') " +
    			"AND ys.process_type_key = 'ORDER_FULFILLMENT' ";
        

        // Obtain a database connection
        Connection connection = DBConnection.getReportingDBConnection();

        if (connection != null) {
            try {
                // Execute the queries
                executeQuery(connection, "Pending Invoice Hold For Vendor", pendingInvoiceHoldVendor);
                executeQuery(connection, "Pending Invoice Hold For Sutter", pendingInvoiceHoldSutter);

                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to obtain database connection.");
        }
    }

    private static void executeQuery(Connection connection, String heading, String sqlQuery) throws SQLException {
    	System.out.println("Executing query for: " + heading);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sqlQuery);

        // Get the row count from the ResultSet
        int rowCount = 0;
        while (resultSet.next()) {
            rowCount++;
        }
        
        System.out.println("Query execution for " + heading + " successful....");

        // Print the row count
        System.out.println("Row count for " + heading + ": " + rowCount);

        resultSet.close();
        statement.close();
    }
}

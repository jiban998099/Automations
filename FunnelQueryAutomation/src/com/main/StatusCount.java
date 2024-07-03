package com.main;
import com.db.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author Jiban
 */
public class StatusCount {

    public static void main(String[] args) {
    	
        //Drop For fulfillment Status - 3200.050 (DFF)
        String dffStatus = "SELECT /*+PARALLEL (16)*/ h.order_no, l.order_line_key as linekey, lc.chargeamount as chargeamount, " +
        		"h.extn_store_ord_source as source, oi.extn_inv_fin_date, oi.status as Invoicestatus, l.fulfillment_type " +
        		"FROM yantra_owner.yfs_order_release_status s, yantra_owner.yfs_order_header h " +
        		"left join yantra_owner.yfs_order_hold_type hth on hth.order_header_key = h.order_header_key and hth.status = '1100' " +
        		"and hth.order_hold_type_key>to_char(sysdate-1, 'YYYYMMDD') and hth.order_line_key is null, " +
        		"yantra_owner.yfs_order_line l left join yantra_owner.yfs_order_hold_type htl on htl.order_line_key = l.order_line_key " +
        		"and htl.status = '1100' and htl.order_hold_type_key > to_char(sysdate-1, 'YYYYMMDD') " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice_detail oid ON oid.order_line_key = l.order_line_key " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice oi ON oi.order_invoice_key = oid.order_invoice_key " +
        		"AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') AND oi.document_type = '0001' " +
        		"AND oi.order_invoice_key > to_char(sysdate-1, 'YYYYMMDD'), yantra_owner.yfs_status ys, yantra_owner.yfs_line_charges lc " +
        		"WHERE h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') and h.order_header_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND lc.line_key = l.order_line_key AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key AND l.item_group_code != 'DS' " +
        		"AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key AND ys.status = s.status " +
        		"And s.status = '3200.050' AND s.status > '1000' AND l.line_type = 'MERCH' AND s.status_quantity > '0' AND h.draft_order_flag <> 'Y' " +
        		"AND l.item_id NOT LIKE 'DeliveryItem%' AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY') " +
        		"AND ys.process_type_key = 'ORDER_FULFILLMENT' ";

        //Acknowledged Status - 3200.100 (ACK)
        String ackStatus = "SELECT /*+PARALLEL (16)*/ h.order_no, l.order_line_key as linekey, lc.chargeamount as chargeamount, " +
        		"h.extn_store_ord_source as source, oi.extn_inv_fin_date, oi.status as Invoicestatus, l.fulfillment_type " +
        		"FROM yantra_owner.yfs_order_release_status s, yantra_owner.yfs_order_header h " +
        		"left join yantra_owner.yfs_order_hold_type hth on hth.order_header_key = h.order_header_key and hth.status = '1100' " +
        		"and hth.order_hold_type_key>to_char(sysdate-1, 'YYYYMMDD') and hth.order_line_key is null, " +
        		"yantra_owner.yfs_order_line l left join yantra_owner.yfs_order_hold_type htl on htl.order_line_key = l.order_line_key " +
        		"and htl.status = '1100' and htl.order_hold_type_key > to_char(sysdate-1, 'YYYYMMDD') " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice_detail oid ON oid.order_line_key = l.order_line_key " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice oi ON oi.order_invoice_key = oid.order_invoice_key " +
        		"AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') AND oi.document_type = '0001' " +
        		"AND oi.order_invoice_key > to_char(sysdate-1, 'YYYYMMDD'), yantra_owner.yfs_status ys, yantra_owner.yfs_line_charges lc " +
        		"WHERE h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') and h.order_header_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND lc.line_key = l.order_line_key AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key AND l.item_group_code != 'DS' " +
        		"AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key AND ys.status = s.status " +
        		"And s.status = '3200.100' AND s.status > '1000' AND l.line_type = 'MERCH' AND s.status_quantity > '0' AND h.draft_order_flag <> 'Y' " +
        		"AND l.item_id NOT LIKE 'DeliveryItem%' AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY') " +
        		"AND ys.process_type_key = 'ORDER_FULFILLMENT' ";

        //Pending Reallocation Status - 1310 (PNDRLC)
        String pndrlcStatus = "SELECT /*+PARALLEL (16)*/ h.order_no, l.order_line_key as linekey, lc.chargeamount as chargeamount, " +
        		"h.extn_store_ord_source as source, oi.extn_inv_fin_date, oi.status as Invoicestatus, l.fulfillment_type " +
        		"FROM yantra_owner.yfs_order_release_status s, yantra_owner.yfs_order_header h " +
        		"left join yantra_owner.yfs_order_hold_type hth on hth.order_header_key = h.order_header_key and hth.status = '1100' " +
        		"and hth.order_hold_type_key>to_char(sysdate-1, 'YYYYMMDD') and hth.order_line_key is null, " +
        		"yantra_owner.yfs_order_line l left join yantra_owner.yfs_order_hold_type htl on htl.order_line_key = l.order_line_key " +
        		"and htl.status = '1100' and htl.order_hold_type_key > to_char(sysdate-1, 'YYYYMMDD') " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice_detail oid ON oid.order_line_key = l.order_line_key " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice oi ON oi.order_invoice_key = oid.order_invoice_key " +
        		"AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') AND oi.document_type = '0001' " +
        		"AND oi.order_invoice_key > to_char(sysdate-1, 'YYYYMMDD'), yantra_owner.yfs_status ys, yantra_owner.yfs_line_charges lc " +
        		"WHERE h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') and h.order_header_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND lc.line_key = l.order_line_key AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key AND l.item_group_code != 'DS' " +
        		"AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key AND ys.status = s.status " +
        		"And s.status = '1310' AND s.status > '1000' AND l.line_type = 'MERCH' AND s.status_quantity > '0' AND h.draft_order_flag <> 'Y' " +
        		"AND l.item_id NOT LIKE 'DeliveryItem%' AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY') " +
        		"AND ys.process_type_key = 'ORDER_FULFILLMENT' ";
        
        // Obtain a database connection
        Connection connection = DBConnection.getRepDBConnection();

        if (connection != null) {
            try {
                executeQuery(connection, "Drop For Fulfillment", dffStatus);
                executeQuery(connection, "Acknowledged", ackStatus);
                executeQuery(connection, "Pending Reallocation", pndrlcStatus);

                connection.close();

                System.out.println("Row counts fetched successfully.");
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

        int rowCount = 0;

        while (resultSet.next()) {
            rowCount++;
        }

        resultSet.close();
        statement.close();

        System.out.println("Query execution for " + heading + " successful....");
        System.out.println("Total count for " + heading + " status: " + rowCount);
    }
}

/**
 * 
 */
package com.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.db.DBConnection;

/**
 * @author Jiban
 */
public class FunnelQueries {

    public static void main(String[] args) {
    	
    	//DailyOrderFunnel
        String dailyOrderFunnel = "SELECT * FROM (SELECT /*+PARALLEL (16)*/ l.order_line_key as linekey , lc.chargeamount as chargeamount, " +
        		"h.extn_store_ord_source as source, oi.extn_inv_fin_date,oi.status as Invoicestatus,l.fulfillment_type, CASE " +
        		"WHEN ((ys.description = 'Invoiced' OR ys.description = 'Awaiting DS PO Creation' OR ys.description = 'Customer Picked Up' " +
        		"OR ys.description = 'Order Delivered' OR ys.description = 'Invoiced PreShip' OR ys.description = 'Ready To Schedule - Shipped' " +
        		"OR ys.description = 'Ready To Schedule- Not Shipped' OR ys.description = 'Return Created' OR ys.description = 'Return Created For Delivery Exception') " +
        		"AND oi.status = '01') THEN 'INVOICED' WHEN (oi.status = '00') THEN 'INVOICED NOT PUBLISHED' " +
        		"WHEN ( (ys.description = 'Shipped' AND l.fulfillment_type = 'FT_SAC' AND oi.status = '01') OR (ys.description = 'Acknowledged' " +
        		"AND l.fulfillment_type = 'FT_SAC' AND oi.status = '01') OR (ys.description = 'Acknowledged' " +
        		"AND l.fulfillment_type = 'FT_ONHAND_DS' AND oi.status = '01') ) THEN 'INVOICED' ELSE 'NOT INVOICED' " +
        		"END AS BUSINESSSTATUS,ys.description ,hth.hold_type as HeadderHold,htl.hold_type as LineHold,NVL(hth.hold_type,0) " +
        		"as headerhldtype, NVL(htl.hold_type, 0) as linehldtype FROM yantra_owner.yfs_order_release_status s, " +
        		"yantra_owner.yfs_order_header h left join yantra_owner.yfs_order_hold_type hth on hth.order_header_key = h.order_header_key " +
        		"and hth.status = '1100' and hth.order_hold_type_key > to_char(sysdate-1, 'YYYYMMDD') and hth.order_line_key is null, " +
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
        		"AND s.status <> '1400' AND s.status > '1000' AND l.line_type = 'MERCH' AND s.status_quantity > '0' " +
        		"AND h.draft_order_flag <> 'Y' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY') AND ys.process_type_key = 'ORDER_FULFILLMENT') " +
        		"PIVOT (count(headerhldtype) as headerholdtype,count(linehldtype) as lineholdtype,count(linekey) as linekeycount, " +
        		"SUM (chargeamount) as Sumchargeamount FOR source IN (' ', 'RTC', 'ESENDS')) ";
        
        //DailyDSOrder
        String dailyDSOrder = "SELECT * FROM (SELECT /*+PARALLEL (16)*/ h.enterprise_key, h.payment_status, l.order_line_key as linekey , " +
        		"lc.chargeamount as chargeamount, h.extn_store_ord_source as source, oi.extn_inv_fin_date,oi.status as Invoicestatus, " +
        		"l.fulfillment_type as fulfilltype, CASE WHEN ((ys.description = 'Invoiced' OR ys.description = 'Awaiting DS PO Creation' " +
        		"OR ys.description = 'Customer Picked Up' OR ys.description = 'Order Delivered' OR ys.description = 'Invoiced PreShip' " +
        		"OR ys.description = 'Ready To Schedule - Shipped' OR ys.description = 'Ready To Schedule- Not Shipped' " +
        		"OR ys.description = 'Return Created' OR ys.description = 'Return Created For Delivery Exception') AND oi.status = '01') " +
        		"THEN 'INVOICED' WHEN (oi.status = '00') THEN 'INVOICED NOT PUBLISHED' WHEN ((ys.description = 'Shipped' " +
        		"AND l.fulfillment_type = 'FT_SAC' AND oi.status = '01') OR (ys.description = 'Acknowledged' AND l.fulfillment_type = 'FT_SAC' " +
        		"AND oi.status = '01') OR (ys.description = 'Acknowledged' AND l.fulfillment_type = 'FT_ONHAND_DS' AND oi.status = '01')) " +
        		"THEN 'INVOICED' ELSE 'NOT INVOICED' END AS BUSINESSSTATUS, CASE WHEN (l.shipnode_key = 'VDR_5579') THEN 'SUTTER' " +
        		"WHEN (l.shipnode_key <> 'VDR_5579' and x.extn_vendor_integration_code = 'SPS') THEN 'SPS' WHEN (sn.node_type = 'DSINT' " +
        		"and x.extn_vendor_integration_code = 'SPS') THEN 'IDS on SPS' WHEN (sn.node_type = 'DSINT' and x.extn_vendor_integration_code is NULL) " +
        		"THEN 'IDS on DSUI' WHEN (l.shipnode_key <> 'VDR_5579' and l.shipnode_key like 'VDR_%' and x.extn_vendor_integration_code is NULL) " +
        		"THEN 'DSUI' ELSE 'DC SHIPMENT' END AS VENDORTYPE, ys.description, hth.hold_type as HeadderHold, htl.hold_type as LineHold, " +
        		"NVL(hth.hold_type, 0) as headerhldtype, NVL(htl.hold_type, 0) as linehldtype FROM yantra_owner.yfs_order_release_status s, " +
        		"yantra_owner.yfs_order_header h left join yantra_owner.yfs_order_hold_type hth on hth.order_header_key = h.order_header_key " +
        		"and hth.status = '1100' and hth.order_hold_type_key > to_char(sysdate-1, 'YYYYMMDD') and hth.order_line_key is null, " +
        		"yantra_owner.yfs_order_line l left join yantra_owner.yfs_order_hold_type htl on htl.order_line_key = l.order_line_key " +
        		"and htl.status = '1100' and htl.order_hold_type_key>to_char(sysdate-1, 'YYYYMMDD') " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice_detail oid ON oid.order_line_key = l.order_line_key " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice oi ON oi.order_invoice_key = oid.order_invoice_key " +
        		"AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') AND oi.document_type = '0001' " +
        		"AND oi.order_invoice_key > to_char(sysdate-1, 'YYYYMMDD'), yantra_owner.yfs_status ys, yantra_owner.yfs_line_charges lc, " +
        		"yantra_owner.yfs_organization x, yantra_owner.yfs_ship_node sn WHERE sn.shipnode_key = l.shipnode_key " +
        		"and x.organization_key = l.shipnode_key and h.order_header_key > to_char(sysdate-1, 'YYYYMMDD') " +
        		"and h.order_header_key < to_char(sysdate, 'YYYYMMDD') AND lc.line_key = l.order_line_key " +
        		"AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') and l.fulfillment_type in ('FT_G_CA_DS','FT_ONHAND_DS') " +
        		"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key AND l.item_group_code != 'DS' " +
        		"AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key AND ys.status = s.status " +
        		"AND s.status > '1000' AND l.line_type = 'MERCH' AND s.status_quantity > '0' AND h.draft_order_flag <> 'Y' " +
        		"AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY','PHONEORDER_G_CA','OTHERS_G_CA','REPLACEMENT_G_CA') " +
        		"AND ys.process_type_key = 'ORDER_FULFILLMENT') " +
        		"PIVOT (count(headerhldtype) as headerholdtype, count(linehldtype) as lineholdtype, count(linekey) as linekeycount, " +
        		"SUM (chargeamount) as Sumchargeamount FOR fulfilltype IN ('FT_G_CA_DS', 'FT_ONHAND_DS')) ";
        
        //DailyDSShipment
        String dailyDSShipment = "SELECT * FROM (SELECT /*+PARALLEL (16)*/ l.order_line_key AS linekey, lc.chargeamount AS chargeamount, " +
        		"h.extn_store_ord_source AS source, h.order_no, h.payment_status, s.status, oi.extn_inv_fin_date, " +
        		"oi.status as Invoicestatus, l.fulfillment_type, CASE WHEN (l.shipnode_key = 'VDR_5579') " +
        		"THEN 'SUTTER' WHEN (l.shipnode_key <> 'VDR_5579' AND sn.node_type = 'DS' AND x.extn_vendor_integration_code = 'SPS') " +
        		"THEN 'SPS' WHEN (sn.node_type='DSINT' and x.extn_vendor_integration_code = 'SPS') " +
        		"THEN 'IDS on SPS' WHEN (sn.node_type = 'DSINT' and x.extn_vendor_integration_code is NULL) " +
        		"THEN 'IDS on DSUI' WHEN (l.shipnode_key <> 'VDR_5579' AND x.extn_vendor_integration_code IS NULL) " +
        		"THEN 'DSUI' ELSE 'UNDEFINED' END AS vendortype, CASE WHEN ((ys.description = 'Invoiced' " +
        		"OR ys.description = 'Awaiting DS PO Creation' OR ys.description = 'Customer Picked Up' " +
        		"OR ys.description = 'Order Delivered' OR ys.description = 'Invoiced PreShip' " +
        		"OR ys.description = 'Ready To Schedule - Shipped' OR ys.description = 'Ready To Schedule- Not Shipped' " +
        		"OR ys.description = 'Return Created' OR ys.description = 'Return Created For Delivery Exception' " +
        		"OR ys.description = 'Return Received' OR (ys.description = 'Shipped' AND l.shipnode_key like '%DTC%')) " +
        		"AND oi.status = '01') THEN 'INVOICED' WHEN (oi.status = '00') THEN 'INVOICED NOT PUBLISHED' " +
        		"WHEN ((ys.description = 'Shipped' AND l.fulfillment_type = 'FT_SAC' AND oi.status = '01') " +
        		"OR (ys.description = 'Acknowledged' AND l.fulfillment_type = 'FT_SAC' AND oi.status = '01') " +
        		"OR (ys.description = 'Acknowledged' AND l.fulfillment_type = 'FT_ONHAND_DS' AND oi.status = '01')) " +
        		"THEN 'INVOICED' ELSE 'NOT INVOICED' END AS businessstatus, ys.description FROM yantra_owner.yfs_organization x, " +
        		"yantra_owner.yfs_order_release_status s, yantra_owner.yfs_order_header h, yantra_owner.yfs_ship_node sn, " +
        		"yantra_owner.yfs_order_line l LEFT JOIN yantra_owner.yfs_order_invoice_detail oid ON oid.order_line_key = l.order_line_key " +
        		"LEFT JOIN yantra_owner.yfs_order_invoice oi ON oi.order_invoice_key = oid.order_invoice_key " +
        		"AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') AND oi.document_type = '0001' " +
        		"and oi.amount_collected >= '0' AND oi.order_invoice_key > to_char(sysdate-365, 'YYYYMMDD'), " +
        		"yantra_owner.yfs_status ys, yantra_owner.yfs_line_charges lc WHERE sn.shipnode_key = l.shipnode_key " +
        		"and to_char(oi.modifyts, 'YYYYMMDD') >= to_char(sysdate-1, 'YYYYMMDD') AND l.shipnode_key = x.organization_key " +
        		"AND h.order_header_key > to_char(sysdate-365, 'YYYYMMDD') AND h.order_header_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND l.fulfillment_type IN ('FT_G_CA_DS', 'FT_ONHAND_DS') AND lc.line_key = l.order_line_key " +
        		"AND s.order_release_status_key > to_char(sysdate-1, 'YYYYMMDD') AND s.order_release_status_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') AND s.status > '3699' " +
        		"AND h.document_type = '0001' AND l.order_header_key = h.order_header_key AND l.item_group_code != 'DS' " +
        		"AND s.order_line_key = l.order_line_key AND s.order_header_key = h.order_header_key AND ys.status = s.status " +
        		"and s.status_quantity > '0' AND l.line_type = 'MERCH' AND h.draft_order_flag <> 'Y' AND l.item_id NOT LIKE 'DeliveryItem%' " +
        		"AND h.order_type IN ('PAPERORDER', 'PHONEORDER', 'OTHERS', 'REGISTRY') AND ys.process_type_key = 'ORDER_FULFILLMENT') " +
        		"PIVOT (COUNT(linekey) AS linekeycount, SUM(chargeamount) AS sumchargeamount FOR source IN (' ', 'RTC', 'ESENDS')) ";

        // Obtain a database connection
        Connection connection = DBConnection.getRepDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("DailyOrderFunnel");
                executeQuery(connection, "DailyOrderFunnel", dailyOrderFunnel, sheet1);
                
                Thread.sleep(30000); //Delay for 0.5 minute (30000 milliseconds)
                
                Sheet sheet2 = workbook.createSheet("DailyDSOrder");
                executeQuery(connection, "DailyDSOrder", dailyDSOrder, sheet2);
                
                Thread.sleep(30000); //Delay for 0.5 minute (30000 milliseconds)
                
                Sheet sheet3 = workbook.createSheet("DailyDSShipment");
                executeQuery(connection, "DailyDSShipment", dailyDSShipment, sheet3);
                
                // Delay for 1 minute (60000 milliseconds)
                Thread.sleep(60000);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\FunnelViewReport\\FunnelQueryOutput.xlsx")) {
                    workbook.write(outputStream);
                }

                connection.close();
                workbook.close();

                System.out.println("Query results have been written to FunnelQueryOutput.xlsx");
            } catch (SQLException | IOException | InterruptedException e) {
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

        int rowNum = 0; // Start from the first row

        // Create heading row
        Row headingRow = sheet.createRow(rowNum++);
        for (int i = 1; i <= columnCount; i++) {
            Cell cell = headingRow.createCell(i - 1);
            cell.setCellValue(metaData.getColumnName(i));
        }

        // Populate data rows
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

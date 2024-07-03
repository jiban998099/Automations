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

public class TotalColDSCol {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {
    	
    	//TotalCollection
        String totalCollection = "SELECT * FROM (SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status " +
        		"WHEN '00' THEN 'PENDING COLLECTION' WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, " +
        		"lc.chargeamount chargeamount, SUM(lc.chargeamount) OVER(PARTITION BY oi.extn_inv_fin_date) AS total " +
        		"FROM yantra_owner.yfs_order_invoice oi JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate - 8, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate - 7, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate - 8, 'MM/DD/YYYY') " +
        		"AND oh.document_type = '0001') PIVOT (SUM (chargeamount) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') ) " +
        		"UNION SELECT * FROM (SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status WHEN '00' THEN 'PENDING COLLECTION' " +
        		"WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, lc.chargeamount chargeamount, SUM(lc.chargeamount) " +
        		"OVER(PARTITION BY oi.extn_inv_fin_date) AS total FROM yantra_owner.yfs_order_invoice oi " +
        		"JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line  ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate-1, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate-1, 'MM/DD/YYYY') AND oh.document_type = '0001' ) " +
        		"PIVOT (SUM (chargeamount) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR')) " +
        		"UNION SELECT * FROM ( SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status WHEN '00' THEN 'PENDING COLLECTION' " +
        		"WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, lc.chargeamount chargeamount, " +
        		"SUM(lc.chargeamount) OVER(PARTITION BY oi.extn_inv_fin_date) AS total FROM yantra_owner.yfs_order_invoice oi " +
        		"JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate - 15, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate - 14, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate - 15, 'MM/DD/YYYY') " +
        		"AND oh.document_type = '0001' ) PIVOT (SUM (chargeamount) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') ) " +
        		"UNION SELECT * FROM ( SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status WHEN '00' THEN 'PENDING COLLECTION' " +
        		"WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, lc.chargeamount chargeamount, " +
        		"SUM(lc.chargeamount) OVER(PARTITION BY oi.extn_inv_fin_date) AS total FROM yantra_owner.yfs_order_invoice oi " +
        		"JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate - 22, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate - 21, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate - 22, 'MM/DD/YYYY') " +
        		"AND oh.document_type = '0001' ) PIVOT ( SUM (chargeamount) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR' ) ) " +
        		"UNION SELECT * FROM ( SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status WHEN '00' THEN 'PENDING COLLECTION' " +
        		"WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, lc.chargeamount chargeamount, " +
        		"SUM(lc.chargeamount)OVER(PARTITION BY oi.extn_inv_fin_date) AS total FROM yantra_owner.yfs_order_invoice oi " +
        		"JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate - 29, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate - 28, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate - 29, 'MM/DD/YYYY') " +
        		"AND oh.document_type = '0001') PIVOT (SUM (chargeamount) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') ) " +
        		"UNION SELECT * FROM ( SELECT oi.enterprise_code AS brand, oi.extn_inv_fin_date, CASE oi.status WHEN '00' THEN 'PENDING COLLECTION' " +
        		"WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS all_invoiced, lc.chargeamount chargeamount, " +
        		"SUM(lc.chargeamount) OVER(PARTITION BY oi.extn_inv_fin_date) AS total FROM yantra_owner.yfs_order_invoice oi " +
        		"JOIN yantra_owner.yfs_order_invoice_detail oid ON oi.order_invoice_key = oid.order_invoice_key " +
        		"JOIN yantra_owner.yfs_order_line ol ON oid.order_line_key = ol.order_line_key " +
        		"JOIN yantra_owner.yfs_line_charges lc ON oid.order_invoice_detail_key = lc.line_key " +
        		"JOIN yantra_owner.yfs_order_header oh ON oi.order_header_key = oh.order_header_key " +
        		"WHERE oi.order_invoice_key > to_char(sysdate - 36, 'YYYYMMDD') AND oi.order_invoice_key < to_char(sysdate - 35, 'YYYYMMDD') " +
        		"AND ol.line_type = 'MERCH' AND oi.invoice_type IN ('ORDER', 'SHIPMENT') AND lc.charge_name IN ('LineMerchEffective', 'LineMonoPZEffective') " +
        		"AND oh.order_type IN ('PHONEORDER', 'PAPERORDER', 'OTHERS', 'REGISTRY') AND oi.enterprise_code IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR') " +
        		"AND oi.document_type = '0001' AND to_char(oi.extn_inv_fin_date, 'MM/DD/YYYY') = to_char(sysdate - 36, 'MM/DD/YYYY') " +
        		"AND oh.document_type = '0001') PIVOT (SUM ( chargeamount ) FOR brand IN ('PB', 'WE', 'WS', 'PK', 'PT', 'MG', 'RJ', 'GR' )) ";
        
        //DSCollection
        String dSCollection = "select * from (SELECT /*+ full(oi) parallel(oi,3)*/ OI.ENTERPRISE_CODE as Brand,OI.EXTN_INV_FIN_DATE, LC.CHARGEAMOUNT as ChargeAmount, " +
        		"CASE OI.STATUS WHEN '00' THEN 'PENDING COLLECTION' WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS DS_INVOICED, " +
        		"SUM( LC.CHARGEAMOUNT ) OVER ( PARTITION BY OI.EXTN_INV_FIN_DATE) AS Total FROM YANTRA_OWNER.YFS_ORDER_INVOICE OI " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_INVOICE_DETAIL OID ON OI.ORDER_INVOICE_KEY = OID.ORDER_INVOICE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_LINE OL ON OID.ORDER_LINE_KEY = OL.ORDER_LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_LINE_CHARGES LC ON OID.ORDER_INVOICE_DETAIL_KEY = LC.LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_HEADER OH ON OI.ORDER_HEADER_KEY = OH.ORDER_HEADER_KEY, YANTRA_OWNER.YFS_organization S " +
        		"WHERE OI.ENTERPRISE_CODE in ('PB','WE','WS','PK','PT','MG','RJ','GR') AND OI.ORDER_INVOICE_KEY >TO_CHAR(SYSDATE - 365, 'YYYYMMDD') " +
        		"AND OI.EXTN_INVOICE_TYPE = 'DS' and ol.shipnode_key=S.organization_key AND OL.LINE_TYPE = 'MERCH' AND OI.INVOICE_TYPE IN ('ORDER','SHIPMENT') " +
        		"AND LC.CHARGE_NAME IN ('LineMerchEffective','LineMonoPZEffective') AND OH.ORDER_TYPE IN ('PHONEORDER','PAPERORDER','OTHERS','REGISTRY') " +
        		"AND OI.STATUS = '01' AND to_char(OI.EXTN_INV_FIN_DATE, 'YYYY-MM-DD') = to_char(sysdate-8, 'YYYY-MM-DD') AND OI.DOCUMENT_TYPE = '0001' " +
        		"AND OH.DOCUMENT_TYPE = '0001' ) pivot(sum(ChargeAmount) for Brand in ('PB','WE','WS','PK','PT','MG','RJ','GR') ) " +
        		"union select * from ( SELECT /*+ full(oi) parallel(oi,3)*/ OI.ENTERPRISE_CODE as Brand,OI.EXTN_INV_FIN_DATE, LC.CHARGEAMOUNT as ChargeAmount, " +
        		"CASE OI.STATUS WHEN '00' THEN 'PENDING COLLECTION' WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS DS_INVOICED, " +
        		"SUM( LC.CHARGEAMOUNT ) OVER ( PARTITION BY OI.EXTN_INV_FIN_DATE) AS Total FROM YANTRA_OWNER.YFS_ORDER_INVOICE OI " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_INVOICE_DETAIL OID ON OI.ORDER_INVOICE_KEY = OID.ORDER_INVOICE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_LINE OL ON OID.ORDER_LINE_KEY = OL.ORDER_LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_LINE_CHARGES LC ON OID.ORDER_INVOICE_DETAIL_KEY = LC.LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_HEADER OH ON OI.ORDER_HEADER_KEY = OH.ORDER_HEADER_KEY, YANTRA_OWNER.YFS_organization S " +
        		"WHERE OI.ENTERPRISE_CODE in ('PB','WE','WS','PK','PT','MG','RJ','GR') AND OI.ORDER_INVOICE_KEY >TO_CHAR(SYSDATE - 365, 'YYYYMMDD') " +
        		"AND OI.EXTN_INVOICE_TYPE = 'DS' and ol.shipnode_key=S.organization_key AND OL.LINE_TYPE = 'MERCH' AND OI.INVOICE_TYPE IN ('ORDER','SHIPMENT') " +
        		"AND LC.CHARGE_NAME IN ('LineMerchEffective','LineMonoPZEffective') AND OH.ORDER_TYPE IN ('PHONEORDER','PAPERORDER','OTHERS','REGISTRY') " +
        		"AND OI.STATUS = '01' AND to_char(OI.EXTN_INV_FIN_DATE, 'YYYY-MM-DD') = to_char(sysdate-1, 'YYYY-MM-DD') AND OI.DOCUMENT_TYPE = '0001' " +
        		"AND OH.DOCUMENT_TYPE = '0001') pivot ( sum(ChargeAmount) for Brand in ('PB','WE','WS','PK','PT','MG','RJ','GR') ) " +
        		"union select * from ( SELECT /*+ full(oi) parallel(oi,3)*/ OI.ENTERPRISE_CODE as Brand,OI.EXTN_INV_FIN_DATE, LC.CHARGEAMOUNT as ChargeAmount, " +
        		"CASE OI.STATUS WHEN '00' THEN 'PENDING COLLECTION' WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS DS_INVOICED, " +
        		"SUM( LC.CHARGEAMOUNT ) OVER ( PARTITION BY OI.EXTN_INV_FIN_DATE) AS Total FROM YANTRA_OWNER.YFS_ORDER_INVOICE OI " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_INVOICE_DETAIL OID ON OI.ORDER_INVOICE_KEY = OID.ORDER_INVOICE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_LINE OL ON OID.ORDER_LINE_KEY = OL.ORDER_LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_LINE_CHARGES LC ON OID.ORDER_INVOICE_DETAIL_KEY = LC.LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_HEADER OH ON OI.ORDER_HEADER_KEY = OH.ORDER_HEADER_KEY, YANTRA_OWNER.YFS_organization S " +
        		"WHERE OI.ENTERPRISE_CODE in ('PB','WE','WS','PK','PT','MG','RJ','GR') AND OI.ORDER_INVOICE_KEY >TO_CHAR(SYSDATE - 365, 'YYYYMMDD') " +
        		"AND OI.EXTN_INVOICE_TYPE = 'DS' and ol.shipnode_key=S.organization_key AND OL.LINE_TYPE = 'MERCH' AND OI.INVOICE_TYPE IN ('ORDER','SHIPMENT') " +
        		"AND LC.CHARGE_NAME IN ('LineMerchEffective','LineMonoPZEffective') AND OH.ORDER_TYPE IN ('PHONEORDER','PAPERORDER','OTHERS','REGISTRY') " +
        		"AND OI.STATUS = '01' AND to_char(OI.EXTN_INV_FIN_DATE, 'YYYY-MM-DD') = to_char(sysdate-15, 'YYYY-MM-DD') " +
        		"AND OI.DOCUMENT_TYPE = '0001' AND OH.DOCUMENT_TYPE = '0001') pivot(sum(ChargeAmount) for Brand in ('PB','WE','WS','PK','PT','MG','RJ','GR') ) " +
        		"union select * from ( SELECT /*+ full(oi) parallel(oi,3)*/ OI.ENTERPRISE_CODE as Brand,OI.EXTN_INV_FIN_DATE, LC.CHARGEAMOUNT as ChargeAmount, " +
        		"CASE OI.STATUS WHEN '00' THEN 'PENDING COLLECTION' WHEN '01' THEN 'COLLECTED' ELSE 'UNDEFINED' END AS DS_INVOICED, " +
        		"SUM( LC.CHARGEAMOUNT ) OVER ( PARTITION BY OI.EXTN_INV_FIN_DATE) AS Total FROM YANTRA_OWNER.YFS_ORDER_INVOICE OI " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_INVOICE_DETAIL OID ON OI.ORDER_INVOICE_KEY = OID.ORDER_INVOICE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_LINE OL ON OID.ORDER_LINE_KEY = OL.ORDER_LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_LINE_CHARGES LC ON OID.ORDER_INVOICE_DETAIL_KEY = LC.LINE_KEY " +
        		"JOIN YANTRA_OWNER.YFS_ORDER_HEADER OH ON OI.ORDER_HEADER_KEY = OH.ORDER_HEADER_KEY, YANTRA_OWNER.YFS_organization S " +
        		"WHERE OI.ENTERPRISE_CODE in ('PB','WE','WS','PK','PT','MG','RJ','GR') AND OI.ORDER_INVOICE_KEY >TO_CHAR(SYSDATE - 365, 'YYYYMMDD') " +
        		"AND OI.EXTN_INVOICE_TYPE = 'DS' and ol.shipnode_key=S.organization_key AND OL.LINE_TYPE = 'MERCH' AND OI.INVOICE_TYPE IN ('ORDER','SHIPMENT') " +
        		"AND LC.CHARGE_NAME IN ('LineMerchEffective','LineMonoPZEffective') AND OH.ORDER_TYPE IN ('PHONEORDER','PAPERORDER','OTHERS','REGISTRY') " +
        		"AND OI.STATUS = '01' AND to_char(OI.EXTN_INV_FIN_DATE, 'YYYY-MM-DD') = to_char(sysdate-22, 'YYYY-MM-DD') AND OI.DOCUMENT_TYPE = '0001' " +
        		"AND OH.DOCUMENT_TYPE = '0001') pivot( sum(ChargeAmount) for Brand in ('PB','WE','WS','PK','PT','MG','RJ','GR')) ";

        // Obtain a database connection
        Connection connection = DBConnection.getReportingDBConnection();

        if (connection != null) {
            try {
                // Create a new Excel workbook
                Workbook workbook = new XSSFWorkbook();
                
                Sheet sheet1 = workbook.createSheet("TotalCollection");
                executeQuery(connection, "TotalCollection", totalCollection, sheet1);
        		
        		Sheet sheet2 = workbook.createSheet("DSCollection");
                executeQuery(connection, "DSCollection", dSCollection, sheet2);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DBRQueryOutput\\TotalColDSCol.xlsx")) {
                    workbook.write(outputStream);
                    workbook.close();
                }

                connection.close();

                System.out.println("Query results have been written to TotalColDSCol.xlsx");
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


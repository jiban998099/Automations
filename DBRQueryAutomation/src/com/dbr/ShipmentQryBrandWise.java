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

public class ShipmentQryBrandWise {
	
	/**
	 * @author Jiban
	 */

    public static void main(String[] args) {
    	
    	//Previous Week UPSN/TRK Orders for PB
    	String previousWeekUPSNTRKOrdersPB = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PB' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Previous Week UPSN/TRK Orders for PB
    	String sumOfPreviousWeekUPSNTRKOrdersPB = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PB' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for PB
    	String reportingDayUPSNTRKOrdersPB = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PB' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for PB
    	String sumOfReportingDayUPSNTRKOrdersPB = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PB' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for WS
    	String previousWeekUPSNTRKOrdersWS = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WS' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Previous Week UPSN/TRK Orders for WS
    	String sumOfPreviousWeekUPSNTRKOrdersWS = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WS' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for WS
    	String reportingDayUPSNTRKOrdersWS = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WS' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Reporting Day UPSN/TRK Orders for WS
    	String sumOfReportingDayUPSNTRKOrdersWS = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WS' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for WE
    	String previousWeekUPSNTRKOrdersWE = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WE' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Previous Week UPSN/TRK Orders for WE
    	String sumOfPreviousWeekUPSNTRKOrdersWE = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WE' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for WE
    	String reportingDayUPSNTRKOrdersWE = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WE' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for WE
    	String sumOfReportingDayUPSNTRKOrdersWE = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'WE' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for PK
    	String previousWeekUPSNTRKOrdersPK = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PK' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Previous Week UPSN/TRK Orders for PK
    	String sumOfPreviousWeekUPSNTRKOrdersPK = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PK' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for PK
    	String reportingDayUPSNTRKOrdersPK = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PK' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for PK
    	String sumOfReportingDayUPSNTRKOrdersPK = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PK' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for MG
    	String previousWeekUPSNTRKOrdersMG = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'MG' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Previous Week UPSN/TRK Orders for MG
    	String sumOfPreviousWeekUPSNTRKOrdersMG = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'MG' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for MG
    	String reportingDayUPSNTRKOrdersMG = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'MG' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Reporting Day UPSN/TRK Orders for MG
    	String sumOfReportingDayUPSNTRKOrdersMG = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'MG' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for PT
    	String previousWeekUPSNTRKOrdersPT = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PT' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";
    			
    	//Sum of Previous Week UPSN/TRK Orders for PT
    	String sumOfPreviousWeekUPSNTRKOrdersPT = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PT' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for PT
    	String reportingDayUPSNTRKOrdersPT = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PT' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for PT
    	String sumOfReportingDayUPSNTRKOrdersPT = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'PT' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for RJ
    	String previousWeekUPSNTRKOrdersRJ = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'RJ' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Previous Week UPSN/TRK Orders for RJ
    	String sumOfPreviousWeekUPSNTRKOrdersRJ = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'RJ' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for RJ
    	String reportingDayUPSNTRKOrdersRJ = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'RJ' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for RJ
    	String sumOfReportingDayUPSNTRKOrdersRJ = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'RJ' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Previous Week UPSN/TRK Orders for GR
    	String previousWeekUPSNTRKOrdersGR = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'GR' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Previous Week UPSN/TRK Orders for GR
    	String sumOfPreviousWeekUPSNTRKOrdersGR = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-8,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-8,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'GR' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN' AS UPSN, 'TRK' AS TRK) ) ) ";

    	//Reporting Day UPS/TRK Orders for GR
    	String reportingDayUPSNTRKOrdersGR = "select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'GR' AND s.status > '1000' " +
    			"and s.status in ('3700.00.02') AND l.KIT_CODE <> 'BUNDLE' AND l.item_id NOT LIKE 'DeliveryItem%' " +
    			"AND h.order_type NOT IN ('RARETAIL','REPLACEMENT','PART_REPLACEMENT') " +
    			"AND YS.PROCESS_TYPE_KEY='ORDER_FULFILLMENT') PIVOT (count(linekey) FOR SCAC IN ('UPSN','TRK') ) ";

    	//Sum of Reporting Day UPSN/TRK Orders for GR
    	String sumOfReportingDayUPSNTRKOrdersGR = "SELECT SUM(UPSN) AS UPSN_Total, SUM(TRK) AS TRK_Total FROM ( " +
    			"select * from ( SELECT /*+PARALLEL (3)*/ l.SCAC as SCAC,l.shipnode_key as vendor,l.order_line_key as linekey " +
    			"FROM YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS S, YANTRA_OWNER.YFS_ORDER_HEADER H, YANTRA_OWNER.yfs_order_line l, " +
    			"YANTRA_OWNER.YFS_STATUS YS WHERE s.ORDER_RELEASE_STATUS_key > to_char(sysdate-1,'YYYYMMDD') " +
    			"and s.ORDER_RELEASE_STATUS_key<to_char(sysdate-1,'YYYYMMDD')||'19' " +
    			"AND H.document_type = '0001' AND l.order_header_key = h.order_header_key " +
    			"AND l.item_group_code! ='DS' AND s.order_line_key = l.order_line_key " +
    			"AND s.order_header_key = h.order_header_key and l.shipnode_key like 'VDR_%' " +
    			"AND YS.STATUS = S.STATUS AND h.enterprise_key = 'GR' AND s.status > '1000' " +
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
                Sheet sheet1 = workbook.createSheet("PB_Previous_Week");
                int nextRow1 = executeQuery(connection, "Previous Week UPSN_TRK Orders for PB", previousWeekUPSNTRKOrdersPB, sheet1);
                executeSummaryQuery(connection, "Sum of UPSN/TRK Orders for PB", sumOfPreviousWeekUPSNTRKOrdersPB, sheet1, nextRow1 + 1);

                Sheet sheet2 = workbook.createSheet("PB_Reporting_Day");
                int nextRow2 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for PB", reportingDayUPSNTRKOrdersPB, sheet2);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for PB", sumOfReportingDayUPSNTRKOrdersPB, sheet2, nextRow2 + 1);
                		
                Sheet sheet3 = workbook.createSheet("WS_Previous_Week");
                int nextRow3 = executeQuery(connection, "Previous Week UPSN_TRK Orders for WS", previousWeekUPSNTRKOrdersWS, sheet3);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for WS", sumOfPreviousWeekUPSNTRKOrdersWS, sheet3, nextRow3 + 1);

                Sheet sheet4 = workbook.createSheet("WS_Reporting_Day");
                int nextRow4 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for WS", reportingDayUPSNTRKOrdersWS, sheet4);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for WS", sumOfReportingDayUPSNTRKOrdersWS, sheet4, nextRow4 + 1);

                Sheet sheet5 = workbook.createSheet("WE_Previous_Week");
                int nextRow5 = executeQuery(connection, "Previous Week UPSN_TRK Orders for WE", previousWeekUPSNTRKOrdersWE, sheet5);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for WE", sumOfPreviousWeekUPSNTRKOrdersWE, sheet5, nextRow5 + 1);

                Sheet sheet6 = workbook.createSheet("WE_Reporting_Day");
                int nextRow6 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for WE", reportingDayUPSNTRKOrdersWE, sheet6);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for WE", sumOfReportingDayUPSNTRKOrdersWE, sheet6, nextRow6 + 1);

                Sheet sheet7 = workbook.createSheet("PK_Previous_Week");
                int nextRow7 = executeQuery(connection, "Previous Week UPSN_TRK Orders for PK", previousWeekUPSNTRKOrdersPK, sheet7);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for PK", sumOfPreviousWeekUPSNTRKOrdersPK, sheet7, nextRow7 + 1);

                Sheet sheet8 = workbook.createSheet("PK_Reporting_Day");
                int nextRow8 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for PK", reportingDayUPSNTRKOrdersPK, sheet8);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for PK", sumOfReportingDayUPSNTRKOrdersPK, sheet8, nextRow8 + 1);

                Sheet sheet9 = workbook.createSheet("MG_Previous_Week");
                int nextRow9 = executeQuery(connection, "Previous Week UPSN_TRK Orders for MG", previousWeekUPSNTRKOrdersMG, sheet9);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for MG", sumOfPreviousWeekUPSNTRKOrdersMG, sheet9, nextRow9 + 1);

                Sheet sheet10 = workbook.createSheet("MG_Reporting_Day");
                int nextRow10 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for MG", reportingDayUPSNTRKOrdersMG, sheet10);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for MG", sumOfReportingDayUPSNTRKOrdersMG, sheet10, nextRow10 + 1);

                Sheet sheet11 = workbook.createSheet("PT_Previous_Week");
                int nextRow11 = executeQuery(connection, "Previous Week UPSN_TRK Orders for PT", previousWeekUPSNTRKOrdersPT, sheet11);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for PT", sumOfPreviousWeekUPSNTRKOrdersPT, sheet11, nextRow11 + 1);

                Sheet sheet12 = workbook.createSheet("PT_Reporting_Day");
                int nextRow12 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for PT", reportingDayUPSNTRKOrdersPT, sheet12);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for PT", sumOfReportingDayUPSNTRKOrdersPT, sheet12, nextRow12 + 1);

                Sheet sheet13 = workbook.createSheet("RJ_Previous_Week");
                int nextRow13 = executeQuery(connection, "Previous Week UPSN_TRK Orders for RJ", previousWeekUPSNTRKOrdersRJ, sheet13);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for RJ", sumOfPreviousWeekUPSNTRKOrdersRJ, sheet13, nextRow13 + 1);

                Sheet sheet14 = workbook.createSheet("RJ_Reporting_Day");
                int nextRow14 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for RJ", reportingDayUPSNTRKOrdersRJ, sheet14);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for RJ", sumOfReportingDayUPSNTRKOrdersRJ, sheet14, nextRow14 + 1);

                Sheet sheet15 = workbook.createSheet("GR_Previous_Week");
                int nextRow15 = executeQuery(connection, "Previous Week UPSN_TRK Orders for GR", previousWeekUPSNTRKOrdersGR, sheet15);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for GR", sumOfPreviousWeekUPSNTRKOrdersGR, sheet15, nextRow15 + 1);

                Sheet sheet16 = workbook.createSheet("GR_Reporting_Day");
                int nextRow16 = executeQuery(connection, "Reporting Day UPSN_TRK Orders for GR", reportingDayUPSNTRKOrdersGR, sheet16);
                executeSummaryQuery(connection, "Sum of UPSN_TRK Orders for GR", sumOfReportingDayUPSNTRKOrdersGR, sheet16, nextRow16 + 1);

                // Write the workbook content to a file
                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DBRQueryOutput\\ShpmntQryBrandWise.xlsx")) {
                    workbook.write(outputStream);
                    workbook.close();
                }

                connection.close();

                System.out.println("Query results have been written to ShpmntQryBrandWise.xlsx");
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
package com.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.db.DBConnection;
/**
 * @author Jiban
 */
public class ExecuteQuery {
    
    public static void main(String[] args) {
        // Define your SQL queries individually
        String query1 = "Drop table JIBAN_UPS_SCRIPT_DATA ";
        
        String query2 = "Drop table JIBAN_UPS_SCRIPT_DATA_FNL ";
        
        String query3 = "Drop table JIBAN_UPS_SCRIPT_SFS ";
        
        String query4 = "Drop table JIBAN_UPS_SCRIPT_DC ";
        
		
		String query5 = "create table JIBAN_UPS_SCRIPT_DATA AS (select dtl.* , ol.PRIME_LINE_NO , ol.SUB_LINE_NO, shp.SHIPMENT_NO,  shp.SHIPMENT_KEY , " +
				"shl.SHIPMENT_LINE_KEY  , shp.ACTUAL_DELIVERY_DATE , shp.ACTUAL_SHIPMENT_DATE " +
				"from STERLING_PROD_SUPPORT.WSI_ORDER_AGING_TRANS dtl , YFS_SHIPMENT_LINE shl , " +
				"YFS_SHIPMENT shp , yfs_order_line ol " +
				"where dtl.ORDER_LINE_KEY = shl.ORDER_LINE_KEY " +
				"and dtl.ORDER_LINE_KEY=ol.ORDER_LINE_KEY " +
				"and shl.shipment_key=shp.shipment_key " +
				"and dtl.SCAC='PARCEL_CARRIER' " +
				"and dtl.ORDER_TYPE <> 'OTHERS_CA' " +
				"and shp.actual_delivery_date = to_date('01/01/2500','mm/dd/yyyy') " +
				"and dtl.STERLING_ORDER_LINE_STATUS in ('Invoiced', 'Shipped') ) ";
		 		
		String query6 = "create table JIBAN_UPS_SCRIPT_DATA_FNL as(select distinct scon.CONTAINER_NO , usd.owner , usd.business_status , usd.enterprise_key , usd.order_no, " +
				"usd.PAYMENT_STATUS , usd.DELIVERY_METHOD, usd.ORDER_TYPE , usd.ORDER_DATE, usd.STERLING_ORDER_TYPE, " +
				"usd.RECEIVING_NODE , usd.SCAC, usd.ITEM_ID, usd.ORDERED_QTY , usd.UNIT_COST, usd.SHIPNODE_DC_STORE, usd.STATUS_NUMBER , " +
				"usd.STERLING_ORDER_LINE_STATUS, usd.STATUS_QUANTITY, usd.ORDER_HEADER_KEY , usd.ORDER_LINE_KEY,  usd.PRIME_LINE_NO , " +
				"usd.sub_LINE_NO ,  usd.ACTUAL_DELIVERY_DATE, usd.ACTUAL_SHIPMENT_DATE ,usd.TRACKING_NO ,usd.SHIPMENT_NO, usd.SHIPMENT_KEY , usd.SHIPMENT_LINE_KEY " +
				"from JIBAN_UPS_SCRIPT_DATA usd , yfs_shipment_container scon " +
				"where usd.SHIPMENT_KEY=scon.SHIPMENT_KEY and " +
				"usd.tracking_no = scon.tracking_no and " +
				"usd.actual_delivery_date = to_date('01/01/2500','mm/dd/yyyy')) ";
		 		
		String query7 = "create table JIBAN_UPS_SCRIPT_SFS as (select udf.order_no, udf.item_id, udf.prime_line_no, udf.sub_line_no, udf.shipment_no, " +
				"udf.enterprise_key as enterprise_code, udf.shipment_key, udf.shipment_line_key, " +
				"udf.tracking_no, oh.document_type ,  udf.container_no, ors.status_quantity as quantity " +
				"from JIBAN_UPS_SCRIPT_DATA_FNL  udf , YFS_ORDER_HEADER oh ,YFS_ORDER_RELEASE_STATUS ors " +
				"where oh.ORDER_HEADER_KEY=udf.ORDER_HEADER_KEY " +
				"and udf.order_line_key = ors.order_line_key " +
				"and ors.status_quantity>0 " +
				"and ors.status in ('3700','3700.00.03') " +
				"and udf.ACTUAL_SHIPMENT_DATE > sysdate-120 " +
				"and udf.ACTUAL_SHIPMENT_DATE < sysdate-1 " +
				"and udf.SHIPNODE_DC_STORE  like '%ST:%' " +
				"and udf.actual_delivery_date = to_date('01/01/2500','mm/dd/yyyy') " +
				"and TRAcking_no like '%1Z%') ";
		 		
		String query8 = "create table JIBAN_UPS_SCRIPT_DC as (select udf.order_no, udf.item_id, udf.prime_line_no, udf.sub_line_no, udf.shipment_no, " +
				"udf.enterprise_key as enterprise_code, udf.shipment_key, udf.shipment_line_key, " +
				"udf.tracking_no, oh.document_type ,  udf.container_no, ors.status_quantity as quantity " +
				"from JIBAN_UPS_SCRIPT_DATA_FNL  udf , YFS_ORDER_HEADER oh ,YFS_ORDER_RELEASE_STATUS ors " +
				"where oh.ORDER_HEADER_KEY=udf.ORDER_HEADER_KEY " +
				"and udf.order_line_key = ors.order_line_key " +
				"and ors.status_quantity>0 " +
				"and ors.status in ('3700','3700.00.03') " +
				"and udf.ACTUAL_SHIPMENT_DATE > sysdate-120 " +
				"and udf.ACTUAL_SHIPMENT_DATE < sysdate-1 " +
				"and udf.SHIPNODE_DC_STORE  not like '%ST:%' " +
				"and udf.actual_delivery_date = to_date('01/01/2500','mm/dd/yyyy') " +
				"and TRAcking_no like '%1Z%') ";
		        
        String query9 = "select trim(order_no) AS order_no, trim(prime_line_no) AS prime_line_no, trim(sub_line_no) AS sub_line_no, " +
        		"trim(shipment_no) AS shipment_no, trim(shipment_line_key) AS shipment_line_key, trim(shipment_key) AS shipment_key, " +
        		"trim(tracking_no) AS tracking_no, trim(container_no) AS container_no, trim(item_id) AS item_id,  " +
        		"trim(enterprise_code) AS enterprise_code, trim(quantity) AS quantity, trim(document_type) AS document_type from JIBAN_UPS_SCRIPT_DC ";
        
        // CSV file path
        String csvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\output" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv";
        
        try (Connection connection = DBConnection.getReportingDBConnection()) {
            // Set auto-commit to false initially
            connection.setAutoCommit(false);
            
            if (!isTableExists(connection, "JIBAN_UPS_SCRIPT_DATA")) {  
                System.out.println("JIBAN_UPS_SCRIPT_DATA table does not exist, so proceeding with table creation...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query5);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DATA query execution failed...");
                    e.printStackTrace();
                    connection.rollback();
                }
            } else {
            	System.out.println("JIBAN_UPS_SCRIPT_DATA table exists...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query1);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA table is dropped...");
            		executeQueryAndLog(statement, query5);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DATA query execution failed...");
                    e.printStackTrace();
                    connection.rollback();				
			}
            }
            
            if (!isTableExists(connection, "JIBAN_UPS_SCRIPT_DATA_FNL")) {  
                System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL table does not exist, so proceeding with table creation...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query6);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL query execution failed...");
                    e.printStackTrace();
                    connection.rollback();
                }
            } else {
            	System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL table exists...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query2);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL table is dropped...");
            		executeQueryAndLog(statement, query6);
            		System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DATA_FNL query execution failed...");
                    e.printStackTrace();
                    connection.rollback();				
			}
            }
            
            if (!isTableExists(connection, "JIBAN_UPS_SCRIPT_SFS")) {  
                System.out.println("JIBAN_UPS_SCRIPT_SFS table does not exist, so proceeding with table creation...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query7);
            		System.out.println("JIBAN_UPS_SCRIPT_SFS table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_SFS query execution failed...");
                    e.printStackTrace();
                    connection.rollback();
                }
            } else {
            	System.out.println("JIBAN_UPS_SCRIPT_SFS table exists...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query3);
            		System.out.println("JIBAN_UPS_SCRIPT_SFS table is dropped...");
            		executeQueryAndLog(statement, query7);
            		System.out.println("JIBAN_UPS_SCRIPT_SFS table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_SFS query execution failed...");
                    e.printStackTrace();
                    connection.rollback();				
			}
            }
            
            if (!isTableExists(connection, "JIBAN_UPS_SCRIPT_DC")) {  
                System.out.println("JIBAN_UPS_SCRIPT_DC table does not exist, so proceeding with table creation...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query8);
            		System.out.println("JIBAN_UPS_SCRIPT_DC table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DC query execution failed...");
                    e.printStackTrace();
                    connection.rollback();
                }
            } else {
            	System.out.println("JIBAN_UPS_SCRIPT_DC table exists...");
            	try (Statement statement = connection.createStatement()) {
            		executeQueryAndLog(statement, query4);
            		System.out.println("JIBAN_UPS_SCRIPT_DC table is dropped...");
            		executeQueryAndLog(statement, query8);
            		System.out.println("JIBAN_UPS_SCRIPT_DC table is created...");
            		connection.commit();
            		System.out.println("Commit successful...");
            	} catch (SQLException e) {
                	System.out.println("JIBAN_UPS_SCRIPT_DC query execution failed...");
                    e.printStackTrace();
                    connection.rollback();				
			}
            }
                        
            try (Statement statement = connection.createStatement()) {
                // Execute the last SELECT query
            	System.out.println("Executing query: " + query9);
                try (ResultSet resultSet = statement.executeQuery(query9)) {
                    // Export the result of SELECT query to CSV
                    writeResultSetToCSV(resultSet, csvFilePath);
                }               
                System.out.println("CSV file has been written to:: input_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
				System.out.println("***********************************************************");
				System.out.println("***********************************************************");
                
            } catch (SQLException e) {
            	System.out.println("Query execution failed...");
                e.printStackTrace();
                connection.rollback();
            }            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void executeQueryAndLog(Statement statement, String query) throws SQLException {
        try {
            // Log message before executing the query
            System.out.println("Executing query: " + query);
            
            // Execute the query
            statement.execute(query);
         
        } catch (SQLException e) {
            // Log message if execution fails
            System.out.println("Query execution failed: " + query);
            throw e; // Rethrow the exception to propagate it up
        }
    }
    
    private static void writeResultSetToCSV(ResultSet resultSet, String csvFilePath) throws SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            // Get ResultSet metadata (to fetch column names)
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write headers to CSV
            for (int i = 1; i <= columnCount; i++) {
                writer.write(metaData.getColumnLabel(i));
                if (i < columnCount) {
                    writer.write(",");
                } else {
                    writer.write("\n");
                }
            }

            // Write each row to CSV
            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    writer.write(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.write(",");
                    } else {
                        writer.write("\n");
                    }
                }
            }
            
            System.out.println("***********************************************************");
            System.out.println("***********************************************************");
            System.out.println("Total rows fetched: " + rowCount);           
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isTableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }
}
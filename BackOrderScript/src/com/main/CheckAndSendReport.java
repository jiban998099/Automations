package com.main;
import com.db.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckAndSendReport {
    public static void main(String[] args) {
		
		//Truncate TEMP_ADC_BO_LIST table
        String truncateTable = "TRUNCATE TABLE TEMP_ADC_BO_LIST";
		
		//Insert new data to TEMP_ADC_BO_LIST table
        String insertQuery = "insert into TEMP_ADC_BO_LIST (select oh.enterprise_key,oh.order_no,oh.order_header_key, " +
					 "oh.payment_status,ol.order_line_key,ol.prime_line_no,ol.sub_line_no,ol.item_id,ol.ordered_qty,ors.status " +
					 "from yantra_owner.yfs_order_header oh, yantra_owner.yfs_order_line ol, " +
					 "yantra_owner.yfs_order_release_status ors where oh.order_header_key = ol.order_header_key " +
					 "and ol.order_line_key = ors.order_line_key and oh.document_type = '0001' " +
					 "and ors.status_quantity > 0 and ors.status > '1100' and ors.status < '3200' " +
					 "and ors.status not in  ('1400','1100.200') and ol.shipnode_key = 'ADCDTC' " +
					 "and oh.order_header_key > '202312') ";
		
		//fetching all data
        String selectQuery = "select adc.*,ob2.ship_node,ob2.alert_type,ob2.alert_Raised_on,ob2.alert_quantity,ob2.alert_level, " +
				     "ob2.onhand_available_quantity,ob2.onhand_available_date,ob2.first_future_available_date, " +
				     "scc.ship_node,scc.alert_type,scc.alert_Raised_on,scc.alert_quantity,scc.alert_level, " +
				     "scc.onhand_available_quantity,scc.onhand_available_date,scc.first_future_available_date " +
				     "from STERLING_PROD_SUPPORT.TEMP_ADC_BO_LIST adc " +
				     "left join yantra_owner.yfs_inventory_item itt on adc.item_id = itt.item_id " +
				     "left join yantra_owner.yfs_inventory_alerts ob2 " +
				     "on itt.inventory_item_key = ob2.inventory_item_key and ob2.inventory_alert_key > '202401' " +
				     "and ob2.ship_node  in ('OB2DTC') " +
				     "left join yantra_owner.yfs_inventory_alerts scc " +
				     "on itt.inventory_item_key = scc.inventory_item_key and scc.inventory_alert_key > '202401' " +
				     "and scc.ship_node  in ('SCCDTC') ";

        // Obtain ZPROD DB connection
        try (Connection connection1 = DBConnection.getZProdDBConnection()) {
            if (connection1 != null) {
                try (Statement statement = connection1.createStatement()) {
                    connection1.setAutoCommit(true);
                    System.out.println("Executing Query: " + truncateTable);
                    statement.executeUpdate(truncateTable);
                    System.out.println("Temp table has been truncated...");
                    System.out.println("Executing Query: " + insertQuery);
                    statement.executeUpdate(insertQuery);
                    System.out.println("Data has been inserted into the Temp Table...");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to obtain ZPROD DB connection.");
            }
			
			connection1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Obtain DR DB connection
        try (Connection connection2 = DBConnection.getSterlingDRConnection()) {
            if (connection2 != null) {
                try (FileWriter writer = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\ADCDTC_BO_List.csv")) {
                    executeQuery(connection2, selectQuery, writer);
                    System.out.println("Query results have been written to ADCDTC_BO_List.csv");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to obtain DR DB connection.");
            }
			connection2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeQuery(Connection connection, String sqlQuery, FileWriter writer) throws SQLException, IOException {
        System.out.println("Executing Query: " + sqlQuery);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            // Write header row
            for (int i = 1; i <= columnCount; i++) {
                writer.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.append(",");
                }
            }
            writer.append("\n");

            // Write data rows
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.append(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
            System.out.println("Query execution successful.");
			
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

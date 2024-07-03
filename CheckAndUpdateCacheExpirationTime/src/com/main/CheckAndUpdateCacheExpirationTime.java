package com.main;
import java.sql.*;

import com.db.DBConnection;

/**
 * @author Jiban
 */
public class CheckAndUpdateCacheExpirationTime {
    public static void main(String[] args) {
        // Define SQL queries as strings
        String dropTempTable = "DROP TABLE STERLING_PROD_SUPPORT.INC003178926_backup " ;
        
        String createTempTable = "create table INC003178926_backup as (select b.organization_code, b.product_line, c.item_id, " +
								 "c.cache_expiration_time FROM  yantra_owner.yfs_item b, yantra_owner.wsi_smart_rtam c " +
								 "where trim(c.item_id)=trim(b.item_id) and trunc(c.cache_expiration_time) < trunc(sysdate) + 7 " +
								 "and c.item_id not in (select item_id from yantra_owner.wsi_item_demand_velocity)) ";
        
        String countRows = "SELECT COUNT(*) FROM STERLING_PROD_SUPPORT.INC003178926_backup " ;
        

        // Define the increment value for cache_expiration_time
        int incrementValue = 50;

        // Define the brand names
        String[] brands = {"PB", "WS", "WE", "PK", "MG", "PT", "RJ", "GR"};

        try (Connection connection = DBConnection.getZProdDBConnection()) {
			System.out.println("Starting CheckAndUpdateCacheExpirationTime Operation...");
			
            // Create a statement for executing the DROP TABLE query
        	
        	// Set auto-commit to false initially
            connection.setAutoCommit(false);
            
            try (Statement dropStatement = connection.createStatement()) {
            	
                // Execute the DROP TABLE query
                dropStatement.executeUpdate(dropTempTable);
				
				// Pause after executing the DROP TABLE query
                Thread.sleep(30000); //0.5 minute = 30,000 milliseconds
				
                System.out.println("Temp table has been dropped successfully...");
				
            }
            
            // Commit the transaction
            connection.commit();
            System.out.println("Commit successful.");

            // Create a statement for executing the CREATE TABLE query
            try (Statement createStatement = connection.createStatement()) {
            	
                // Execute the CREATE TABLE query
                createStatement.executeUpdate(createTempTable);
                System.out.println("Fresh Temp table has been created successfully.");
                
                // Pause after executing the CREATE TABLE query
                Thread.sleep(60000); //1 minute = 60,000 milliseconds
                
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Commit the transaction
            connection.commit();
            System.out.println("Commit successful.");

            // Create a statement for executing the SELECT COUNT(*) query
            try (Connection countConnection = DBConnection.getSterlingDRConnection()) {
                try (Statement countStatement = countConnection.createStatement()) {
                	
                    // Execute the SELECT COUNT(*) query
                    ResultSet resultSet = countStatement.executeQuery(countRows);
                    // Process the result
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        System.out.println("Total records fetched: " + count);
                    }
                }
            }

            // Loop through each brand and execute the update query
            int totalUpdated = 0;            
            for (int i = 0; i < brands.length; i++) {
                // Calculate the increment value based on the index
                int increment = incrementValue + i;

                // Prepare the SQL statement
                String updateQuery = "update yantra_owner.wsi_smart_rtam set cache_expiration_time = sysdate + " + increment + " , " +
								   "modifyts=sysdate, modifyprogid='INC003178926' where item_id in (select c.item_id FROM yantra_owner.yfs_item b, " +
								   "yantra_owner.wsi_smart_rtam c where trim(c.item_id)=trim(b.item_id) and trunc(c.cache_expiration_time) < trunc(sysdate) + 7 " +
								   "and c.item_id not in (select item_id from yantra_owner.wsi_item_demand_velocity) and b.organization_code = '" + brands[i] + "') ";
                
                // Print the increment value for the current brand
                System.out.println("Increment value for " + brands[i] + ": " + increment);
                
                try (Statement statement = connection.createStatement()) {
                    // Execute the update statement
                    //int updatedRows = statement.executeUpdate(updateQuery);
                    //totalUpdated += updatedRows;
                    // Commit the transaction after each update
                    //connection.commit();
                    System.out.println("Commit successful.");
                }catch (SQLException e) {
                    e.printStackTrace();
                    // Rollback the transaction if an exception occurs
                    connection.rollback();
                }
                // Pause after completing the update for each brand
                Thread.sleep(60000); // 1 minute = 60,000 milliseconds
            }
            System.out.println("Total rows updated: " + totalUpdated);

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            triggerP2();
        }
    }
    
    private static void triggerP2() {
		System.out.println("Raising P2");
		//ProcessBuilder pb = new ProcessBuilder("/app/sqlworkbench/scripts/CTS/CheckAndUpdateCacheExpirationTime/triggerP2.sh");
		 try {
			//Process p = pb.start();
			System.out.println("P2 triggered.");
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
	}
}
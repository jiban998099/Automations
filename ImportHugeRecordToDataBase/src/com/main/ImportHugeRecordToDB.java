package com.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.db.DBConnection;
/**
 * @author Jiban
 */
public class ImportHugeRecordToDB {

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        BufferedReader reader = null;

        try {
            conn = DBConnection.getZProdDBConnection();
            conn.setAutoCommit(true); // Enable auto-commit

            reader = new BufferedReader(new FileReader("C:\\\\Users\\\\jpradhan\\\\OneDrive - Williams-Sonoma Inc\\\\Documents\\\\input.csv"));

            // Prepare SQL statement
            String sql = "INSERT INTO STERLING_PROD_SUPPORT.Fidleton_del_2 (ORDER_NO, DEL_DATE, PRIME_LINE_NO, SUB_LINE_NO, ITEM_ID) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            // Skip header row
            String headerRow = reader.readLine();
            if (headerRow != null) {
                // Read CSV rows and insert data one by one
                String line;
                int totalCount = 0;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(","); // Split by comma (customize if delimiter is different)
                    
                    String orderNo = values[0].trim();
                    String delDate = values[1].trim();
                    int primeLineNumber = Integer.parseInt(values[2].trim());
                    int subLineNumber = Integer.parseInt(values[3].trim());
                    int itemId = Integer.parseInt(values[4].trim());
                    

                    stmt.setString(1, orderNo);
                    stmt.setString(2, delDate);
                    stmt.setInt(3, primeLineNumber);
                    stmt.setInt(4, subLineNumber);
                    stmt.setInt(5, itemId);

                    // Execute the insert statement for each row
                    stmt.executeUpdate();

                    totalCount++;
                    System.out.println("Row " + totalCount + " imported.");
                }

                System.out.println("Import successful. Total Records: " + totalCount);
            } else {
                System.out.println("CSV file is empty.");
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (reader != null)
                    reader.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}

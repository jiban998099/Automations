package com.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.db.DBConnection;
/**
 * @author Jiban
 */
public class FetchOrderDetails {

	public static void main(String[] args) {
	    String inputFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\input.csv";
	    String outputFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\FetchOrderDetailsOutput.csv";

	    int rowCount = 0; // Initialize row counter

	    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
	         BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

	        System.out.println("Reading input file...");

	        // Write headers to output CSV file
	        bw.write("ORDER_NO,ITEM_ID,UPDATED_CUST_DEL_DATE,ORDER_HEADER_KEY,ORDER_LINE_KEY,PRODUCT_LINE\n");

	        String line;
	        boolean isFirstLine = true;
	        while ((line = br.readLine()) != null) {
	            if (isFirstLine) {
	                isFirstLine = false;
	                continue; // Skip header row
	            }

	            if (line.trim().isEmpty()) {
	                System.out.println("Skipping empty line.");
	                continue; // Skip empty lines
	            }

	            String[] data = line.split(","); // Assuming CSV format is comma-separated

	            if (data.length >= 3) { // Ensure data array has at least 3 elements (orderNo, itemId, updatedCustDelDate)
	                processDataRow(data, bw);
	                rowCount++; // Increment row counter
	            } else {
	                System.out.println("Skipping invalid data row: " + line);
	            }
	        }

	        System.out.println("Total valid rows processed: " + rowCount);
	        System.out.println("Processing completed successfully.");

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void processDataRow(String[] data, BufferedWriter bw) throws IOException {
	    // Assuming data array contains orderNo at index 0, itemId at index 1, and Updated Cust. Del. Date at index 2

	    String orderNo = data[0].trim();
	    String itemId = data[1].trim();
	    String updatedCustDelDate = data[2].trim(); // Assuming Updated Cust. Del. Date is at index 2

	    System.out.println("Processing data: OrderNo = " + orderNo + ", ItemID = " + itemId + ", Updated Cust. Del. Date = " + updatedCustDelDate);

	    String[] queryOutput = performQuery(orderNo, itemId).split(",");

	    // Write to output CSV file
	    StringBuilder sb = new StringBuilder();
	    sb.append(orderNo).append(",").append(itemId).append(",").append(updatedCustDelDate); // Input data including Updated Cust. Del. Date
	    for (String output : queryOutput) {
	        sb.append(",").append(output); // Query output
	    }
	    bw.write(sb.toString() + "\n");
	}


    private static String performQuery(String orderNo, String itemId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getSterlingDRConnection();
            String sql = "SELECT TRIM(o.order_header_key) AS ORDER_HEADER_KEY, " +
                         "TRIM(l.order_line_key) AS ORDER_LINE_KEY, TRIM(l.product_line) AS PRODUCT_LINE " +
                         "FROM yantra_owner.yfs_order_header o, yantra_owner.yfs_order_line l " +
                         "WHERE o.order_header_key = l.order_header_key AND o.order_no = '" + orderNo + "' AND l.item_id = '" + itemId + "'";

            System.out.println("Executing SQL query: " + sql);

            stmt = conn.prepareStatement(sql); // No need to set parameters

            rs = stmt.executeQuery();

            if (rs.next()) {
                String orderHeaderKey = rs.getString("ORDER_HEADER_KEY");
                String orderLineKey = rs.getString("ORDER_LINE_KEY");
                String productLine = rs.getString("PRODUCT_LINE");

                return orderHeaderKey + "," + orderLineKey + "," + productLine;
            } else {
                return "No data found for OrderNo = " + orderNo + ", ItemID = " + itemId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}

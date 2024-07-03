package com.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.db.DBConnection;
/**
 * @author Jiban
 */
public class FetchWsiPromiseDateMonitor {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\FetchOrderDetailsOutput.csv";
        String outputFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\PromiseDateMonitorOutput.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {

            System.out.println("Reading input file...");

            // Skip header line
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("Empty input file.");
                return;
            }

            // Find the index of ORDER_LINE_KEY column
            String[] headers = headerLine.split(",");
            int orderLineKeyIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if ("ORDER_LINE_KEY".equals(headers[i].trim())) {
                    orderLineKeyIndex = i;
                    break;
                }
            }
            if (orderLineKeyIndex == -1) {
                System.out.println("ORDER_LINE_KEY column not found in header.");
                return;
            }

            // Read ORDER_LINE_KEY values from output.csv
            List<String> orderLineKeys = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > orderLineKeyIndex) {
                    String orderLineKey = data[orderLineKeyIndex].trim();
                    if (!orderLineKey.isEmpty()) {
                        orderLineKeys.add(orderLineKey);
                    }
                }
            }

            // Execute SQL query based on ORDER_LINE_KEY values
            executeQueryAndWriteToCSV(orderLineKeys, bw);

            System.out.println("Processing completed successfully.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeQueryAndWriteToCSV(List<String> orderLineKeys, BufferedWriter bw) throws SQLException, IOException {
        try (Connection conn = DBConnection.getSterlingDRConnection();
             BufferedWriter writer = new BufferedWriter(bw)) {

            writer.write("ORDER_LINE_KEY,ITEM_ID\n");

            // Prepare SQL query
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT trim(order_line_key), trim(item_id) FROM yantra_owner.WSI_PROMISE_DATE_MONITOR WHERE ORDER_LINE_KEY IN (");

            boolean isFirst = true;
            for (String orderLineKey : orderLineKeys) {
                if (!isFirst) {
                    sqlBuilder.append(",");
                }
                sqlBuilder.append("'").append(orderLineKey).append("'");
                isFirst = false;
            }
            sqlBuilder.append(")");

            String sql = sqlBuilder.toString();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                // Iterate through result set and write to output CSV
                while (rs.next()) {
                    String fetchedOrderLineKey = rs.getString(1);
                    String itemId = rs.getString(2);
                    writer.write(fetchedOrderLineKey + "," + itemId + "\n");
                }
            }
        }
    }
}
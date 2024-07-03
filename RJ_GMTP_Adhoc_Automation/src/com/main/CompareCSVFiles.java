package com.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @author Jiban
 */
public class CompareCSVFiles {

    public static void main(String[] args) {
    	String outputCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\FetchOrderDetailsOutput.csv";
		String output2CsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\PromiseDateMonitorOutput.csv";
		String bothApiCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI.csv";
		String topApiCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\topAPIDocs\\topAPI.csv";

        try {
            List<Record> recordsOutput = readCsvFile(outputCsvFilePath);
            List<Record> recordsOutput2 = readCsvFile(output2CsvFilePath);

            Set<String> output2Keys = new HashSet<>();
            for (Record record : recordsOutput2) {
                String key = record.getOrderLineKey() + "_" + record.getItemId();
                output2Keys.add(key);
            }

            List<Record> bothApiRecords = new ArrayList<>();
            List<Record> topApiRecords = new ArrayList<>();

            int comparisonCount = 0;

            // Compare records from FetchOrderDetailsOutput.csv with PromiseDateMonitorOutput.csv
            for (Record record : recordsOutput) {
                String key = record.getOrderLineKey() + "_" + record.getItemId();
                comparisonCount++;

                // Print the combination being compared
                System.out.println("Comparing combination: " + key + " from FetchOrderDetailsOutput.csv"
                        + " with combination: " + key + " from PromiseDateMonitorOutput.csv");

                if (output2Keys.contains(key)) {
                    bothApiRecords.add(record);
                    System.out.println("Result: Found in both files");
                } else {
                    topApiRecords.add(record);
                    System.out.println("Result: Found only in FetchOrderDetailsOutput.csv");
                }
            }

            // Write results to CSV files
            writeCsvFile(bothApiCsvFilePath, bothApiRecords);
            writeCsvFile(topApiCsvFilePath, topApiRecords);

            // Print comparison count
            System.out.println("Comparison count: " + comparisonCount);
            System.out.println("Files processed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Record> readCsvFile(String filePath) throws IOException {
        List<Record> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Read header
        String header = reader.readLine();
        String[] headers = header.split(",");

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1); // -1 to keep trailing empty fields
            Record record = new Record();
            for (int i = 0; i < headers.length; i++) {
                switch (headers[i]) {
                    case "ORDER_NO":
                        record.setOrderNo(data[i]);
                        break;
                    case "ITEM_ID":
                        record.setItemId(data[i]);
                        break;
                    case "UPDATED_CUST_DEL_DATE":
                        record.setUpdatedCustDelDate(data[i]);
                        break;
                    case "ORDER_HEADER_KEY":
                        record.setOrderHeaderKey(data[i]);
                        break;
                    case "ORDER_LINE_KEY":
                        record.setOrderLineKey(data[i]);
                        break;
                    case "PRODUCT_LINE":
                        record.setProductLine(data[i]);
                        break;
                    default:
                        // Handle unexpected columns
                        break;
                }
            }
            records.add(record);
        }

        reader.close();
        return records;
    }

    private static void writeCsvFile(String filePath, List<Record> records) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // Write header
        writer.write("ORDER_NO,ITEM_ID,UPDATED_CUST_DEL_DATE,ORDER_HEADER_KEY,ORDER_LINE_KEY,PRODUCT_LINE\n");

        // Write records
        for (Record record : records) {
            writer.write(record.getOrderNo() + "," +
                    record.getItemId() + "," +
                    record.getUpdatedCustDelDate() + "," +
                    record.getOrderHeaderKey() + "," +
                    record.getOrderLineKey() + "," +
                    record.getProductLine() + "\n");
        }

        writer.close();
    }

    private static class Record {
        private String orderNo;
        private String itemId;
        private String updatedCustDelDate;
        private String orderHeaderKey;
        private String orderLineKey;
        private String productLine;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getUpdatedCustDelDate() {
            return updatedCustDelDate;
        }

        public void setUpdatedCustDelDate(String updatedCustDelDate) {
            this.updatedCustDelDate = updatedCustDelDate;
        }

        public String getOrderHeaderKey() {
            return orderHeaderKey;
        }

        public void setOrderHeaderKey(String orderHeaderKey) {
            this.orderHeaderKey = orderHeaderKey;
        }

        public String getOrderLineKey() {
            return orderLineKey;
        }

        public void setOrderLineKey(String orderLineKey) {
            this.orderLineKey = orderLineKey;
        }

        public String getProductLine() {
            return productLine;
        }

        public void setProductLine(String productLine) {
            this.productLine = productLine;
        }
    }
}

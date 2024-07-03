package com.main;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * @author Jiban
 */
public class BothAPISSAvlDateCalc {

    public static void main(String[] args) {
        String inputFilename = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\BothAPISSEndDateCalc.csv"; // Path to the input CSV file
        String outputFilename = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\BothAPISSAvlDateCalc.csv"; // Path to the output CSV file

        try {
            List<String[]> inputData = readFromCSV(inputFilename);
            List<String[]> outputData = calculateNextDay(inputData);
            writeToCSV(outputFilename, outputData);
            System.out.println("Output CSV file generated successfully: " + outputFilename);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static List<String[]> readFromCSV(String filename) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean headerSkipped = false; // Skip the header line if present
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip header line
                }
                String[] fields = line.split(","); // Split by comma (adjust as per your CSV format)
                data.add(fields);
            }
        }
        return data;
    }

    private static List<String[]> calculateNextDay(List<String[]> inputData) throws ParseException {
        List<String[]> outputData = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String[] rowData : inputData) {
            String currentPromiseEndDateStr = rowData[4].trim(); // Assuming CurrentPromiseEndDate is in the 5th column (index 4)
            Date endDate = sdf.parse(currentPromiseEndDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String nextDay = sdf.format(calendar.getTime());
            
            // Construct new row with AvailableDate inserted after CurrentPromiseEndDate
            List<String> newRow = new ArrayList<>(Arrays.asList(rowData));
            newRow.add(5, nextDay); // Insert AvailableDate after index 4 (CurrentPromiseEndDate)
            
            outputData.add(newRow.toArray(new String[0]));
        }
        return outputData;
    }

    private static void writeToCSV(String filename, List<String[]> outputData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header line
            writer.write("ORDER_NO,ITEM_ID,UPDATED_CUST_DEL_DATE,CurrentPromiseDate,CurrentPromiseEndDate,AvailableDate,ORDER_HEADER_KEY,ORDER_LINE_KEY,PRODUCT_LINE\n");
            
            // Write data rows
            for (String[] rowData : outputData) {
                writer.write(String.join(",", rowData) + "\n");
            }
        }
    }
}

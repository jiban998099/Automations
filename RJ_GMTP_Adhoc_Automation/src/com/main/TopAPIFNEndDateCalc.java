package com.main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/**
 * @author Jiban
 */
public class TopAPIFNEndDateCalc {

    // USA holidays
    private static final Set<LocalDate> holidays = new HashSet<>();

    static {
        holidays.add(LocalDate.of(2024, 1, 1));
        holidays.add(LocalDate.of(2024, 1, 15));
        holidays.add(LocalDate.of(2024, 2, 19));
        holidays.add(LocalDate.of(2024, 5, 27));
        holidays.add(LocalDate.of(2024, 6, 19));
        holidays.add(LocalDate.of(2024, 7, 4));
        holidays.add(LocalDate.of(2024, 9, 2));
        holidays.add(LocalDate.of(2024, 10, 14));
        holidays.add(LocalDate.of(2024, 11, 11));
        holidays.add(LocalDate.of(2024, 11, 28));
        holidays.add(LocalDate.of(2024, 12, 25));
    }

    public static void main(String[] args) {
        String inputCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\topAPIDocs\\topAPI_FN.csv";
        String outputCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\topAPIDocs\\TopAPIFNEndDateCalc.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputCsvFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFilePath))) {

            // Write header for the output CSV file with CurrentPromiseDate and CurrentPromiseEndDate columns
            String header = reader.readLine();
            String[] headerFields = header.split(",");

            // Construct new header with added columns
            StringBuilder newHeader = new StringBuilder();
            for (int i = 0; i < headerFields.length; i++) {
                newHeader.append(headerFields[i]);
                if (i == 2) {
                    newHeader.append(",CurrentPromiseDate,CurrentPromiseEndDate");
                }
                if (i < headerFields.length - 1) {
                    newHeader.append(",");
                }
            }
            newHeader.append("\n");
            writer.write(newHeader.toString());

            // Process each line (skipping header)
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Extract relevant fields
                String orderNo = fields[0];
                String itemId = fields[1];
                String updatedCustDelDateStr = fields[2];
                String orderHeaderKey = fields[3];
                String orderLineKey = fields[4];
                String productLine = fields[5]; // Adjust based on actual CSV structure

                LocalDate updatedCustDelDate = LocalDate.parse(updatedCustDelDateStr);

                // Calculate CurrentPromiseDate (assuming it's the same as UPDATED_CUST_DEL_DATE)
                String currentPromiseDate = updatedCustDelDateStr;

                // Calculate CurrentPromiseEndDate
                LocalDate currentPromiseEndDate = calculateOutputDate(updatedCustDelDate);

                // Construct the line to write
                String outputLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                        orderNo, itemId, updatedCustDelDateStr, currentPromiseDate, currentPromiseEndDate.toString(),
                        orderHeaderKey, orderLineKey, productLine);

                // Write the line to the output CSV file
                writer.write(outputLine);
            }

            System.out.println("Output CSV file generated successfully: " + outputCsvFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LocalDate calculateOutputDate(LocalDate inputDate) {
        LocalDate currentDate = inputDate;
        int daysToAdd = 0;
        while (daysToAdd < 14) {
            currentDate = currentDate.plusDays(1);
            if (!isHolidayOrWeekend(currentDate)) {
                daysToAdd++;
            }
        }
        return currentDate;
    }

    private static boolean isHolidayOrWeekend(LocalDate date) {
        return holidays.contains(date) || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}
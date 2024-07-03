package com.main;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
/**
 * @author Jiban
 */
public class BothAPISSEndDateCalc {

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
        String inputCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI_SS.csv"; // Original CSV file path
        String outputCsvFilePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\BothAPISSEndDateCalc.csv"; // Output CSV file path

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputCsvFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvFilePath));

            // Write header for the output CSV file with CurrentPromiseDate and CurrentPromiseEndDate columns
            String header = reader.readLine();
            writer.write(header.replaceFirst("(UPDATED_CUST_DEL_DATE,)", "$1CurrentPromiseDate,CurrentPromiseEndDate,") + "\n");

            // Process each line (skipping header)
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String orderNo = fields[0];
                String itemId = fields[1];
                String updatedCustDelDateStr = fields[2];
                String orderHeaderKey = fields[3];
                String orderLineKey = fields[4];
                String productLine = fields[5]; // Adjust if necessary based on actual CSV structure

                LocalDate updatedCustDelDate = LocalDate.parse(updatedCustDelDateStr);

                // Calculate CurrentPromiseDate (assuming it's the same as UPDATED_CUST_DEL_DATE)
                LocalDate currentPromiseDate = updatedCustDelDate;

                // Calculate CurrentPromiseEndDate
                LocalDate currentPromiseEndDate = calculateOutputDate(currentPromiseDate);

                // Write line to output CSV with CurrentPromiseDate and CurrentPromiseEndDate columns
                writer.write(String.join(",", orderNo, itemId, updatedCustDelDateStr, currentPromiseDate.toString(), currentPromiseEndDate.toString(), orderHeaderKey, orderLineKey, productLine) + "\n");
            }

            System.out.println("Output CSV file generated successfully: " + outputCsvFilePath);

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LocalDate calculateOutputDate(LocalDate inputDate) {
        LocalDate currentDate = inputDate;
        int daysToAdd = 0;
        while (daysToAdd < 2) {
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

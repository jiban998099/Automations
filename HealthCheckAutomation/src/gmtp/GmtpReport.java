package gmtp;

import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GmtpReport {
    public static void main(String[] args) {
        try {
            // Create a new Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("GMTP Order Dashboard");

            // Define data for the table
            String[][] data = {
            		{"", "", "", "GMTP Order Dashboard", "", "", "", "", ""},
                    {"", "Order type", "Current Date", "DOD (Including Net Suite Orders)", "WOW (Including Net Suite Orders)", "YOY (Including Net Suite Orders)", "", "", ""},
                    {"OrderCreate", "Order Total", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", ""},
                    {"", "Locations", "Current Date", "DOD (Including Net Suite Orders)", "WOW (Including Net Suite Orders)", "YOY (Including Net Suite Orders)", "", "", ""},
                    {"", "DC", "", "", "", "", "", "", ""},
                    {"Drop", "GCWH + SPWH", "", "", "", "", "", "", ""},
                    {"", "STORE", "", "", "", "", "", "", ""},
                    {"", "", "", "", "", "", "", "", ""},
                    {"", "Payment Type", "Current Date", "DOD (Including Net Suite Orders)", "WOW (Including Net Suite Orders)", "YOY (Including Net Suite Orders)", "", "", ""},
                    {"Order Payment", "Gift_Card + E_Gift_Card", "", "", "", "", "", "", ""},
                    {"", "CREDIT", "", "", "", "", "", "", ""},
                    {"", "CREDIT CARD", "", "", "", "", "", "", ""},
                    {"", "MERCH_CARD +DUE_BILL", "", "", "", "", "", "", ""},
                    {"", "+REFUND_CHECK+LRC", "", "", "", "", "", "", ""},
                    {"", "+VOUCHER.ADJUSTMENT", "", "", "", "", "", "", ""},
                    {"", "+CASH", "", "", "", "", "", "", ""},
                    {"Order Cancellation", "", "", "", "", "", "", "", ""}
            };

            // Write data to the Excel sheet
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[i][j]);
                }
            }

            // Write the workbook to a file
            String filePath = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\GMTP_Report\\GMTP_Order_Dashboard.xlsx";
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();

            // Close the workbook
            workbook.close();

            System.out.println("Excel file created successfully at: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

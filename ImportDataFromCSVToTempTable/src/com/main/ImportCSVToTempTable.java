package com.main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.db.DBConnection; // Assuming this imports your DB connection utility
/**
 * @author Jiban
 */
public class ImportCSVToTempTable {

    // Method to remove BOM and other special characters if present
    private static String cleanString(String line) {
        // Remove BOM characters from the beginning of the line
        if (line != null && line.startsWith("\uFEFF")) {
            line = line.substring(1);
        }
        // Remove other non-printable characters
        line = line.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        return line.trim();
    }

    public static void main(String[] args) {
        Connection conn = null;
        BufferedReader reader = null;

        try {
            conn = DBConnection.getZProdDBConnection(); // Obtain your database connection
            conn.setAutoCommit(false); // Disable auto-commit for manual transaction control

            reader = new BufferedReader(new FileReader("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\input.csv"));

            // Read the first line to get headers and clean any BOM characters
            String headerRow = cleanString(reader.readLine());

            if (headerRow != null) {
                // Split headers by comma, trim each column name, and handle empty column names
                String[] headers = headerRow.split(",");
                for (int i = 0; i < headers.length; i++) {
                    headers[i] = headers[i].trim();
                    if (headers[i].isEmpty()) {
                        headers[i] = "COLUMN_" + i;
                    }
                }

                // Create SQL statement to dynamically create the table
                StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE Temp_Sutter_Backorders (");
                for (int i = 0; i < headers.length; i++) {
                    // Enclose column name in double quotes to handle special characters or reserved words
                    sqlBuilder.append("\"").append(headers[i]).append("\" VARCHAR(255)");

                    if (i < headers.length - 1) {
                        sqlBuilder.append(", ");
                    }
                }
                sqlBuilder.append(")");

                String createTableSQL = sqlBuilder.toString();
                System.out.println("SQL to create table: " + createTableSQL);

                // Execute the create table statement
                try (PreparedStatement createTableStmt = conn.prepareStatement(createTableSQL)) {
                    createTableStmt.executeUpdate();
                    System.out.println("Table created successfully.");
                }

                // Read data rows and insert into the dynamically created table
                String line;
                int totalCount = 0;
                while ((line = reader.readLine()) != null) {
                    // Clean the line of BOM and other special characters
                    line = cleanString(line);

                    // Split values by comma, trim each value, and prepare for insertion
                    String[] values = line.split(",");
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }

                    // Prepare SQL statement to insert data into the dynamically created table
                    StringBuilder insertSqlBuilder = new StringBuilder("INSERT INTO Temp_Sutter_Backorders VALUES (");
                    for (int i = 0; i < values.length; i++) {
                        insertSqlBuilder.append("?");
                        if (i < values.length - 1) {
                            insertSqlBuilder.append(", ");
                        }
                    }
                    insertSqlBuilder.append(")");

                    String insertSql = insertSqlBuilder.toString();
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        // Set parameters dynamically based on column count
                        for (int i = 0; i < values.length; i++) {
                            insertStmt.setString(i + 1, values[i]); // Insert trimmed values
                        }

                        // Execute the insert statement for each row
                        insertStmt.executeUpdate();

                        totalCount++;
                        System.out.println("Row " + totalCount + " inserted into dynamically created table.");
                    }
                }

                // Commit the transaction
                conn.commit();
                System.out.println("Import successful. Total Records: " + totalCount);
            } else {
                System.out.println("CSV file is empty.");
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback(); // Rollback the transaction in case of error
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (conn != null)
                    conn.close();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Query to change column name: "+"[ALTER TABLE jiban_temp RENAME COLUMN \"???Order No\" TO \"Order No\";]");
    }
}
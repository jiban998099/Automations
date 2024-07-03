package com.main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author Jiban
 */
public class BothAPIProductLineFilter {

    public static void main(String[] args) {
    	String inputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI.csv";
    	String outputFileSS = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI_SS.csv";
    	String outputFileFN = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI_FN.csv";
    	String outputFileBlank = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\bothAPI_Blank.csv";

        int countSSCMOGROUND = 0;
        int countFN = 0;
        int countBlank = 0;

        try {
            // Open the input file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            // Create writers for each output file
            FileWriter writerSS = new FileWriter(outputFileSS);
            FileWriter writerFN = new FileWriter(outputFileFN);
            FileWriter writerBlank = new FileWriter(outputFileBlank);

            // Read the header and write it to each output file
            String header = reader.readLine();
            writerSS.write(header + "\n");
            writerFN.write(header + "\n");
            writerBlank.write(header + "\n");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String productLine = parts[5].trim();
                    if (productLine.equals("SS") || productLine.equals("CMO") || productLine.equals("GROUND")) {
                        writerSS.write(line + "\n");
                        countSSCMOGROUND++;
                    } else if (productLine.equals("FN")) {
                        writerFN.write(line + "\n");
                        countFN++;
                    } else if (productLine.isEmpty()) {
                        writerBlank.write(line + "\n");
                        countBlank++;
                    }
                }
            }

            // Close all writers and reader
            writerSS.close();
            writerFN.close();
            writerBlank.close();
            reader.close();

            // Print counts to console
            System.out.println("Counts:");
            System.out.println("SS/CMO/GROUND: " + countSSCMOGROUND);
            System.out.println("FN: " + countFN);
            System.out.println("Blank: " + countBlank);
            System.out.println("Filtered data has been written to " + outputFileSS + ", " + outputFileFN + ", and " + outputFileBlank);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

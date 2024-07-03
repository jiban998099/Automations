package com.main;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.db.DBConnection;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Jiban
 */

public class FilterAndTriggerReleaseOrder {
	
	private static final Logger logger = Logger.getLogger(FilterAndTriggerReleaseOrder.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Properties\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Properties\\Production.properties";
    public static final String serviceRequestNo = "SERREQ0750096";
    public static final String userId = "jpradhan";
    private static int orderHeaderKeyCount = 0; // Counter to keep track of the number of items processed

    public static void main(String[] args) {
        // Input file path
        String inputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\input.csv";

        // Filter ACTION as RELEASE
        String releaseOutputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\release.csv";
        String reviewOutputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\review.csv";
        
        // Filter and write RELEASE data
        int[] releaseCounts = filterAndWriteCSV(inputFile, releaseOutputFile, "RELEASE");
        System.out.println("Release - " + "Total Records: " + releaseCounts[1]);

        // Filter and write REVIEW data
        int[] reviewCounts = filterAndWriteCSV(inputFile, reviewOutputFile, "REVIEW");
        System.out.println("Review - " + "Total Records: " + reviewCounts[1]);

        System.out.println("Filtered data has been written to release.csv and review.csv");

        // Extract unique ORDER_NO values and write to release_order_no.csv
        String releaseFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\release.csv";
        String orderNoFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\release_order_no.csv";
        int[] orderNoCounts = extractAndWriteOrderNo(releaseFile, orderNoFile);
        System.out.println("Duplicates removed from ORDER_NO: " + orderNoCounts[0] + ", Unique ORDER_NO values: " + orderNoCounts[1]);
        System.out.println("Unique ORDER_NO values have been written to release_order_no.csv");
        
        // Execute SQL query for each order number and write results to release_order_header_keys.csv
        String outputFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\LRCFraudOrderAutomation\\release_order_header_keys.csv";
        executeQueryAndWriteResults(orderNoFile, outputFile);
        
        configureLogging();
        
        try {
            loadProperties();
            List<String> orderHeaderKeys = readOrderHeaderKeysFromFile("C:\\\\Users\\\\jpradhan\\\\OneDrive - Williams-Sonoma Inc\\\\Documents\\\\LRCFraudOrderAutomation\\\\release_order_header_keys.csv");
            if (orderHeaderKeys.isEmpty()) {
                logger.warn("No OrderHeaderKeys found in the file.");
            } else {
                for (String orderHeaderKey : orderHeaderKeys) {
                	triggerApi(orderHeaderKey);
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        }
        
        System.out.println("Transition_Payment_Hold has been resloved for the attched release orders. Kindly validate for few orders manually.....");
                      
     }
    
    private static List<String> readOrderHeaderKeysFromFile(String filePath) throws IOException {
        List<String> orderHeaderKeys = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the first line (header)
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                orderHeaderKeys.add(line.trim());
            }
        }
        return orderHeaderKeys;
    }
    
    private static void triggerApi(String orderHeaderKey) {
        try {
        	orderHeaderKeyCount++; // Increment the item count
            Document changeOrderdoc = createChangeOrderDocument(orderHeaderKey);
            logger.debug("Input XML for orderHeaderKey " + orderHeaderKey + ": " + documentToString(changeOrderdoc));
            triggerChangeOrder(changeOrderdoc);
            System.out.println("orderHeaderKey " + orderHeaderKeyCount + ": changeOrder API triggered for OrderHeaderKey " + orderHeaderKey);
        } catch (Exception e) {
            logger.error("Error while triggering changeOrder for OrderHeaderKey " + orderHeaderKey + ": " + e.getMessage());
        }
    }
    
    // Configure Log4j
 	private static void configureLogging() {
         PropertyConfigurator.configure(LOG4J_CONFIG_FILE);
     }

    private static int[] filterAndWriteCSV(String inputFile, String outputFile, String filterValue) {
        List<String[]> filteredData = new ArrayList<>();
        int duplicatesRemoved = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean headerFiltered = false;
            while ((line = br.readLine()) != null) {
                if (!headerFiltered) {
                    // Filter header
                    String[] headers = line.split(",");
                    filteredData.add(headers);
                    headerFiltered = true;
                } else {
                    String[] values = line.split(",");
                    if (values[values.length - 1].equals(filterValue)) {
                        // Filter rows with specified action
                        filteredData.add(values);
                    } else {
                        duplicatesRemoved++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeCSV(outputFile, filteredData);

        int uniqueValues = filteredData.size() - 1; // Subtracting 1 to exclude header
        return new int[]{duplicatesRemoved, uniqueValues};
    }

    private static void writeCSV(String outputFile, List<String[]> data) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (String[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    writer.append(row[i]);
                    if (i < row.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] extractAndWriteOrderNo(String inputFile, String outputFile) {
        Set<String> orderNoSet = new HashSet<>();
        int duplicatesRemoved = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (!orderNoSet.add(values[0])) { // Assuming ORDER_NO is the first column
                    duplicatesRemoved++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        writeOrderNoCSV(outputFile, orderNoSet);

        int uniqueValues = orderNoSet.size();
        return new int[]{duplicatesRemoved, uniqueValues};
    }

    private static void writeOrderNoCSV(String outputFile, Set<String> orderNoSet) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (String orderNo : orderNoSet) {
                writer.write(orderNo + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void executeQueryAndWriteResults(String orderNoFile, String outputFile) {
        // Get database connection
        Connection connection = DBConnection.getSterlingDRConnection();
        if (connection == null) {
            System.out.println("Failed to establish database connection.");
            return;
        }

        try (Statement statement = connection.createStatement();
             FileWriter writer = new FileWriter(outputFile)) {

            // Write header
            writer.write("OrderHeaderKey\n");

            // Read order numbers from release_order_no.csv
            Set<String> orderNumbers = readOrderNumbers(orderNoFile);
            System.out.println("Total number of Unique Orders after removing the duplicates: " + orderNumbers.size());

            // Execute SQL query for each order number
            for (String orderNumber : orderNumbers) {
                String query = "SELECT DISTINCT oh.order_header_key FROM yantra_owner.yfs_order_header oh " +
                        "JOIN yantra_owner.yfs_order_line ol ON oh.order_header_key = ol.order_header_key " +
                        "JOIN yantra_owner.yfs_order_release_status ors ON ol.order_line_key = ors.order_line_key " +
                        "JOIN yantra_owner.yfs_status s ON ors.status = s.status WHERE oh.order_no = '" + orderNumber + "' " +
                        "AND oh.order_header_key IN (SELECT order_header_key FROM yantra_owner.yfs_order_hold_type WHERE hold_type = 'TRANSITION_PAY_HOLD' AND status = '1100') " +
                        "AND ol.ordered_qty > 0 AND ors.status_quantity > 0 AND s.process_type_key = 'ORDER_FULFILLMENT'";
                
                ResultSet resultSet = statement.executeQuery(query);

                // Process query results
                while (resultSet.next()) {
                    String orderHeaderKey = resultSet.getString("order_header_key");
                    // Write to CSV
                    writer.write(orderHeaderKey + "\n");
                }
            }

            // Flush and close the writer to ensure all data is written to the file
            writer.flush();
            writer.close();

            // Print row count of order_header_keys.csv file excluding header
            long rowCount = Files.lines(Paths.get(outputFile)).skip(1).count();
            System.out.println("Row count of release_order_header_keys after removing the duplicates: " + rowCount);

            System.out.println("Query results have been written to release_order_header_keys.csv");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close(); // Close the connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private static Set<String> readOrderNumbers(String orderNoFile) {
        Set<String> orderNumbers = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(orderNoFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                orderNumbers.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderNumbers;
    }
    
    private static Document createChangeOrderDocument(String orderHeaderKey) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document changeOrderdoc = docBuilder.newDocument();
        changeOrderdoc.setXmlStandalone(true); // Exclude XML declaration
        
        Element orderElement = changeOrderdoc.createElement("Order");
        orderElement.setAttribute("Action", "MODIFY");
        orderElement.setAttribute("OrderHeaderKey", orderHeaderKey);
        changeOrderdoc.appendChild(orderElement);

        Element orderHoldTypes = changeOrderdoc.createElement("OrderHoldTypes");
        orderElement.appendChild(orderHoldTypes);

        Element orderHoldType = changeOrderdoc.createElement("OrderHoldType");
        orderHoldType.setAttribute("HoldType", "TRANSITION_PAY_HOLD");
        orderHoldType.setAttribute("ReasonText", "Release LRC");
        orderHoldType.setAttribute("Status", "1300");
        orderHoldTypes.appendChild(orderHoldType);

        return changeOrderdoc;
    }
    
    private static String documentToString(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Exclude XML declaration
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
    
    private static void triggerChangeOrder(Document changeOrderdoc) {
        try {
            if (api != null && env != null) {
                api.invoke(env,"changeOrder", changeOrderdoc); //-->used for api
                logger.info("API hit success");
            } else {
                logger.error("API or Environment is not initialized properly.");
            }
        } catch (YFSException | IOException e) {
            logger.error("Error while triggering multiApi: " + e.getMessage());
        }
    }
    
    private static void loadProperties() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(PROD_PROPERTIES_PATH));
            String envURL = properties.getProperty("EnvURL");
            logger.debug("Connected to: " + envURL);

            HashMap<String, String> envProps = new HashMap<>();
            envProps.put("yif.httpapi.url", envURL);

            String connProtocol = "HTTPS"; // Assuming HTTP, change if needed
            api = YIFClientFactory.getInstance().getApi(connProtocol, envProps);

            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = fac.newDocumentBuilder();
            Document environmentDoc = docBuilder.newDocument();
            Element envElement = environmentDoc.createElement("YFSEnvironment");
            envElement.setAttribute("userId", userId);
            envElement.setAttribute("progId", serviceRequestNo);
            environmentDoc.appendChild(envElement);
            env = api.createEnvironment(environmentDoc);
        } catch (Exception e) {
            logger.error("Exception while connecting to application: " + e.getMessage());
            System.exit(1);
        }
    }
}

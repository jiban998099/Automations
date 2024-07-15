package com.main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator; // Import added for Log4j configuration
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.cts.sterling.custom.accelerators.util.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Jiban
 */
public class TriggerRTAM {
    private static final Logger logger = Logger.getLogger(TriggerRTAM.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Service&APIhit\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Service&APIhit\\Production.properties";
    private static int itemCount = 0; // Counter to keep track of the number of items processed
    private static int successCount = 0; // Counter to keep track of successful RTAM triggers
    private static int failureCount = 0; // Counter to keep track of failed RTAM triggers
    private static final String link = "https://supplychain-elk.wsgc.com/goto/25172611ac1a19fd5348d9b3a9ccceea?security_tenant=global";
    public static final String modifyProgId = "SERREQ0757191";
    public static final String userId = "jpradhan";
    public static final String organizationCode = "PK";
    public static final String marketCode = "CAN";

    public static void main(String[] args) {
        // Configure Log4j
        PropertyConfigurator.configure(LOG4J_CONFIG_FILE);

        System.out.println("link to validate --> " + link);

        try {
            loadProperties();
            List<String> itemIds = readItemIdsFromFile("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RTAM_input.txt");
            if (itemIds.isEmpty()) {
                logger.warn("No item IDs found in the file.");
            } else {
                for (String itemId : itemIds) {
                    triggerRTAM(itemId);
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        } finally {
            printCounts(); // Print counts after processing
        }
    }

    private static List<String> readItemIdsFromFile(String filePath) throws IOException {
        List<String> itemIds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                itemIds.add(line.trim());
            }
        }
        return itemIds;
    }

    private static void triggerRTAM(String itemId) {
        try {
            itemCount++; // Increment the item count
            Document checkInventoryDoc = createCheckInventoryDocument(itemId);
            logger.debug("Input XML for item ID " + itemId + ": " + documentToString(checkInventoryDoc));
            publishRTAMTrigger(checkInventoryDoc);
            successCount++; // Increment success count upon successful trigger
            System.out.println("Item " + itemCount + ": RTAM triggered successfully for item ID: " + itemId);
        } catch (Exception e) {
            failureCount++; // Increment failure count upon exception
            logger.error("Error while triggering RTAM for item ID " + itemId + ": " + e.getMessage());
        }
    }

    private static Document createCheckInventoryDocument(String itemId) throws ParserConfigurationException, YFSException, RemoteException {
        // Create a new document for the CheckInventoryRequest
        Document checkInventoryDoc = XMLUtil.createDocument("CheckInventoryRequest");
        Element checkInventoryRequest = checkInventoryDoc.getDocumentElement();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String currentDateTime = now.format(isoFormatter);

        checkInventoryRequest.setAttribute("EnvironmentIdentifier", "prod");
        checkInventoryRequest.setAttribute("RegionIdentifier", "");
        checkInventoryRequest.setAttribute("RoutingSpecification", "");
        checkInventoryRequest.setAttribute("SourceSystemIdentifier", "pt3prdrk");
        checkInventoryRequest.setAttribute("TestingMode", "false");
        checkInventoryRequest.setAttribute("TransactionId", "pt3prdrk/78cf9ab73cad4afeaff489e7310ff492");
        checkInventoryRequest.setAttribute("TransactionTime", currentDateTime);

        // Create MonitorItemAvailabilityList
        Element monitorItemAvailabilityList = checkInventoryDoc.createElement("MonitorItemAvailabilityList");
        checkInventoryRequest.appendChild(monitorItemAvailabilityList);
        // Create MonitorItemAvailability with the required attributes
        Element monitorItemAvailability = checkInventoryDoc.createElement("MonitorItemAvailability");
        monitorItemAvailability.setAttribute("ItemID", itemId);
        monitorItemAvailability.setAttribute("OrganizationCode", organizationCode);
        monitorItemAvailability.setAttribute("UnitOfMeasure", "EACH");
        monitorItemAvailability.setAttribute("ProductClass", "");
        monitorItemAvailability.setAttribute("LineId", "1");
        monitorItemAvailability.setAttribute("MarketCode", marketCode);
        // Append the MonitorItemAvailability to the list
        monitorItemAvailabilityList.appendChild(monitorItemAvailability);

        return checkInventoryDoc;
    }

    private static String documentToString(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Exclude XML declaration
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    private static void publishRTAMTrigger(Document checkInventoryDoc) {
        try {
            if (api != null && env != null) {
                api.executeFlow(env, "CheckInventoryRequestForATP", checkInventoryDoc); ////-->used for service
                //api.invoke(env,"modifycache", cachedGroupsdoc); -->used for api
                logger.info("API hit success");
            } else {
                logger.error("API or Environment is not initialized properly.");
            }
        } catch (YFSException | IOException e) {
            logger.error("Error while publishing RTAM trigger: " + e.getMessage());
            e.printStackTrace();
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
            envElement.setAttribute("progId", modifyProgId);
            environmentDoc.appendChild(envElement);
            env = api.createEnvironment(environmentDoc);
        } catch (Exception e) {
            logger.error("Exception while connecting to application: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printCounts() {
    	System.out.println("*********************************************************");
    	System.out.println("*********************************************************");
        System.out.println("Total items processed: " + itemCount);
        System.out.println("RTAM triggered successfully count: " + successCount);
        System.out.println("RTAM triggered failed count: " + failureCount);
        System.out.println("*********************************************************");
        System.out.println("*********************************************************");
    }
}

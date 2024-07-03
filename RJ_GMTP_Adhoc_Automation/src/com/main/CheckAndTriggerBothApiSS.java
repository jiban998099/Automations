package com.main;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
/**
 * @author Jiban
 */
public class CheckAndTriggerBothApiSS { 
    
    private static final Logger logger = Logger.getLogger(CheckAndTriggerBothApiSS.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\RJ_GMTP_Adhoc_Automation\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\RJ_GMTP_Adhoc_Automation\\Production.properties";
    public static final String serviceRequestNo = "SERREQ0751087";
    public static final String userId = "jpradhan";

    public static void main(String[] args) {
        
        int csvCount = 0; // Counter for CSV rows
        
        configureLogging();
        
        try {
            // Read values from CSV file
            FileInputStream file = new FileInputStream(new File("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\bothAPIDocs\\BothAPISSAvlDateCalc.csv"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            
            String line;
            boolean headerSkipped = false;
            
            // Iterate over each line in CSV
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip header line
                }
                
                // Split line by comma into fields
                String[] fields = line.split(",");
                
                // Extract values from CSV fields
                String orderNo = fields[0].trim();
                String rmsSku = fields[1].trim();
                String updatedCustDelDate = fields[2].trim();
                String currentPromiseDate = fields[3].trim();
                String currentPromiseEndDate = fields[4].trim();
                String availableDate = fields[5].trim();
                String orderHeaderKey = fields[6].trim();
                String orderLineKey = fields[7].trim();
                String productLine = fields[8].trim();
                
                // Generate XML document
                Document multiApiDoc = createMultiApiDocument(orderNo, rmsSku, updatedCustDelDate, currentPromiseDate, currentPromiseEndDate, availableDate, orderHeaderKey, orderLineKey, productLine);
                
                // Increment CSV count
                csvCount++;
                
                // Print XML count
                System.out.println("Preparing XML " + csvCount);
                
                // Print the generated XML in the console
                printXmlDocument(multiApiDoc);
                
                try {
                    loadProperties();
                    
                    // Wait for 1 second
                    Thread.sleep(1000);
                    
                    // Trigger API
                    triggerAPI(orderNo, rmsSku, updatedCustDelDate, currentPromiseDate, currentPromiseEndDate, availableDate, orderHeaderKey, orderLineKey, productLine);
                
                } catch (Exception e) {
                    logger.error("An error occurred: " + e.getMessage());
                }
            }
            
            reader.close();
            file.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An error occurred: " + e.getMessage());
        }
        
        // Print total CSV count
        System.out.println("Total CSV Rows Processed: " + csvCount);
        
    }
    
    private static void triggerAPI(String orderNo, String rmsSku, String updatedCustDelDate, String currentPromiseDate, String currentPromiseEndDate, String availableDate, String orderHeaderKey, String orderLineKey, String productLine) {
        try {
            Document multiApiDoc = createMultiApiDocument(orderNo, rmsSku, updatedCustDelDate, currentPromiseDate, currentPromiseEndDate, availableDate, orderHeaderKey, orderLineKey, productLine);
            logger.debug("Input XML: " + documentToString(multiApiDoc));
            
            //bothApiTrigger(multiApiDoc);
        } catch (Exception e) {
            logger.error("Error triggering multiApi: " + e.getMessage());
        }
    }
    
    private static String documentToString(Document doc) {
        try {
            // Create a Transformer
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (TransformerException e) {
            logger.error("Error converting Document to String: " + e.getMessage());
            return null;
        }
    }
    
    // Configure Log4j
    private static void configureLogging() {
        PropertyConfigurator.configure(LOG4J_CONFIG_FILE);
    }
    
    private static Document createMultiApiDocument(String orderNo, String rmsSku, String updatedCustDelDate, String currentPromiseDate, String currentPromiseEndDate, String availableDate, String orderHeaderKey, String orderLineKey, String productLine) throws Exception {
        
        // Create a new document for the MultiApi
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document multiApiDoc = builder.newDocument();
        Element multiApiElement = multiApiDoc.createElement("MultiApi");
        multiApiDoc.appendChild(multiApiElement);
        
        // Create first API element for UpdatePromiseDateOnOrder
        Element apiElement1 = multiApiDoc.createElement("API");
        apiElement1.setAttribute("FlowName", "UpdatePromiseDateOnOrder");
        
        // Create Input element for first API
        Element inputElement1 = multiApiDoc.createElement("Input");
        
        // Create Order element for first API
        Element orderElement = multiApiDoc.createElement("Order");
        orderElement.setAttribute("OrderHeaderKey", orderHeaderKey);
        orderElement.setAttribute("Override", "Y");
        
        // Create OrderLines element for first API
        Element orderLinesElement = multiApiDoc.createElement("OrderLines");
        
        // Create OrderLine element for first API
        Element orderLineElement = multiApiDoc.createElement("OrderLine");
        orderLineElement.setAttribute("OrderLineKey", orderLineKey);
        
        // Create OrderDates element for first API
        Element orderDatesElement = multiApiDoc.createElement("OrderDates");
        
        // Create OrderDate elements for first API
        Element orderDate1Element = multiApiDoc.createElement("OrderDate");
        orderDate1Element.setAttribute("DateTypeId", "CurrentPromiseDate");
        orderDate1Element.setAttribute("ExpectedDate", currentPromiseDate);
        
        Element orderDate2Element = multiApiDoc.createElement("OrderDate");
        orderDate2Element.setAttribute("DateTypeId", "CurrentPromiseEndDate");
        orderDate2Element.setAttribute("ExpectedDate", currentPromiseEndDate);
        
        // Create Notes element for first API
        Element notesElement1 = multiApiDoc.createElement("Notes");
        
        // Create Note element for first API
        Element noteElement1 = multiApiDoc.createElement("Note");
        noteElement1.setAttribute("NoteText", serviceRequestNo);
        
        // Construct the XML structure for first API
        multiApiElement.appendChild(apiElement1);
        apiElement1.appendChild(inputElement1);
        inputElement1.appendChild(orderElement);
        orderElement.appendChild(orderLinesElement);
        orderLinesElement.appendChild(orderLineElement);
        orderLineElement.appendChild(orderDatesElement);
        orderDatesElement.appendChild(orderDate1Element);
        orderDatesElement.appendChild(orderDate2Element);
        orderElement.appendChild(notesElement1);
        notesElement1.appendChild(noteElement1);
        
        // Create second API element for UpdateWSIPromiseDate
        Element apiElement2 = multiApiDoc.createElement("API");
        apiElement2.setAttribute("FlowName", "UpdateWSIPromiseDate");
        
        // Create Input element for second API
        Element inputElement2 = multiApiDoc.createElement("Input");
        
        // Create WSIPromiseDateMonitor element for second API
        Element wsiPromiseDateMonitorElement = multiApiDoc.createElement("WSIPromiseDateMonitor");
        wsiPromiseDateMonitorElement.setAttribute("CurrentPromiseStartDate", currentPromiseDate);
        wsiPromiseDateMonitorElement.setAttribute("CurrentPromiseEndDate", currentPromiseEndDate);
        wsiPromiseDateMonitorElement.setAttribute("AvailableDate", availableDate);
        wsiPromiseDateMonitorElement.setAttribute("NextAction", "RECALCULATION_REQUIRED");
        wsiPromiseDateMonitorElement.setAttribute("OrderLineKey", orderLineKey);
        
        // Construct the XML structure for second API
        multiApiElement.appendChild(apiElement2);
        apiElement2.appendChild(inputElement2);
        inputElement2.appendChild(wsiPromiseDateMonitorElement);
        
        return multiApiDoc;
    }
    
    private static void printXmlDocument(Document doc) {
        try {
            // Remove the XML declaration
            doc.setXmlStandalone(true);
            
            // Use a Transformer to print the XML document
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); // No indentation
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
            // Print the XML in a single line
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            
            // Remove line breaks
            String xmlString = writer.toString().replaceAll("\\r\\n|\\r|\\n", "");
            
            System.out.println(xmlString);
        } catch (Exception e) {
            logger.error("An error occurred while printing XML document: " + e.getMessage());
        }
    }
    
    private static void bothApiTrigger(Document multiApiDoc) {
        try {
            if (api != null && env != null) {
                api.invoke(env, "multiApi", multiApiDoc); //-->used for api
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
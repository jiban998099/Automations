package com.main;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
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
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * @author Jiban
 */
public class CheckAndTriggerTopApiSS {

    private static final Logger logger = Logger.getLogger(CheckAndTriggerTopApiSS.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\RJ_GMTP_Adhoc_Automation\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\RJ_GMTP_Adhoc_Automation\\Production.properties";
    public static final String serviceRequestNo = "SERREQ0745903";
    public static final String userId = "jpradhan";

    public static void main(String[] args) {

        int xmlCount = 0; // Counter for XML documents

        configureLogging();

        try {
            // Read values from CSV file
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\RJ_GMTP_Adhoc\\topAPIDocs\\TopAPISSEndDateCalc.csv")));
            String line;

            // Skip header if exists
            reader.readLine(); // Assuming the first line is a header

            while ((line = reader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                if (tokenizer.countTokens() >= 8) {
                    String orderNo = tokenizer.nextToken().trim();
                    String rmsSku = tokenizer.nextToken().trim();
                    String updatedCustDelDate = tokenizer.nextToken().trim();
                    String currentPromiseDate = tokenizer.nextToken().trim();
                    String currentPromiseEndDate = tokenizer.nextToken().trim();
                    String orderHeaderKey = tokenizer.nextToken().trim();
                    String orderLineKey = tokenizer.nextToken().trim();
                    String productLine = tokenizer.nextToken().trim();

                    // Generate XML document
                    Document multiApiDoc = createMultiApiDocument(orderNo, rmsSku, updatedCustDelDate,
                            currentPromiseDate, currentPromiseEndDate, orderHeaderKey, orderLineKey, productLine);

                    // Increment XML count
                    xmlCount++;

                    // Print XML count
                    System.out.println("Printing XML " + xmlCount);

                    // Print the generated XML in the console
                    printXmlDocument(multiApiDoc);

                    try {
                        loadProperties();

                        // Wait for 1 second
                        Thread.sleep(1000);

                        triggerAPI(orderNo, rmsSku, updatedCustDelDate, currentPromiseDate, currentPromiseEndDate,
                                orderHeaderKey, orderLineKey, productLine); // Call triggerAPI without passing any
                                                                               // arguments

                    } catch (Exception e) {
                        logger.error("An error occurred: " + e.getMessage());
                    }
                } else {
                    logger.warn("Skipping line: " + line + ". Insufficient data tokens.");
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An error occurred: " + e.getMessage());
        }

        // Print total XML count
        System.out.println("Total XMLs Triggered: " + xmlCount);

    }

    private static void triggerAPI(String orderNo, String rmsSku, String updatedCustDelDate,
            String currentPromiseDate, String currentPromiseEndDate, String orderHeaderKey, String orderLineKey,
            String productLine) {
        try {
            Document multiApiDoc = createMultiApiDocument(orderNo, rmsSku, updatedCustDelDate, currentPromiseDate,
                    currentPromiseEndDate, orderHeaderKey, orderLineKey, productLine);
            logger.debug("Input XML: " + documentToString(multiApiDoc));

            //topApiTrigger(multiApiDoc);
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
        } catch (Exception e) {
            logger.error("Error converting Document to String: " + e.getMessage());
            return null;
        }
    }

    // Configure Log4j
    private static void configureLogging() {
        PropertyConfigurator.configure(LOG4J_CONFIG_FILE);
    }

    private static Document createMultiApiDocument(String orderNo, String rmsSku, String updatedCustDelDate,
            String currentPromiseDate, String currentPromiseEndDate, String orderHeaderKey, String orderLineKey,
            String productLine) {
        try {
            // Create a new document for the MultiApi
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document multiApiDoc = builder.newDocument();
            Element multiApiElement = multiApiDoc.createElement("MultiApi");
            multiApiDoc.appendChild(multiApiElement);

            // Create API element
            Element apiElement = multiApiDoc.createElement("API");
            apiElement.setAttribute("FlowName", "UpdatePromiseDateOnOrder");

            // Create Input element
            Element inputElement = multiApiDoc.createElement("Input");

            // Create Order element
            Element orderElement = multiApiDoc.createElement("Order");
            orderElement.setAttribute("OrderHeaderKey", orderHeaderKey);
            orderElement.setAttribute("Override", "Y");

            // Create OrderLines element
            Element orderLinesElement = multiApiDoc.createElement("OrderLines");

            // Create OrderLine element
            Element orderLineElement = multiApiDoc.createElement("OrderLine");
            orderLineElement.setAttribute("OrderLineKey", orderLineKey);

            // Create OrderDates element
            Element orderDatesElement = multiApiDoc.createElement("OrderDates");

            // Create OrderDate elements
            Element orderDate1Element = multiApiDoc.createElement("OrderDate");
            orderDate1Element.setAttribute("DateTypeId", "CurrentPromiseDate");
            orderDate1Element.setAttribute("ExpectedDate", currentPromiseDate);

            Element orderDate2Element = multiApiDoc.createElement("OrderDate");
            orderDate2Element.setAttribute("DateTypeId", "CurrentPromiseEndDate");
            orderDate2Element.setAttribute("ExpectedDate", currentPromiseEndDate);

            // Create Notes element
            Element notesElement = multiApiDoc.createElement("Notes");

            // Create Note element
            Element noteElement = multiApiDoc.createElement("Note");
            noteElement.setAttribute("NoteText", serviceRequestNo);

            // Construct the XML structure
            multiApiElement.appendChild(apiElement);
            apiElement.appendChild(inputElement);
            inputElement.appendChild(orderElement);
            orderElement.appendChild(orderLinesElement);
            orderLinesElement.appendChild(orderLineElement);
            orderLineElement.appendChild(orderDatesElement);
            orderDatesElement.appendChild(orderDate1Element);
            orderDatesElement.appendChild(orderDate2Element);
            orderElement.appendChild(notesElement);
            notesElement.appendChild(noteElement);

            return multiApiDoc;
        } catch (Exception e) {
            logger.error("Error creating MultiApi XML document: " + e.getMessage());
            return null;
        }
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

    private static void topApiTrigger(Document multiApiDoc) {
        try {
            if (api != null && env != null) {
                api.invoke(env, "multiApi", multiApiDoc); // -->used for api
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
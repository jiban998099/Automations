package com.main;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
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
public class CheckAndTriggerWISMOPOD { 
    
    private static final Logger logger = Logger.getLogger(CheckAndTriggerWISMOPOD.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Properties\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Properties\\Production.properties";
    private static final String INPUT_CSV_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\delivered_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv";
    private static final String FAILED_ORDERS_CSV_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\failed_orders_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv";
    private static final String CSV_SEPARATOR = ",";

    public static void main(String[] args) {
        
        int csvCount = 0;
        int successfulCount = 0;
        int failureCount = 0;
        List<String[]> failedOrders = new ArrayList<>();
        
        configureLogging();
        
        try {
            FileInputStream file = new FileInputStream(new File(INPUT_CSV_FILE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            
            String line;
            boolean headerSkipped = false;
            
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; 
                }
                
                String[] fields = line.split(",");
                
                String orderNo = fields[0].trim();
                String primeLineNo = fields[1].trim();
                String subLineNo = fields[2].trim();
                String shipmentNo = fields[3].trim();
                String shipmentLineKey = fields[4].trim();
                String shipmentKey = fields[5].trim();
                String trackingNo = fields[6].trim();
                String containerNo = fields[7].trim();
                String itemID = fields[8].trim();
                String enterpriseCode = fields[9].trim();
                String quantity = fields[10].trim();
                String documentType = fields[11].trim();
                String statusDate = fields[12].trim();
                
                Document docInputToWISMOPOD = createWISMOPODDocument(orderNo, primeLineNo, subLineNo, shipmentNo, shipmentLineKey, shipmentKey, trackingNo, containerNo, itemID, enterpriseCode, quantity, documentType, statusDate);
                
                csvCount++;
                
                System.out.println("Preparing XML " + csvCount);
                
                printXmlDocument(docInputToWISMOPOD);
                
                try {
                    loadProperties();
                    
                    Thread.sleep(1000);
                    
                    hitService(orderNo, primeLineNo, subLineNo, shipmentNo, shipmentLineKey, shipmentKey, trackingNo, containerNo, itemID, enterpriseCode, quantity, documentType, statusDate);
                    
                    successfulCount++;
                    
                } catch (Exception e) {
                    failureCount++;
                    logger.error("An error occurred for OrderNo " + orderNo + ": " + e.getMessage());
                    
                    failedOrders.add(fields);
                }
            }
            
            writeFailedOrdersToCSV(failedOrders);
            
            reader.close();
            file.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An error occurred: " + e.getMessage());
        }
        
		System.out.println("*********************************************************");
		System.out.println("*********************************************************");
        System.out.println("Total xmls triggered: " + csvCount);
        System.out.println("Count of XMLs successfully triggered: " + successfulCount);
        System.out.println("Count of XMLs failed: " + failureCount);
		System.out.println("Failed orders have been written to:: failed_orders_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
		System.out.println("*********************************************************");
		System.out.println("*********************************************************");
    }
    
    private static void hitService(String orderNo, String primeLineNo, String subLineNo, String shipmentNo, String shipmentLineKey, String shipmentKey, String trackingNo, String containerNo, String itemID, String enterpriseCode, String quantity, String documentType, String statusDate) {
        try {
            Document docInputToWISMOPOD = createWISMOPODDocument(orderNo, primeLineNo, subLineNo, shipmentNo, shipmentLineKey, shipmentKey, trackingNo, containerNo, itemID, enterpriseCode, quantity, documentType, statusDate);
            logger.debug("Input XML: " + documentToString(docInputToWISMOPOD));
            
            hitWISMOPODService(docInputToWISMOPOD);
            
        } catch (Exception e) {
            logger.error("Error triggering WISMOProcessPOD for OrderNo " + orderNo + ": " + e.getMessage());
            throw new RuntimeException("Error triggering WISMOProcessPOD", e); 
        }
    }
    
    private static String documentToString(Document doc) {
        try {
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
    
    private static void configureLogging() {
        PropertyConfigurator.configure(LOG4J_CONFIG_FILE);
    }
    
    private static Document createWISMOPODDocument(String orderNo, String primeLineNo, String subLineNo, String shipmentNo, String shipmentLineKey, String shipmentKey, String trackingNo, String containerNo, String itemID, String enterpriseCode, String quantity, String documentType, String statusDate) throws Exception {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document docInputToWISMOPOD = builder.newDocument();
        
        // Create root element <Shipment>
        Element shipmentElement = docInputToWISMOPOD.createElement("Shipment");
        shipmentElement.setAttribute("DocumentType", documentType);
        shipmentElement.setAttribute("EnterpriseCode", enterpriseCode);
        shipmentElement.setAttribute("OrderNo", orderNo);
        shipmentElement.setAttribute("StatusDate", statusDate);
        docInputToWISMOPOD.appendChild(shipmentElement);
        
        // Create <Containers> element
        Element containersElement = docInputToWISMOPOD.createElement("Containers");
        shipmentElement.appendChild(containersElement);
        
        // Create <Container> element
        Element containerElement = docInputToWISMOPOD.createElement("Container");
        containerElement.setAttribute("ContainerNo", containerNo);
        containersElement.appendChild(containerElement);
        
        // Create <ContainerDetails> element
        Element containerDetailsElement = docInputToWISMOPOD.createElement("ContainerDetails");
        containerElement.appendChild(containerDetailsElement);
        
        // Create <ContainerDetail> element
        Element containerDetailElement = docInputToWISMOPOD.createElement("ContainerDetail");
        containerDetailElement.setAttribute("ItemID", itemID);
        containerDetailElement.setAttribute("Quantity", quantity);
        containerDetailsElement.appendChild(containerDetailElement);
        
        // Create <ShipmentLine> element within <ContainerDetail>
        Element shipmentLineElement = docInputToWISMOPOD.createElement("ShipmentLine");
        shipmentLineElement.setAttribute("OrderNo", orderNo);
        shipmentLineElement.setAttribute("PrimeLineNo", primeLineNo);
        shipmentLineElement.setAttribute("SubLineNo", subLineNo);
        containerDetailElement.appendChild(shipmentLineElement);
        
        return docInputToWISMOPOD;
    }
    
    private static void printXmlDocument(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no"); 
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            
            String xmlString = writer.toString().replaceAll("\\r\\n|\\r|\\n", "");
            
            System.out.println(xmlString);
        } catch (Exception e) {
            logger.error("An error occurred while printing XML document: " + e.getMessage());
        }
    }
    
    private static void hitWISMOPODService(Document docInputToWISMOPOD) {
        try {
            if (api != null && env != null) {
                api.executeFlow(env, "WISMOProcessPOD", docInputToWISMOPOD); 
                logger.info("Service hit success");
            } else {
                logger.error("Service or Environment is not initialized properly.");
            }
        } catch (YFSException | IOException e) {
            logger.error("Error while triggering WISMOProcessPOD: " + e.getMessage());
            throw new RuntimeException("Error while triggering WISMOProcessPOD", e); 
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
            String connProtocol = "HTTP";
            api = YIFClientFactory.getInstance().getApi(connProtocol, envProps);
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = fac.newDocumentBuilder();
            Document environmentDoc = docBuilder.newDocument();
            Element envElement = environmentDoc.createElement("YFSEnvironment");
            envElement.setAttribute("userId", "admin");
            envElement.setAttribute("progId", "ULSS_POD");
            environmentDoc.appendChild(envElement);
            env = api.createEnvironment(environmentDoc);
        } catch (Exception e) {
            logger.error("Exception while connecting to application: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void writeFailedOrdersToCSV(List<String[]> failedOrders) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FAILED_ORDERS_CSV_FILE))) {
            writer.write("OrderNo,PrimeLineNo,SubLineNo,ShipmentNo,ShipmentLineKey,ShipmentKey,TrackingNo,ContainerNo,ItemID,EnterpriseCode,Quantity,DocumentType,StatusDate\n");
            for (String[] fields : failedOrders) {
                writer.write(String.join(CSV_SEPARATOR, fields) + "\n");
            }
            logger.info("Failed orders written to: " + FAILED_ORDERS_CSV_FILE);
        } catch (IOException e) {
            logger.error("Error writing failed orders to CSV: " + e.getMessage());
        }
    }
}

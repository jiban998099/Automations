package com.main;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
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
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class ModifyCacheTrigger {
    private static final Logger logger = Logger.getLogger(ModifyCacheTrigger.class);
    private static YFSEnvironment env = null;
    private static YIFApi api = null;
    private static Properties properties = null;
    private static final String LOG4J_CONFIG_FILE = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Service&APIhit\\log4j.properties";
    private static final String PROD_PROPERTIES_PATH = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\Service&APIhit\\Production.properties";

    public static void main(String[] args) {
        // Configure Log4j
        PropertyConfigurator.configure(LOG4J_CONFIG_FILE);
        
        try {
            loadProperties();
            triggerModifyCache(); // Call triggerModifyCache without passing any arguments
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
        }
    }

    private static void triggerModifyCache() {
        try {
            Document cachedGroupsdoc = createCachedGroupsDocument();
            logger.debug("Input XML: " + documentToString(cachedGroupsdoc));
            modifyCacheTrigger(cachedGroupsdoc);
        } catch (Exception e) {
            logger.error("Error triggering cache modification: " + e.getMessage());
        }
    }

    private static Document createCachedGroupsDocument() throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document cachedGroupsdoc = docBuilder.newDocument();
        cachedGroupsdoc.setXmlStandalone(true); // Exclude XML declaration
        
        Element cachedGroups = cachedGroupsdoc.createElement("CachedGroups");
        cachedGroupsdoc.appendChild(cachedGroups);

        Element cachedGroup = cachedGroupsdoc.createElement("CachedGroup");
        cachedGroups.appendChild(cachedGroup);
        cachedGroup.setAttribute("Action", "CLEAR");
        cachedGroup.setAttribute("Name", "Database");

        return cachedGroupsdoc;
    }
    private static String documentToString(Document document) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // Exclude XML declaration
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
	
	private static void modifyCacheTrigger(Document cachedGroupsdoc) throws YFSException, RemoteException { 
		
		if (api != null && env != null) {
			api.invoke(env, "modifyCache", cachedGroupsdoc); ////-->used for api
			//api.executeFlow(env,"modifycache", cachedGroupsdoc); -->used for service
			//Log success message 
			logger.info("API hit success"); 
		} else {
			logger.error("API or Environment is not initialized properly."); 
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
            envElement.setAttribute("userId", "admin");
            envElement.setAttribute("progId", "modifycache");
            environmentDoc.appendChild(envElement);
            env = api.createEnvironment(environmentDoc);
        } catch (Exception e) {
            logger.error("Exception while connecting to application: " + e.getMessage());
            System.exit(1);
        }
    }
}

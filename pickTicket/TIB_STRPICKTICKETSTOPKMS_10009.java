import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cts.sterling.custom.accelerators.util.XMLUtil;
import db.DBConnection;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;
/**
 * @author Rajdeep Chakraborty
 */
public class TIB_STRPICKTICKETSTOPKMS_10009 
{
	private static YFSEnvironment env = null;
	private static YIFApi callapi = null;
	private final static Logger logger = Logger.getLogger(TIB_STRPICKTICKETSTOPKMS_10009.class.getName());
	private static int FIELD_LENGTH=49;
	public static void main(String[] args) 
	{
		loadingPropertiesFile();
		Connection cleConnection = null;
		cleConnection = DBConnection.getCLEDBConnection();
		logger.info("got the connection...");
		Statement cleStmt = null;
		try 
		{

			cleStmt = cleConnection.createStatement();
			updatepersoninfo(cleStmt);

		} catch (Exception e) 
		{

			e.printStackTrace();
		}
		finally 
		{
			try 
			{

				if (cleStmt != null) 
				{
					cleStmt.close();
				}

				cleConnection.close();
			} 
			catch (SQLException e)
			{

				e.printStackTrace();
			}

		}
		System.out.println("Done");
	}

	private static void updatepersoninfo(Statement cleStmt)
	{
		try
		{
			int count=0;
			List<String> payloadlists = new ArrayList<String>();
			String payloadReading = "";
			payloadlists = getDataFromCLE(cleStmt);
			System.out.println("Total data fetched="+payloadlists.size());
			for (int i = 0; i <payloadlists.size() ; i++) ///reduce size payloadlists.size()
			{
				try
				{
					
					payloadReading = payloadlists.get(i).trim();
					//System.out.println(payloadReading);
					YFCDocument document2 = YFCDocument.getDocumentFor(payloadReading);
					Document doc = document2.getDocument();
					//System.out.println(XmlUtils.getString(doc.getDocumentElement()));
					Element rootEle = doc.getDocumentElement();
					Element elePersonInfoBillTo=XMLUtil.getFirstElementByXPath(doc, "WorkOrder/Order/PersonInfoBillTo");
					Element elePersonInfo = XMLUtil.getFirstElementByXPath(doc, "WorkOrder/PersonInfo");
					int companyFlag=companyCorrection(doc,rootEle,elePersonInfo,elePersonInfoBillTo);
					if(companyFlag==1)
					{
						System.out.println("Api hit success");
						count ++;
					}
					else if(companyFlag==-1)
					{
						System.out.println("Api hit failed");
					}
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
				System.out.println("*****************************************************");
			}
			System.out.println("Payloads corrected="+count);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public static int companyCorrection(Document doc,Element rootEle,Element e1,Element e2)
	{
		int flag=0;
		int p=0;
		Element on=null;
		int modified=0;
		try 
		{
			 on = XMLUtil.getFirstElementByXPath(doc, "WorkOrder");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-----------------------------------------------------------");
		System.out.println("Order_No="+XmlUtils.getAttribute(on, "OrderNo").trim());
//////////////////////////////////////////////--WorkOrder/PersonInfo--//////////////////////////////////////
		System.out.println("----WorkOrder/Order//PersonInfoBillTo----");
		String company_name = XmlUtils.getAttribute(e2, "Company");
		if(company_name!=null)
		{
			company_name=company_name.trim();
			if (company_name.length() > FIELD_LENGTH) 
			{
				System.out.println("before="+company_name);
				company_name=company_name.substring(0, FIELD_LENGTH);
				p++;
				modified++;
			}
			if(p>0)
			{
				e2.setAttribute("Company", company_name);
				rootEle.appendChild(e2);
				System.out.println("after="+company_name);
			}
		}
		else
		{
			System.out.println("null string encountered");
		}
		
		
//////////////////////////////////////--WorkOrder/Order/PersonInfoBillTo--//////////////////////////////////
		p=0;
		System.out.println("----WorkOrder/PersonInfo----");
		company_name = XmlUtils.getAttribute(e1, "Company");
		if(company_name!=null)
		{
			company_name=company_name.trim();
			if (company_name.length() > FIELD_LENGTH) 
			{
				System.out.println("before="+company_name);
				company_name=company_name.substring(0, FIELD_LENGTH);
				p++;
				modified++;
			}
			if(p>0)
			{
				e1.setAttribute("Company", company_name);
				rootEle.appendChild(e1);
				System.out.println("after="+company_name);
			}
		}
		else
		{
			System.out.println("null string encountered");
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String payload=XmlUtils.getString(doc.getDocumentElement());
		
		///////////////////////////////////Correcting position of PersonInfoBillTo///////////////////////////
		modified=correctPersonInfoBillTo(doc, modified);
		////////////////////////////////////////////////////////////////////////////////////////////////////
		System.out.println("Corrected Payload:\n"+XMLUtil.getString(doc));
		if(modified>0)
		{
			try
			{
				callapi.executeFlow(env, "RePublishPickticketToPKMS", doc);
				System.out.println("API hit");
				flag=1;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				flag=-1;
			}
		}
		else
		{
			System.out.println("Modification not required");
		}
		//System.out.println("-----------------------Output------------------------------------");
		//System.out.println(XMLUtil.getString(doc));
		return flag;
	}
	private static int correctPersonInfoBillTo(Document doc,int modified)
	{
		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression="WorkOrder//PersonInfoBillTo";
		//System.out.println(expression);
		ArrayList<Node>al=new ArrayList<Node>();
		try
		{
			NodeList personInfoBillTo = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			// store WorkOrder/PersonInfoBillTo
			for (int i = 0; i<personInfoBillTo.getLength(); i++) 
			{
				al.add(personInfoBillTo.item(i));
				//System.out.println(nodeToString(personInfoBillTo.item(i)));
			}
			//remove WorkOrder/PersonInfoBillTo
			for (int i = 0; i<personInfoBillTo.getLength(); i++) 
			{
				personInfoBillTo.item(i).getParentNode().removeChild(personInfoBillTo.item(i));
			}
			expression="WorkOrder//Order";
			personInfoBillTo = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i<personInfoBillTo.getLength(); i++) 
			{
				//System.out.println(nodeToString(personInfoBillTo.item(i)));
				if(personInfoBillTo.item(i).getParentNode().getNodeName().equals("WorkOrder"))
				{
					Element personInfoBillToEle=(Element)personInfoBillTo.item(i);
					//System.out.println(nodeToString(personInfoBillToEle));
					for(int j=0;j<al.size();j++)
					{
						personInfoBillToEle.appendChild(al.get(j));
						//System.out.println(nodeToString(al.get(j)));
						modified++;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return modified;
	}
	private static List<String> getDataFromCLE(Statement cleStmt) {
		List<String> messagePayloadList = new ArrayList<String>();
		String sqlpayloadValue = "";
		String valuefrompayload = "";
		ResultSet resultSet = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String today=sdf.format(new Date());
		System.out.println("Fetching data....");
		try {
			sqlpayloadValue ="SELECT\r\n" + 
					"   /*+ parallel(a,10) parallel(b,10) parallel(c,10) parallel(d,10) */\r\n" + 
					"   d.payload,\r\n" + 
					"   a.TRANSACTIONID \r\n" + 
					"FROM\r\n" + 
					"   cle_owner.app_exception a,\r\n" + 
					"   cle_owner.cf_interface b,\r\n" + 
					"   cle_owner.cf_exception c,\r\n" + 
					"   cle_owner.app_payloadarchive d \r\n" + 
					"WHERE\r\n" + 
					"   a.interfaceid = b.interfaceid \r\n" + 
					"   AND a.exceptionid = c.exceptionid \r\n" + 
					"   AND a.payloadid = d.payloadid \r\n" + 
					"   AND b.interfacecode = 'STR_PKM.STRPICKTICKETSTOPKMS' \r\n" + 
					"   AND c.exceptioncode = 'TIB_STRPICKTICKETSTOPKMS_10009' \r\n" + 
					"   AND a.statusflag = 'Open' \r\n" + 
					"   AND a.exception_timestamp >= sysdate-30\r\n" + 
					"   and a.exception_timestamp <= sysdate\r\n" + 
					"order by\r\n" + 
					"   2";
			//System.out.println(sqlpayloadValue);
			resultSet = cleStmt.executeQuery(sqlpayloadValue);
			System.out.println("This will test your patience!");
			while (resultSet.next()) 
			{
				valuefrompayload = resultSet.getString(1);
				//System.out.println(resultSet.getString(2));
				
				messagePayloadList.add(valuefrompayload);
			}
			resultSet.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		System.out.println("Data has been fetched");
		return messagePayloadList;
	}
	public static String nodeToString(Node node) throws Exception
	{
	    StringWriter sw = new StringWriter();

	      Transformer t = TransformerFactory.newInstance().newTransformer();
	      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	      t.setOutputProperty(OutputKeys.INDENT, "yes");
	      t.transform(new DOMSource(node), new StreamResult(sw));
	    return sw.toString();
	  }
	/**
	 * Setting up the environment
	 */

	private static void loadingPropertiesFile() {
		String connProtocol = "HTTP";

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\pickTicket\\YANNEW.properties"));
			String envurl = properties.getProperty("ENVURL");
			Map<Object, Object> envproperties = new HashMap<>();
			envproperties.put("yif.httpapi.url", envurl);

			callapi = YIFClientFactory.getInstance().getApi(connProtocol, envproperties);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element ele = doc.createElement("YFSEnvironment");
			ele.setAttribute("userId", "admin");
			ele.setAttribute("progId", "SterlingPickTicketUpdate");
			doc.appendChild(ele);
			env = callapi.createEnvironment(doc);

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (YIFClientCreationException e) {

			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		}
	}
}

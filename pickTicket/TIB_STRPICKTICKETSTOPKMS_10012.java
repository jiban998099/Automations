import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class TIB_STRPICKTICKETSTOPKMS_10012 
{
	private static YFSEnvironment env = null;
	private static YIFApi callapi = null;
	private final static Logger logger = Logger.getLogger(TIB_STRPICKTICKETSTOPKMS_10012.class.getName());
	private static int FIELD_LENGTH=34;
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

	private static void updatepersoninfo(Statement cleStmt) throws IllegalArgumentException, Exception 
	{
		int count=0;
		List<String> payloadlists = new ArrayList<String>();
		String payloadReading = "";
		/*String[] eName;*/
		payloadlists = republishData(cleStmt);
		System.out.println("Total data fetched="+payloadlists.size());
		for (int i = 0; i <payloadlists.size() ; i++) ///reduce size payloadlists.size()
		{
			payloadReading = payloadlists.get(i).trim();
			YFCDocument document2 = YFCDocument.getDocumentFor(payloadReading);
			Document doc = document2.getDocument();
			//System.out.println(XmlUtils.getString(doc.getDocumentElement()));
			Element rootEle = doc.getDocumentElement();
			Element elePersonInfoBillTo=XMLUtil.getFirstElementByXPath(doc, "WorkOrder/Order/PersonInfoBillTo");
			Element elePersonInfo = XMLUtil.getFirstElementByXPath(doc, "WorkOrder/PersonInfo");
			int nameFlag=nameCorrection(doc,rootEle,elePersonInfo,elePersonInfoBillTo);
			if(nameFlag==1)
			{
				System.out.println("Api hit success");
				count ++;
			}
			else
			{
				System.out.println("Api hit failed");
			}
		}
		System.out.println("Payloads corrected="+count);
	}
	public static int nameCorrection(Document doc,Element rootEle,Element e1,Element e2)
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
		System.out.println("----WorkOrder/PersonInfo----");
		String strFirstName = XmlUtils.getAttribute(e1, "FirstName").trim();
		String strMidName=XmlUtils.getAttribute(e1, "MiddleName").trim();
		String strLastName = XmlUtils.getAttribute(e1, "LastName").trim();
		String addressLine1=XmlUtils.getAttribute(e1, "AddressLine1").trim();
		String sampleString = strFirstName+strMidName+strLastName;
		if (sampleString.length() > FIELD_LENGTH) 
		{
			System.out.println(strFirstName);
			System.out.println(strMidName);
			System.out.println(strLastName);
			System.out.println(addressLine1);
			if(strFirstName.length()>FIELD_LENGTH)
			{
				strFirstName=strFirstName.substring(0, FIELD_LENGTH);
				strMidName="";
				strLastName="";
			}
			else if((strFirstName+strMidName).length()>FIELD_LENGTH)
			{
				int fl=strFirstName.length();
				strMidName=strMidName.substring(0,FIELD_LENGTH-fl);
				strLastName="";
			}
			else if((strFirstName+strMidName+strLastName).length()>FIELD_LENGTH)
			{
				int fl=strFirstName.length();
				int ml=strMidName.length();
				strLastName=strLastName.substring(0,FIELD_LENGTH-(fl+ml));
			}
			p++;
			modified++;
		}
		if(addressLine1.length()>FIELD_LENGTH)
		{
			addressLine1=addressLine1.substring(0, FIELD_LENGTH);
			p++;
			modified++;
		}
		if(p>0)
		{
			e1.setAttribute("FirstName", strFirstName);
			e1.setAttribute("MiddleName", strMidName);
			e1.setAttribute("LastName", strLastName);
			e1.setAttribute("AddressLine1", addressLine1);
			rootEle.appendChild(e1);
			System.out.println(strFirstName);
			System.out.println(strMidName);
			System.out.println(strLastName);
			System.out.println(addressLine1);
		}
		
//////////////////////////////////////--WorkOrder/Order/PersonInfoBillTo--//////////////////////////////////
		p=0;
		System.out.println("----WorkOrder/Order/PersonInfoBillTo----");
		strFirstName = XmlUtils.getAttribute(e2, "FirstName").trim();
		strMidName=XmlUtils.getAttribute(e2, "MiddleName").trim();
		strLastName = XmlUtils.getAttribute(e2, "LastName").trim();
		addressLine1=XmlUtils.getAttribute(e2, "AddressLine1").trim();
		System.out.println(strFirstName);
		System.out.println(strMidName);
		System.out.println(strLastName);
		System.out.println(addressLine1);
		sampleString = strFirstName+strMidName+strLastName;
		if (sampleString.length() > FIELD_LENGTH) 
		{
			System.out.println(strFirstName);
			System.out.println(strMidName);
			System.out.println(strLastName);
			System.out.println(addressLine1);
			if(strFirstName.length()>FIELD_LENGTH)
			{
				strFirstName=strFirstName.substring(0, FIELD_LENGTH);
				strMidName="";
				strLastName="";
			}
			else if((strFirstName+strMidName).length()>FIELD_LENGTH)
			{
				int fl=strFirstName.length();
				strMidName=strMidName.substring(0,FIELD_LENGTH-fl);
				strLastName="";
			}
			else if((strFirstName+strMidName+strLastName).length()>FIELD_LENGTH)
			{
				int fl=strFirstName.length();
				int ml=strMidName.length();
				strLastName=strLastName.substring(0,FIELD_LENGTH-(fl+ml));
			}
			p++;
			modified++;
		}
		if(addressLine1.length()>FIELD_LENGTH)
		{
			addressLine1=addressLine1.substring(0, FIELD_LENGTH);
			p++;
			modified++;
		}
		if(p>0)
		{
			e2.setAttribute("FirstName", strFirstName);
			e2.setAttribute("MiddleName", strMidName);
			e2.setAttribute("LastName", strLastName);
			e2.setAttribute("AddressLine1", addressLine1);
			rootEle.appendChild(e2);
			System.out.println(strFirstName);
			System.out.println(strMidName);
			System.out.println(strLastName);
			System.out.println(addressLine1);
		}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String payload=XmlUtils.getString(doc.getDocumentElement());
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
		System.out.println("-----------------------------------------------------------");
		//System.out.println(XMLUtil.getString(doc));
		return flag;
		
	}
	private static List<String> republishData(Statement cleStmt) {
		List<String> messagePayloadList = new ArrayList<String>();
		String sqlpayloadValue = "";
		String valuefrompayload = "";
		ResultSet resultSet = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String today=sdf.format(new Date());
		System.out.println("Fetching data");
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
					"   AND c.exceptioncode = 'TIB_STRPICKTICKETSTOPKMS_10012' \r\n" + 
					"   AND a.statusflag = 'Open' \r\n" + 
					"   AND a.exception_timestamp > sysdate-30\r\n" + 
					"   and a.exception_timestamp <= sysdate\r\n" + 
					"order by\r\n" + 
					"   2";
			System.out.println("This will test your patience");
			resultSet = cleStmt.executeQuery(sqlpayloadValue);
			while (resultSet.next()) 
			{
				valuefrompayload = resultSet.getString(1);
				System.out.println(resultSet.getString(2));
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

	/**
	 * Setting up the environment
	 */

	private static void loadingPropertiesFile() {
		String connProtocol = "HTTP";

		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream("C:\\\\Users\\\\jpradhan\\\\OneDrive - Williams-Sonoma Inc\\\\Documents\\\\workspace\\\\pickTicket\\\\YANNEW.properties"));
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


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

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
import com.cts.sterling.custom.accelerators.util.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import db.DBConnection;
/**
 * @author Rajdeep Chakraborty
 * @Updated on 2021/07/20
 * @Updated on 2021/11/02
 * @java_version >=8
 */
interface createTable 
{
	void createTable();
}
interface modifyData<T>
{
	T modify(T t);
}
@SuppressWarnings("unchecked")
class modData
{
	static <T>T implementModifyData(T t)
	{
		try
		{
			String data=(String) t;
			if(data.isEmpty()||data.equals(null)||data.equals("")||data.equals(" "))
			{
				data+="Not Available";
			}
			else
			{
				data.trim();
			}
			t=(T) data;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return t;
	}
}
public class DCShipConfirm 
{
	@SuppressWarnings("unused")
	private static YFSEnvironment env = null;
	private static YIFApi callapi = null;
	static Connection strConnection = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
	static String path = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\DCShipConfirm\\ShipConfirm_";
	static int shipped_count=0;
	static int failed_count=0,count=0;;
	static String errorFileName=path + "ErrorOut_" + sdf1.format(new Date()) + ".csv";
	public static void main(String[] args)
	{
		try
		{
			Connect_DB();
			loadingPropertiesFile();
			//converts xlx to csv
			//CSVUtils.convertExcelToCSV(path+sdf.format(new Date())+".xls");
			createTable();
			readFile_CreateTables();
			fetchData();
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		finally
		{
			try
			{
				if(strConnection!=null)
				{
					strConnection.close();
					System.out.println("Connection closed!!!");
				}
				//creating error xls file
				CSVToExcelConverter.init(errorFileName);
				//Update:BackUp
				BackUp_Master.takeBackUP(errorFileName.split("\\.")[0]+".xls");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		System.out.println("Script Ended...");
	}
	public static void createTable() 
	{
		createTable ct;
		ct = () ->
		{
			String createQuery = "CREATE TABLE DC_SHIPMENT ( " + "    ORDER_NO VARCHAR2(40) NOT NULL, "
					+ "    PRIME_LINE_NO VARCHAR2(5) NOT NULL, " + "    SUB_LINE_NO VARCHAR2(5) NOT NULL, "
					+ "    ITEM_ID VARCHAR2(40) NOT NULL, " + "	CONTAINER_ID VARCHAR2(40) NOT NULL, "
					+ "	TRACKING_NO VARCHAR2(40), " + "	SHIP_DATE VARCHAR2(12) NOT NULL, "
					+ "	STATUS_QUANTITY VARCHAR2(4) NOT NULL, " + "    SHIP_ADVICE_NO VARCHAR2(50) NOT NULL,"
					//Update: to allow purging
							+ "CREATETS DATE NOT NULL ) ";
			//System.out.println(createQuery);
			int temp = -1;
			try 
			{
				Statement stmt = strConnection.createStatement();
				temp = stmt.executeUpdate(createQuery);
				if (temp >= 0) 
				{
					System.out.println("DC_SHIPMENT table has been created");
				}
				if(stmt!=null)
				{
					stmt.close();
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		};
		try 
		{
			int temp;
			String dropTable = "DROP TABLE DC_SHIPMENT";
			Statement stmt = strConnection.createStatement();
			temp = stmt.executeUpdate(dropTable);
			if (temp >= 0)
			{
				System.out.println("DC_SHIPMENT table has been dropped");
			}
			if(stmt!=null)
			{
				stmt.close();
			}
		} 
		catch (Exception e)
		{
			System.out.println("Table DC_SHIPMENT does not exist!");
		}
		try
		{
			ct.createTable();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void Connect_DB()
	{
		try 
		{
			strConnection = DBConnection.getZProdDBConnection();
			//System.out.println("Connected to DB");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static <T>T refMethod(modifyData md,T t)
	{
		return ((T) (md.modify(t)));
	}
	public static void readFile_CreateTables() 
	{
		BufferedReader br = null;
		String line;
		int count = 0;
		int rows = 0;
		int temp;
		String filename = sdf.format(new Date());
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		//String currentYear = sdf1.format(new Date());
		// populating DC_SHIPMENT table
		try
		{
			br = new BufferedReader(new FileReader(path + filename + ".csv"));
			String insertQuery = "INSERT INTO DC_SHIPMENT "
					+ "(ORDER_NO, PRIME_LINE_NO, SUB_LINE_NO, ITEM_ID, CONTAINER_ID, TRACKING_NO, SHIP_DATE, "
					+ "STATUS_QUANTITY, SHIP_ADVICE_NO,CREATETS) VALUES" + "(?,?,?,?,?,?,?,?,?,?)";//Update: Insert CREATETS to allow purge
			PreparedStatement preparedStatement = strConnection.prepareStatement(insertQuery);
			System.out.println("populating DC_SHIPMENT table");
			while ((line = br.readLine()) != null)
			{
				if (count > 0)
				{
					String data[] = line.split(",");
					for (int i = 0; i < data.length; i++) 
					{
						String ans=refMethod(modData::implementModifyData, data[i]);
						preparedStatement.setString(i + 1,ans);
					}
					preparedStatement.setDate(data.length + 1,new java.sql.Date(new java.util.Date().getTime()));
					preparedStatement.addBatch();
					//preparedStatement.executeUpdate();
					rows++;
				}
				count++;
			}
			preparedStatement.executeBatch();
			strConnection.commit();
			preparedStatement.close();
			System.out.println(rows + " Rows inserted");
			try 
			{
				String dropTable = "DROP TABLE DC_MISMATCH";
				java.sql.Statement stmt = strConnection.createStatement();
				temp = stmt.executeUpdate(dropTable);
				if (temp >= 0) 
				{
					System.out.println("DC_MISMATCH table has been dropped");
				}
				if(stmt!=null)
				{
					stmt.close();
				}
			} 
			catch (Exception e)
			{
				System.out.println("DC_MISMATCH table does not exist");
			}
			temp = -1;
			String mismatchQuery = "create table DC_MISMATCH as (select ORDER_NO,PRIME_LINE_NO,SUB_LINE_NO,ITEM_ID, "
					//CREATETS to implement purge
					+ "SHIP_DATE,CONTAINER_ID,TRACKING_NO,STATUS_QUANTITY,SHIP_ADVICE_NO,sysdate  \"CREATETS\"  " + "from DC_SHIPMENT B "
					+ "WHERE  " 
					+ " NOT EXISTS "
					+ "( " + "SELECT CONTAINER_ID FROM YANTRA_OWNER.YFS_SHIPMENT_CONTAINER A "
					//Update: All container key>3 months will be considered
					+ "WHERE  A.CONTAINER_NO=TRIM(B.CONTAINER_ID) " + "AND A.SHIPMENT_CONTAINER_KEY>TO_CHAR(ADD_MONTHS(sysdate,-3),'YYYYMMDD')"
					+ "" + ")) ";
			System.out.println(mismatchQuery);
			java.sql.Statement stmt = strConnection.createStatement();
			temp = stmt.executeUpdate(mismatchQuery);
			if (temp >= 0) 
			{
				System.out.println("DC_MISMATCH table has been created");
			}
			if(stmt!=null)
			{
				stmt.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	@SuppressWarnings("unused")
	public static void fetchData() 
	{
		java.sql.Statement Stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		System.out.println("Fetching data...");
		/*String fetchQuery = "select  distinct yol.order_line_key,yol.scac,yol.extn_consolidator_address_code,yol.carrier_service_code,yoh.seller_organization_code, "
				+ " yol.shipnode_key,yor.ship_advice_no,yol.prime_line_no,yol.sub_line_no,ypi.address_line1,ypi.city,ypi.state,ypi.country,ypi.zip_code, "
				+ " yoh.enterprise_key,yol.item_id,yoh.order_no,B.STATUS_QUANTITY,B.SHIP_DATE,B.CONTAINER_ID,B.TRACKING_NO"
				+ " from YANTRA_OWNER.YFS_ORDER_HEADER yoh, YANTRA_OWNER.YFS_ORDER_LINE yol, YANTRA_OWNER.YFS_ORDER_RELEASE yor,  "
				+ " YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS  yors, DC_MISMATCH B, YANTRA_OWNER.yfs_person_info ypi "
				+ " where B.SHIP_ADVICE_NO= yor.SHIP_ADVICE_NO " + "and yor.ORDER_RELEASE_KEY=yors.ORDER_RELEASE_KEY "
				+ " and yors.order_line_key=yol.ORDER_LINE_KEY " + "and yors.status_quantity>0 "
				+ " and yol.order_line_key=yors.order_line_key " + "and yol.ORDER_HEADER_KEY=yoh.ORDER_HEADER_KEY "
				+ " and yol.ship_to_key=ypi.person_info_key " + "and b.item_id=trim(yol.item_id)";*/
		
		///----ShipAdviceNO is now read from sterling DB
		String fetchQuery="select  distinct yol.order_line_key,yol.scac,yol.extn_consolidator_address_code,yol.carrier_service_code,yoh.seller_organization_code, "
				+ "				 yol.shipnode_key,yor.ship_advice_no,yol.prime_line_no,yol.sub_line_no,ypi.address_line1,ypi.city,ypi.state,ypi.country,ypi.zip_code, "
				+ "				 yoh.enterprise_key,yol.item_id,yoh.order_no,B.STATUS_QUANTITY,B.SHIP_DATE,B.CONTAINER_ID,B.TRACKING_NO "
				+ "				 from YANTRA_OWNER.YFS_ORDER_HEADER yoh, YANTRA_OWNER.YFS_ORDER_LINE yol, YANTRA_OWNER.YFS_ORDER_RELEASE yor, "
				+ "				 YANTRA_OWNER.YFS_ORDER_RELEASE_STATUS  yors, STERLING_PROD_SUPPORT.DC_MISMATCH B, YANTRA_OWNER.yfs_person_info ypi "
				+ "				 where "
				//+ "                --B.SHIP_ADVICE_NO= yor.SHIP_ADVICE_NO   "
				//+ "                 --and  "
				+ "                 yor.ORDER_RELEASE_KEY=yors.ORDER_RELEASE_KEY "
				+ "				 and yors.order_line_key=yol.ORDER_LINE_KEY  and yors.status_quantity>0 and yors.status not in ('1400') and yors.status < '3700' "
				+ "				 and SUBSTR(b.ORDER_NO,1,12)=yoh.ORDER_NO  and yol.ORDER_HEADER_KEY=yoh.ORDER_HEADER_KEY "
				+ "				 and yol.ship_to_key=ypi.person_info_key  and b.item_id=trim(yol.item_id) "
				+ "                 and b.prime_line_no=yol.prime_line_no  and b.sub_line_no=yol.sub_line_no"
				//+ "				and B.tracking_no='Not Available'"
				;
		//System.out.println(fetchQuery);
		String s="";
		TreeMap<String,ArrayList<String>>tm=new TreeMap<String,ArrayList<String>>();
		try
		{
			Stmt = strConnection.createStatement();
			rs = Stmt.executeQuery(fetchQuery);
			rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			System.out.println("Query Executed");
			FileWriter pw = new FileWriter(errorFileName,true);
			for(int i=1;i<=cols;i++)
			{
				pw.append(rsmd.getColumnName(i));
				pw.append(",");
			}
			pw.append("API");
			pw.append(",");
			pw.append("ErrorCode");
			pw.append(",");
			pw.append("ErrorDescription");
			pw.append(",");
			pw.append("\n");
			pw.close();
			Document shipmentdocument = null;
			while (rs.next()) 
			{
				try 
				{
					s = "";
					for (int i = 1; i <= cols; i++) 
					{
						String temp=rs.getString(i).replaceAll(",", "");
						if(temp.equals("Not Available")) 
						{
							s = s + " " + ",";
						}
						else if (temp != null || (!temp.equals("")) || (!temp.equals(" ")))
						{
							s = s +temp.trim()+ ",";
						}
						else
						{
							s = s + " " + ",";
						}
					}
					if (s != "" || s != null) 
					{
						count++;
						//;
						String data[] = s.split(",");
						if(data[20]==null||data[20].equals(" ")||data[20].equals(""))
						{
							System.out.println("!!!!Empty Tracking No.\nNot adding to the list!!!!");
							ArrayList <String>al=new ArrayList<String>();
							if(tm.containsKey("Not Available"))
							{
								al=tm.get(data[20]);
								al.add(s);
								tm.put("Not Available", al);
							}
							else
							{
								al.add(s);
								tm.put("Not Available", al);
							}
						}
						else
						{
							ArrayList <String>al=new ArrayList<String>();
							if(tm.containsKey(data[20]))
							{
								al=tm.get(data[20]);
								al.add(s);
								tm.put(data[20], al);
							}
							else
							{
								al.add(s);
								tm.put(data[20], al);
							}
						}
					}
				}
				catch(Exception e12)
				{
					e12.printStackTrace();
				}
			}
			for (Map.Entry<String,ArrayList<String>> entry : tm.entrySet()) 
			{
		        ArrayList<String> value = entry.getValue();
		        String key = entry.getKey();
		        System.out.println("***********************************");
		        System.out.println("Tracking number="+key);
		        if(key.equalsIgnoreCase("Not Available"))
		        {
		        	for (String blank_tracking_data : value) 
		        	{
		        		shipmentdocument=createXML(key,blank_tracking_data);
		        		if(shipmentdocument!=null)
		        			hitAPI(pw, shipmentdocument, s);
		        		else
		        			System.out.println("Document is empty");
					}
		        }
		        else
		        {
		        	shipmentdocument = createXML(key,value);
		        	if(shipmentdocument!=null)
		        		hitAPI(pw, shipmentdocument, s);
		        	else
	        			System.out.println("Document is empty");
		        }
				System.out.println("***********************************");
			}
			
			//}
			System.out.println("Total no. of Mismatches: "+(count));
			System.out.println("Success Count: "+shipped_count);
			System.err.println("Failed Count: "+failed_count);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("\n ------------------------------------------------------------------------");
		}
		finally
		{
			try
			{
				if(rs!=null)
				{
					rs.close();
					System.out.println("ResultSet Closed!!!");
				}
				if(Stmt!=null)
				{
					Stmt.close();
					System.out.println("Statement Closed!!!");
				}
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	public static Document createXML(String key,ArrayList<String> value) 
	{
		Document doc=null;
        if(value.size()>1)
        {
        	if(!(key.equalsIgnoreCase("Not Available")))
        	{
	        	System.out.println("MultiLine Msg");
	        	doc=createMultiLineMsg(value);
        	}
        }
        else if(value.size()==1)
        {
        	System.out.println("SingleLine Msg");
        	doc=createSingleLineMsg(value.get(0).toString());
        }
		return doc;
	}
	//for Tracking_Number='Not Available'
	public static Document createXML(String key,String value) 
	{
		Document doc = null;
		if(key.equalsIgnoreCase("Not Available"))
    	{
			System.out.println("SingleLine Msg");
			System.out.println("Tracking_No="+key);
			doc = createSingleLineMsg(value);
    	}
		return doc;
	}
	//create document for multiple lines under same tracking number
	public static Document createMultiLineMsg(ArrayList<String> value)
	{
		//System.out.println(value.toString());
		Document shipmentdocument = null;
		int count=0;
		Element shipmentelement = null;
		Element shipmentlineselement=null;
		Element shipmentlineelement=null;
		Element containerselement=null;
		Element containerelement = null;
		Element containerdetailselement = null;
		Element containerdetailelement=null;
		Element containershipmentlineelement=null;
		Element shipmenttagserialselement=null;
		Element shipmenttagserialelement=null;
		Element toaddresselement = null;
		
		for (String s : value) 
		{
			try 
			{
				//System.out.println(count);
				System.out.println("Line no="+s.split(",")[0]);
				SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
				String data[] = s.split(",");
				String stringOrderline = data[0];
				String scac = data[1];
				String consolidator_address_code = data[2];
				String carrier_service_code = data[3];
				String seller_organization_code = data[4];
				String shipnode = data[5];
				String ship_advice_no = data[6];
				String prime_line_no = data[7];
				String sub_line_no = data[8];
				String address_line1 = data[9];
				String city = data[10];
				String state = data[11];
				String country = data[12];
				String zip_code = data[13];
				String enterprise_key = data[14];
				String item = data[15];
				String order_no = data[16];
				String quantity = data[17];
				String actualShipmentDate =sdf2.format(sdf.parse((data[18])))+"T00:00:00";
				String container_id = data[19];
				
				//Update:Dummy Tracking No
				String tracking_no=null;
				if(data[20]==null||data[20].equals(""))
				{
					tracking_no ="Dummy Tracking No";
				}
				else
				{
					tracking_no = data[20];
				}
				if(count==0)
				{
					shipmentdocument = XMLUtil.createDocument("Shipment");
					shipmentelement = shipmentdocument.getDocumentElement();
					shipmentelement.setAttribute("Action", "Create");
					shipmentelement.setAttribute("ActualShipmentDate", actualShipmentDate);
					shipmentelement.setAttribute("CarrierServiceCode", carrier_service_code);
					shipmentelement.setAttribute("ConsolidatorAddressCode", consolidator_address_code);
					shipmentelement.setAttribute("EnterpriseCode", enterprise_key);
					shipmentelement.setAttribute("PkMSShipmentTimestamp", actualShipmentDate);
					shipmentelement.setAttribute("SCAC", scac);
					shipmentelement.setAttribute("SellerOrganizationCode", seller_organization_code);
					shipmentelement.setAttribute("ShipNode", shipnode);
					shipmentelement.setAttribute("TrackingNo", tracking_no);
					shipmentelement.setAttribute("DocumentType", "0001");
				}
				if(count==0)
				{
					shipmentlineselement = shipmentdocument.createElement("ShipmentLines");
				}
				shipmentlineelement = shipmentdocument.createElement("ShipmentLine");
				shipmentlineelement.setAttribute("Action", "Create");
				shipmentlineelement.setAttribute("DocumentType", "0001");
				shipmentlineelement.setAttribute("ItemID", item);
				shipmentlineelement.setAttribute("OrderNo", order_no);
				shipmentlineelement.setAttribute("PrimeLineNo", prime_line_no);
				shipmentlineelement.setAttribute("Quantity", quantity);
				shipmentlineelement.setAttribute("ShipAdviceNo", ship_advice_no);
				shipmentlineelement.setAttribute("SubLineNo", sub_line_no);
				shipmentlineelement.setAttribute("UnitOfMeasure", "EACH");
				if(count==0)
				{
					containerselement = shipmentdocument.createElement("Containers");
					containerelement = shipmentdocument.createElement("Container");
				
	
					containerelement.setAttribute("Action", "Create");
					containerelement.setAttribute("ContainerNo", container_id);
					containerelement.setAttribute("TrackingNo", tracking_no);
		
					containerdetailselement = shipmentdocument.createElement("ContainerDetails");
				}
				containerdetailelement = shipmentdocument.createElement("ContainerDetail");
	
				containerdetailelement.setAttribute("Action", "Create");
				containerdetailelement.setAttribute("Quantity", quantity);
	
				containershipmentlineelement = shipmentdocument.createElement("ShipmentLine");
	
				containershipmentlineelement.setAttribute("OrderNo", order_no);
				containershipmentlineelement.setAttribute("PrimeLineNo", prime_line_no);
				containershipmentlineelement.setAttribute("ShipAdviceNo", ship_advice_no);
				containershipmentlineelement.setAttribute("SubLineNo", sub_line_no);
	
				shipmenttagserialselement = shipmentdocument.createElement("ShipmentTagSerials");
				shipmenttagserialelement = shipmentdocument.createElement("ShipmentTagSerial");
	
				shipmenttagserialelement.setAttribute("Quantity", quantity);
				shipmenttagserialelement.setAttribute("ShipByDate", "2500-01-01");
				if(count==0)
				{
				toaddresselement = shipmentdocument.createElement("ToAddress");
	
				toaddresselement.setAttribute("AddressLine1", address_line1);
				toaddresselement.setAttribute("City", city);
				toaddresselement.setAttribute("Country", country);
				toaddresselement.setAttribute("State", state);
				toaddresselement.setAttribute("ZipCode", zip_code);
				}
				shipmenttagserialselement.appendChild(shipmenttagserialelement);
				containerdetailelement.appendChild(containershipmentlineelement);
				containerdetailelement.appendChild(shipmenttagserialselement);
				containerdetailselement.appendChild(containerdetailelement);
				containerelement.appendChild(containerdetailselement);
				containerselement.appendChild(containerelement);
	
				shipmentlineselement.appendChild(shipmentlineelement);
				if(count==value.size()-1)
				{
					shipmentelement.appendChild(shipmentlineselement);//1
					shipmentelement.appendChild(containerselement);//1
					shipmentelement.appendChild(toaddresselement);//1
				}
				count++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		try {
			//System.out.println(XMLUtil.getXmlString(shipmentelement));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shipmentdocument;
	}
	public static Document createSingleLineMsg(String s)
	{
		
		Document shipmentdocument = null;
		try 
		{
			SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
			String data[] = s.split(",");
			String stringOrderline = data[0];
			String scac = data[1];
			String consolidator_address_code = data[2];
			String carrier_service_code = data[3];
			String seller_organization_code = data[4];
			String shipnode = data[5];
			String ship_advice_no = data[6];
			String prime_line_no = data[7];
			String sub_line_no = data[8];
			String address_line1 = data[9];
			String city = data[10];
			String state = data[11];
			String country = data[12];
			String zip_code = data[13];
			String enterprise_key = data[14];
			String item = data[15];
			String order_no = data[16];
			String quantity = data[17];
			String actualShipmentDate =sdf2.format(sdf.parse((data[18])))+"T00:00:00";
			String container_id = data[19];
			//Update:Dummy Tracking No
			String tracking_no=null;
			if(data[20]==null||data[20].equals(""))
			{
				tracking_no ="Dummy Tracking No";
			}
			else
			{
				tracking_no = data[20];
			}
			shipmentdocument = XMLUtil.createDocument("Shipment");
			Element shipmentelement = shipmentdocument.getDocumentElement();
			shipmentelement.setAttribute("Action", "Create");
			shipmentelement.setAttribute("ActualShipmentDate", actualShipmentDate);
			shipmentelement.setAttribute("CarrierServiceCode", carrier_service_code);
			shipmentelement.setAttribute("ConsolidatorAddressCode", consolidator_address_code);
			shipmentelement.setAttribute("EnterpriseCode", enterprise_key);
			shipmentelement.setAttribute("PkMSShipmentTimestamp", actualShipmentDate);
			shipmentelement.setAttribute("SCAC", scac);
			shipmentelement.setAttribute("SellerOrganizationCode", seller_organization_code);
			shipmentelement.setAttribute("ShipNode", shipnode);
			shipmentelement.setAttribute("TrackingNo", tracking_no);
			shipmentelement.setAttribute("DocumentType", "0001");

			Element shipmentlineselement = shipmentdocument.createElement("ShipmentLines");
			Element shipmentlineelement = shipmentdocument.createElement("ShipmentLine");

			shipmentlineelement.setAttribute("Action", "Create");
			shipmentlineelement.setAttribute("DocumentType", "0001");
			shipmentlineelement.setAttribute("ItemID", item);
			shipmentlineelement.setAttribute("OrderNo", order_no);
			shipmentlineelement.setAttribute("PrimeLineNo", prime_line_no);
			shipmentlineelement.setAttribute("Quantity", quantity);
			shipmentlineelement.setAttribute("ShipAdviceNo", ship_advice_no);
			shipmentlineelement.setAttribute("SubLineNo", sub_line_no);
			shipmentlineelement.setAttribute("UnitOfMeasure", "EACH");

			Element containerselement = shipmentdocument.createElement("Containers");
			Element containerelement = shipmentdocument.createElement("Container");

			containerelement.setAttribute("Action", "Create");
			containerelement.setAttribute("ContainerNo", container_id);
			containerelement.setAttribute("TrackingNo", tracking_no);

			Element containerdetailselement = shipmentdocument.createElement("ContainerDetails");
			Element containerdetailelement = shipmentdocument.createElement("ContainerDetail");

			containerdetailelement.setAttribute("Action", "Create");
			containerdetailelement.setAttribute("Quantity", quantity);

			Element containershipmentlineelement = shipmentdocument.createElement("ShipmentLine");

			containershipmentlineelement.setAttribute("OrderNo", order_no);
			containershipmentlineelement.setAttribute("PrimeLineNo", prime_line_no);
			containershipmentlineelement.setAttribute("ShipAdviceNo", ship_advice_no);
			containershipmentlineelement.setAttribute("SubLineNo", sub_line_no);

			Element shipmenttagserialselement = shipmentdocument.createElement("ShipmentTagSerials");
			Element shipmenttagserialelement = shipmentdocument.createElement("ShipmentTagSerial");

			shipmenttagserialelement.setAttribute("Quantity", quantity);
			shipmenttagserialelement.setAttribute("ShipByDate", "2500-01-01");

			Element toaddresselement = shipmentdocument.createElement("ToAddress");

			toaddresselement.setAttribute("AddressLine1", address_line1);
			toaddresselement.setAttribute("City", city);
			toaddresselement.setAttribute("Country", country);
			toaddresselement.setAttribute("State", state);
			toaddresselement.setAttribute("ZipCode", zip_code);

			shipmenttagserialselement.appendChild(shipmenttagserialelement);
			containerdetailelement.appendChild(containershipmentlineelement);
			containerdetailelement.appendChild(shipmenttagserialselement);
			containerdetailselement.appendChild(containerdetailelement);
			containerelement.appendChild(containerdetailselement);
			containerselement.appendChild(containerelement);

			shipmentlineselement.appendChild(shipmentlineelement);

			shipmentelement.appendChild(shipmentlineselement);
			shipmentelement.appendChild(containerselement);
			shipmentelement.appendChild(toaddresselement);
			
			//System.out.println(XMLUtil.getXmlString(shipmentelement));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return shipmentdocument;
	}
	public static void hitAPI(FileWriter pw, Document shipmentdocument,String s)
	{
		if (shipmentdocument != null) 
		{
			try
			{
				System.out.println("--->");
				System.out.println(XMLUtil.getString(shipmentdocument));
				callapi.executeFlow(env, "ConfirmShipment", shipmentdocument);
				System.out.println("Record "+count+" Executed");
				shipped_count++;
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				failed_count++;
				try
				{
					
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder;
					dBuilder = dbFactory.newDocumentBuilder();
					Document doc = XMLUtil.getDocument(e1.getMessage());
					doc.getDocumentElement().normalize();
					// ErrorCode Extraction
					XPath xPath = XPathFactory.newInstance().newXPath();
					NodeList nodeList = (NodeList) xPath.compile("/Errors/Error/@ErrorCode").evaluate(doc,
							XPathConstants.NODESET);
					String errorCode = nodeList.item(0).getNodeValue();
					// ErrorDescription extraction
					xPath = XPathFactory.newInstance().newXPath();
					nodeList = (NodeList) xPath.compile("/Errors/Error/@ErrorDescription").evaluate(doc,
							XPathConstants.NODESET);
					String errorDesc = nodeList.item(0).getNodeValue();
					if (shipmentdocument != null) 
					{
						//Update:Error Code=Not Enough Quantity
						if(errorCode.toLowerCase().contains("not enough quantity"))
						{
							System.out.println("'ConfirmShipment Service' failed hence trying 'confirmShipment API'");
							try
							{
								callapi.confirmShipment(env, shipmentdocument);
								failed_count--;
								shipped_count++;
							}
							catch(Exception e7)
							{
								dbFactory = DocumentBuilderFactory.newInstance();
								dBuilder = dbFactory.newDocumentBuilder();
								doc = XMLUtil.getDocument(e7.getMessage());
								doc.getDocumentElement().normalize();
								// ErrorCode Extraction
								xPath = XPathFactory.newInstance().newXPath();
								nodeList = (NodeList) xPath.compile("/Errors/Error/@ErrorCode").evaluate(doc,
										XPathConstants.NODESET);
								errorCode = nodeList.item(0).getNodeValue();
								// ErrorDescription extraction
								xPath = XPathFactory.newInstance().newXPath();
								nodeList = (NodeList) xPath.compile("/Errors/Error/@ErrorDescription").evaluate(doc,
										XPathConstants.NODESET);
								errorDesc = nodeList.item(0).getNodeValue();
								try
								{
									System.out.println("Record "+count+" execution failed");
									pw = new FileWriter(errorFileName,true);
									String err = s;
									pw.append(err);
									pw.append(XMLUtil.getXmlString(shipmentdocument.getDocumentElement()).toString().replaceAll("(\\r|\\n)", ""));
									pw.append(",");
									pw.append(errorCode);
									pw.append(",");
									pw.append(errorDesc);
									pw.append("\n");
									pw.close();
								}
								catch(Exception ex)
								{
									ex.printStackTrace();
									pw = new FileWriter(errorFileName,true);
									String err = s;
									pw.append(err);
									pw.append("Error creating the XML");
									pw.append(",");
									pw.append(ex.getMessage().toString().replaceAll("(\\r|\\n)", ""));
									pw.append("\n");
									pw.close();
								}
							}
						}
						/////////////End of Update:Error Code=Not Enough Quantity//////////////
						else
						{
							try
							{
								System.out.println("Record "+count+" execution failed");
								pw = new FileWriter(errorFileName,true);
								String err = s;
								pw.append(err);
								pw.append(XMLUtil.getXmlString(shipmentdocument.getDocumentElement()).toString().replaceAll("(\\r|\\n)", ""));
								pw.append(",");
								pw.append(errorCode);
								pw.append(",");
								pw.append(errorDesc);
								pw.append("\n");
								pw.close();
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								pw = new FileWriter(errorFileName,true);
								String err = s;
								pw.append(err);
								pw.append("Error creating the XML");
								pw.append(",");
								pw.append(ex.getMessage().toString().replaceAll("(\\r|\\n)", ""));
								pw.append("\n");
								pw.close();
							}
						}
					}
					else
					{
						System.out.println("document not generated");
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				e1.printStackTrace();
			}
			
		}
	}
	private static void loadingPropertiesFile() {
		String connProtocol = "HTTP";
		try {
			System.out.println("Creating env");
			Properties properties = new Properties();
			properties.load(new FileInputStream("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\DC_ShipConfirm\\src\\YANNEW.properties"));
			String envurl = properties.getProperty("ENVURL");
			Map<Object, Object> envproperties = new HashMap<>();
			envproperties.put("yif.httpapi.url", envurl);
			callapi = YIFClientFactory.getInstance().getApi(connProtocol, envproperties);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element ele = doc.createElement("YFSEnvironment");
			ele.setAttribute("userId", "admin");
			ele.setAttribute("progId", "DCShipConfirm");
			doc.appendChild(ele);
			env = callapi.createEnvironment(doc);
			System.out.println("Connected to Environment: "+envurl);

		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (YIFClientCreationException e)
		{
			e.printStackTrace();
		} 
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	public static String nodeToString(Node node) throws Exception{
	    StringWriter sw = new StringWriter();

	      Transformer t = TransformerFactory.newInstance().newTransformer();
	      t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	      t.setOutputProperty(OutputKeys.INDENT, "yes");
	      t.transform(new DOMSource(node), new StreamResult(sw));
	    return sw.toString();
	  }

}

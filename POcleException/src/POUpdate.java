import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import constants.Constants;
import com.cts.sterling.custom.accelerators.util.XMLUtil;
import db.DBConnection;

public class POUpdate {

	private final static Logger logger = Logger.getLogger(POUpdate.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection cleConnection = null;
		Connection strConnection = null;
		cleConnection = DBConnection.getCLEDBConnection();
		strConnection = DBConnection.getSterlingDBConnection();
		Statement cleStmt = null;
		ResultSet rs = null;

		try {
			cleStmt = cleConnection.createStatement();
			getUpdateExceptionDetails(rs, cleStmt, strConnection);
			getSterlingDetailsLock(rs, strConnection);
			getSterlingDetailsNotEnoughQty(rs, strConnection);
			getSterlingDetailsQtyGreaterThanAllowedShipment(rs, strConnection);
			getSterlingDetailsShipmentForCancelledLine(rs, strConnection);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				cleStmt.close();
				cleConnection.close();
				strConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Done!");
	}

	private static void getSterlingDetailsLock(ResultSet rs,
			Connection strConnection) throws SQLException {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt.executeQuery(Constants.STR_PO_DATA_4_UPDATE_LOCK);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , "
					+ rs.getString(5) + " , " + rs.getString(6) + " , "
					+ rs.getString(7) + " , " + rs.getString(8) + " , "
					+ rs.getString(9) + " , " + rs.getString(10) + " , "
					+ rs.getString(11) + " , " + rs.getString(12));
			sBuilder.append("<API FlowName=\"ProcessPOUpdate\"><Input>"
					+ rs.getString(16) + "</Input></API>");
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of Order Need to be Received/Reprocessed In Sterling: "
				+ count);
		writeToFile(sBuilder.toString(), "POUpdate-Lock.xml");

	}

	private static void getSterlingDetailsNotEnoughQty(ResultSet rs,
			Connection strConnection) throws SQLException {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt
				.executeQuery(Constants.STR_PO_DATA_4_UPDATE_NOT_ENOUGH_QTY);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1));
			sBuilder.append(rs.getString(1));
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of Order Need to be Received/Reprocessed In Sterling: "
				+ count);
		writeToFile(sBuilder.toString(), "POUpdate-NotEnoughQty.xml");

	}

	private static void getSterlingDetailsQtyGreaterThanAllowedShipment(
			ResultSet rs, Connection strConnection) throws Exception {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt
				.executeQuery(Constants.STR_PO_DATA_4_UPDATE_QTY_G8_THAN_SHIPMENT);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1));
			// modifyShipmentQty(rs.getString(1));
			sBuilder.append("<API FlowName=\"ProcessPOUpdate\"><Input>"
					+ modifyShipmentQty(rs.getString(1)).replace(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
					+ "</Input></API>");
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of Order Need to be Received/Reprocessed In Sterling: "
				+ count);
		writeToFile(sBuilder.toString(),
				"POUpdate-QtyGreaterThanAllowedShipment.xml");

	}

	private static void getSterlingDetailsShipmentForCancelledLine(
			ResultSet rs, Connection strConnection) throws Exception {
		Statement strStmt = strConnection.createStatement();
		rs = strStmt
				.executeQuery(Constants.STR_PO_DATA_4_UPDATE_SHIPMENT_4_CANCELLED_LINE);
		StringBuilder sBuilder = new StringBuilder("<MultiApi>");
		int count = 0;
		while (rs.next()) {
			logger.debug(rs.getString(1));
			// modifyShipmentQty(rs.getString(1));
			sBuilder.append("<API FlowName=\"ProcessPOUpdate\"><Input>"
					+ removeShipmentLine(rs.getString(1)).replace(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "")
					+ "</Input></API>");
			count++;
		}
		sBuilder.append("</MultiApi>");
		logger.info("Total Number of Order Need to be Received/Reprocessed In Sterling: "
				+ count);
		writeToFile(sBuilder.toString(),
				"POUpdate-ShipmentNotForCancelledLine.xml");

	}

	private static String modifyShipmentQty(String string) throws Exception {
		Document inXML = XMLUtil.getDocument(string, true);
		List<Element> orderLineList = XMLUtil.getElementsByXpath(inXML,
				"/POUpdate/Order/OrderLines/OrderLine");
		for (Element orderLine : orderLineList) {
			String orderLineOrderedQty = orderLine.getAttribute("OrderedQty");
			String orderLineItemId = XMLUtil.getAttributeFromXPath(
					XMLUtil.getDocument(orderLine, true),
					"/OrderLine/Item/@ItemID");
			String orderLinePrimeLineNo = orderLine.getAttribute("PrimeLineNo");
			List<Element> shipmentLineList = XMLUtil.getElementsByXpath(inXML,
					"/POUpdate/Shipments/Shipment/ShipmentLines/ShipmentLine");
			for (Element shipmentLine : shipmentLineList) {
				String shipmentItemId = shipmentLine.getAttribute("ItemID");
				String shipmentPrimeLineNo = shipmentLine
						.getAttribute("PrimeLineNo");
				if (shipmentItemId.equals(orderLineItemId)
						&& shipmentPrimeLineNo.equals(orderLinePrimeLineNo)) {
					shipmentLine.setAttribute("Quantity", orderLineOrderedQty);
				}
			}

		}
		return XMLUtil.getString(inXML);
	}

	private static String removeShipmentLine(String string) throws Exception {
		Document inXML = XMLUtil.getDocument(string, true);
		List<Element> orderLineList = XMLUtil.getElementsByXpath(inXML,
				"/POUpdate/Order/OrderLines/OrderLine");
		for (Element orderLine : orderLineList) {
			String orderLineOrderedQty = orderLine.getAttribute("OrderedQty");
			String orderLineItemId = XMLUtil.getAttributeFromXPath(
					XMLUtil.getDocument(orderLine, true),
					"/OrderLine/Item/@ItemID");
			String orderLinePrimeLineNo = orderLine.getAttribute("PrimeLineNo");
			if ("0".equalsIgnoreCase(orderLineOrderedQty)) {
				NodeList shipmentList = inXML.getElementsByTagName("Shipment");
				for (int i=0; i < shipmentList.getLength();i++) {
					Element shipment = (Element)shipmentList.item(i);
					NodeList shipmentLineList = shipment.getElementsByTagName("ShipmentLine");
					for (int j=0; j<shipmentLineList.getLength();j++) {
						Element shipmentLine = (Element)shipmentLineList.item(j);
						String shipmentItemId = shipmentLine.getAttribute("ItemID");
						String shipmentPrimeLineNo = shipmentLine
								.getAttribute("PrimeLineNo");
						if (shipmentItemId.equals(orderLineItemId)
								&& shipmentPrimeLineNo
										.equals(orderLinePrimeLineNo)) {
							shipment.getParentNode().removeChild(shipment);
						}
						
					}
				}
			}
		}
		return XMLUtil.getString(inXML);
	}

	private static void writeToFile(String content, String fileName) {
		try {

			File file = new File(fileName);

			// if file doesn't exist, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(fileName+" is created");
	}

	private static void getUpdateExceptionDetails(ResultSet rs,
			Statement cleStmt, Connection strConnection) throws SQLException {
		logger.debug(Constants.CLE_UPDATE_EXCEPTION);
		rs = cleStmt.executeQuery(Constants.CLE_UPDATE_EXCEPTION);
		PreparedStatement strPrepStmt = strConnection
				.prepareStatement("TRUNCATE TABLE PO_EXP_REPROCESS_UPDATE_1");
		strPrepStmt.execute();
		strPrepStmt = strConnection
				.prepareStatement("INSERT INTO PO_EXP_REPROCESS_UPDATE_1 VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		int count = 0;
		while (rs.next()) {

			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + " , "
					+ rs.getString(5) + " , " + rs.getString(6) + " , "
					+ rs.getString(7) + " , " + rs.getString(8) + " , "
					+ rs.getString(9) + " , " + rs.getString(10) + " , "
					+ rs.getString(11) + " , " + rs.getString(12) + " , "
					+ rs.getString(13));

			strPrepStmt.setString(2, rs.getString(3));
			strPrepStmt.setString(1, rs.getString(2));
			strPrepStmt.setString(3, rs.getString(4));
			strPrepStmt.setString(4, rs.getString(5));
			strPrepStmt.setString(5, rs.getString(6));
			strPrepStmt.setString(6, rs.getString(7));
			strPrepStmt.setString(7, rs.getString(8));
			strPrepStmt.setString(8, rs.getString(9));
			strPrepStmt.setString(9, rs.getString(10));
			strPrepStmt.setString(10, rs.getString(11));
			strPrepStmt.setString(11, rs.getString(12));
			strPrepStmt.setString(12, rs.getString(13));
			strPrepStmt.setString(13, rs.getString(14));
			strPrepStmt.setString(14, rs.getString(15));
			strPrepStmt.setString(15, rs.getString(16));

			strPrepStmt.setCharacterStream(16,
					new StringReader(rs.getString(17)), rs.getString(17)
							.length());
			strPrepStmt.addBatch();
			strPrepStmt.clearParameters();
			count++;
		}
		logger.info("Total Number of PO Update Exception in CLE: " + count);
		strPrepStmt.executeBatch();
	}

	@SuppressWarnings("unused")
	private static void getExceptionSummary(ResultSet rs, Statement cleStmt)
			throws SQLException {
		rs = cleStmt.executeQuery(Constants.CLE_COUNT_EXCEPTION);
		StringBuilder sBuilder = new StringBuilder(
				"InterfaceName, ExceptionCode, Msg, Count\n");
		while (rs.next()) {
			logger.debug(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4));
			sBuilder.append(rs.getString(1) + " , " + rs.getString(2) + " , "
					+ rs.getString(3) + " , " + rs.getString(4) + "\n");
		}

		writeToFile(sBuilder.toString(), "ExceptionSummary.csv");
	}

}

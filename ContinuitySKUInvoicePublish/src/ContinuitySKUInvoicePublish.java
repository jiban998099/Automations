import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author Jiban
 */

public class ContinuitySKUInvoicePublish {
	
	public static ArrayList<String> success = new ArrayList<String>();
	public static ArrayList<String> failure = new ArrayList<String>();
	public static StringBuilder sb = new StringBuilder();
	public static int counter=0;

	public static void main(String[] args) {
		
		try {
			System.out.println("Starting Continuity SKU EXTN_AVAILABLE_DATE Update Process...");
			fetchData();
			writeData();
			System.out.println("****Java Code Ended****");
		} catch(Exception e ) {
			counter++;
			e.printStackTrace();
		} finally {
			if(counter>0) {
				//triggerP2();
			}
		}

	}
	
	public static void fetchData() {
	    sb.append("ORDER_NO"+","+"ITEM_ID"+","+"LINE_TYPE"+"ORDER_INVOICE_KEY"+","+"EXTN_AVAILABLE_DATE"+","+"EXTN_INV_FIN_DATE"+","+"EXTN_IS_PUBLISH_TO_VERTEX"+","+"CREATETS"+","+"MODIFYTS"+"\n");
	    
	    Connection conn = null;
	    try {
	        conn = DBConnection.getSterlingDRConnection();
	        Statement stmt = null;
	        ResultSet rs = null;

	        stmt = conn.createStatement();
	        System.out.println("Connected to Sterling DB.");
	        
	        // Set NLS_DATE_FORMAT for the session
	        stmt.execute("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'");
	        conn.setAutoCommit(true);
	        
	        System.out.println("Running select query to fetch the data...");
	        
	        String fetchQuery = "SELECT DISTINCT oh.order_no, TRIM(id.item_id) AS ITEM_ID, " +
	        "ol.line_type, TRIM(i.order_invoice_key) AS ORDER_INVOICE_KEY, " +
	        "TO_CHAR(i.extn_available_date, 'YYYY-MM-DD HH24:MI:SS') AS EXTN_AVAILABLE_DATE, " +
	        "TO_CHAR(i.extn_inv_fin_date, 'YYYY-MM-DD HH24:MI:SS') AS EXTN_INV_FIN_DATE, " +
	        "i.extn_is_publish_to_vertex, " +
	        "TO_CHAR(i.createts, 'YYYY-MM-DD HH24:MI:SS') AS CREATETS, " +
	        "TO_CHAR(i.modifyts, 'YYYY-MM-DD HH24:MI:SS') AS MODIFYTS " +
	        "FROM yantra_owner.yfs_order_invoice i " +
	        "JOIN yantra_owner.yfs_order_invoice_detail id ON i.order_invoice_key = id.order_invoice_key " +
	        "JOIN yantra_owner.yfs_order_line ol ON ol.order_line_key = id.order_line_key " +
	        "JOIN yantra_owner.yfs_order_header oh ON oh.order_header_key = ol.order_header_key " +
	        "JOIN yantra_owner.yfs_order_release_status ors ON ol.order_line_key = ors.order_line_key " +
	        "JOIN yantra_owner.yfs_item i on ol.item_id=i.item_id " +
	        "JOIN yantra_owner.yfs_order_line_relationship olr on ol.ORDER_LINE_KEY = olr.PARENT_ORDER_LINE_KEY " +
	        "WHERE ors.status <> '1100' AND ors.status = '1100.9005' AND ol.line_type = 'Continuity' " +
	        "AND oh.order_no = '341232839256' AND i.status = '00' AND ors.status_quantity > '0' " +
	        "AND i.EXTN_AVAILABLE_DATE='2500-01-01 00:00:00' " +
	        "AND EXISTS (select 1 from yantra_owner.yfs_order_line l where l.order_line_key=olr.CHILD_ORDER_LINE_KEY and l.EXTN_LINE_TYPE  ='Continuity Month' and l.shipped_quantity >'0')";
	    
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(fetchQuery);
	        
	        HashSet<String> hs = new HashSet<String>();
	        
	        while(rs.next()) {
	            
	            // Retrieve date columns as Timestamp objects
	            java.sql.Timestamp extnAvailableDate = rs.getTimestamp("EXTN_AVAILABLE_DATE");  //5
	            java.sql.Timestamp extnInvFinDate = rs.getTimestamp("EXTN_INV_FIN_DATE"); //6
	            java.sql.Timestamp createts = rs.getTimestamp("CREATETS"); //8
	            java.sql.Timestamp modifyts = rs.getTimestamp("MODIFYTS"); //9
	            
	            
	            
	            //Format the Timestamp objects to desired string format
	            String formattedExtnAvailableDate = formatDate(extnAvailableDate);
	            String formattedExtnInvFinDate = formatDate(extnInvFinDate);
                String formattedCreatets = formatDate(createts);
	            String formattedModifyts = formatDate(modifyts);
	            
	         // Retrieve string values from the ResultSet and handle null values
                String value1 = rs.getString(1);
                String value2 = rs.getString(2);
                String value3 = rs.getString(3);
                String value4 = rs.getString(4);
                String value7 = rs.getString(7);

                // Check for null values and assign empty string if null
                if (value1 == null) value1 = "";
                if (value2 == null) value2 = "";
                if (value3 == null) value3 = "";
                if (value4 == null) value4 = "";
                if (value7 == null) value7 = "";

                // Add the formatted data to the HashSet and StringBuilder
                hs.add(value1.trim() + "," + value2.trim() + "," + value3.trim() + "," + value4.trim() + ","
                        + formattedExtnAvailableDate + "," + formattedExtnInvFinDate + "," + value7.trim() + ","
                        + formattedCreatets + "," + formattedModifyts);

                sb.append(value1.trim() + "," + value2.trim() + "," + value3.trim() + "," + value4.trim() + ","
                        + formattedExtnAvailableDate + "," + formattedExtnInvFinDate + "," + value7.trim() + ","
                        + formattedCreatets + "," + formattedModifyts + "\n");
	            
//	            formattedExtnAvailableDate = formatDate(extnAvailableDate);
//	            formattedExtnInvFinDate = formatDate(extnInvFinDate);
//	            formattedCreatets = formatDate(createts);
//	            formattedModifyts = formatDate(modifyts);
	            
//	            hs.add(rs.getString(1).trim()+","+rs.getString(2).trim()+","+rs.getString(3).trim()+","+rs.getString(4).trim()+","+formattedExtnAvailableDate+","
//	                    +formattedExtnInvFinDate+","+rs.getString(7).trim()+","+formattedCreatets+","+formattedModifyts);
//	            
//	            sb.append(rs.getString(1).trim()+","+rs.getString(2).trim()+","+rs.getString(3).trim()+","+rs.getString(4).trim()+","+formattedExtnAvailableDate+","
//	                    +formattedExtnInvFinDate+","+rs.getString(7).trim()+","+formattedCreatets+","+formattedModifyts+"\n");
	        }
	        rs.close();
	        stmt.close();
	        
	        System.out.println("Data Fetched Successfully.");
	        
	        conn.close();
	        System.out.println("Sterling DB Connection closed.");
	        
	        if(hs.size()>0) {
	            System.out.println("Count : "+hs.size());
	            updateData(hs);
	        }
	        else {
	            System.out.println("Count within threshold. No action required.");
	        }

		
		} catch (SQLException e) {
			System.err.println("SQL Exception in fetchData: " + e.getMessage());
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	private static String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return ""; 
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return sdf.format(timestamp);
		
    }
	
	public static void updateData(HashSet<String> hs) {
		Connection conn1 = null;
		try {
			conn1 = DBConnection.getZProdDBConnection();
			System.out.println("Connected to ZPROD.");
			Statement stmt1 = conn1.createStatement();
			Iterator<String> it = hs.iterator();

			conn1.setAutoCommit(true);
			
			while(it.hasNext()) {
				String temp = it.next();
				String order_invoice_key = temp.split(",")[3];
		
				String updateQuery = "UPDATE YANTRA_OWNER.YFS_ORDER_INVOICE SET EXTN_AVAILABLE_DATE = SYSDATE, MODIFYTS = SYSDATE, MODIFYPROGID = 'SERREQ0739969' WHERE ORDER_INVOICE_KEY = '"+order_invoice_key+"' ";
				System.out.println(updateQuery);
				
				System.out.println("Running update query : "+updateQuery);
				System.out.println("Order Invoice Key: " + order_invoice_key);
				
				try {
					int rowsUpdated = -1;
					rowsUpdated = stmt1.executeUpdate(updateQuery);
					
					if(rowsUpdated>0) {
						System.out.println("EXTN_AVAILABLE_DATE updated for ORDER_INVOICE_KEY : "+order_invoice_key);
						success.add(order_invoice_key);							
					}
					else {
						System.out.println("EXTN_AVAILABLE_DATE could not be updated for ORDER_INVOICE_KEY : "+order_invoice_key);
						failure.add(order_invoice_key);
					}
				}
				catch(Exception e) {
					
					try {
						conn1.close();
						System.out.println("ZPROD Connection closed.");
					} catch (SQLException e1) {
						counter++;
						e1.printStackTrace();
					}
					counter++;
					e.printStackTrace();
				}
						
			}
			
			stmt1.close();
			conn1.close();
			System.out.println("ZPROD Connection closed.");
		}
		catch(Exception e) {
			
			try {
				conn1.close();
				System.out.println("ZPROD Connection closed.");
			} catch (SQLException e1) {
					counter++;
					e1.printStackTrace();
			}
					counter++;
					e.printStackTrace();
		}
	}
	
	
	public static void writeData() {
	    try {
	        // Specify the local directory path where Eclipse project is located
	        String projectDirectory = System.getProperty("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\SKU");

	        // Create the directory if it doesn't exist
	        File directory = new File(projectDirectory + "/CSV_Files");
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }

	        // Write each file to the local directory
	        BufferedWriter out = new BufferedWriter(new FileWriter(projectDirectory + "/CSV_Files/Missing_Continuity_Orders.csv"));
	        out.write(sb.toString());
	        out.close();

	        BufferedWriter out_s = new BufferedWriter(new FileWriter(projectDirectory + "/CSV_Files/Success.csv"));
	        out_s.write("order_invoice_key");
	        for(int i=0; i<success.size(); i++) {
	            out_s.newLine();
	            out_s.write(success.get(i));
	        }
	        out_s.close();

	        BufferedWriter out_f = new BufferedWriter(new FileWriter(projectDirectory + "/CSV_Files/Failure.csv"));
	        out_f.write("order_invoice_key");
	        for(int i=0; i<failure.size(); i++){
	            out_f.newLine();
	            out_f.write(failure.get(i));
	        }
	        out_f.close();

	        // Rest of your code remains unchanged...
	    } catch(Exception e) {
	        counter++;
	        e.printStackTrace();
	    }
	}
}

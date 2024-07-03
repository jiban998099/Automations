package com.main;

import com.dbr.DSFlowSnapCurStat;
import com.dbr.OrderLineCount;
import com.dbr.POReceiptQry;
import com.dbr.PendingInvoiceCount;
import com.dbr.ShafterHold;
import com.dbr.ShipmentQryBrandWise;
import com.dbr.ShipmentQueries;
import com.dbr.TotalColDSCol;

public class GetAllQueryResult {
	
	public static void main(String[] args) {
		
		System.out.println("Starting TotalColDSCol execution process...");
		try {			
			TotalColDSCol.main(null);
			System.out.println("TotalColDSCol script executed successfully...");
		}catch(Exception e){
			System.out.println("TotalColDSCol script execution failed...");
		}
		
		System.out.println("Starting DSFlowSnapCurStat execution process...");
		try {			
			DSFlowSnapCurStat.main(null);
			System.out.println("DSFlowSnapCurStat script executed successfully...");
		}catch(Exception e){
			System.out.println("DSFlowSnapCurStat script execution failed...");
		}
		
		System.out.println("Starting OrderLineCount execution process...");
		try {			
			OrderLineCount.main(null);
			System.out.println("OrderLineCount script executed successfully...");
		}catch(Exception e){
			System.out.println("OrderLineCount script execution failed...");
		}
		
		System.out.println("Starting PendingInvoiceCount execution process...");
		try {			
			PendingInvoiceCount.main(null);
			System.out.println("PendingInvoiceCount script executed successfully...");
		}catch(Exception e){
			System.out.println("PendingInvoiceCount script execution failed...");
		}
		
		System.out.println("Starting ShipmentQueries execution process...");
		try {			
			ShipmentQueries.main(null);
			System.out.println("ShipmentQueries script executed successfully...");
		}catch(Exception e){
			System.out.println("ShipmentQueries script execution failed...");
		}
		
		System.out.println("Starting ShipmentQryBrandWise execution process...");
		try {			
			ShipmentQryBrandWise.main(null);
			System.out.println("ShipmentQryBrandWise script executed successfully...");
		}catch(Exception e){
			System.out.println("ShipmentQryBrandWise script execution failed...");
		}
		
		System.out.println("Starting ShafterHold execution process...");
		try {			
			ShafterHold.main(null);
			System.out.println("ShafterHold script executed successfully...");
		}catch(Exception e){
			System.out.println("ShafterHold script execution failed...");
		}
		
		System.out.println("Starting POReceiptQry execution process...");
		try {			
			POReceiptQry.main(null);
			System.out.println("POReceiptQry script executed successfully...");
		}catch(Exception e){
			System.out.println("POReceiptQry script execution failed...");
		}
		
		System.out.println("DBR Report all mandatory query results have been written successfully...");
	}
}

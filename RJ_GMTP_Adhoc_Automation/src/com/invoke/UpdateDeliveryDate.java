package com.invoke;

import com.main.BothAPIFNAvlDateCalc;
import com.main.BothAPIFNEndDateCalc;
import com.main.BothAPIProductLineFilter;
import com.main.BothAPISSAvlDateCalc;
import com.main.BothAPISSEndDateCalc;
import com.main.CheckAndTriggerBothApiFN;
import com.main.CheckAndTriggerBothApiSS;
import com.main.CheckAndTriggerTopApiFN;
import com.main.CheckAndTriggerTopApiSS;
import com.main.CompareCSVFiles;
import com.main.FetchOrderDetails;
import com.main.FetchWsiPromiseDateMonitor;
import com.main.TopAPIFNEndDateCalc;
import com.main.TopAPIProductLineFilter;
import com.main.TopAPISSEndDateCalc;

public class UpdateDeliveryDate {
	public static void main(String[] args) {
		
		//To fetch ORDER_HEADER_KEY,ORDER_LINE_KEY and PRODUCT_LINE
		System.out.println("Starting FetchOrderDetails execution process...");
		try {			
			FetchOrderDetails.main(null);
			System.out.println("FetchOrderDetails script executed successfully...");
		}catch(Exception e){
			System.out.println("FetchOrderDetails script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//To fetch ORDER_LINE_KEY and ITEM_ID from WsiPromiseDateMonitor
		System.out.println("Starting FetchWsiPromiseDateMonitor execution process...");
		try {			
			FetchWsiPromiseDateMonitor.main(null);
			System.out.println("FetchWsiPromiseDateMonitor script executed successfully...");
		}catch(Exception e){
			System.out.println("FetchWsiPromiseDateMonitor script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//To compare order details with WsiPromiseDateMonitor table & separate BothAPI and TopAPI
		System.out.println("Starting CompareCSVFiles execution process...");
		try {			
			CompareCSVFiles.main(null);
			System.out.println("CompareCSVFiles script executed successfully...");
		}catch(Exception e){
			System.out.println("CompareCSVFiles script execution failed...");
		}

		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//To filter SS,CMO,Groud,FN
		System.out.println("Starting TopAPIProductLineFilter execution process...");
		try {			
			TopAPIProductLineFilter.main(null);
			System.out.println("TopAPIProductLineFilter script executed successfully...");
		}catch(Exception e){
			System.out.println("TopAPIProductLineFilter script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//CurrentPromiseDate, CurrentPromiseEndDate calculator for SS
		System.out.println("Starting TopAPISSEndDateCalc execution process...");
		try {			
			TopAPISSEndDateCalc.main(null);
			System.out.println("TopAPISSEndDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("TopAPISSEndDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Trigger multiAPI for SS
		System.out.println("Starting CheckAndTriggerTopApiSS execution process...");
		try {			
			CheckAndTriggerTopApiSS.main(null);
			System.out.println("CheckAndTriggerTopApiSS script executed successfully...");
		}catch(Exception e){
			System.out.println("CheckAndTriggerTopApiSS script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//CurrentPromiseDate, CurrentPromiseEndDate calculator for FN
		System.out.println("Starting TopAPIFNEndDateCalc execution process...");
		try {			
			TopAPIFNEndDateCalc.main(null);
			System.out.println("TopAPIFNEndDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("TopAPIFNEndDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Trigger multiAPI for FN
		System.out.println("Starting CheckAndTriggerTopApiFN execution process...");
		try {			
			CheckAndTriggerTopApiFN.main(null);
			System.out.println("CheckAndTriggerTopApiFN script executed successfully...");
		}catch(Exception e){
			System.out.println("CheckAndTriggerTopApiFN script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//To filter SS,CMO,Groud,FN
		System.out.println("Starting BothAPIProductLineFilter execution process...");
		try {			
			BothAPIProductLineFilter.main(null);
			System.out.println("BothAPIProductLineFilter script executed successfully...");
		}catch(Exception e){
			System.out.println("BothAPIProductLineFilter script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//CurrentPromiseDate, CurrentPromiseEndDate calculator for SS
		System.out.println("Starting BothAPISSEndDateCalc execution process...");
		try {			
			BothAPISSEndDateCalc.main(null);
			System.out.println("BothAPISSEndDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("BothAPISSEndDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//AvailableDate calculator for SS
		System.out.println("Starting BothAPISSAvlDateCalc execution process...");
		try {			
			BothAPISSAvlDateCalc.main(null);
			System.out.println("BothAPISSAvlDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("BothAPISSAvlDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Trigger multiAPI for SS
		System.out.println("Starting CheckAndTriggerBothApiSS execution process...");
		try {			
			CheckAndTriggerBothApiSS.main(null);
			System.out.println("CheckAndTriggerBothApiSS script executed successfully...");
		}catch(Exception e){
			System.out.println("CheckAndTriggerBothApiSS script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//CurrentPromiseDate, CurrentPromiseEndDate calculator for FN
		System.out.println("Starting BothAPIFNEndDateCalc execution process...");
		try {			
			BothAPIFNEndDateCalc.main(null);
			System.out.println("BothAPIFNEndDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("BothAPIFNEndDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//AvailableDate calculator for FN
		System.out.println("Starting BothAPIFNAvlDateCalc execution process...");
		try {			
			BothAPIFNAvlDateCalc.main(null);
			System.out.println("BothAPIFNAvlDateCalc script executed successfully...");
		}catch(Exception e){
			System.out.println("BothAPIFNAvlDateCalc script execution failed...");
		}
		
		try {
		    Thread.sleep(10000); // 10000 milliseconds = 10 seconds
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//Trigger multiAPI for FN
		System.out.println("Starting CheckAndTriggerBothApiFN execution process...");
		try {			
			CheckAndTriggerBothApiFN.main(null);
			System.out.println("CheckAndTriggerBothApiFN script executed successfully...");
		}catch(Exception e){
			System.out.println("CheckAndTriggerBothApiFN script execution failed...");
		}
		
		System.out.println("GMTP orders have been updated with the new projected delivery dates...");
		
	}
}


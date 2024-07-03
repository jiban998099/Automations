import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import db.DBConnection;

public class BackUp_Master
{
    private static Connection strConnection = null;
    public static void takeBackUP(String errorFileName)
    {
    	try
    	{
    		Connect_DB();
    		System.out.println("Tacking DB BackUp...");
    		BackUp_DC_SHIPCONFIRM_ERROR_HISTORY(errorFileName);
    		BackUpOtherTables("DC_SHIPMENT_HISTORY", "DC_SHIPMENT");
    		BackUpOtherTables("DC_MISMATCH_HISTORY", "DC_MISMATCH");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
        {
        	try
        	{
        		if(strConnection!=null)
        		{
        			strConnection.close();
        			System.out.println("Connection Closed!!!");
        		}
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
        }
    	
    }
    @SuppressWarnings("deprecation")
	private static void BackUp_DC_SHIPCONFIRM_ERROR_HISTORY(String errorFileName)
    {
    	System.out.println("Backing up Error File:"+errorFileName);
    	File errorFile=new File(errorFileName);
        PreparedStatement pstmt=null;
        try
        {
        	if(errorFile.exists())
        	{
	        	//obtaining input bytes from a file
	            FileInputStream fis = new FileInputStream(errorFile);
	            //creating workbook instance that refers to .xls file
	            HSSFWorkbook wb = new HSSFWorkbook(fis);
	            //creating a Sheet object to retrieve the object
	            HSSFSheet sheet = wb.getSheetAt(0);
	            //evaluating cell type
	            FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
	            //Creating a Statement object
	            //Setting auto-commit false
	            strConnection.setAutoCommit(false);
	            pstmt = strConnection.prepareStatement("INSERT INTO DC_SHIPCONFIRM_ERROR_HISTORY (ORDER_LINE_KEY,SCAC,EXTN_CONSOLIDATOR_ADDRESS_CODE,CARRIER_SERVICE_CODE,SELLER_ORGANIZATION_CODE,SHIPNODE_KEY,SHIP_ADVICE_NO,PRIME_LINE_NO,SUB_LINE_NO,ADDRESS_LINE1,CITY,STATE,COUNTRY,ZIP_CODE,ENTERPRISE_KEY,ITEM_ID,ORDER_NO,STATUS_QUANTITY,SHIP_DATE,CONTAINER_ID,TRACKING_NO,XML,ErrorCode,ErrorDescription,CREATETS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	            for (Row row: sheet)// iteration over row using for each loop
	            {
	                if(row.getRowNum()+1>1)
	                {
	                	int colCount=1;
		                for (Cell cell: row)// iteration over cell using for each loop
		                {
		                    switch (formulaEvaluator.evaluateInCell(cell).getCellType())
		                    {
		                        case Cell.CELL_TYPE_NUMERIC: // field that represents numeric
		                            // cell type
		                            // getting the value of the cell as a number
		                            System.out.print(cell.getNumericCellValue() + "\t\t");
		                            System.out.println("Number:" + cell.getNumericCellValue());
		                            pstmt.setString(colCount, String.valueOf(cell.getNumericCellValue()));
		                            break;
		                        case Cell.CELL_TYPE_STRING: // field that represents string cell
		                            // type
		                            // getting the value of the cell as a string
		                            //System.out.println(cell.getStringCellValue() + "-->Col="+colCount);
		                            //System.out.println("String:" + cell.getStringCellValue());
		                            pstmt.setString(colCount, cell.getStringCellValue());
		                            break;
		                    }
		                    colCount++;
		                }
		                pstmt.setDate(colCount, new java.sql.Date(new java.util.Date().getTime()));
		                pstmt.addBatch();
	                }
	                else
	                {
	                	//System.out.println("Ignoring Header Row:"+row.getRowNum());
	                }
	                //System.out.println();
	            }
	            //Executing the batch
	            int a[]=pstmt.executeBatch();
	            //System.out.println(Arrays.toString(a));
	            //Saving the changes
	            strConnection.commit();
	            System.out.println("Table backed up: DC_SHIPCONFIRM_ERROR_HISTORY");
	            fis.close();
	            wb.close();
        	}
        	else
        	{
        		System.out.println("File does not exist");
        	}
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	try
        	{
        		if(pstmt!=null)
        		{
        			pstmt.close();
        		}
        	}
        	catch(Exception ex)
        	{
        		ex.printStackTrace();
        	}
        }
    }
    private static void BackUpOtherTables(String toTable,String fromTable)
    {
    	String query="insert into "+toTable+"(select * from "+fromTable+")";
    	Statement stmt = null;
    	try 
        {
            stmt = strConnection.createStatement();
            //System.out.println(query);
            stmt.execute(query);
            strConnection.commit();
            System.out.println("Table backed up: "+fromTable);
        }
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	 finally
         {
         	try
         	{
         		if(stmt!=null)
         		{
         			stmt.close();
         		}
         	}
         	catch(Exception ex)
         	{
         		ex.printStackTrace();
         	}
         }
    }
    private static void Connect_DB() 
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
}
import java.io.*;
import java.text.*;
import java.util.*;

public class Find_Unique 
{
	static String path_dump="C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\UniqueError\\src\\dump.csv";
	static String date1="";
	static int result=0;
	static String why="";
	public static void main(String[] args) 
	{
		String path_today="C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\UniqueError\\src\\today.csv";
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date1 = simpleDateFormat.format(new Date());
		System.out.println(date1);
		File f = new File("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\UniqueError\\src\\"+date1+".csv");
		if(!f.exists())
		{
			try
			{
				FileWriter pw = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\UniqueError\\src\\"+date1+".csv",true);
		        pw.append("Error Code");
		        pw.append(",");
		        pw.append("Error Description");
		        pw.append(",");
		        pw.append("Reason");
		        pw.flush();
		        pw.close();
			}
			catch (IOException e) 
			{
				
				e.printStackTrace();
			}
		}
		read_file(path_today);
		System.out.println("You have "+result+" Unique Error");
	}
	public static void read_file(String path_today)
	{
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int count=0;
        try 
        {
            br = new BufferedReader(new FileReader(path_today));
            while ((line = br.readLine()) != null)
            {
                
            	String[] data = line.split(cvsSplitBy);
                String code=data[0];
                String description=data[1];
                count++;
                System.out.println(count+".Looking for Error Code="+code+"->Error Description="+description);
                if(!(code.equals("10")||code.contains("ErrorRelatedMoreInfo")||code.contains("Request Data Error")||code.startsWith(" ")||code.contains("ORDER_NOT_EXIST")||code.contains("java.io.FileNotFoundException")||code.contains("IDS0026")||code.contains("ShipmentUpdate.0002")))
                {
                	System.out.println("No");
                	search_error(code,description);
                }
                else
                	System.out.println("Yes");
            }

        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
	public static void search_error(String code, String description)
	{
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int count_d=0;
        int count_c=0;
        try 
        {
        	
            br = new BufferedReader(new FileReader(path_dump));
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(cvsSplitBy);
                
                if(data[0].equals(code)||data[0].equals("\""+code+"\""))
                {
                	count_c++;
                	if(data.length>1)
                	{
	                	if(data[1].contains(description))
	                	{
	                		count_d++;
	                		
	                	}
                	}
                	else
                		count_d=100;
                }
            }
            if(count_c==0)
            {
	            System.out.println("Error code with UNIQUE ERRORCODE..");
	    		System.out.println("Error Code="+code);
	    		System.out.println("Decription="+description);
	    		why="UNIQUE ERRORCODE";
	    		add_newError(code,description);
            }
            else if(count_d==0)
            {
            	System.out.println("Error code with UNIQUE DESCRIPTION..");
	    		System.out.println("Error Code="+code);
	    		System.out.println("Decription="+description);
	    		why="UNIQUE DESCRIPTION";
	    		add_newError(code,description);
            }

        } 
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}
	public static void add_newError(String code, String description) throws IOException
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		result++;
		try
		{
			FileWriter pw = new FileWriter(path_dump,true);
			pw.append("\n");
	        pw.append(code);
	        pw.append(",");
	        pw.append(description);
	        pw.append(",");
	        pw.append(why);
	        pw.flush();
	        pw.close();
	        System.out.println("New Error Code has been added to dump");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date1 = simpleDateFormat.format(new Date());
		FileWriter pw = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\UniqueError\\src\\"+date1+".csv",true);
		try
		{
			pw.append("\n");
	        pw.append(code);
	        pw.append(",");
	        pw.append(description);
	        pw.append(",");
	        pw.append(why);
	        pw.flush();
	        pw.close();
	        System.out.println("New Error Code has been added to "+date1+".csv");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

}

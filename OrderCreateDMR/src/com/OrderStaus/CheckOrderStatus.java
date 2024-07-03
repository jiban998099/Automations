package com.OrderStaus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import db.DBConnection;

public class CheckOrderStatus
{
	static String fileName="";
	public void checkStatus(String inputFolder)
	{
		findInputFile(inputFolder);
		Connection con=getDBConnection();
		readInputFile(con,fileName);
		
	}
	private String findInputFile(String inputPath)
	{
		
		try (Stream<Path> walk = Files.walk(Paths.get(inputPath)))
		{

			List<String> result = walk.map(x -> x.toString())
					.filter(f -> f.endsWith(".csv")).collect(Collectors.toList());

			result.forEach(r->fileName=r);

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return fileName;
	}
	private void readInputFile(Connection con,String file)
	{
		try 
		{
			ArrayList<String> line=CSVUtils.readFileReturnList(file);
			ArrayList<String> presentOrders=new ArrayList<String>();
			//line.forEach(r->System.out.println(r));
			//System.out.println(line.size());
			StringBuilder sb=new StringBuilder();
			try
			{
				for(int i=0;i<line.size();i++)
				{
					//System.out.println(line.size()+"--"+i);
					if(i%900==0 && i!=0)
					{
						System.out.println(i);
						System.out.println(sb.deleteCharAt(sb.length()-1));
						getResultFromDB(con,sb,presentOrders);
						sb=new StringBuilder();
						sb.append("'"+line.get(i)+"',");
					}
					else
					{
						sb.append("'"+line.get(i)+"',");
						if(line.size()-i==1)
						{
							System.out.println(i);
							System.out.println(sb.deleteCharAt(sb.length()-1));
							getResultFromDB(con,sb,presentOrders);
							sb=new StringBuilder();
						}
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			line.removeAll(presentOrders);
			System.out.println("orders not present="+line);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				con.close();
				System.out.println("Connection closed");
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
					
		}
	}
	private ArrayList<String> getResultFromDB(Connection con, StringBuilder sb,ArrayList<String> presentOrders) 
	{
		Statement st=null;
		ResultSet rs=null;
		try
		{
			st=con.createStatement();
			rs=st.executeQuery("select order_no from yantra_owner.yfs_order_header where order_no in ("+sb+")");
			while(rs.next())
			{
				//System.out.println("i am there"+rs.getString(1));
				presentOrders.add(rs.getString(1));
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
				System.out.println("resultset closed");
				st.close();
				System.out.println("statement closed");
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
					
		}
		return presentOrders;		
	}
	private Connection getDBConnection()
	{
		Connection con=DBConnection.getZProdDBConnection();
		System.out.println("Connected to db");
		return con;
	}

}

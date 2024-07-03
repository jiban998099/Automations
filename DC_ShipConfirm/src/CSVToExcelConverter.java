
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;

/**
 * @author Rajdeep Chakraborty
 */

public class CSVToExcelConverter {
	static String path = "";

	public static void init(String dest) throws IOException {
		System.out.println("Creating Local BackUp...");
		path = dest;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String s[] = dest.split("\\.");
		// System.out.println(dest);
		// System.out.println(s[0]+".xls");
		File f = new File(s[0] + ".xls");
		// System.out.println(f.delete());
		Convert(dest, s[0] + ".xls");
	}

	public static void Convert(String fileName, String toName) throws IOException 
	{
		String ToName = toName;
		String fName = fileName;
		ArrayList arList = null;
		ArrayList al = null;
		String thisLine;
		int count = 0;
		FileInputStream fis = new FileInputStream(fName);
		//DataInputStream myInput = new DataInputStream(fis);
		BufferedReader myInput= new BufferedReader(new InputStreamReader(fis));
		int i = 0;
		arList = new ArrayList();
		//int c_o = 0;
		while ((thisLine = myInput.readLine()) != null) 
		{
			// System.out.println("Reading line.."+c_o++);
			al = new ArrayList();
			String strar[] = thisLine.split(",");
			for (int j = 0; j < strar.length; j++) 
			{
				al.add(strar[j]);
			}
			arList.add(al);
			// System.out.println();
			i++;
		}

		try 
		{
			HSSFWorkbook hwb = new HSSFWorkbook();
			HSSFSheet sheet = hwb.createSheet("new sheet");
			//int p_o = 0;
			for (int k = 0; k < arList.size(); k++) {
				// System.out.println("Writing line.."+p_o++);
				ArrayList ardata = (ArrayList) arList.get(k);
				HSSFRow row = sheet.createRow((short) 0 + k);
				for (int p = 0; p < ardata.size(); p++) {

					HSSFCell cell = row.createCell((short) p);
					String data = ardata.get(p).toString();
					if (data.startsWith("=")) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						data = data.replaceAll("\"", "");
						data = data.replaceAll("=", "");
						cell.setCellValue(data);
					} else if (data.startsWith("\"")) {
						data = data.replaceAll("\"", "");
						cell.setCellType(Cell.CELL_TYPE_STRING);
						cell.setCellValue(data);
					} else {
						//data = data.replaceAll("\"", "");
						cell.setCellType(Cell.CELL_TYPE_NUMERIC);
						cell.setCellValue(data);
					}
					// */
					// cell.setCellValue(ardata.get(p).toString());
				}
				// System.out.println();
			}
			FileOutputStream fileOut = new FileOutputStream(ToName);
			hwb.write(fileOut);
			fileOut.close();
			System.out.println("Your excel file has been generated:" + ToName);
			myInput.close();
			hwb.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
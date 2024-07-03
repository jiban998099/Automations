package com.OrderStaus;
/**
 * @author Rajdeep Chakraborty
 */
public class OrderCreateDMR
{
	public static void main(String[] args)
	{
		String inputFileCompletePath="C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\OrderCreateDMR\\Input\\Order_details.zip";
		String outputFolderPath="C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\OrderCreateDMR\\Output";
		UnZip.unzipFolder(inputFileCompletePath, outputFolderPath);
		CheckOrderStatus c=new CheckOrderStatus();
		c.checkStatus(outputFolderPath);
	}
}
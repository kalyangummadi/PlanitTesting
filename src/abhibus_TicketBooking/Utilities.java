package abhibus_TicketBooking;

import java.util.Hashtable;

public class Utilities {
	
	    //Enables to get the data from the Data Sheet
		public static Object[][] getData(Xls_Reader xls, String testCaseName)
		{
			String sheetName="ticket-booking";
			int testStartRowNo=1;
			while(!xls.getCellData(sheetName, 0, testStartRowNo).equals(testCaseName))
			{
				testStartRowNo++;
			}
			int colStartRowNum=testStartRowNo+1;
			int dataStartRowNum=testStartRowNo+2;
			
			int rows=0;
			while(!xls.getCellData(sheetName, 0, dataStartRowNum+rows).equals(""))
			{
				rows++;
			}		
			int cols=0;
			while(!xls.getCellData(sheetName, cols, colStartRowNum).equals(""))
			{
				cols++;
			}
			Object[][] data=new Object[rows][1];
			int dataRow=0;
			Hashtable<String,String> ht=null;
			for(int rNum=dataStartRowNum;rNum<dataStartRowNum+rows;rNum++)
			{
				ht=new Hashtable<String,String>();
				for(int cNum=0;cNum<cols;cNum++)
				{
					String key=xls.getCellData(sheetName, cNum, colStartRowNum);
					String value=xls.getCellData(sheetName, cNum, rNum);
					ht.put(key, value);
				}
				data[dataRow][0]=ht;
				dataRow++;
			}

			return data;
		}
		
		//Enables to set the data into the Data Sheet
		public static void writeData(Xls_Reader xls, String sheetName1, String testCaseName, int rowNo, String fieldName, String dataTobeStored)
		{
			String sheetName="ticket-booking";
			int testStartRowNo=1;
			while(!xls.getCellData(sheetName, 0, testStartRowNo).equals(testCaseName))
			{
				testStartRowNo++;
			}
			int colStartRowNum=testStartRowNo+1;
			int dataStartRowNum=testStartRowNo+2;
			
			int rows=0;
			while(!xls.getCellData(sheetName, 0, dataStartRowNum+rows).equals(""))
			{
				rows++;
			}		
			int cols=0;
			while(!xls.getCellData(sheetName, cols, colStartRowNum).equals(""))
			{
				cols++;
			}
			for(int rNum=dataStartRowNum;rNum<dataStartRowNum+rows;rNum++)
			{
				for(int cNum=0;cNum<cols;cNum++)
				{
					if(xls.getCellData(sheetName, cNum,rNum-(rowNo+1) ).equals(fieldName))
					{						
						xls.setCellData(sheetName, fieldName, rNum,colStartRowNum, dataTobeStored);
						rowNo=rowNo+1;
					}
				}
		    }
		}
}
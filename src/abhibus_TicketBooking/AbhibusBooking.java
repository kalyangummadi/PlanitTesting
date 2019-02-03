package abhibus_TicketBooking;

import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AbhibusBooking {
	
	public static Xls_Reader xls=new Xls_Reader(Constants.EXCELPATH);
	public static String onwardAmount;
	public static String returnAmount;
	public static double totalAmount;

	public static void main(String[] args) throws NoSuchElementException {
		// TODO Auto-generated method stub

		try
		{
			System.setProperty(Constants.WEBDRIVER, Constants.CHROMEEXE);
			WebDriver driver = new ChromeDriver();
			driver.get(Constants.ABHIBUSURL);
			driver.manage().window().maximize();
			
			//Mouse over for the Logo
			WebElement element = driver.findElement(By.xpath("//a/img[@src='https://static.abhibus.com/img/logo1.png']"));
			Actions a = new Actions(driver);
			a.moveToElement(element).build().perform();
			String ToolTipText = element.getAttribute("title");	
			
			//Reading Data from the Excel
			String testCaseName="SeatBooking";
			Object[][] obj= Utilities.getData(xls, testCaseName);			
			int count=obj.length;
			for(int m=0;m<count;m++)
			{
				@SuppressWarnings("unchecked")
				Hashtable<String,String> xlData=(Hashtable<String,String>) obj[m][0];	
				
				//Enter Leaving From 
				WebElement elementSrc = driver.findElement(By.name("source"));
				elementSrc.sendKeys(xlData.get("Source").toString());
				driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS) ;
				driver.findElement(By.xpath("//body/ul[1]/li[1]")).click();
					
				//Enter Going To
				WebElement elementDes = driver.findElement(By.name("destination"));
				elementDes.sendKeys(xlData.get("Destination").toString());
				driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS) ;
				driver.findElement(By.xpath("//ul[2]//li[1]")).click();				
				
				//Pick Date of Journey
				driver.findElement(By.xpath("//input[@id='datepicker1']")).click();
				List<WebElement> dateWidgetTo = driver.findElements(By.xpath("//table[@class='ui-datepicker-calendar']"));
				List<WebElement> columnsTo =dateWidgetTo.get(0).findElements(By.tagName("td"));
				for (WebElement cell: columnsTo) {
		           
		            if (cell.getText().equals(xlData.get("FromDate").toString())) {
		                cell.click();
		                break;
		            }
		        }
				
				//Pick Date of Return
				driver.findElement(By.xpath("//input[@id='datepicker2']")).click();
				List<WebElement> dateWidgetReturn = driver.findElements(By.xpath("//table[@class='ui-datepicker-calendar']"));
				List<WebElement> columnsReturn =dateWidgetReturn.get(0).findElements(By.tagName("td"));
				for (WebElement cell: columnsReturn) {
		           
		            if (cell.getText().equals(xlData.get("ToDate").toString())) {
		                cell.click();
		                break;
		            }
		        }	
				
				//Click on Search
				WebElement searchBuses = driver.findElement(By.xpath("//a[@title='Search Buses']"));
				searchBuses.click();	
				
				//Select Bus for Journey To
				WebDriverWait wait = new WebDriverWait(driver, 20);
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='book'][contains(text(),'Select Seat')]")));				
				WebElement elementSelect = driver.findElement(By.xpath("//span[@class='book'][contains(text(),'Select Seat')]"));
				Actions as = new Actions(driver);
				as.moveToElement(elementSelect).build().perform();
				elementSelect.click();	
				
				//Implicit wait to load the seats available
				driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS) ;
				List<WebElement> availableSeats = driver.findElements(By.xpath("//li[@class='sleeper available']"));				
				if(availableSeats.size()>0)
				{
					driver.findElement(By.xpath("//a[@id='O1-15ZZ']")).click(); // We can pass this seat number from Excel Data 
				}
				else
				{
					System.out.println("No Seats Available");
				}
				
				//Boarding 
				driver.findElement(By.xpath("//select[@id='boardingpoint_id1']")).click();
				driver.findElement(By.xpath("//option[@value='"+xlData.get("BoardingPointSource").toString()+"']")).click();
				
				//Onward Journey Amount
				onwardAmount = driver.findElement(By.xpath("//span[@id='totalfare']")).getText();
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "OnwardJourney", onwardAmount);
				
				//Validating Source and Destination Fields 
				boolean sourceValue = driver.findElement(By.xpath("//strong[contains(text(),'"+xlData.get("Source").toString()+"')]")).isDisplayed();
				boolean destinationValue = driver.findElement(By.xpath("//strong[contains(text(),'"+xlData.get("Destination").toString()+"')]")).isDisplayed();
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "SourceValidation", Boolean.toString(sourceValue));
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "ReturnValidation", Boolean.toString(destinationValue));
				
				//Select Bus for Return Journey
				driver.findElement(By.xpath("//input[@id='btnEnable1']")).click();	
				Thread.sleep(2000);			
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@ng-show='pvtService.availableSeats > 0']")));			
				WebElement elementReturnSelect = driver.findElement(By.xpath("//a[@ng-show='pvtService.availableSeats > 0']"));
				Actions ars = new Actions(driver);
				ars.moveToElement(elementReturnSelect).build().perform();
				elementReturnSelect.click();
				
				List<WebElement> availableSeatsReturn = driver.findElements(By.xpath("//div[@id='search_list_return']//div[@class='borderdtd']//div[2]//div[1]//ul//li[@class='sleeper available']"));				
				if(availableSeatsReturn.size()>0)
				{			
					List<WebElement> selectSeat = driver.findElements(By.xpath("//div[@id='search_list_return']//div[@class='borderdtd']//div[2]//div[1]//ul//li//a"));
					selectSeat.get(0).click();
				}
				else
				{
					System.out.println("No Seats Available");
				}
				
				//Boarding
				driver.findElement(By.xpath("//select[@id='boardingpoint_id2']")).click();
				driver.findElement(By.xpath("//select[@id='boardingpoint_id2']//option[@value='"+xlData.get("BoardingPointDestination").toString()+"']")).click();
				
				//Return Journey Amount
				returnAmount = driver.findElements(By.xpath("//form//div//p[2]//span[@id='totalfare']")).get(1).getText();
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "ReturnJourney", returnAmount);
				totalAmount= Double.parseDouble(onwardAmount.toString())+Double.parseDouble(returnAmount.toString());
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "NetPayable", Double.toString(totalAmount));
				Utilities.writeData(xls, Constants.EXCELPATH,testCaseName, m, "LogoValidation", ToolTipText);
						
				//Click Continue for Payment
				//driver.findElement(By.xpath("//form[@id='frmSeat504350800']//input[@id='btnEnable1']")).click();	
				//driver.close();
			}
			
		} 
		catch (NoSuchElementException | InterruptedException e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

}

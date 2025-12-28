package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class ElderCare_EnquiryForm extends BaseClass {
	
	
	// Excel helper
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if (sheet.getRow(rowNum) == null)
                sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }
	
	 // ===================== Enquiry Form =====================
    @Test(priority = 1)
	  public void ElderCareProgramPage_EnquiryForm() throws Exception {

    	  driver.navigate().to("https://www.medanta.org/elder-care-program");
          driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    	
	        Thread.sleep(3000);
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        js.executeScript("window.scrollBy(0,1950)", "");
	        
	        Thread.sleep(3000);
	        driver.findElement(By.xpath("(//input[@type='text'])[6]")).sendKeys("Dipesh");
	        Thread.sleep(1000);
	        driver.findElement(By.xpath("(//input[@type='number'])[5]")).sendKeys("9876543210"); 
	        Thread.sleep(1000);
	        driver.findElement(By.xpath("(//input[@type='email'])[4]")).sendKeys("dipesh@yopmail.com");
	        Thread.sleep(1000);

	        WebElement cityDD = driver.findElement(By.xpath("(//select[@class='inputbox'])[2]"));
	        Select city = new Select(cityDD);
	        city.selectByIndex(1);
	        Thread.sleep(1000);
	        WebElement modeDD = driver.findElement(By.xpath("(//select[@class='inputbox'])[3]"));
	        Select preferredMode = new Select(modeDD);
	        preferredMode.selectByIndex(1);
	        Thread.sleep(1000);
	        driver.findElement(By.xpath("//input[@type='checkbox']")).click();
	        Thread.sleep(3000);
	        driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	        try {
	            WebElement successMsg = wait.until(
	                    ExpectedConditions.visibilityOfElementLocated(
	                            By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

	            writeExcel(10, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
	            System.out.println("✅ ElderCare_RequestACallbackForm Enquiry Form PASS");

	        } catch (Exception e) {

	            System.out.println("❌ ElderCare_RequestACallbackForm Enquiry Form FAIL");

	            String nameVal = driver.findElement(By.xpath("(//input[@type='text'])[6]")).getAttribute("value");
	            String mobileVal = driver.findElement(By.xpath("(//input[@type='number'])[5]")).getAttribute("value");
	            String emailVal = driver.findElement(By.xpath("(//input[@type='email'])[4]")).getAttribute("value");
	            String cityVal = city.getFirstSelectedOption().getText();
	            String modeVal = preferredMode.getFirstSelectedOption().getText();

	            StringBuilder errorMsg = new StringBuilder();
	            

	            try {
	                WebElement nameErr = driver.findElement(
	                        By.xpath("(//input[@type='text'])[6]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (nameErr.isDisplayed())
	                    errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement mobileErr = driver.findElement(
	                        By.xpath("(//input[@type='number'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (mobileErr.isDisplayed())
	                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
	            } catch (Exception ignored) {}
	            

	            try {
	                WebElement cityErr = driver.findElement(
	                        By.xpath("(//select[@class='inputbox'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (cityErr.isDisplayed())
	                    errorMsg.append("Mobile Error: ").append(cityErr.getText()).append(" | ");
	            } catch (Exception ignored) {}
	            
	            
	            Thread.sleep(3000);

	            String finalResult =
	                    "   Name=" + nameVal +
	                    " | Mobile=" + mobileVal +
	                    " | Email=" + emailVal +
	                    " | City=" + cityVal +
	                    " | Mode=" + modeVal +
	                    " | Errors => " + errorMsg;

	            writeExcel(10, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
	            Thread.sleep(3000);
	            writeExcel(10, 5, finalResult);
	            Thread.sleep(3000);
	            Assert.fail("ElderCare_RequestACallbackForm Enquiry form validation failed: " + finalResult);
	            Thread.sleep(3000);
	        }
	    }

}

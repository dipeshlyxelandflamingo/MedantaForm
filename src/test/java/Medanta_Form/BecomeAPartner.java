package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class BecomeAPartner extends BaseClass {

	@Test(priority = 1)
	public void BecomeAPartnerPage_BusinessModelForm() throws Exception {

	    driver.navigate().to("https://www.medanta.org/medanta-labs/become-a-partner");
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	    Thread.sleep(3000);
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript("scrollBy(0, 2300)");
	    Thread.sleep(3000);
	    // ---------- Fill Form (INTENTIONALLY INVALID) ----------
	    driver.findElement(By.xpath("(//input[@type='text'])[4]")).sendKeys("Dipesh");
	    Thread.sleep(1000);
	    driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");
	    Thread.sleep(1000);
	    driver.findElement(By.xpath("(//input[@type='text'])[5]")).sendKeys("9876543210"); 
	    Thread.sleep(1000);
	    driver.findElement(By.xpath("(//input[@type='text'])[6]")).sendKeys("Test");
	    Thread.sleep(1000);
	    driver.findElement(By.xpath("(//input[@type='text'])[7]")).sendKeys("123456"); 
	    Thread.sleep(3000);
	    driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        // ---------- PASS CHECK ----------
	        WebElement thankYouMsg = wait.until(
	                ExpectedConditions.visibilityOfElementLocated(
	                        By.xpath("//div[contains(text(),'Thank you')]")));

	        sheet.getRow(3).createCell(4).setCellValue("✅ FORM SUBMITTED SUCCSESSFULLY!");
	        System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");

	    } catch (Exception e) {

	        // ---------- FAIL ----------
	        System.out.println("❌ FORM FAIL");

	        // ---------- Get filled values ----------
	        String nameVal = driver.findElement(By.xpath("(//input[@type='text'])[4]"))
	                .getAttribute("value");
	        String emailVal = driver.findElement(By.xpath("(//input[@type='email'])[2]"))
	                .getAttribute("value");
	        String mobileVal = driver.findElement(By.xpath("(//input[@type='text'])[5]"))
	                .getAttribute("value");
	        String addressVal = driver.findElement(By.xpath("(//input[@type='text'])[6]"))
	                .getAttribute("value");
	        String pinVal = driver.findElement(By.xpath("(//input[@type='text'])[7]"))
	                .getAttribute("value");

	        // ---------- Capture Errors ----------
	        StringBuilder errorMsg = new StringBuilder();
	        
	        
	        try {
	            WebElement nameErr = driver.findElement(
	                    By.xpath("(//input[@type='text'])[4]/following-sibling::span"));
	            if (nameErr.isDisplayed())
	                errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
	        } catch (Exception ignored) {}
	        

	        try {
	            WebElement mobileErr = driver.findElement(
	                    By.xpath("(//input[@type='text'])[5]/following-sibling::span"));
	            if (mobileErr.isDisplayed())
	                errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
	        } catch (Exception ignored) {}	        

	        
	        try {
	            WebElement addressErr = driver.findElement(
	                    By.xpath("(//input[@type='text'])[6]/following-sibling::span"));
	            if (addressErr.isDisplayed())
	                errorMsg.append("Address Error: ").append(addressErr.getText()).append(" | ");
	        } catch (Exception ignored) {}
	        
	        
	        try {
	            WebElement pinErr = driver.findElement(
	                    By.xpath("(//input[@type='text'])[7]/following-sibling::span"));
	            if (pinErr.isDisplayed())
	                errorMsg.append("Pin Error: ").append(pinErr.getText()).append(" | ");
	        } catch (Exception ignored) {}
	        
	        
	        

	        // ---------- Final Excel Message ----------
	        String finalResult =
	                "Name=" + nameVal +
	                " | Email=" + emailVal +
	                " | Mobile=" + mobileVal +
	                " | City=" + addressVal +
	                " | Pincode=" + pinVal +
	                " | Errors => " + errorMsg;

	        sheet.getRow(3).createCell(4).setCellValue("❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
	        sheet.getRow(3).createCell(5).setCellValue(finalResult);
	        Thread.sleep(3000);
	        System.out.println(finalResult);
	        Assert.fail("Become a Partner form validation failed");
	        Thread.sleep(3000);
	    }
	}
	
}
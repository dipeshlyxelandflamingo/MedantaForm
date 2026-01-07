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

public class RequestAnEstimate extends BaseClass {

	

	@Test(priority = 1)
	public void RequestAnEstimatePage_PriceEstimateForm() {

		 driver.navigate().to("https://www.medanta.org/international-patient/services/request-an-estimate");

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		    // Scroll slightly
		    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,210)", "");

		    // ===== Locate fields =====
		    WebElement fnameInput = wait.until(ExpectedConditions
		            .visibilityOfElementLocated(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));
		    WebElement lnameInput = driver.findElement(By.xpath("//input[@placeholder='Enter Your Last Name']"));
		    WebElement emailInput = driver.findElement(By.xpath("//input[@placeholder='Enter Your E-mail']"));
		    WebElement mobileInput = driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile No.']"));
		    WebElement countryDD = driver.findElement(By.xpath("//select[@class='inputbox']"));
		    WebElement departmentDD = driver.findElement(By.xpath("(//select[@class='inputbox'])[2]"));
		    WebElement fileInput = driver.findElement(By.xpath("(//input[@type='file'])[2]"));
		    WebElement submitBtn = driver.findElement(By.xpath("(//button[@type='submit'])[3]"));

		    // ===== Fill form using slowType =====
		    slowType(fnameInput, "Dipesh");
		    slowType(lnameInput, "Singh");
		    slowType(emailInput, "dipesh@yopmail.com");
		    slowType(mobileInput, "9876543210");

		    // Select dropdowns
		    new Select(countryDD).selectByIndex(8);
		    new Select(departmentDD).selectByValue("Obstetrics");
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // Upload file
		    String filePath = System.getProperty("user.dir") + "\\SampleDocs\\upload.docx";
		    fileInput.sendKeys(filePath);
		    
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // ===== Submit =====
		    submitBtn.click();

		    // ====== TRY-CATCH starts from thank you message =====
		    try {
		        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you')]")));

		        writeExcel(24, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
		        System.out.println("✅ Request An Estimate PASS");

		    } catch (Exception e) {

		        System.out.println("❌ Request An Estimate FAIL");

		        // ===== Capture entered values directly =====
		        String fnameVal = fnameInput.getAttribute("value");
		        String lnameVal = lnameInput.getAttribute("value");
		        String emailVal = emailInput.getAttribute("value");
		        String mobileVal = mobileInput.getAttribute("value");

		        // ===== Capture field errors using try-catch =====
		        StringBuilder errorMsg = new StringBuilder();

		        try {
		            WebElement err = driver.findElement(By.xpath(
		                    "//label[contains(text(),'First Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("First Name Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(By.xpath(
		                    "//label[contains(text(),'Last Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Last Name Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(By.xpath(
		                    "//label[contains(text(),'E-mail')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(By.xpath(
		                    "//label[contains(text(),'Mobile')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        String finalResult = "FirstName=" + fnameVal + " | LastName=" + lnameVal + " | Mobile=" + mobileVal
		                + " | Email=" + emailVal + " | Errors => " + errorMsg;

		        writeExcel(24, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
		        writeExcel(24, 5, finalResult);
		        System.out.println(finalResult);
		        Assert.fail("Request An Estimate validation failed: " + finalResult);
		    }
		}
}
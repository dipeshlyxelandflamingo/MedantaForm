package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class SecondOpiniun extends BaseClass {

	// ================= Excel Helper =================
	public void writeExcel(int row, int col, String value) {
		try {
			if (sheet.getRow(row) == null)
				sheet.createRow(row);
			sheet.getRow(row).createCell(col).setCellValue(value);
		} catch (Exception e) {
			System.out.println("Excel write error: " + e.getMessage());
		}
	}

	@Test(priority=1)
	public void SecondOpinionPage_RequestACallbackForm() throws Exception {

	    driver.navigate().to("https://www.medanta.org/second-opinion");
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

	    // ================= Fill Form =================
	    driver.findElement(By.name("name")).sendKeys("Dipesh");
	    driver.findElement(By.name("mobile")).sendKeys("9876543210"); 
	    driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
	    driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']")).sendKeys("Test");

	    driver.findElement(By.xpath("(//input[@type='file'])[2]"))
	            .sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");

	    driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

	    // ================= Validation =================
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//div[contains(text(),'Thank you')]")));

	        System.out.println("✅ Second Opinion – Request A Callback PASS");
	        writeExcel(25, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

	    } catch (Exception e) {

	        // ===== Capture entered values =====
	        String nameVal = driver.findElement(By.name("name")).getAttribute("value");
	        String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
	        String emailVal = driver.findElement(By.name("email")).getAttribute("value");

	        StringBuilder errorMsg = new StringBuilder();

	        // ===== Field wise errors =====
	        try {
	            WebElement err = driver.findElement(
	                    By.xpath("//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	            if (err.isDisplayed())
	                errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        try {
	            WebElement err = driver.findElement(
	                    By.xpath("//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	            if (err.isDisplayed())
	                errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        try {
	            WebElement err = driver.findElement(
	                    By.xpath("//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
	            if (err.isDisplayed())
	                errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        // ===== Final Result =====
	        String finalResult =
	                "Name=" + nameVal +
	                " | Mobile=" + mobileVal +
	                " | Email=" + emailVal +
	                " | Errors => " + errorMsg;

	        System.out.println("❌ Second Opinion – Request A Callback FAIL");

	        writeExcel(25, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
	        writeExcel(25, 5, finalResult);
	        Thread.sleep(3000);
	        Assert.fail("Second Opinion validation failed: " + finalResult);
	        Thread.sleep(3000);
	    }
	}
	
}
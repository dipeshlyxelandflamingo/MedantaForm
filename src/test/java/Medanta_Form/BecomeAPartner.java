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

	@Test(priority=1)
	public void BecomeAPartnerPage_BusinessModelForm() {

		 driver.navigate().to("https://www.medanta.org/medanta-labs/become-a-partner");
		    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("scrollBy(0, 2300)", "");

		    driver.findElement(By.xpath("(//input[@type='text'])[4]")).sendKeys("Dipesh");
		    driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");
		    driver.findElement(By.xpath("(//input[@type='text'])[5]")).sendKeys("9876543210");
		    driver.findElement(By.xpath("(//input[@type='text'])[6]")).sendKeys("Test");
		    driver.findElement(By.xpath("(//input[@type='text'])[7]")).sendKeys("987654");
		    driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		    try {
		        WebElement emt = wait.until(ExpectedConditions
		                .visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

		        String msg = emt.getText();
		        System.out.println("Extracted message: " + msg);

		        // Using Assert instead of if-else
		        Assert.assertTrue(msg.contains("Thank you"), "Form submission message validation failed");

		        // Optional: Writing to Excel
		        sheet.getRow(2).createCell(4).setCellValue("PASS!");

		    } catch (Exception e) {
		        System.out.println("Element not found or timeout occurred: " + e.getMessage());
		        sheet.getRow(2).createCell(4).setCellValue("FAIL!");
		        Assert.fail("Form submission message not found or timeout occurred: " + e.getMessage());
		    }
		}
	
}
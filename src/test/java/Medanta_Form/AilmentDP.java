package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class AilmentDP extends BaseClass {

	@Test(priority = 1)
	public void AilmentDetailPage_RequestCallBackForm() throws InterruptedException {
	    driver.navigate().to(
	            "https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/orthopaedics/disease/achilles-tendon-injury-symptoms-causes-treatment-recovery");
	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

	    driver.findElement(By.xpath("//input[@name='name']")).sendKeys("Saurabh Test");
	    driver.findElement(By.xpath("//input[@name='mobile']")).sendKeys("9876543210");
	    driver.findElement(By.xpath("//input[@name='email']")).sendKeys("wakemedantatest@gmail.com");
	    driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"))
	            .sendKeys("Testing the form Please ignore");
	    Thread.sleep(2000);
	    driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        WebElement emt = wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//div[contains(text(), 'Thank you for filling the form.')]")));

	        String msg = emt.getText();
	        System.out.println("Extracted message: " + msg);

	        // Using Assert instead of if-else
	        Assert.assertTrue(msg.contains("Thank you"), "Form submission message validation failed");

	        // Optional: Writing to Excel if needed
	        sheet.getRow(1).createCell(4).setCellValue("PASS!");

	    } catch (Exception e) {
	        System.out.println("Element not found or timeout occurred: " + e.getMessage());
	        sheet.getRow(1).createCell(4).setCellValue("FAIL!");
	        Assert.fail("Form submission message not found or timeout occurred: " + e.getMessage());
	    }
	}
	
}
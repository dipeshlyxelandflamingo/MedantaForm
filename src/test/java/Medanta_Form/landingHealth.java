package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class landingHealth extends BaseClass {

	@Test(priority=1)
	public void LandingHealth_Page_QueryForm() throws InterruptedException {
		
		driver.navigate().to(
				"https://www.medanta.org/ehc/hishealth-checkup/L1gz");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.xpath("//input[@name='name']")).sendKeys("Saurabh Test");
		driver.findElement(By.xpath("//input[@name='mobile']")).sendKeys("9876543210");
		driver.findElement(By.xpath("//input[@name='email']"))
				.sendKeys("wakemedantatest@gmail.com");
		driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']")).sendKeys("Testing the form Please ignore");
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//button[@type='submit'])[2]")).click();

		
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        WebElement emt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
	                "//div[contains(text(), 'Your query has been Successfully Submitted')]"))); 

	        String msg = emt.getText();
	        System.out.println("Extracted message: " + msg); 

	        if (msg.contains("Your query")) {
	            System.out.println("PASS");
	            sheet.getRow(19).createCell(4).setCellValue("PASS!");
	        } else {
	            System.out.println("FAIL");
	            sheet.getRow(19).createCell(4).setCellValue("FAIL!");
	        }
	    } catch (Exception e) {
	        System.out.println("Element not found or timeout occurred: " + e.getMessage());
	    }

}
}
package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class planYT extends BaseClass {

	@Test
	public void TC_01() throws InterruptedException {
		driver.navigate().to("https://www.medanta.org/plan-your-trip");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.xpath("(//input[@type='text' and @placeholder='Enter Your Name'])[3]"))
				.sendKeys("Saurabh Test");
		driver.findElement(By.xpath("(//input[@type='number' and @placeholder='Enter Your Mobile Number'])[2]"))
				.sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]"))
				.sendKeys("wakemedantatest@gmail.com");
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			WebElement emt = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(text(), 'Thank you for filling the form.')]")));

			String msg = emt.getText();
			System.out.println("Extracted message: " + msg);

			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(23).createCell(5).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(23).createCell(5).setCellValue("FAIL!");
			}
		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}

	}
}
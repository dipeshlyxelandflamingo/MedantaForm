package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class RequestACallback extends BaseClass {

	@Test(priority=1)
	public void TelemedicinePage_RequestACallbackForm() {

		driver.navigate().to("https://www.medanta.org/tele-medicine");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("name")).sendKeys("Dipesh");
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//textarea [@class='inputbox']")).sendKeys("Test");
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));
			
			Thread.sleep(3000);
			
			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(24).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(24).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test(priority=2)
	public void Air_Ambulance() {

		driver.navigate().to("https://www.medanta.org/air-ambulance");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("name")).sendKeys("Dipesh");
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//textarea [@class='inputbox']")).sendKeys("Test");
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));
			
			Thread.sleep(3000);
			
			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(25).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(25).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test(priority=3)
	public void Health_Checkup() {

		driver.navigate().to("https://www.medanta.org/healthcheckup/gurugram-hospital");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("name")).sendKeys("Dipesh");
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//textarea [@class='inputbox']")).sendKeys("Test");
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));
			
			Thread.sleep(3000);
			
			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(26).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(26).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}
}

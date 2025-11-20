package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class HomeCare extends BaseClass {

	@Test (priority=1)
	public void HomeCarePage_RequestACallbackForm () {

		driver.navigate().to("https://www.medanta.org/home-care");
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

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(14).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(14).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test (priority=2)
	public void HomeCarePage_EnquiryForm() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,2000)", "");

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]")).sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[4]")).sendKeys("dipesh@yopmail.com");

		WebElement DD = driver.findElement(By.xpath("//select[@placeholder='Select Location']"));
		Select s = new Select(DD);
		s.selectByIndex(1);

		driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(15).createCell(4).setCellValue("PASS!");
				
			} else {
				System.out.println("FAIL");
				sheet.getRow(15).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test (priority=3)
	public void HomeCarePage_StrokeRehabilitationProgramPage_QueryForm() {

		driver.navigate().to("https://www.medanta.org/home-care-service-program/stroke-rehabilitation-program");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='number'])[3]")).sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");

		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(16).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(16).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test (priority=4)
	public void HomeCarePage_ElderCareProgramPage_QueryForm() {

		driver.navigate().to("https://www.medanta.org/home-care-service-program/elder-care-program");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='number'])[3]")).sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");

		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(17).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(17).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

}

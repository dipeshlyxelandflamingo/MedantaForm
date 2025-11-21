package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class MedantaPharmacy extends BaseClass {

	@Test
	public void MedantaPharmacyPage_RequestACallbackForm() {

		driver.navigate().to("https://www.medanta.org/medanta-pharmacy");
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
				sheet.getRow(20).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(20).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test
	public void TC_02() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,800)", "");

		driver.findElement(By.xpath("(//input[@type='text'])[5]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='email'])[3]")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("(//input[@type='file'])[2]")).sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");
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
				sheet.getRow(21).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(21).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}
}
package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class CPR extends BaseClass {

	// Helper method for Excel writing
	public void writeExcel(int rowNum, int cellNum, String value) {
		try {
			if (sheet.getRow(rowNum) == null)
				sheet.createRow(rowNum);
			sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
		} catch (Exception e) {
			System.out.println("Excel write error: " + e.getMessage());
		}
	}

	@Test
	public void CPRPage_EnrollnowForm() throws InterruptedException {

		driver.navigate().to("https://www.medanta.org/cpr");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		// Fill form (INTENTIONALLY INVALID MOBILE)
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("Test");

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]")).sendKeys("98210"); 

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[3]"))
				.sendKeys("wakemedantatest@gmail.com");

		Select locationDD = new Select(driver.findElement(By.xpath("//select[@placeholder='Select Location']")));
		locationDD.selectByVisibleText("Gurugram");

		Thread.sleep(1500);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			// SUCCESS CASE
			WebElement successMsg = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(8, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
			System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");

		} catch (Exception e) {

			System.out.println("❌ CPR Enroll Form FAIL");

			// Capture entered values
			String nameVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]"))
					.getAttribute("value");

			String mobileVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]"))
					.getAttribute("value");

			String emailVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[3]"))
					.getAttribute("value");

			String locationVal = locationDD.getFirstSelectedOption().getText();

			// Capture validation errors
			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement nameErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (nameErr.isDisplayed())
					errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement mobileErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Mobile Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (mobileErr.isDisplayed())
					errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement emailErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Email'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (emailErr.isDisplayed())
					errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement locationErr = driver.findElement(By.xpath(
						"//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
				if (locationErr.isDisplayed())
					errorMsg.append("Location Error: ").append(locationErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			// Final result string
			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Location="
					+ locationVal + " | Errors => " + errorMsg.toString();

			writeExcel(8, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
			  Thread.sleep(3000);
			writeExcel(8, 5, finalResult);

			Assert.fail("CPR Enroll form validation failed: " + finalResult);
			  Thread.sleep(3000);
		}
	}
}
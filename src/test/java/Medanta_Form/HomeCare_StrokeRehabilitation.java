package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class HomeCare_StrokeRehabilitation extends BaseClass {

	// Excel helper
	public void writeExcel(int rowNum, int cellNum, String value) {
		try {
			if (sheet.getRow(rowNum) == null)
				sheet.createRow(rowNum);
			sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
		} catch (Exception e) {
			System.out.println("Excel write error: " + e.getMessage());
		}
	}

	@Test(priority = 1)
	public void HomeCare_StrokeRehabilitation_QueryForm() throws Throwable {

		driver.navigate().to("https://www.medanta.org/home-care");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.navigate().to("https://www.medanta.org/home-care-service-program/stroke-rehabilitation-program");

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='number'])[3]")).sendKeys("9876543210"); 
		driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");

		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(17, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
			System.out.println("✅ Stroke Rehab Form PASS");

		} catch (Exception e) {

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name="
					+ driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).getAttribute("value")
					+ " | Mobile=" + driver.findElement(By.xpath("(//input[@type='number'])[3]")).getAttribute("value")
					+ " | Email=" + driver.findElement(By.xpath("(//input[@type='email'])[2]")).getAttribute("value")
					+ " | Errors => " + errorMsg;

			writeExcel(17, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
			writeExcel(17, 5, finalResult);
			  Thread.sleep(3000);
			Assert.fail("Stroke Rehabilitation validation failed: " + finalResult);
			  Thread.sleep(3000);
		}
	}

}

package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class HomeCare_RequestACallbackForm extends BaseClass {

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

	// ================= Request A Callback =================
	@Test(priority = 1)
	public void HomeCarePage_RequestACallbackForm() throws Throwable {

		driver.navigate().to("https://www.medanta.org/home-care");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.name("name")).sendKeys("Dipesh");
		driver.findElement(By.name("mobile")).sendKeys("9876543210"); 
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//textarea[@class='inputbox']")).sendKeys("Test");

		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(16, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

		} catch (Exception e) {

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@name='email']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("//textarea[@class='inputbox']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + driver.findElement(By.name("name")).getAttribute("value") + " | Mobile="
					+ driver.findElement(By.name("mobile")).getAttribute("value") + " | Email="
					+ driver.findElement(By.name("email")).getAttribute("value") + " | Errors => " + errorMsg;

			writeExcel(16, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
			writeExcel(16, 5, finalResult);
			  Thread.sleep(3000);
			Assert.fail(finalResult);
			  Thread.sleep(3000);
		}
	}

	

	

}
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

public class Headerform extends BaseClass {

	@Test(priority = 1)
	public void Header_RequestACallBackForm() {
		driver.navigate().to("https://www.medanta.org");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- Open Request Call Back modal --------
		WebElement callbackBtn = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Request Call Back']")));
		js.executeScript("arguments[0].click();", callbackBtn);

		// -------- Locate fields --------
		WebElement name = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fname")));
		WebElement mobile = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//input[@placeholder='Enter Your Mobile Number']")));
		// WebElement email =
		// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lname")));
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='submitpopupbt']")));

		// -------- Scroll first field into view --------
		scrollToElement(name);

		// -------- Fill form --------
		slowType(name, "Test");
		slowType(mobile, "9876543211");
		// slowType(email, "wakemedantatest@gmail.com");

		// -------- Submit --------
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submitBtn.click();

		try {// -------- PASS --------
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(12, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Header Request Call Back – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = safeGetValue(By.id("fname"));
			String mobileVal = safeGetValue(By.xpath("//input[@placeholder='Enter Your Mobile Number']"));
			String emailVal = safeGetValue(By.id("lname"));

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@id='fname']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Mobile Number']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@id='lname']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(12, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(12, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Header Request Call Back validation failed: " + finalResult);
		}
	}

	// ================= Helper Methods =================
	private String safeGetValue(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			return element.isDisplayed() ? element.getAttribute("value") : "";
		} catch (Exception e) {
			return "";
		}
	}
}
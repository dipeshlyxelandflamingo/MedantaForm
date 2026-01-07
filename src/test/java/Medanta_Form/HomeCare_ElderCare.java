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

public class HomeCare_ElderCare extends BaseClass {

	@Test(priority = 1)
	public void HomeCare_ElderCareProgram_QueryForm() {

		driver.navigate().to("https://www.medanta.org/home-care-service-program/elder-care-program");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// -------- Locate fields --------
		WebElement name = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));
		WebElement mobile = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='number'])[3]")));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='email'])[2]")));
		
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));

		// -------- Scroll and fill fields --------
		scrollToElement(name);
		slowType(name, "Dipesh");
		slowType(mobile, "9876543210");
		slowType(email, "dipesh@yopmail.com");

		

		// -------- Wait before submit --------
		try {
			Thread.sleep(800);
		} catch (Exception ignored) {
		}
		submitBtn.click();

		// -------- Try-catch for success/fail starts with success message --------
		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(14, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Elder Care Program Form – PASS");

		} catch (Exception e) {
			// -------- FAIL: capture entered values and errors --------
			String nameVal = name.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String emailVal = email.getAttribute("value");
			

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

			try {
				WebElement err = driver.findElement(By.xpath(
						"//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Location Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal  + " | Errors => " + errorMsg;

			writeExcel(14, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(14, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Elder Care Program validation failed: " + finalResult);
		}
	}
}
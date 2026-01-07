package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class planYT extends BaseClass {

	@Test(priority = 1)
	public void PlanYourTripPage_QueryForm() {
		driver.navigate().to("https://www.medanta.org/plan-your-trip");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// ===== Locate fields =====
		WebElement nameInput = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));
		WebElement mobileInput = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]")));
		
		WebElement submitBtn = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("(//button[@type='submit'])[3]")));

		// ===== Fill form with slow typing =====
		slowType(nameInput, "Test");
		slowType(mobileInput, "9876543210");
		// slowType(emailInput, "wakemedantatest@gmail.com");

		// ===== Submit form =====
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submitBtn.click();

		// ===== Try-catch for success/fail (EDP style) =====
		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(23, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Plan Your Trip – Query Form PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String mobileVal = mobileInput.getAttribute("value");
			// String emailVal = emailInput.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Errors => " + errorMsg;

			writeExcel(23, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(23, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Plan Your Trip validation failed: " + finalResult);
		}
	}
}
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

public class InternationalP extends BaseClass {

	

	@Test(priority = 1)
	public void InternationalPatientsPage_QueryForm() {

		driver.navigate().to("https://www.medanta.org/international-patient");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// -------- Locate fields --------
		WebElement nameInput = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@type='text' and @placeholder='Enter Your Name'])[3]")));
		WebElement mobileInput = wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("(//input[@type='number' and @placeholder='Enter Your Mobile Number'])[2]")));
		WebElement emailInput = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]")));
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));

		// -------- Fill form with slow typing --------
		slowType(nameInput, "Test");
		slowType(mobileInput, "9876543210");
		slowType(emailInput, "wakemedantatest@gmail.com");

		// -------- Submit form --------
		submitBtn.click();

		// -------- Try-catch for success/fail (EDP style) --------
		try {

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(18, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ International Patient Query Form PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String mobileVal = mobileInput.getAttribute("value");
			String emailVal = emailInput.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@type='text' and @placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@type='number' and @placeholder='Enter Your Mobile Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@type='email' and @placeholder='Enter Your Email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(18, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(18, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("International Patient Query form validation failed: " + finalResult);
		}
	}
}
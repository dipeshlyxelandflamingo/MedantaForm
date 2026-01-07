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

public class landingHealth extends BaseClass {

	

	@Test(priority = 1)
	public void LandingHealth_Page_QueryForm() {

		driver.navigate().to("https://www.medanta.org/ehc/hishealth-checkup/L1gz");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// -------- Locate fields --------
		WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
		WebElement mobileInput = driver.findElement(By.name("mobile"));
		WebElement emailInput = driver.findElement(By.name("email"));
		WebElement msgInput = driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"));
		WebElement submitBtn = driver.findElement(By.xpath("(//button[@type='submit'])[2]"));

		// -------- Fill form with slow typing --------
		slowType(nameInput, "Test");
		slowType(mobileInput, "9876543210");
		slowType(emailInput, "wakemedantatest@gmail.com");
		slowType(msgInput, "Testing the form Please ignore");

		// -------- Submit form --------
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submitBtn.click();

		// -------- Try-catch for success/fail (EDP style) --------
		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(text(),'Your query has been Successfully Submitted')]")));

			writeExcel(19, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Landing Health Query Form PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String mobileVal = mobileInput.getAttribute("value");
			String emailVal = emailInput.getAttribute("value");
			String msgVal = msgInput.getAttribute("value");

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
				WebElement err = driver.findElement(
						By.xpath("//input[@name='email']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(By.xpath(
						"//textarea[@placeholder='Enter Your Message']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ msgVal + " | Errors => " + errorMsg;

			writeExcel(19, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(19, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Landing Health Query form failed: " + finalResult);
		}
	}
}
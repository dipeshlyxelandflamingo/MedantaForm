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

public class MedantaLabs extends BaseClass {

	

	@Test(priority = 1)
	public void MedantaLabsPage_RequestACallbackForm() {

		driver.navigate().to("https://www.medanta.org/medanta-labs");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- Locate fields --------
		WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
		WebElement mobileInput = driver.findElement(By.name("mobile"));
		WebElement emailInput = driver.findElement(By.name("email"));
		WebElement messageInput = driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"));
		WebElement submitBtn = driver.findElement(By.xpath("(//button[@type='submit'])[3]"));

		// -------- Fill form with slow typing --------
		slowType(nameInput, "Dipesh");
		slowType(mobileInput, "9876543210");
		slowType(emailInput, "dipesh@yopmail.com");
		slowType(messageInput, "Test");

		// -------- Scroll to submit and click --------
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submitBtn.click();

		// -------- Try-catch for success/fail (EDP style) --------
		try {

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(20, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Medanta Labs – Request Callback PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String mobileVal = mobileInput.getAttribute("value");
			String emailVal = emailInput.getAttribute("value");
			String messageVal = messageInput.getAttribute("value");

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
						By.xpath("//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(By.xpath(
						"//textarea[@placeholder='Enter Your Message']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ messageVal + " | Errors => " + errorMsg;

			writeExcel(20, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(20, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Medanta Labs validation failed: " + finalResult);
		}
	}
}
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

	

	@Test(priority = 1)
	public void HomeCarePage_RequestACallbackForm() {

		driver.navigate().to("https://www.medanta.org/home-care");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		// -------- Locate fields --------
		WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
		WebElement mobileInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("mobile")));
		WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
		WebElement messageInput = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@class='inputbox']")));
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));

		// -------- Fill form --------
		slowType(nameInput, "Dipesh");
		slowType(mobileInput, "9876543210");
		slowType(emailInput, "dipesh@yopmail.com");
		slowType(messageInput, "Test");

		submitBtn.click();

		// -------- Try-catch for success/fail (EDP style) --------
		try {

			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(16, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ HomeCare Request Callback Form PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String mobileVal = mobileInput.getAttribute("value");
			String emailVal = emailInput.getAttribute("value");
			String msgVal = messageInput.getAttribute("value");

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

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ msgVal + " | Errors => " + errorMsg;

			writeExcel(16, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(16, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("HomeCare Request Callback form validation failed: " + finalResult);
		}
	}
}
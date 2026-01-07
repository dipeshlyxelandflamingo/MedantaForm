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

public class ContactUs_Feedback extends BaseClass {

	@Test(priority = 1)
	public void ContactUsPage_FeedbackForm() {

		driver.navigate().to("https://www.medanta.org/contact-us");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- Wait loader disappear --------
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));

		// -------- Open Feedback Form --------
		WebElement feedbackBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
		feedbackBtn.click();

		// -------- Locate fields --------
		WebElement message = wait.until(ExpectedConditions.elementToBeClickable(By.name("feedback_message")));
		WebElement name = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")));
		WebElement mobile = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]")));
		WebElement email = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]")));

		// -------- Scroll first field into view --------
		

		// -------- Fill form (SLOW TYPE) --------
		slowType(message, "Test");
		slowType(name, "Dipesh");
		slowType(mobile, "9876543210");
		slowType(email, "dipesh@yopmail.com");

		// -------- Safety validation --------
		Assert.assertFalse(message.getAttribute("value").isEmpty(), "Message is empty");
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email is empty");

		// -------- Submit --------
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[4]")));
		try {
			Thread.sleep(800);
		} catch (Exception ignored) {
		}
		submitBtn.click();

		try {
			// -------- PASS --------
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]")));

			writeExcel(6, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Feedback Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = name.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String emailVal = email.getAttribute("value");
			String msgVal = message.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(By.xpath(
						"//textarea[@name='feedback_message']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Feedback Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Phone Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your E-mail'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ msgVal + " | Errors => " + errorMsg;

			writeExcel(6, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(6, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Feedback form validation failed: " + finalResult);
		}
	}

}
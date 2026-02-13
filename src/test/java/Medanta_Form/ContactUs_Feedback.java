package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
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

		// -------- By locators (same as your code) --------
		By messageBy = By.name("feedback_message");
		By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
		By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]");
		By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]");
		By submitBy = By.xpath("(//button[@type='submit'])[4]");
		By successBy = By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]");

		// -------- Scroll first field into view --------
		WebElement msgForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(messageBy));
		scrollToElement(msgForScroll);

		// -------- Fill form (SAFE TYPE) --------
		typeAndEnsureValue(wait, js, messageBy, "Test");
		typeAndEnsureValue(wait, js, nameBy, "Dipesh");
		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

		// -------- Safety validation (fresh fetch) --------
		Assert.assertFalse(driver.findElement(messageBy).getAttribute("value").isEmpty(), "Message is empty");
		Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email is empty");

		// -------- Submit --------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
		try {
			Thread.sleep(800);
		} catch (Exception ignored) {
		}

		try {
			submitBtn.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", submitBtn);
		}

		try {
			// -------- PASS --------
			wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

			writeExcel(6, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Feedback Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(emailBy);
			String msgVal = safeGetValue(messageBy);

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

	// ✅ same helper (paste in every form for now)
	private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

				try {
					el.click();
				} catch (Exception ignored) {
				}
				try {
					el.clear();
				} catch (Exception ignored) {
				}

				slowType(el, value);

				try {
					Thread.sleep(250);
				} catch (InterruptedException ignored) {
				}
				String actual = el.getAttribute("value");
				if (actual != null && actual.trim().equals(value))
					return;

			} catch (StaleElementReferenceException ignored) {
			} catch (Exception ignored) {
			}
		}
		Assert.fail("Value did not persist for locator: " + locator + " expected='" + value + "'");
	}

	private String safeGetValue(By locator) {
		try {
			WebElement el = driver.findElement(locator);
			String v = el.getAttribute("value");
			return v == null ? "" : v;
		} catch (Exception e) {
			return "";
		}
	}
}
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

public class BlogDP extends BaseClass {

	@Test(priority = 1)
	public void BlogDetailPage_QueryForm() {

		driver.navigate().to(
				"https://www.medanta.org/patient-education-blog/omicron-variant-key-facts-you-need-to-know-in-2025");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// ---------- By locators (same xpath as your code) ----------
		By nameBy = By.xpath("(//label[normalize-space()='Name *']/parent::div/input)[2]");
		By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
		By emailBy = By.xpath("(//label[normalize-space()='Email *']/parent::div/input)[2]");
		By submitBy = By.xpath("(//button[@type='submit'])[3]");
		By thankYouBy = By.xpath("//*[contains(text(),'Thank you')]");

		// ---------- Scroll first field into view ----------
		WebElement nameElForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		scrollToElement(nameElForScroll);

		// ---------- Fill form (SAFE TYPE) ----------
		typeAndEnsureValue(wait, js, nameBy, "Test");
		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

		// ---------- Safety check (fresh fetch) ----------
		Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name empty");
		Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile empty");
		Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email empty");

		// ---------- Submit ----------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
		try {
			Thread.sleep(800);
		} catch (InterruptedException ignored) {
		}
		try {
			submitBtn.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", submitBtn);
		}

		try {
			// ---------- PASS ----------
			wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouBy));

			writeExcel(4, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ BLOG FORM SUBMITTED SUCCESSFULLY!");

		} catch (Exception e) {
			// ---------- FAIL ----------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(emailBy);

			StringBuilder errorMsg = new StringBuilder();

			try {
				String nameErr = driver.findElement(nameBy).getAttribute("validationMessage");
				if (nameErr != null && !nameErr.isEmpty())
					errorMsg.append("Name Error: ").append(nameErr).append(" | ");
			} catch (Exception ignored) {
			}

			// NOTE: your FAIL xpath had no [2] earlier; keeping your exact one,
			// but if this page has 2 forms then you may need [2] here too.
			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Mobile Number']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Email']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(4, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(4, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Blog query form validation failed");
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
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

public class AilmentDP extends BaseClass {

	@Test(priority = 1)
	public void AilmentDetailPage_RequestCallBackForm() {

		driver.navigate().to(
				"https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/orthopaedics/disease/achilles-tendon-injury-symptoms-causes-treatment-recovery");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// ---------- Locators (By use karenge; WebElement store nahi) ----------
		By nameBy = By.name("name");
		By mobileBy = By.name("mobile");
		By emailBy = By.name("email");
		By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
		By submitBy = By.xpath("(//button[@type='submit'])[3]");
		By thankYouBy = By.xpath("//*[contains(text(),'Thank you')]");

		// ---------- Wait for form ----------
		wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));

		// ---------- Scroll to form ----------
		WebElement nameElForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", nameElForScroll);

		// ---------- Fill Form (type + verify + retry) ----------
		typeAndEnsureValue(wait, js, nameBy, "dipesh");
		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");
		typeAndEnsureValue(wait, js, messageBy, "test message");

		// ---------- Final safety check (re-find fresh elements) ----------
		Assert.assertEquals(driver.findElement(nameBy).getAttribute("value"), "dipesh", "Name changed unexpectedly");
		Assert.assertEquals(driver.findElement(mobileBy).getAttribute("value"), "9876543210",
				"Mobile changed unexpectedly");
		Assert.assertEquals(driver.findElement(emailBy).getAttribute("value"), "dipesh@yopmail.com",
				"Email changed unexpectedly");
		Assert.assertEquals(driver.findElement(messageBy).getAttribute("value"), "test message",
				"Message changed unexpectedly");

		// ---------- Submit ONLY after all fields ----------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

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
			// ---------- PASS condition ----------
			wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouBy));

			writeExcel(1, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ FORM SUBMITTED SUCCESSFULLY!");

		} catch (Exception e) {

			System.out.println("❌ FORM FAIL");

			// ---------- Get filled values (fresh find) ----------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(emailBy);
			String msgVal = safeGetValue(messageBy);

			// ---------- Get errors ----------
			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement nameError = driver.findElement(
						By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'error')]"));
				if (nameError.isDisplayed()) {
					errorMsg.append("Name Error: ").append(nameError.getText()).append(" | ");
				}
			} catch (Exception ignored) {
			}

			try {
				WebElement mobileError = driver.findElement(
						By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'error')]"));
				if (mobileError.isDisplayed()) {
					errorMsg.append("Mobile Error: ").append(mobileError.getText()).append(" | ");
				}
			} catch (Exception ignored) {
			}

			try {
				WebElement emailEl = driver.findElement(emailBy);
				String emailError = emailEl.getAttribute("validationMessage");
				if (emailError != null && !emailError.isEmpty()) {
					errorMsg.append("Email Error: ").append(emailError).append(" | ");
				}
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ msgVal + " | Errors => " + errorMsg;

			// ---------- Write to Excel ----------
			writeExcel(1, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(1, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Form validation failed");
		}
	}

	// ============================================================
	// ✅ Helper: Type + verify value persists (handles re-render/value reset)
	// ============================================================
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

				// Use your existing slowType from BaseClass
				slowType(el, value);

				// small settle
				try {
					Thread.sleep(250);
				} catch (InterruptedException ignored) {
				}

				// re-check value
				String actual = el.getAttribute("value");
				if (actual != null && actual.trim().equals(value)) {
					return; // ✅ OK
				}

				System.out.println("⚠ Retry " + attempt + " | locator=" + locator + " | expected='" + value
						+ "' | actual='" + actual + "'");

			} catch (StaleElementReferenceException sere) {
				// re-render case: retry
				System.out.println("⚠ Stale element, retry " + attempt + " | locator=" + locator);
			} catch (Exception ex) {
				System.out.println("⚠ Exception, retry " + attempt + " | locator=" + locator + " | " + ex.getMessage());
			}
		}

		Assert.fail("Value did not persist after retries for locator: " + locator + " expected='" + value + "'");
	}

	// ============================================================
	// ✅ Helper: Safe get value (so failure logs don't crash)
	// ============================================================
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

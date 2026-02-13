package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class CPR extends BaseClass {

	@Test(priority = 1)
	public void CPRPage_EnrollnowForm() {

		driver.navigate().to("https://www.medanta.org/cpr");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- By locators (same as your code) --------
		By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
		By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
		By emailBy = By.xpath("(//input[@placeholder='Enter Your Email'])[3]");
		By locationBy = By.xpath("//select[@placeholder='Select Location']");
		By submitBy = By.xpath("(//button[@type='submit'])[3]");
		By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

		// -------- Scroll first field into view --------
		WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		scrollToElement(nameForScroll);

		// -------- Fill form (SAFE TYPE) --------
		typeAndEnsureValue(wait, js, nameBy, "Test");

		// Dropdown selection (ensure)
		selectByVisibleTextAndEnsure(wait, locationBy, "Gurugram");
		String locationVal = safeGetSelectedText(wait, locationBy);

		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, emailBy, "wakemedantatest@gmail.com");

		// -------- Safety validation (fresh fetch) --------
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

			writeExcel(8, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ CPR Enroll Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(emailBy);

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Mobile Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Email'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
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

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Location="
					+ locationVal + " | Errors => " + errorMsg;

			writeExcel(8, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(8, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("CPR Enroll form validation failed: " + finalResult);
		}
	}

	// ✅ text/textarea helper
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

	// ✅ dropdown helper (visible text)
	private void selectByVisibleTextAndEnsure(WebDriverWait wait, By selectLocator, String visibleText) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
				Select s = new Select(dd);
				s.selectByVisibleText(visibleText);

				String selected = s.getFirstSelectedOption().getText().trim();
				if (selected.equalsIgnoreCase(visibleText.trim()))
					return;

			} catch (StaleElementReferenceException ignored) {
			} catch (Exception ignored) {
			}
		}
		Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " text='" + visibleText + "'");
	}

	private String safeGetSelectedText(WebDriverWait wait, By selectLocator) {
		try {
			WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
			Select s = new Select(dd);
			String txt = s.getFirstSelectedOption().getText();
			return txt == null ? "" : txt.trim();
		} catch (Exception e) {
			return "";
		}
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
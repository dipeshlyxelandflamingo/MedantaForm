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

public class ContactUs_Enquiry extends BaseClass {

	@Test(priority = 1)
	public void ContactUsPage_EnquiryForm() {

		driver.navigate().to("https://www.medanta.org/contact-us");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// ---------- By locators (same xpath as your code) ----------
		By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
		By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[1]");
		By hospitalDDBy = By.xpath("//select[@placeholder='Select Hospital']");
		By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[1]");
		By messageBy = By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[1]");
		By submitBy = By.xpath("(//button[@type='submit'])[3]");

		// ---------- Scroll ----------
		WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		scrollToElement(nameForScroll);

		// ---------- Fill Form (SAFE TYPE) ----------
		typeAndEnsureValue(wait, js, nameBy, "dipesh");
		typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

		// Dropdown selection (ensure selected)
		selectByIndexAndEnsure(wait, hospitalDDBy, 1);

		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, messageBy, "Automation test enquiry");

		// ---------- Safety validation (fresh fetch) ----------
		Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email is empty");
		Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(driver.findElement(messageBy).getAttribute("value").isEmpty(), "Message is empty");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// ---------- Submit ----------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
		try {
			submitBtn.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", submitBtn);
		}

		try {
			// ---------- PASS ----------
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(5, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ FORM SUBMITTED SUCCESSFULLY!");

		} catch (Exception e) {
			// ---------- FAIL ----------
			StringBuilder errorMsg = new StringBuilder();

			// Name validation (fresh element)
			try {
				String nameErr = driver.findElement(nameBy).getAttribute("validationMessage");
				if (nameErr != null && !nameErr.isEmpty())
					errorMsg.append("Name Error: ").append(nameErr).append(" | ");
			} catch (Exception ignored) {
			}

			// Mobile validation
			try {
				WebElement mobileErr = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Phone Number']/following-sibling::div[contains(@class,'errmsg')]"));
				if (mobileErr.isDisplayed())
					errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			// Email validation
			try {
				WebElement emailErr = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your E-mail']/following-sibling::div[contains(@class,'errmsg')]"));
				if (emailErr.isDisplayed())
					errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = String.format("Name=%s | Mobile=%s | Email=%s | Errors => %s", safeGetValue(nameBy),
					safeGetValue(mobileBy), safeGetValue(emailBy), errorMsg);

			writeExcel(5, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(5, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Contact Us form validation failed");
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

	// ✅ dropdown helper
	private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
				Select s = new Select(dd);
				s.selectByIndex(index);

				// verify selection not default
				String selected = s.getFirstSelectedOption().getText().trim();
				if (selected != null && !selected.isEmpty())
					return;

			} catch (StaleElementReferenceException ignored) {
			} catch (Exception ignored) {
			}
		}
		Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
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
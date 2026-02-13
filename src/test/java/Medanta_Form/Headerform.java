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

public class Headerform extends BaseClass {

	@Test(priority = 1)
	public void Header_RequestACallBackForm() {

		driver.navigate().to("https://www.medanta.org");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- Open Request Call Back modal --------
		WebElement callbackBtn = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Request Call Back']")));
		js.executeScript("arguments[0].click();", callbackBtn);

		// -------- By locators (same as your code) --------
		By nameBy = By.id("fname");
		By mobileBy = By.xpath("//input[@placeholder='Enter Your Mobile Number']");
		By submitBy = By.xpath("//button[@class='submitpopupbt']");
		By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

		// -------- Scroll first field into view --------
		WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		scrollToElement(nameForScroll);

		// -------- Fill form (SAFE TYPE) --------
		typeAndEnsureValue(wait, js, nameBy, "Test");
		typeAndEnsureValue(wait, js, mobileBy, "9876543211");

		// -------- Submit --------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			submitBtn.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", submitBtn);
		}

		try {
			// -------- PASS --------
			wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

			writeExcel(12, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Header Request Call Back – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(By.id("lname")); // kept as you had (even though you don't fill it)

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@id='fname']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Mobile Number']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("//input[@id='lname']/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(12, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(12, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Header Request Call Back validation failed: " + finalResult);
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

	// ================= Helper Methods =================
	private String safeGetValue(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			return element.isDisplayed() ? element.getAttribute("value") : "";
		} catch (Exception e) {
			return "";
		}
	}
}
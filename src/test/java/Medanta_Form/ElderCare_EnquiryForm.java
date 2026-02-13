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

public class ElderCare_EnquiryForm extends BaseClass {

	@Test(priority = 1)
	public void ElderCareProgramPage_EnquiryForm() {

		driver.navigate().to("https://www.medanta.org/elder-care-program");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// -------- Optional hard scroll --------
		js.executeScript("window.scrollBy(0,1500)");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// -------- By locators (same as your code) --------
		By nameBy = By.xpath("(//input[@type='text'])[6]");
		By mobileBy = By.xpath("(//input[@type='number'])[5]");
		By emailBy = By.xpath("(//input[@type='email'])[4]");
		By cityBy = By.xpath("(//select[@class='inputbox'])[2]");
		By modeBy = By.xpath("(//select[@class='inputbox'])[3]");
		By checkboxBy = By.xpath("//input[@type='checkbox']");
		By submitBy = By.xpath("(//button[@type='submit'])[5]");
		By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

		// -------- Scroll and fill fields --------
		WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
		scrollToElement(nameForScroll);

		typeAndEnsureValue(wait, js, nameBy, "Dipesh");
		typeAndEnsureValue(wait, js, mobileBy, "9876543210");
		typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

		// dropdowns
		selectByIndexAndEnsure(wait, cityBy, 1);
		selectByIndexAndEnsure(wait, modeBy, 1);

		String cityVal = safeGetSelectedText(wait, cityBy);
		String modeVal = safeGetSelectedText(wait, modeBy);

		// checkbox (ensure checked)
		clickCheckboxAndEnsureChecked(wait, js, checkboxBy);

		// -------- Safety validation --------
		Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email is empty");

		// -------- Wait before submit --------
		WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
		try {
			Thread.sleep(1000);
		} catch (Exception ignored) {
		}

		try {
			submitBtn.click();
		} catch (Exception e) {
			js.executeScript("arguments[0].click();", submitBtn);
		}

		// -------- Try-catch for success/fail --------
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

			writeExcel(10, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ ElderCare Program Enquiry Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = safeGetValue(nameBy);
			String mobileVal = safeGetValue(mobileBy);
			String emailVal = safeGetValue(emailBy);

			// keep the values we already read (safer than stale Select on fail)
			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='text'])[6]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='number'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='email'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//select[@class='inputbox'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("City Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//select[@class='inputbox'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mode Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | City="
					+ cityVal + " | Mode=" + modeVal + " | Errors => " + errorMsg;

			writeExcel(10, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(10, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("ElderCare Program Enquiry form validation failed: " + finalResult);
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

	// ✅ dropdown helper (index)
	private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
				Select s = new Select(dd);
				s.selectByIndex(index);

				String selected = s.getFirstSelectedOption().getText().trim();
				if (selected != null && !selected.isEmpty())
					return;

			} catch (StaleElementReferenceException ignored) {
			} catch (Exception ignored) {
			}
		}
		Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
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

	// ✅ checkbox helper
	private void clickCheckboxAndEnsureChecked(WebDriverWait wait, JavascriptExecutor js, By checkboxLocator) {
		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				WebElement cb = wait.until(ExpectedConditions.elementToBeClickable(checkboxLocator));
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", cb);

				if (!cb.isSelected()) {
					try {
						cb.click();
					} catch (Exception e) {
						js.executeScript("arguments[0].click();", cb);
					}
				}

				if (cb.isSelected())
					return;

			} catch (StaleElementReferenceException ignored) {
			} catch (Exception ignored) {
			}
		}
		Assert.fail("Checkbox not selected for locator: " + checkboxLocator);
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
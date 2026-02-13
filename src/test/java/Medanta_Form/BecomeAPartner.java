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

public class BecomeAPartner extends BaseClass {

	

	 @Test(priority = 1)
	    public void BecomeAPartnerPage_BusinessModelForm() {

	        driver.navigate().to("https://www.medanta.org/medanta-labs/become-a-partner");

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ---------- Scroll to form ----------
	        js.executeScript("window.scrollBy(0,2600)");

	        // ---------- By locators (same xpath as your code) ----------
	        By nameBy = By.xpath("(//input[@type='text'])[4]");
	        By emailBy = By.xpath("(//input[@type='email'])[2]");
	        By mobileBy = By.xpath("(//input[@type='text'])[5]");
	        By addressBy = By.xpath("(//input[@type='text'])[6]");
	        By pinBy = By.xpath("(//input[@type='text'])[7]");
	        By submitBy = By.xpath("(//button[@type='submit'])[3]");

	        // ---------- Fill form (SAFE TYPE) ----------
	        typeAndEnsureValue(wait, js, nameBy, "Dipesh");
	        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");
	        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
	        typeAndEnsureValue(wait, js, addressBy, "Test");
	        typeAndEnsureValue(wait, js, pinBy, "123456");

	        // ---------- Safety check ----------
	        Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name empty");
	        Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email empty");
	        Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile empty");
	        Assert.assertFalse(driver.findElement(addressBy).getAttribute("value").isEmpty(), "Address empty");
	        Assert.assertFalse(driver.findElement(pinBy).getAttribute("value").isEmpty(), "Pin empty");

	        // ---------- Submit ----------
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

	        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
	        try { submitBtn.click(); } catch (Exception e) { js.executeScript("arguments[0].click();", submitBtn); }

	        try {
	            // ---------- PASS ----------
	            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Thank you')]")));

	            writeExcel(3, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	            System.out.println("✅ Become a Partner – PASS");

	        } catch (Exception e) {
	            // ---------- FAIL ----------
	            String nameVal = safeGetValue(nameBy);
	            String emailVal = safeGetValue(emailBy);
	            String mobileVal = safeGetValue(mobileBy);
	            String addressVal = safeGetValue(addressBy);
	            String pinVal = safeGetValue(pinBy);

	            StringBuilder errorMsg = new StringBuilder();

	            try {
	                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[4]/following-sibling::span"));
	                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[5]/following-sibling::span"));
	                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[6]/following-sibling::span"));
	                if (err.isDisplayed()) errorMsg.append("Address Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[7]/following-sibling::span"));
	                if (err.isDisplayed()) errorMsg.append("Pin Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            String finalResult =
	                    "Name=" + nameVal +
	                    " | Email=" + emailVal +
	                    " | Mobile=" + mobileVal +
	                    " | Address=" + addressVal +
	                    " | Pincode=" + pinVal +
	                    " | Errors => " + errorMsg;

	            writeExcel(3, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	            writeExcel(3, 5, finalResult);

	            System.out.println(finalResult);
	            Assert.fail("Become a Partner form validation failed");
	        }
	    }

	    // ✅ same helper (paste in every form for now)
	    private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
	                js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

	                try { el.click(); } catch (Exception ignored) {}
	                try { el.clear(); } catch (Exception ignored) {}

	                slowType(el, value);

	                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
	                String actual = el.getAttribute("value");
	                if (actual != null && actual.trim().equals(value)) return;

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
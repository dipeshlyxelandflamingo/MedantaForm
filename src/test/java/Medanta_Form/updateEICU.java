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

public class updateEICU extends BaseClass {

	 @Test(priority = 1)
	    public void EICUPage_RequestACallbackForm() {

	        driver.navigate().to("https://www.medanta.org/eicu");

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ===== By locators (same as your code) =====
	        By nameBy = By.name("name");
	        By mobileBy = By.name("mobile");
	        By emailBy = By.name("email");
	        By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
	        By submitBy = By.xpath("(//button[@type='submit'])[3]");
	        By thankYouBy = By.xpath("//div[contains(text(),'Thank you')]");

	        // ===== Wait for first field =====
	        wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));

	        // ===== Fill form (SAFE TYPE) =====
	        typeAndEnsureValue(wait, js, nameBy, "Test");
	        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
	        typeAndEnsureValue(wait, js, emailBy, "wakemedantatest@gmail.com");
	        typeAndEnsureValue(wait, js, messageBy, "Testing the form Please ignore");

	        // ===== Submit =====
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        // ===== Try-catch from Thank You message =====
	        try {
	            wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouBy));

	            writeExcel(29, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	            System.out.println("✅ EICU Page – Request A Callback Form PASS");

	        } catch (Exception e) {
	            System.out.println("❌ EICU Page – Request A Callback Form FAIL");

	            // ===== Capture field values (fresh fetch) =====
	            String nameVal = safeGetValue(nameBy);
	            String mobileVal = safeGetValue(mobileBy);
	            String emailVal = safeGetValue(emailBy);
	            String msgVal = safeGetValue(messageBy);

	            // ===== Capture errors =====
	            StringBuilder errorMsg = new StringBuilder();
	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
	                    + " | Message=" + msgVal + " | Errors => " + errorMsg;

	            writeExcel(29, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	            writeExcel(29, 5, finalResult);
	            Assert.fail("EICU Page form validation failed: " + finalResult);
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
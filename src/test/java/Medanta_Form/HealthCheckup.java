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

public class HealthCheckup extends BaseClass {


	  @Test(priority = 1)
	    public void HealthCheckup_RequestACallbackForm() {

	        driver.navigate().to("https://www.medanta.org/healthcheckup/gurugram-hospital");

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ===== By locators (same as your code) =====
	        By nameBy = By.name("name");
	        By mobileBy = By.name("mobile");
	        By emailBy = By.name("email");
	        By messageBy = By.xpath("//textarea[@class='inputbox']");
	        By submitBy = By.xpath("(//button[@type='submit'])[3]");
	        By successBy = By.xpath("//div[contains(text(),'Thank you')]");

	        // ===== Locate first field + scroll =====
	        WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	        scrollToElement(nameForScroll);

	        // ===== Fill form (SAFE TYPE) =====
	        typeAndEnsureValue(wait, js, nameBy, "Dipesh");
	        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
	        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");
	        typeAndEnsureValue(wait, js, messageBy, "Test");

	        // ===== Submit form =====
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        try {
	            // ===== PASS: Wait for success message =====
	            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

	            writeExcel(13, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	            System.out.println("✅ Health Checkup – PASS");

	        } catch (Exception e) {
	            // ===== FAIL: Capture entered values safely =====
	            String nameVal = safeGetValue(nameBy);
	            String mobileVal = safeGetValue(mobileBy);
	            String emailVal = safeGetValue(emailBy);
	            String messageVal = safeGetValue(messageBy);

	            StringBuilder errorMsg = new StringBuilder();
	            try {
	                WebElement err = driver.findElement(
	                        By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(
	                        By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(
	                        By.xpath("//input[@name='email']/following-sibling::span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(
	                        By.xpath("//textarea[@class='inputbox']/following-sibling::span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed()) errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
	                    + " | Message=" + messageVal
	                    + " | Errors => " + errorMsg;

	            writeExcel(13, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	            writeExcel(13, 5, finalResult);

	            System.out.println(finalResult);
	            Assert.fail("Health Checkup form validation failed: " + finalResult);
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

	    // ===== Helper: safely get field value =====
	    private String safeGetValue(By locator) {
	        try {
	            WebElement element = driver.findElement(locator);
	            String v = element.getAttribute("value");
	            return v == null ? "" : v;
	        } catch (Exception e) {
	            return "";
	        }
	    }
	}
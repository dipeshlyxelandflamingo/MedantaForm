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

public class planYT extends BaseClass {

	 @Test(priority = 1)
	    public void PlanYourTripPage_QueryForm() {

	        driver.navigate().to("https://www.medanta.org/plan-your-trip");

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ===== By locators (same as your code) =====
	        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
	        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
	        By submitBy = By.xpath("(//button[@type='submit'])[3]");
	        By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

	        // ===== Fill form (SAFE TYPE) =====
	        typeAndEnsureValue(wait, js, nameBy, "Test");
	        typeAndEnsureValue(wait, js, mobileBy, "9876543210");

	        // ===== Submit form =====
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        // ===== Try-catch for success/fail (EDP style) =====
	        try {
	            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

	            writeExcel(23, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	            System.out.println("✅ Plan Your Trip – Query Form PASS");

	        } catch (Exception e) {

	            String nameVal = safeGetValue(nameBy);
	            String mobileVal = safeGetValue(mobileBy);

	            StringBuilder errorMsg = new StringBuilder();
	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            // keeping your email error block (even though email field not filled)
	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "(//input[@placeholder='Enter Your Email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Errors => " + errorMsg;

	            writeExcel(23, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	            writeExcel(23, 5, finalResult);

	            System.out.println(finalResult);
	            Assert.fail("Plan Your Trip validation failed: " + finalResult);
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
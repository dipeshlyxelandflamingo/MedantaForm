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

public class ContactUs_Others extends BaseClass {

	 @Test(priority = 1)
	    public void ContactUsPage_OthersForm() {

	        int row = 7;

	        String url = "https://www.medanta.org/contact-us";
	        driver.navigate().to(url);

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        System.out.println("➡️ [ContactUs_Others] Opening page...");

	        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

	        // loader disappear
	        try {
	            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
	        } catch (Exception ignored) {}

	        // Open Others Form
	        WebElement otherFormBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
	        otherFormBtn.click();

	        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

	        // ===== Locators =====
	        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[5]");
	        By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]");
	        By hospitalBy = By.xpath("(//select[@name='hospital'])[2]");
	        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]");
	        By messageBy = By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]");
	        By submitBy = By.xpath("(//button[@type='submit'])[5]");

	        // Success element (your original)
	        By successBy = By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]");

	        // 🔥 Strong ThankYou fallback
	        By thankYouBy = By.xpath(
	                "//*[contains(@class,'successmsg') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank')]"
	                        + " | //*[(contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted'))]"
	        );

	        // ===== Test Data =====
	        String expName = "dipesh";
	        String expEmail = "dipesh@yopmail.com";
	        int hospitalIndex = 1;
	        String expMobile = "9876543210";
	        String expMsg = "Test";

	        System.out.println("➡️ [ContactUs_Others] Filling form...");

	        typeAndEnsureValue(wait, js, nameBy, expName);
	        typeAndEnsureValue(wait, js, emailBy, expEmail);

	        // Dropdown
	        selectByIndexAndEnsure(wait, hospitalBy, hospitalIndex);
	        String hospitalVal = safeGetSelectedText(wait, hospitalBy);

	        typeAndEnsureValue(wait, js, mobileBy, expMobile);
	        typeAndEnsureValue(wait, js, messageBy, expMsg);

	        // ⭐ value wipe protection for text fields
	        ensureValueStillPresent(nameBy, expName);
	        ensureValueStillPresent(emailBy, expEmail);
	        ensureValueStillPresent(mobileBy, expMobile);
	        ensureValueStillPresent(messageBy, expMsg);

	        // ✅ capture inputs BEFORE submit
	        String inputs =
	                "Name=" + safeGetValue(nameBy)
	                        + " | Email=" + safeGetValue(emailBy)
	                        + " | Hospital=" + hospitalVal
	                        + " | Mobile=" + safeGetValue(mobileBy)
	                        + " | Message=" + safeGetValue(messageBy);

	        // ===== Submit =====
	        System.out.println("➡️ [ContactUs_Others] Clicking submit...");
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

	        try { Thread.sleep(800); } catch (Exception ignored) {}

	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        // ===== Detect outcomes =====
	        boolean thankYouSeen =
	                waitForFlashPresence(successBy, 4000) || waitForFlashPresence(thankYouBy, 8000);

	        boolean network5xx = waitForNetwork5xx(9000);

	        String fieldErrors = collectAllValidationErrors();
	        String globalErrors = collectGlobalErrors();

	        // ===== Decide status =====
	        String status;
	        String serverInfo = "";

	        if (thankYouSeen && network5xx) {
	            status = "❌ SERVER_FAIL (POST SUBMIT)";
	            serverInfo = "API returned 5xx after submit";
	        } else if (thankYouSeen) {
	            status = "✅ PASS";
	        } else if (fieldErrors != null && !fieldErrors.isBlank()) {
	            status = "❌ VALIDATION_FAIL";
	        } else if (network5xx || (globalErrors != null && !globalErrors.isBlank())) {
	            status = "❌ SERVER_FAIL";
	            serverInfo = network5xx ? "API returned 5xx" : "Global error shown";
	        } else {
	            status = "⚠ UNKNOWN";
	            serverInfo = "No success/error signal detected";
	        }

	        String debug = driver.getCurrentUrl() + " | " + driver.getTitle();

	        // ===== PRINT =====
	        System.out.println("============== CONTACT US OTHERS FORM RESULT ==============");
	        System.out.println("STATUS        : " + status);
	        System.out.println("THANK YOU     : " + thankYouSeen);
	        System.out.println("NETWORK 5XX   : " + network5xx);
	        System.out.println("INPUTS        : " + inputs);
	        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	        System.out.println("SERVER INFO   : " + serverInfo);
	        System.out.println("DEBUG         : " + debug);
	        System.out.println("===========================================================");

	        // ===== Excel (E → L) =====
	        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);

	        if (!status.contains("PASS")) {
	            Assert.fail("ContactUs_Others form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= SAFE TYPE ================= */

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

	    /* ================= DROPDOWN HELPERS ================= */

	    private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
	                Select s = new Select(dd);
	                s.selectByIndex(index);

	                String selected = s.getFirstSelectedOption().getText();
	                if (selected != null && !selected.trim().isEmpty()) return;

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

	    /* ================= SAFE GET VALUE ================= */

	    private String safeGetValue(By locator) {
	        try {
	            WebElement el = driver.findElement(locator);
	            String v = el.getAttribute("value");
	            return v == null ? "" : v.trim();
	        } catch (Exception e) {
	            return "";
	        }
	    }
	}
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

	        int row = 29;

	        // ✅ Make these available to finally block (so Excel ALWAYS writes)
	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        String serverInfo = "";
	        boolean thankYouSeen = false;
	        String debug = "";

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ===== URL =====
	        String url = "https://www.medanta.org/eicu";

	        // ===== Locators =====
	        By nameBy     = By.name("name");
	        By mobileBy   = By.name("mobile");
	        By emailBy    = By.name("email");
	        By messageBy  = By.xpath("//textarea[@placeholder='Enter Your Message']");
	        By submitBy   = By.xpath("(//button[@type='submit'])[3]");

	        // ✅ Strong thank you (your old one was fine, this is stronger/consistent)
	        By thankYouBy = By.xpath(
	                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	        );

	        // Optional DOM check (may or may not appear)
	        By server500DomBy = By.xpath(
	                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'500') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'server error') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'internal server error')]"
	        );

	        // ===== Test Data =====
	        String expName   = "Test";
	        String expMobile = "9876543210";
	        String expEmail  = "wakemedantatest@gmail.com";
	        String expMsg    = "Testing the form Please ignore";

	        try {
	            driver.navigate().to(url);

	            System.out.println("➡️ [EICU] Opening page...");
	            wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));

	            // Scroll to form
	            WebElement nameElForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", nameElForScroll);

	            System.out.println("➡️ [EICU] Filling form...");

	            typeAndEnsureValue(wait, js, nameBy, expName);
	            typeAndEnsureValue(wait, js, mobileBy, expMobile);
	            typeAndEnsureValue(wait, js, emailBy, expEmail);
	            typeAndEnsureValue(wait, js, messageBy, expMsg);

	            // value wipe protection
	            ensureValueStillPresent(nameBy, expName);
	            ensureValueStillPresent(mobileBy, expMobile);
	            ensureValueStillPresent(emailBy, expEmail);
	            ensureValueStillPresent(messageBy, expMsg);

	            // ✅ capture inputs BEFORE submit
	            inputs =
	                    "Name=" + safeGetValue(nameBy)
	                            + " | Mobile=" + safeGetValue(mobileBy)
	                            + " | Email=" + safeGetValue(emailBy)
	                            + " | Message=" + safeGetValue(messageBy);

	            // ===== Submit =====
	            System.out.println("➡️ [EICU] Clicking submit...");

	            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

	            try { Thread.sleep(800); } catch (InterruptedException ignored) {}

	            // ✅ baseline logs before submit (so only post-submit 5xx counts)
	            clearPerformanceLogs();

	            try {
	                submitBtn.click();
	            } catch (Exception e) {
	                js.executeScript("arguments[0].click();", submitBtn);
	            }

	            // ===== Detect outcomes =====
	            thankYouSeen = waitForFlashPresence(thankYouBy, 8000);

	            boolean network5xx = waitForNetwork5xx(9000);

	            // ✅ Server error fallback checks
	            boolean server500Seen =
	                    network5xx ||
	                    isServer500Like() ||
	                    waitForPresence(server500DomBy, 3000);

	            fieldErrors  = collectAllValidationErrors();
	            globalErrors = collectGlobalErrors();

	            // ===== Decide Status =====
	            if (thankYouSeen && server500Seen) {
	                status = "❌ SERVER_FAIL (POST SUBMIT)";
	                serverInfo = network5xx ? "API returned 5xx after submit" : "500/Server Error detected after submit";
	            } else if (thankYouSeen) {
	                status = "✅ PASS";
	            } else if (fieldErrors != null && !fieldErrors.isBlank()) {
	                status = "❌ VALIDATION_FAIL";
	            } else if (server500Seen || (globalErrors != null && !globalErrors.isBlank())) {
	                status = "❌ SERVER_FAIL";
	                serverInfo = network5xx ? "API returned 5xx" : (server500Seen ? "500/Server Error detected" : "Global error shown");
	            } else {
	                status = "⚠ UNKNOWN";
	                serverInfo = "No success/error signal detected";
	            }

	        } catch (Exception e) {

	            status = "❌ EXCEPTION";
	            serverInfo = e.getClass().getSimpleName() + " | " + e.getMessage();

	            if (isServer500Like()) {
	                status = "❌ SERVER_FAIL (PAGE 500)";
	                serverInfo = "500 page detected during flow";
	            }

	        } finally {

	            try {
	                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
	            } catch (Exception ignored) {
	                debug = "Debug not available";
	            }

	            System.out.println("============== EICU FORM RESULT ==============");
	            System.out.println("STATUS        : " + status);
	            System.out.println("THANK YOU     : " + thankYouSeen);
	            System.out.println("INPUTS        : " + inputs);
	            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("SERVER INFO   : " + serverInfo);
	            System.out.println("DEBUG         : " + debug);
	            System.out.println("===============================================");

	            // ✅ Excel ALWAYS writes
	            writeFormResult(
	                    row,
	                    status,
	                    inputs,
	                    fieldErrors,
	                    globalErrors,
	                    serverInfo,
	                    thankYouSeen,
	                    debug
	            );
	        }

	        // ✅ Fail AFTER excel write
	        if (!status.contains("PASS")) {
	            Assert.fail("EICU form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= Local helpers ================= */

	    private boolean waitForPresence(By locator, int maxMillis) {
	        long end = System.currentTimeMillis() + maxMillis;
	        while (System.currentTimeMillis() < end) {
	            try {
	                if (!driver.findElements(locator).isEmpty()) return true;
	            } catch (Exception ignored) {}
	            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
	        }
	        return false;
	    }

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

	        Assert.fail("Value did not persist for locator: " + locator);
	    }

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
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

	        int row = 23;

	        // ✅ Make these available to finally block (so Excel ALWAYS writes)
	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        String serverInfo = "";
	        boolean successSeen = false;
	        String debug = "";

	        try {

	            String url = "https://www.medanta.org/plan-your-trip";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            // ===== Locators (your original) =====
	            By nameBy   = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
	            By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
	            By submitBy = By.xpath("(//button[@type='submit'])[3]");

	            // success (your original)
	            By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

	            // fallback success (flash/text changes)
	            By successFallbackBy = By.xpath(
	                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	            );

	            // ===== Test Data =====
	            String expName = "Test";
	            String expMobile = "9876543210";

	            System.out.println("➡️ [PlanYourTrip] Opening page...");

	            // wait first field visible & scroll
	            WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	            scrollToElement(first);

	            System.out.println("➡️ [PlanYourTrip] Filling form...");

	            typeAndEnsureValue(wait, js, nameBy, expName);
	            typeAndEnsureValue(wait, js, mobileBy, expMobile);

	            // ⭐ value wipe protection
	            ensureValueStillPresent(nameBy, expName);
	            ensureValueStillPresent(mobileBy, expMobile);

	            // ✅ capture inputs BEFORE submit
	            inputs = "Name=" + safeGetValue(nameBy)
	                    + " | Mobile=" + safeGetValue(mobileBy);

	            // ===== Submit =====
	            System.out.println("➡️ [PlanYourTrip] Clicking submit...");
	            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

	            try { Thread.sleep(800); } catch (Exception ignored) {}

	            // ✅ clear perf logs before submit (post-submit only check)
	            clearPerformanceLogs();

	            try {
	                submitBtn.click();
	            } catch (Exception e) {
	                js.executeScript("arguments[0].click();", submitBtn);
	            }

	            // ===== Detect outcomes =====
	            successSeen =
	                    waitForFlashPresence(successBy, 4000) || waitForFlashPresence(successFallbackBy, 9000);

	            boolean network5xx = waitForNetwork5xx(9000);

	            fieldErrors = collectAllValidationErrors();
	            globalErrors = collectGlobalErrors();

	            // ===== Decide Status =====
	            if (successSeen && network5xx) {
	                status = "❌ SERVER_FAIL (POST SUBMIT)";
	                serverInfo = "API returned 5xx after submit";
	            } else if (successSeen) {
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

	        } catch (Exception e) {

	            if (status == null || status.trim().isEmpty() || status.equals("⚠ UNKNOWN")) {
	                status = "❌ EXCEPTION";
	                serverInfo = e.getClass().getSimpleName() + " | " + e.getMessage();
	            }

	            if (isServer500Like()) {
	                status = "❌ SERVER_FAIL (PAGE 500)";
	                serverInfo = "500 page detected during flow";
	            }

	        } finally {

	            // always compute debug safely
	            try {
	                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
	            } catch (Exception ignored) {
	                debug = "Debug not available";
	            }

	            // ===== PRINT =====
	            System.out.println("============== PLAN YOUR TRIP FORM RESULT ==============");
	            System.out.println("STATUS        : " + status);
	            System.out.println("SUCCESS       : " + successSeen);
	            System.out.println("INPUTS        : " + inputs);
	            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("SERVER INFO   : " + serverInfo);
	            System.out.println("DEBUG         : " + debug);
	            System.out.println("========================================================");

	            // ✅ Excel ALWAYS writes
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);
	        }

	        // ✅ Fail AFTER excel write
	        if (!status.contains("PASS")) {
	            Assert.fail("Plan Your Trip form failed -> " + status + " | " + debug);
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
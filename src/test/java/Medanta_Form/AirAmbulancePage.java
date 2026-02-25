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

public class AirAmbulancePage extends BaseClass {

	@Test(priority = 1)
    public void AirAmbulance_RequestACallbackForm() {

        int row = 2;

        // ✅ Make these available to finally block (so Excel ALWAYS writes)
        String status = "⚠ UNKNOWN";
        String inputs = "";
        String fieldErrors = "";
        String globalErrors = "";
        String serverInfo = "";
        boolean thankYouSeen = false;
        String debug = "";

        try {
            String url = "https://www.medanta.org/air-ambulance";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // ===== Locators =====
            By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
            By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
            By emailBy = By.name("email");
            By messageBy = By.xpath("//textarea[@class='inputbox']");
            By submitBy = By.xpath("(//button[@class='theme-button'])[2]");

            // 🔥 Strong ThankYou locator
            By thankYouBy = By.xpath(
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
            );

            // ===== Test Data =====
            String expName = "Dipesh";
            String expMobile = "9876543210";
            String expEmail = "dipesh@yopmail.com";
            String expMsg = "Test";

            System.out.println("➡️ [AirAmbulance] Opening page...");
            WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", nameForScroll);

            System.out.println("➡️ [AirAmbulance] Filling form...");
            typeAndEnsureValue(wait, js, nameBy, expName);
            typeAndEnsureValue(wait, js, mobileBy, expMobile);
            typeAndEnsureValue(wait, js, emailBy, expEmail);
            typeAndEnsureValue(wait, js, messageBy, expMsg);

            // ⭐ value wipe protection
            ensureValueStillPresent(nameBy, expName);
            ensureValueStillPresent(mobileBy, expMobile);
            ensureValueStillPresent(emailBy, expEmail);
            ensureValueStillPresent(messageBy, expMsg);

            // ✅ Capture inputs BEFORE submit (form may clear after submit)
            inputs =
                    "Name=" + safeGetValue(nameBy)
                            + " | Mobile=" + safeGetValue(mobileBy)
                            + " | Email=" + safeGetValue(emailBy)
                            + " | Message=" + safeGetValue(messageBy);

            // ===== Submit =====
            System.out.println("➡️ [AirAmbulance] Clicking submit...");
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

            try { Thread.sleep(800); } catch (InterruptedException ignored) {}

            // ✅ Recommended: clear perf logs before submit, so only post-submit 5xx is captured
            clearPerformanceLogs();

            try {
                submitBtn.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", submitBtn);
            }

            // ===== Detect outcomes =====
            thankYouSeen = waitForFlashPresence(thankYouBy, 8000);
            boolean network5xx = waitForNetwork5xx(9000);

            fieldErrors = collectAllValidationErrors();
            globalErrors = collectGlobalErrors();

            // ===== Decide status =====
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

        } catch (Exception e) {

            status = "❌ EXCEPTION";
            serverInfo = e.getClass().getSimpleName() + " | " + e.getMessage();

            if (isServer500Like()) {
                status = "❌ SERVER_FAIL (PAGE 500)";
                serverInfo = "500 page detected during flow";
            }

        } finally {

            // Always compute debug safely
            try {
                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
            } catch (Exception ignored) {
                debug = "Debug not available";
            }

            // ===== PRINT =====
            System.out.println("============== AIR AMBULANCE FORM RESULT ==============");
            System.out.println("STATUS        : " + status);
            System.out.println("THANK YOU     : " + thankYouSeen);
            System.out.println("INPUTS        : " + inputs);
            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("SERVER INFO   : " + serverInfo);
            System.out.println("DEBUG         : " + debug);
            System.out.println("=======================================================");

            // ✅ Excel ALWAYS writes
            writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);
        }

        // ✅ Fail AFTER excel write
        if (!status.contains("PASS")) {
            Assert.fail("AirAmbulance form failed -> " + status + " | " + debug);
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
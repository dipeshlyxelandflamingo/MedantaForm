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

public class CPR extends BaseClass {

	@Test(priority = 1)
    public void CPRPage_EnrollnowForm() {

        int row = 8;

        // ✅ Make these available to finally block (so Excel ALWAYS writes)
        String status = "⚠ UNKNOWN";
        String inputs = "";
        String fieldErrors = "";
        String globalErrors = "";
        String serverInfo = "";
        boolean thankYouSeen = false;
        String debug = "";

        try {
            String url = "https://www.medanta.org/cpr";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // ===== Locators =====
            By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
            By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]");
            By emailBy = By.xpath("(//input[@placeholder='Enter Your Email'])[3]");
            By locationBy = By.xpath("//select[@placeholder='Select Location']");
            By submitBy = By.xpath("(//button[@type='submit'])[3]");

            // success element (your original)
            By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

            // 🔥 Strong ThankYou fallback
            By thankYouBy = By.xpath(
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you for filling the form') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
            );

            // ===== Test Data =====
            String expName = "Test";
            String expMobile = "9876543210";
            String expEmail = "wakemedantatest@gmail.com";
            String expLocationText = "Gurugram";

            System.out.println("➡️ [CPR] Opening page...");

            // Scroll first field into view
            WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
            scrollToElement(nameForScroll);

            System.out.println("➡️ [CPR] Filling form...");

            typeAndEnsureValue(wait, js, nameBy, expName);

            // Dropdown selection (ensure)
            selectByVisibleTextAndEnsure(wait, locationBy, expLocationText);
            String locationVal = safeGetSelectedText(wait, locationBy);

            typeAndEnsureValue(wait, js, mobileBy, expMobile);
            typeAndEnsureValue(wait, js, emailBy, expEmail);

            // ⭐ value wipe protection
            ensureValueStillPresent(nameBy, expName);
            ensureValueStillPresent(mobileBy, expMobile);
            ensureValueStillPresent(emailBy, expEmail);

            // ✅ Capture inputs BEFORE submit
            inputs =
                    "Name=" + safeGetValue(nameBy)
                            + " | Mobile=" + safeGetValue(mobileBy)
                            + " | Email=" + safeGetValue(emailBy)
                            + " | Location=" + locationVal;

            // ===== Submit =====
            System.out.println("➡️ [CPR] Clicking submit...");
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

            try { Thread.sleep(800); } catch (Exception ignored) {}

            // ✅ Recommended: clear perf logs before submit, so only post-submit 5xx is captured
            clearPerformanceLogs();

            try {
                submitBtn.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", submitBtn);
            }

            // ===== Detect outcomes =====
            thankYouSeen =
                    waitForFlashPresence(successBy, 4000) || waitForFlashPresence(thankYouBy, 8000);

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
            System.out.println("============== CPR ENROLL FORM RESULT ==============");
            System.out.println("STATUS        : " + status);
            System.out.println("THANK YOU     : " + thankYouSeen);
            System.out.println("INPUTS        : " + inputs);
            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("SERVER INFO   : " + serverInfo);
            System.out.println("DEBUG         : " + debug);
            System.out.println("====================================================");

            // ✅ Excel ALWAYS writes
            writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);
        }

        // ✅ Fail AFTER excel write
        if (!status.contains("PASS")) {
            Assert.fail("CPR Enroll form failed -> " + status + " | " + debug);
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

    private void selectByVisibleTextAndEnsure(WebDriverWait wait, By selectLocator, String visibleText) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
                Select s = new Select(dd);
                s.selectByVisibleText(visibleText);

                String selected = s.getFirstSelectedOption().getText();
                if (selected != null && selected.trim().equalsIgnoreCase(visibleText.trim())) return;

            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " text='" + visibleText + "'");
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
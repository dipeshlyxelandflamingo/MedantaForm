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

        int row = 3;

        String url = "https://www.medanta.org/medanta-labs/become-a-partner";
        driver.navigate().to(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll (as per your code)
        js.executeScript("window.scrollBy(0,2600)");

        // ===== Locators =====
        By nameBy    = By.xpath("(//input[@type='text'])[4]");
        By emailBy   = By.xpath("(//input[@type='email'])[2]");
        By mobileBy  = By.xpath("(//input[@type='text'])[5]");
        By addressBy = By.xpath("(//input[@type='text'])[6]");
        By pinBy     = By.xpath("(//input[@type='text'])[7]");
        By submitBy  = By.xpath("(//button[@type='submit'])[3]");

        // 🔥 Strong ThankYou locator
        By thankYouBy = By.xpath(
                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
        );

        // ===== Test Data =====
        String expName = "Dipesh";
        String expEmail = "dipesh@yopmail.com";
        String expMobile = "9876543210";
        String expAddress = "Test";
        String expPin = "123456";

        System.out.println("➡️ [BecomeAPartner] Opening page...");

        // Ensure at least first field visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", driver.findElement(nameBy));

        System.out.println("➡️ [BecomeAPartner] Filling form...");
        typeAndEnsureValue(wait, js, nameBy, expName);
        typeAndEnsureValue(wait, js, emailBy, expEmail);
        typeAndEnsureValue(wait, js, mobileBy, expMobile);
        typeAndEnsureValue(wait, js, addressBy, expAddress);
        typeAndEnsureValue(wait, js, pinBy, expPin);

        // ⭐ value wipe protection
        ensureValueStillPresent(nameBy, expName);
        ensureValueStillPresent(emailBy, expEmail);
        ensureValueStillPresent(mobileBy, expMobile);
        ensureValueStillPresent(addressBy, expAddress);
        ensureValueStillPresent(pinBy, expPin);

        // ✅ Capture inputs BEFORE submit
        String inputs =
                "Name=" + safeGetValue(nameBy)
                        + " | Email=" + safeGetValue(emailBy)
                        + " | Mobile=" + safeGetValue(mobileBy)
                        + " | Address=" + safeGetValue(addressBy)
                        + " | Pincode=" + safeGetValue(pinBy);

        // ===== Submit =====
        System.out.println("➡️ [BecomeAPartner] Clicking submit...");
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));

        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        try {
            submitBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", submitBtn);
        }

        // ===== Detect outcomes =====
        boolean thankYouSeen = waitForFlashPresence(thankYouBy, 8000);
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
        System.out.println("============== BECOME A PARTNER FORM RESULT ==============");
        System.out.println("STATUS        : " + status);
        System.out.println("THANK YOU     : " + thankYouSeen);
        System.out.println("NETWORK 5XX   : " + network5xx);
        System.out.println("INPUTS        : " + inputs);
        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
        System.out.println("SERVER INFO   : " + serverInfo);
        System.out.println("DEBUG         : " + debug);
        System.out.println("==========================================================");

        // ===== Excel (E → L) =====
        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);

        if (!status.contains("PASS")) {
            Assert.fail("BecomeAPartner form failed -> " + status + " | " + debug);
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
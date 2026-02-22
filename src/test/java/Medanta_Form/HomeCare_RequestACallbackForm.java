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

public class HomeCare_RequestACallbackForm extends BaseClass {

	@Test(priority = 1)
    public void HomeCarePage_RequestACallbackForm() {

        int row = 16;

        String url = "https://www.medanta.org/home-care";
        driver.navigate().to(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ===== Locators =====
        By nameBy = By.name("name");
        By mobileBy = By.name("mobile");
        By emailBy = By.name("email");
        By messageBy = By.xpath("//textarea[@class='inputbox']");
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
        String expName = "Dipesh";
        String expMobile = "9876543210";
        String expEmail = "dipesh@yopmail.com";
        String expMsg = "Test";

        System.out.println("➡️ [HomeCare_Callback] Opening page...");

        // Ensure form is visible (safer)
        WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
        scrollToElement(nameForScroll);

        System.out.println("➡️ [HomeCare_Callback] Filling form...");

        typeAndEnsureValue(wait, js, nameBy, expName);
        typeAndEnsureValue(wait, js, mobileBy, expMobile);
        typeAndEnsureValue(wait, js, emailBy, expEmail);
        typeAndEnsureValue(wait, js, messageBy, expMsg);

        // ⭐ value wipe protection
        ensureValueStillPresent(nameBy, expName);
        ensureValueStillPresent(mobileBy, expMobile);
        ensureValueStillPresent(emailBy, expEmail);
        ensureValueStillPresent(messageBy, expMsg);

        // ✅ capture inputs BEFORE submit
        String inputs =
                "Name=" + safeGetValue(nameBy)
                        + " | Mobile=" + safeGetValue(mobileBy)
                        + " | Email=" + safeGetValue(emailBy)
                        + " | Message=" + safeGetValue(messageBy);

        // ===== Submit =====
        System.out.println("➡️ [HomeCare_Callback] Clicking submit...");
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
        System.out.println("============== HOMECARE CALLBACK FORM RESULT ==============");
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
            Assert.fail("HomeCare Request Callback failed -> " + status + " | " + debug);
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
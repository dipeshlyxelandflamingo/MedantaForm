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

public class ContactUs_Feedback extends BaseClass {

	@Test(priority = 1)
    public void ContactUsPage_FeedbackForm() {

        int row = 6;

        String url = "https://www.medanta.org/contact-us";
        driver.navigate().to(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        System.out.println("➡️ [ContactUs_Feedback] Opening page...");

        // loader disappear (as in your code)
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
        } catch (Exception ignored) {}

        // Open Feedback Form tab/button
        WebElement feedbackBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
        feedbackBtn.click();

        // ===== Locators =====
        By messageBy = By.name("feedback_message");
        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]");
        By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]");
        By submitBy = By.xpath("(//button[@type='submit'])[4]");

        // success element (your original)
        By successBy = By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]");

        // 🔥 Strong ThankYou fallback
        By thankYouBy = By.xpath(
                "//*[contains(@class,'successmsg') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank')]"
                        + " | //*[(contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted'))]"
        );

        // ===== Test Data =====
        String expMsg = "Test";
        String expName = "Dipesh";
        String expMobile = "9876543210";
        String expEmail = "dipesh@yopmail.com";

        // Scroll first field into view
        WebElement msgForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(messageBy));
        scrollToElement(msgForScroll);

        System.out.println("➡️ [ContactUs_Feedback] Filling form...");

        typeAndEnsureValue(wait, js, messageBy, expMsg);
        typeAndEnsureValue(wait, js, nameBy, expName);
        typeAndEnsureValue(wait, js, mobileBy, expMobile);
        typeAndEnsureValue(wait, js, emailBy, expEmail);

        // ⭐ value wipe protection
        ensureValueStillPresent(messageBy, expMsg);
        ensureValueStillPresent(nameBy, expName);
        ensureValueStillPresent(mobileBy, expMobile);
        ensureValueStillPresent(emailBy, expEmail);

        // ✅ capture inputs BEFORE submit
        String inputs =
                "Message=" + safeGetValue(messageBy)
                        + " | Name=" + safeGetValue(nameBy)
                        + " | Mobile=" + safeGetValue(mobileBy)
                        + " | Email=" + safeGetValue(emailBy);

        // ===== Submit =====
        System.out.println("➡️ [ContactUs_Feedback] Clicking submit...");

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
        System.out.println("============== CONTACT US FEEDBACK FORM RESULT ==============");
        System.out.println("STATUS        : " + status);
        System.out.println("THANK YOU     : " + thankYouSeen);
        System.out.println("NETWORK 5XX   : " + network5xx);
        System.out.println("INPUTS        : " + inputs);
        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
        System.out.println("SERVER INFO   : " + serverInfo);
        System.out.println("DEBUG         : " + debug);
        System.out.println("=============================================================");

        // ===== Excel (E → L) =====
        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);

        if (!status.contains("PASS")) {
            Assert.fail("ContactUs_Feedback form failed -> " + status + " | " + debug);
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
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

        int row = 13;

        String status = "⚠ UNKNOWN";
        String inputs = "";
        String fieldErrors = "";
        String globalErrors = "";
        String info = "";
        boolean thankYouSeen = false;
        String debug = "";

        boolean submitClicked = false;
        String fillIssues = "";

        try {
            String url = "https://www.medanta.org/healthcheckup/gurugram-hospital";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // ===== Locators =====
            By nameBy = By.name("name");
            By mobileBy = By.name("mobile");
            By emailBy = By.name("email");
            By messageBy = By.xpath("//textarea[@class='inputbox']");
            By submitBy = By.xpath("(//button[@type='submit'])[3]");

            // success (your original)
            By successBy = By.xpath("//div[contains(text(),'Thank you')]");

            // ✅ thank you fallback (avoid broad "success")
            By thankYouBy = By.xpath(
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
            );

            // ===== Test Data =====
            String expName = "Dipesh";
            String expMobile = "9876543210";
            String expEmail = "dipesh@yopmail.com";
            String expMsg = "Test";

            System.out.println("➡️ [HealthCheckup] Opening page...");

            // scroll (safe)
            try {
                WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
                scrollToElement(nameForScroll);
            } catch (Exception ignored) {}

            System.out.println("➡️ [HealthCheckup] Filling form...");

            // ===== SAFE FILL =====
            try { typeAndEnsureValue(wait, js, nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name fill failed | "; }

            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email fill failed | "; }

            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
            catch (Exception ex) { fillIssues += "Message fill failed | "; }

            // optional wipe protection (safe)
            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}
            try { ensureValueStillPresent(messageBy, expMsg); } catch (Exception ignored) {}

            // ✅ inputs ALWAYS captured
            inputs =
                    "Name=" + safeGetValue(nameBy)
                            + " | Mobile=" + safeGetValue(mobileBy)
                            + " | Email=" + safeGetValue(emailBy)
                            + " | Message=" + safeGetValue(messageBy);

            // ===== Submit (ALWAYS attempt) =====
            System.out.println("➡️ [HealthCheckup] Clicking submit...");
            try {
                WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

                try { Thread.sleep(400); } catch (Exception ignored) {}

                try {
                    submitBtn.click();
                } catch (Exception e) {
                    js.executeScript("arguments[0].click();", submitBtn);
                }

                submitClicked = true;

            } catch (Exception ex) {
                submitClicked = false;
                fillIssues += "Submit click failed | ";
            }

            // ===== Detect outcomes =====
            boolean successSeen = submitClicked
                    && (waitForFlashPresence(successBy, 4500) || waitForFlashPresence(thankYouBy, 9000));

            thankYouSeen = successSeen;

            // ✅ Always collect errors
            fieldErrors = collectAllValidationErrors();
            globalErrors = collectGlobalErrors();

            // clean issues
            String issues = (fillIssues == null) ? "" : fillIssues.trim();
            if (issues.endsWith("|")) issues = issues.substring(0, issues.length() - 1).trim();

            // ===== Decide status =====
            if (thankYouSeen) {
                status = "✅ PASS";
                info = "Submitted";
            } else if (!submitClicked) {
                status = "❌ FORM_NOT_SUBMITTED";
                info = issues.isBlank() ? "Submit not clicked" : issues;
            } else if (fieldErrors != null && !fieldErrors.isBlank()) {
                status = "❌ VALIDATION_FAIL";
                info = issues.isBlank() ? "Validation errors" : issues;
            } else if (globalErrors != null && !globalErrors.isBlank()) {
                status = "❌ GLOBAL_FAIL";
                info = issues.isBlank() ? "Global error shown" : issues;
            } else {
                status = "⚠ UNKNOWN";
                info = issues.isBlank() ? "No success/error signal detected" : issues;
            }

        } catch (Exception e) {
            status = "❌ FORM_NOT_SUBMITTED";
            info = "Flow crashed: " + e.getClass().getSimpleName() + " | " + e.getMessage();
        } finally {

            try {
                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
            } catch (Exception ignored) {
                debug = "Debug not available";
            }

            System.out.println("============== HEALTH CHECKUP FORM RESULT ==============");
            System.out.println("STATUS        : " + status);
            System.out.println("SUBMIT CLICKED: " + submitClicked);
            System.out.println("THANK YOU     : " + thankYouSeen);
            System.out.println("INPUTS        : " + inputs);
            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("INFO          : " + info);
            System.out.println("DEBUG         : " + debug);
            System.out.println("========================================================");

            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, thankYouSeen, debug);
        }

        if (!status.contains("PASS")) {
            Assert.fail("HealthCheckup form failed -> " + status + " | " + debug);
        }
    }

    /* ================= SAFE TYPE (local for now) ================= */

    private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

                try { el.click(); } catch (Exception ignored) {}
                try { el.clear(); } catch (Exception ignored) {}

                slowType(el, value);

                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

                String actual = el.getAttribute("value");
                if (actual != null && actual.trim().equals(value)) return;

            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("Value did not persist for locator: " + locator + " expected='" + value + "'");
    }

    private String safeGetValue(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            String v = element.getAttribute("value");
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
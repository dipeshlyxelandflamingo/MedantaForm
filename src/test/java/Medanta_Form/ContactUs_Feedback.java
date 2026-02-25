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
            String url = "https://www.medanta.org/contact-us";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            System.out.println("➡️ [ContactUs_Feedback] Opening page...");

            // loader disappear (as in your code)
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
            } catch (Exception ignored) {}

            // Open Feedback Form tab/button (safe)
            try {
                WebElement feedbackBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
                feedbackBtn.click();
            } catch (Exception ex) {
                fillIssues += "Open feedback tab failed | ";
            }

            // ===== Locators =====
            By messageBy = By.name("feedback_message");
            By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
            By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]");
            By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]");
            By submitBy = By.xpath("(//button[@type='submit'])[4]");

            // success element (your original)
            By successBy = By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]");

            // ✅ Thank you fallback (avoid broad "success")
            By thankYouBy = By.xpath(
                    "//*[contains(@class,'successmsg') and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank')]"
                            + " | //*[(contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted'))]"
            );

            // ===== Test Data =====
            String expMsg = "Test";
            String expName = "Dipesh";
            String expMobile = "9876543210";
            String expEmail = "dipesh@yopmail.com";

            // Scroll first field into view (safe)
            try {
                WebElement msgForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(messageBy));
                scrollToElement(msgForScroll);
            } catch (Exception ignored) {}

            System.out.println("➡️ [ContactUs_Feedback] Filling form...");

            // ===== SAFE FILL (never stop flow if one field fails) =====
            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
            catch (Exception ex) { fillIssues += "Message fill failed | "; }

            try { typeAndEnsureValue(wait, js, nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name fill failed | "; }

            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email fill failed | "; }

            // Optional: value wipe protection (safe)
            try { ensureValueStillPresent(messageBy, expMsg); } catch (Exception ignored) {}
            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}

            // ✅ Inputs ALWAYS captured (partial ok)
            inputs =
                    "Message=" + safeGetValue(messageBy)
                            + " | Name=" + safeGetValue(nameBy)
                            + " | Mobile=" + safeGetValue(mobileBy)
                            + " | Email=" + safeGetValue(emailBy);

            // ===== Submit (ALWAYS attempt) =====
            System.out.println("➡️ [ContactUs_Feedback] Clicking submit...");
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
                    && (waitForFlashPresence(successBy, 4000) || waitForFlashPresence(thankYouBy, 9000));

            thankYouSeen = successSeen;

            // ✅ Always collect errors (fail/pass)
            fieldErrors = collectAllValidationErrors();
            globalErrors = collectGlobalErrors();

            // Clean fillIssues text
            String issues = (fillIssues == null) ? "" : fillIssues.trim();
            if (issues.endsWith("|")) issues = issues.substring(0, issues.length() - 1).trim();

            // ===== Decide status (NO EXCEPTION status) =====
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

            System.out.println("============== CONTACT US FEEDBACK FORM RESULT ==============");
            System.out.println("STATUS        : " + status);
            System.out.println("SUBMIT CLICKED: " + submitClicked);
            System.out.println("THANK YOU     : " + thankYouSeen);
            System.out.println("INPUTS        : " + inputs);
            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("INFO          : " + info);
            System.out.println("DEBUG         : " + debug);
            System.out.println("=============================================================");

            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, thankYouSeen, debug);
        }

        if (!status.contains("PASS")) {
            Assert.fail("ContactUs_Feedback form failed -> " + status + " | " + debug);
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
            WebElement el = driver.findElement(locator);
            String v = el.getAttribute("value");
            return v == null ? "" : v.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
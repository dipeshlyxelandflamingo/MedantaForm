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

public class TreatmentD extends BaseClass {

	@Test(priority = 1)
    public void TreatmentDetailPage_RequestACallBackForm() {

        int row = 28;

        String status = "⚠ UNKNOWN";
        String inputs = "";
        String fieldErrors = "";
        String globalErrors = "";
        boolean successSeen = false;
        boolean submitClicked = false;
        String fillIssues = "";
        String debug = "";

        String url =
                "https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/plastic-surgery/treatment/abdominoplasty-tummy-tuck-operation";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ===== Locators =====
        By nameBy    = By.name("name");
        By mobileBy  = By.name("mobile");
        By emailBy   = By.name("email");
        By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
        By submitBy  = By.xpath("(//button[@type='submit'])[3]");

        By thankYouBy = By.xpath("//div[contains(text(),'Thank you')]");
        By successFallbackBy = By.xpath(
                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
        );

        // ===== Test Data =====
        String expName   = "Test";
        String expMobile = "9876543210";
        String expEmail  = "wakemedantatest@gmail.com";
        String expMsg    = "Testing the form Please ignore";

        try {
            driver.navigate().to(url);

            System.out.println("➡️ [TreatmentDetail] Opening page...");
            WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
            scrollToElement(first);

            System.out.println("➡️ [TreatmentDetail] Filling form...");

            // ===== SAFE FILL =====
            try { typeAndEnsureValue(wait, js, nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name fill failed | "; }

            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email fill failed | "; }

            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
            catch (Exception ex) { fillIssues += "Message fill failed | "; }

            // ===== SAFE value wipe protection =====
            try { ensureValueStillPresent(nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name value wiped | "; }

            try { ensureValueStillPresent(mobileBy, expMobile); }
            catch (Exception ex) { fillIssues += "Mobile value wiped | "; }

            try { ensureValueStillPresent(emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email value wiped | "; }

            try { ensureValueStillPresent(messageBy, expMsg); }
            catch (Exception ex) { fillIssues += "Message value wiped | "; }

            // ✅ capture inputs BEFORE submit (ALWAYS)
            inputs = "Name=" + safeGetValue(nameBy)
                    + " | Mobile=" + safeGetValue(mobileBy)
                    + " | Email=" + safeGetValue(emailBy)
                    + " | Message=" + safeGetValue(messageBy)
                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim());

            // ===== Submit (SAFE) =====
            System.out.println("➡️ [TreatmentDetail] Clicking submit...");

            try {
                WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
                try { Thread.sleep(800); } catch (Exception ignored) {}

                try {
                    submitBtn.click();
                } catch (Exception e) {
                    js.executeScript("arguments[0].click();", submitBtn);
                }

                submitClicked = true;

            } catch (Exception clickEx) {
                submitClicked = false;
                status = "❌ FORM_NOT_SUBMITTED";
            }

            // ===== Detect outcomes =====
            if (submitClicked) {
                successSeen =
                        waitForFlashPresence(thankYouBy, 4500)
                                || waitForFlashPresence(successFallbackBy, 9000);
            }

            fieldErrors  = collectAllValidationErrors();
            globalErrors = collectGlobalErrors();

            // ===== Decide Status =====
            if (!submitClicked) {
                status = "❌ FORM_NOT_SUBMITTED";
            } else if (successSeen) {
                status = "✅ PASS";
            } else if (fieldErrors != null && !fieldErrors.isBlank()) {
                status = "❌ VALIDATION_FAIL";
            } else if (globalErrors != null && !globalErrors.isBlank()) {
                status = "❌ GLOBAL_ERROR";
            } else {
                status = "⚠ UNKNOWN";
            }

        } catch (Exception e) {
            if (status == null || status.trim().isEmpty() || status.equals("⚠ UNKNOWN")) {
                status = "❌ EXCEPTION";
            }

        } finally {

            try {
                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
            } catch (Exception ignored) {
                debug = "Debug not available";
            }

            System.out.println("============== TREATMENT DETAIL FORM RESULT ==============");
            System.out.println("STATUS         : " + status);
            System.out.println("SUBMIT CLICKED : " + submitClicked);
            System.out.println("SUCCESS        : " + successSeen);
            System.out.println("INPUTS         : " + inputs);
            System.out.println("FIELD ERRORS   : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS  : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("DEBUG          : " + debug);
            System.out.println("==========================================================");

            // ✅ Excel info column: PASS => "Submitted"
            String excelInfo;
            if (status.contains("PASS")) {
                excelInfo = fillIssues.isBlank() ? "Submitted" : ("Submitted | " + fillIssues.trim());
            } else {
                excelInfo = (fillIssues == null) ? "" : fillIssues.trim();
            }

            writeFormResult(row, status, inputs, fieldErrors, globalErrors, excelInfo, successSeen, debug);
        }

        // ✅ Fail AFTER excel write
        if (!status.contains("PASS")) {
            Assert.fail("Treatment Detail form failed -> " + status + " | " + debug);
        }
    }

    /* ================= Local helpers ================= */

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
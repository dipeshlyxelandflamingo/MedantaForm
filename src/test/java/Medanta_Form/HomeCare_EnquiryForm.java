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

public class HomeCare_EnquiryForm extends BaseClass{
	
	@Test(priority = 1)
    public void HomeCarePage_EnquiryForm() {

        int row = 15;

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
            String url = "https://www.medanta.org/home-care";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // scroll to form section (safe)
            try { js.executeScript("window.scrollBy(0,2000)"); } catch (Exception ignored) {}
            try { Thread.sleep(600); } catch (Exception ignored) {}

            // ===== Locators =====
            By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
            By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]");
            By emailBy = By.xpath("(//input[@placeholder='Enter Your Email'])[4]");
            By locationBy = By.xpath("//select[@placeholder='Select Location']");
            By submitBy = By.xpath("(//button[@type='submit'])[4]");

            // success element (your original)
            By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

            // ✅ ThankYou fallback (avoid broad "success")
            By thankYouBy = By.xpath(
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you for filling the form') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
            );

            // ===== Test Data =====
            String expName = "Dipesh";
            String expMobile = "9876543210";
            String expEmail = "dipesh@yopmail.com";
            int locationIndex = 1;

            System.out.println("➡️ [HomeCare_Enquiry] Opening page...");

            // make sure first field visible
            try {
                WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
                scrollToElement(nameForScroll);
            } catch (Exception ignored) {}

            System.out.println("➡️ [HomeCare_Enquiry] Filling form...");

            // ===== SAFE FILL =====
            try { typeAndEnsureValue(wait, js, nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name fill failed | "; }

            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email fill failed | "; }

            // dropdown selection (ensure)
            try {
                selectByIndexAndEnsure(wait, locationBy, locationIndex);
            } catch (Exception ex) {
                fillIssues += "Location dropdown failed | ";
            }
            String locationVal = safeGetSelectedText(locationBy);

            // optional wipe protection
            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}

            // ✅ inputs ALWAYS captured
            inputs =
                    "Name=" + safeGetValue(nameBy)
                            + " | Mobile=" + safeGetValue(mobileBy)
                            + " | Email=" + safeGetValue(emailBy)
                            + " | Location=" + locationVal;

            // ===== Submit (ALWAYS attempt) =====
            System.out.println("➡️ [HomeCare_Enquiry] Clicking submit...");
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

            System.out.println("============== HOMECARE ENQUIRY FORM RESULT ==============");
            System.out.println("STATUS        : " + status);
            System.out.println("SUBMIT CLICKED: " + submitClicked);
            System.out.println("THANK YOU     : " + thankYouSeen);
            System.out.println("INPUTS        : " + inputs);
            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("INFO          : " + info);
            System.out.println("DEBUG         : " + debug);
            System.out.println("=========================================================");

            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, thankYouSeen, debug);
        }

        if (!status.contains("PASS")) {
            Assert.fail("HomeCare Enquiry form failed -> " + status + " | " + debug);
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

    /* ================= DROPDOWN HELPERS ================= */

    private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
                Select s = new Select(dd);
                s.selectByIndex(index);

                String selected = s.getFirstSelectedOption().getText();
                if (selected != null && !selected.trim().isEmpty()) return;

            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
    }

    private String safeGetSelectedText(By selectLocator) {
        try {
            WebElement dd = driver.findElement(selectLocator);
            Select s = new Select(dd);
            String txt = s.getFirstSelectedOption().getText();
            return txt == null ? "" : txt.trim();
        } catch (Exception e) {
            return "";
        }
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
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

        String url = "https://www.medanta.org/home-care";
        driver.navigate().to(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // scroll to form section
        js.executeScript("window.scrollBy(0,2000)");
        try { Thread.sleep(800); } catch (Exception ignored) {}

        // ===== Locators =====
        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]");
        By emailBy = By.xpath("(//input[@placeholder='Enter Your Email'])[4]");
        By locationBy = By.xpath("//select[@placeholder='Select Location']");
        By submitBy = By.xpath("(//button[@type='submit'])[4]");

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
        int locationIndex = 1;

        System.out.println("➡️ [HomeCare_Enquiry] Opening page...");

        // make sure first field is visible (safer than only scrollBy)
        WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
        scrollToElement(nameForScroll);

        System.out.println("➡️ [HomeCare_Enquiry] Filling form...");

        typeAndEnsureValue(wait, js, nameBy, expName);
        typeAndEnsureValue(wait, js, mobileBy, expMobile);
        typeAndEnsureValue(wait, js, emailBy, expEmail);

        // dropdown (ensure)
        selectByIndexAndEnsure(wait, locationBy, locationIndex);
        String locationVal = safeGetSelectedText(wait, locationBy);

        // ⭐ value wipe protection
        ensureValueStillPresent(nameBy, expName);
        ensureValueStillPresent(mobileBy, expMobile);
        ensureValueStillPresent(emailBy, expEmail);

        // ✅ capture inputs BEFORE submit
        String inputs =
                "Name=" + safeGetValue(nameBy)
                        + " | Mobile=" + safeGetValue(mobileBy)
                        + " | Email=" + safeGetValue(emailBy)
                        + " | Location=" + locationVal;

        // ===== Submit =====
        System.out.println("➡️ [HomeCare_Enquiry] Clicking submit...");
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
        System.out.println("============== HOMECARE ENQUIRY FORM RESULT ==============");
        System.out.println("STATUS        : " + status);
        System.out.println("THANK YOU     : " + thankYouSeen);
        System.out.println("NETWORK 5XX   : " + network5xx);
        System.out.println("INPUTS        : " + inputs);
        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
        System.out.println("SERVER INFO   : " + serverInfo);
        System.out.println("DEBUG         : " + debug);
        System.out.println("=========================================================");

        // ===== Excel (E → L) =====
        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, thankYouSeen, debug);

        if (!status.contains("PASS")) {
            Assert.fail("HomeCare Enquiry form failed -> " + status + " | " + debug);
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
        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
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
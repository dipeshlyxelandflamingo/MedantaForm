package Medanta_Form;

import java.io.File;
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

public class SecondOpiniun extends BaseClass {

	 @Test(priority = 1)
	    public void SecondOpinionPage_RequestACallbackForm() {

	        int row = 25; // ✅ keeping same row you used earlier in writeExcel(25,...)

	        String url = "https://www.medanta.org/second-opinion";
	        driver.navigate().to(url);

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // ===== Locators (same as your code) =====
	        By nameBy     = By.name("name");
	        By mobileBy   = By.name("mobile");
	        By emailBy    = By.name("email");
	        By messageBy  = By.xpath("//textarea[@placeholder='Enter Your Message']");
	        By fileBy     = By.xpath("(//input[@type='file'])[2]");
	        By submitBy   = By.xpath("(//button[@type='submit'])[3]");

	        By thankYouBy = By.xpath("//div[contains(text(),'Thank you')]");
	        By successFallbackBy = By.xpath(
	                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	        );

	        // ===== Test Data =====
	        String expName   = "Dipesh";
	        String expMobile = "9876543210";
	        String expEmail  = "dipesh@yopmail.com";
	        String expMsg    = "Test";

	        // ===== File upload path (OS safe) =====
	        String filePath = System.getProperty("user.dir")
	                + File.separator + "SampleDocs"
	                + File.separator + "upload.docx";

	        System.out.println("➡️ [SecondOpinion] Opening page...");
	        wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));

	        System.out.println("➡️ [SecondOpinion] Filling form...");
	        typeAndEnsureValue(wait, js, nameBy, expName);
	        typeAndEnsureValue(wait, js, mobileBy, expMobile);
	        typeAndEnsureValue(wait, js, emailBy, expEmail);
	        typeAndEnsureValue(wait, js, messageBy, expMsg);

	        // value wipe protection (same pattern as your other forms)
	        ensureValueStillPresent(nameBy, expName);
	        ensureValueStillPresent(mobileBy, expMobile);
	        ensureValueStillPresent(emailBy, expEmail);

	        // ===== Upload file =====
	        try {
	            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
	            fileInput.sendKeys(filePath);
	        } catch (Exception e) {
	            System.out.println("⚠️ [SecondOpinion] File upload failed/skipped: " + e.getMessage());
	        }

	        // capture inputs BEFORE submit
	        String inputs =
	                "Name=" + safeGetValue(nameBy)
	                        + " | Mobile=" + safeGetValue(mobileBy)
	                        + " | Email=" + safeGetValue(emailBy)
	                        + " | Message=" + safeGetValue(messageBy)
	                        + " | File=" + filePath;

	        // ===== Submit =====
	        System.out.println("➡️ [SecondOpinion] Clicking submit...");
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	        js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

	        try { Thread.sleep(800); } catch (Exception ignored) {}

	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        // ===== Detect outcomes =====
	        boolean successSeen =
	                waitForFlashPresence(thankYouBy, 4500) || waitForFlashPresence(successFallbackBy, 9000);

	        boolean network5xx = waitForNetwork5xx(9000);

	        String fieldErrors  = collectAllValidationErrors();
	        String globalErrors = collectGlobalErrors();

	        // ===== Decide Status (same style as your existing forms) =====
	        String status;
	        String serverInfo = "";

	        if (successSeen && network5xx) {
	            status = "❌ SERVER_FAIL (POST SUBMIT)";
	            serverInfo = "API returned 5xx after submit";
	        } else if (successSeen) {
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
	        System.out.println("============== SECOND OPINION FORM RESULT ==============");
	        System.out.println("STATUS        : " + status);
	        System.out.println("SUCCESS       : " + successSeen);
	        System.out.println("NETWORK 5XX   : " + network5xx);
	        System.out.println("INPUTS        : " + inputs);
	        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	        System.out.println("SERVER INFO   : " + serverInfo);
	        System.out.println("DEBUG         : " + debug);
	        System.out.println("========================================================");

	        // ===== Excel (E → L) =====
	        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);

	        if (!status.contains("PASS")) {
	            Assert.fail("Second Opinion form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= Local helpers (same pattern as your other forms) ================= */

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
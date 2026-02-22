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

public class MedantaPharmacy_UploadForm extends BaseClass {

	  @Test(priority = 1)
	    public void MedantaPharmacyPage_UploadForm() {

	        int row = 22;
	        String url = "https://www.medanta.org/medanta-pharmacy";

	        driver.navigate().to(url);

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // --- scroll down to upload form ---
	        js.executeScript("window.scrollBy(0,800)");
	        try { Thread.sleep(1200); } catch (Exception ignored) {}

	        // ===== Locators =====
	        By nameBy   = By.xpath("(//input[@type='text'])[5]");
	        By emailBy  = By.xpath("(//input[@type='email'])[3]");
	        By fileBy   = By.xpath("(//input[@type='file'])[2]");
	        By submitBy = By.xpath("(//button[@type='submit'])[4]");

	        // success (your original)
	        By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

	        // fallback success (if text changes / flashes)
	        By successFallbackBy = By.xpath(
	                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	        );

	        // ===== Test Data =====
	        String expName  = "Dipesh";
	        String expEmail = "dipesh@yopmail.com";

	        // Cross-platform file path (Windows + Linux)
	        String filePath = System.getProperty("user.dir")
	                + File.separator + "SampleDocs"
	                + File.separator + "upload.docx";

	        System.out.println("➡️ [MedantaPharmacyUpload] Opening page...");

	        // wait first field & scroll
	        WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	        scrollToElement(first);

	        System.out.println("➡️ [MedantaPharmacyUpload] Filling form...");
	        typeAndEnsureValue(wait, js, nameBy, expName);
	        typeAndEnsureValue(wait, js, emailBy, expEmail);

	        // ⭐ value wipe protection
	        ensureValueStillPresent(nameBy, expName);
	        ensureValueStillPresent(emailBy, expEmail);

	        // ===== File upload =====
	        System.out.println("➡️ [MedantaPharmacyUpload] Uploading file...");

	        File f = new File(filePath);
	        if (!f.exists()) {
	            // If file missing, treat as test failure but still log nicely in excel
	            String status = "❌ VALIDATION_FAIL";
	            String inputs = "Name=" + safeGetValue(nameBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | File=MISSING(" + filePath + ")";

	            String fieldErrors = "Upload file not found on machine: " + filePath;
	            String globalErrors = collectGlobalErrors();
	            String serverInfo = "Local file missing";
	            boolean successSeen = false;

	            String debug = driver.getCurrentUrl() + " | " + driver.getTitle();

	            System.out.println("============== MEDANTA PHARMACY UPLOAD FORM RESULT ==============");
	            System.out.println("STATUS        : " + status);
	            System.out.println("SUCCESS       : " + successSeen);
	            System.out.println("NETWORK 5XX   : false");
	            System.out.println("INPUTS        : " + inputs);
	            System.out.println("FIELD ERRORS  : " + fieldErrors);
	            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("SERVER INFO   : " + serverInfo);
	            System.out.println("DEBUG         : " + debug);
	            System.out.println("=================================================================");

	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);
	            Assert.fail("Upload doc missing at: " + filePath);
	            return;
	        }

	        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	        js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);

	        try {
	            fileInput.sendKeys(f.getAbsolutePath());
	        } catch (Exception e) {
	            // fallback click & send keys again
	            try { js.executeScript("arguments[0].click();", fileInput); } catch (Exception ignored) {}
	            fileInput.sendKeys(f.getAbsolutePath());
	        }

	        try { Thread.sleep(800); } catch (Exception ignored) {}

	        // NOTE: Some sites clear file input; try reading value (may be blank due to browser security)
	        boolean fileProbablySet = false;
	        try {
	            String v = fileInput.getAttribute("value");
	            if (v != null && !v.trim().isEmpty()) fileProbablySet = true;
	        } catch (Exception ignored) {}

	        // capture inputs BEFORE submit
	        String inputs = "Name=" + safeGetValue(nameBy)
	                + " | Email=" + safeGetValue(emailBy)
	                + " | File=" + (fileProbablySet ? "SELECTED" : "SELECTED(unknown)");

	        // ===== Submit =====
	        System.out.println("➡️ [MedantaPharmacyUpload] Clicking submit...");
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
	                waitForFlashPresence(successBy, 4000) || waitForFlashPresence(successFallbackBy, 9000);

	        boolean network5xx = waitForNetwork5xx(9000);

	        String fieldErrors = collectAllValidationErrors();
	        String globalErrors = collectGlobalErrors();

	        // ===== Decide Status =====
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
	        System.out.println("============== MEDANTA PHARMACY UPLOAD FORM RESULT ==============");
	        System.out.println("STATUS        : " + status);
	        System.out.println("SUCCESS       : " + successSeen);
	        System.out.println("NETWORK 5XX   : " + network5xx);
	        System.out.println("INPUTS        : " + inputs);
	        System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	        System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	        System.out.println("SERVER INFO   : " + serverInfo);
	        System.out.println("DEBUG         : " + debug);
	        System.out.println("=================================================================");

	        // ===== Excel (E → L) =====
	        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);

	        if (!status.contains("PASS")) {
	            Assert.fail("Medanta Pharmacy Upload form failed -> " + status + " | " + debug);
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
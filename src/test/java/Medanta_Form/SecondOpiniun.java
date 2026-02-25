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

	        int row = 25;

	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        String info = "";
	        boolean successSeen = false;
	        boolean submitClicked = false;
	        String debug = "";
	        String fillIssues = "";
	        String uploadIssues = "";

	        try {

	            String url = "https://www.medanta.org/second-opinion";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            // ===== Locators =====
	            By nameBy    = By.name("name");
	            By mobileBy  = By.name("mobile");
	            By emailBy   = By.name("email");
	            By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
	            By fileBy    = By.xpath("(//input[@type='file'])[2]");
	            By submitBy  = By.xpath("(//button[@type='submit'])[3]");

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

	            // ===== File path =====
	            String filePath = System.getProperty("user.dir")
	                    + File.separator + "SampleDocs"
	                    + File.separator + "upload.docx";

	            System.out.println("➡️ [SecondOpinion] Opening page...");

	            // ensure first field visible (safe)
	            try {
	                WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	                scrollToElement(first);
	            } catch (Exception ignored) {}

	            System.out.println("➡️ [SecondOpinion] Filling form...");

	            // ===== SAFE FILL =====
	            try { typeAndEnsureValue(wait, js, nameBy, expName); }
	            catch (Exception ex) { fillIssues += "Name fill failed | "; }

	            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
	            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

	            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
	            catch (Exception ex) { fillIssues += "Email fill failed | "; }

	            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
	            catch (Exception ex) { fillIssues += "Message fill failed | "; }

	            // value wipe protection (safe)
	            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(messageBy, expMsg); } catch (Exception ignored) {}

	            // ===== File upload (exist check) =====
	            String fileInfo = "NOT_SELECTED";
	            File f = new File(filePath);

	            if (!f.exists()) {
	                fileInfo = "MISSING(" + filePath + ")";
	                uploadIssues = "Upload file not found on machine: " + filePath;
	            } else {
	                try {
	                    WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
	                    fileInput.sendKeys(f.getAbsolutePath());
	                    fileInfo = "SELECTED";
	                } catch (Exception e) {
	                    fileInfo = "UPLOAD_FAILED(" + e.getClass().getSimpleName() + ")";
	                    uploadIssues = "File upload failed: " + e.getClass().getSimpleName() + " | " + e.getMessage();
	                }
	            }

	            // ✅ capture inputs BEFORE submit (ALWAYS)
	            inputs = "Name=" + safeGetValue(nameBy)
	                    + " | Mobile=" + safeGetValue(mobileBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | Message=" + safeGetValue(messageBy)
	                    + " | File=" + fileInfo
	                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim())
	                    + (uploadIssues.isBlank() ? "" : " | UploadIssues=" + uploadIssues.trim());

	            // If file missing/upload failed -> validation fail & stop submit
	            if (fileInfo.startsWith("MISSING") || fileInfo.startsWith("UPLOAD_FAILED")) {
	                status = "❌ VALIDATION_FAIL";
	                fieldErrors = uploadIssues;
	                globalErrors = collectGlobalErrors();
	                info = uploadIssues.isBlank() ? "File issue" : uploadIssues;
	            } else {

	                // ===== Submit =====
	                System.out.println("➡️ [SecondOpinion] Clicking submit...");

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
	                    info = "Submit click failed: " + clickEx.getClass().getSimpleName() + " | " + clickEx.getMessage();
	                }

	                // ===== Detect outcomes =====
	                if (submitClicked) {
	                    successSeen = waitForFlashPresence(thankYouBy, 4500)
	                            || waitForFlashPresence(successFallbackBy, 9000);
	                }

	                fieldErrors = collectAllValidationErrors();
	                globalErrors = collectGlobalErrors();

	                // ===== Decide Status =====
	                if (!submitClicked) {
	                    status = "❌ FORM_NOT_SUBMITTED";
	                    successSeen = false;
	                    if (info == null || info.isBlank()) info = "Submit not clicked";
	                } else if (successSeen) {
	                    status = "✅ PASS";
	                    info = fillIssues.isBlank() ? "Submitted" : ("Submitted | " + fillIssues.trim());
	                } else if (fieldErrors != null && !fieldErrors.isBlank()) {
	                    status = "❌ VALIDATION_FAIL";
	                    info = fillIssues.isBlank() ? "Validation errors" : fillIssues.trim();
	                } else if (globalErrors != null && !globalErrors.isBlank()) {
	                    status = "❌ GLOBAL_FAIL";
	                    info = fillIssues.isBlank() ? "Global error shown" : fillIssues.trim();
	                } else {
	                    status = "⚠ UNKNOWN";
	                    info = fillIssues.isBlank() ? "No success/error signal detected" : fillIssues.trim();
	                }
	            }

	        } catch (Exception e) {
	            if (status == null || status.trim().isEmpty() || status.equals("⚠ UNKNOWN")) {
	                status = "❌ EXCEPTION";
	                info = e.getClass().getSimpleName() + " | " + e.getMessage();
	            }

	        } finally {

	            try {
	                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
	            } catch (Exception ignored) {
	                debug = "Debug not available";
	            }

	            System.out.println("============== SECOND OPINION FORM RESULT ==============");
	            System.out.println("STATUS        : " + status);
	            System.out.println("SUBMIT CLICKED: " + submitClicked);
	            System.out.println("SUCCESS       : " + successSeen);
	            System.out.println("INPUTS        : " + inputs);
	            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("INFO          : " + info);
	            System.out.println("DEBUG         : " + debug);
	            System.out.println("========================================================");

	            // serverInfo column me info pass
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
	        }

	        if (!status.contains("PASS")) {
	            Assert.fail("Second Opinion form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= Local helpers ================= */

	    private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
	                js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

	                try { el.click(); } catch (Exception ignored) {}

	                // slowType already does strong clear + JS fallback
	                slowType(el, value);

	                try { Thread.sleep(250); } catch (InterruptedException ignored) {}

	                String actual = el.getAttribute("value");
	                if (actual == null) actual = "";
	                if (actual.trim().equals(value.trim())) return;

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
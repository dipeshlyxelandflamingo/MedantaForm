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

            String url = "https://www.medanta.org/medanta-pharmacy";
            driver.navigate().to(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // --- scroll down to upload form ---
            try { js.executeScript("window.scrollBy(0,800)"); } catch (Exception ignored) {}
            try { Thread.sleep(1200); } catch (Exception ignored) {}

            // ===== Locators =====
            By nameBy   = By.xpath("(//input[@type='text'])[5]");
            By emailBy  = By.xpath("(//input[@type='email'])[3]");
            By fileBy   = By.xpath("(//input[@type='file'])[2]");
            By submitBy = By.xpath("(//button[@type='submit'])[4]");

            By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");
            By successFallbackBy = By.xpath(
                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
            );

            // ===== Test Data =====
            String expName  = "Dipesh";
            String expEmail = "dipesh@yopmail.com";

            String filePath = System.getProperty("user.dir")
                    + File.separator + "SampleDocs"
                    + File.separator + "upload.docx";

            System.out.println("➡️ [MedantaPharmacyUpload] Opening page...");

            // make sure first field visible
            try {
                WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
                scrollToElement(first);
            } catch (Exception ignored) {}

            System.out.println("➡️ [MedantaPharmacyUpload] Filling form...");

            // ===== SAFE FILL =====
            try { typeAndEnsureValue(wait, js, nameBy, expName); }
            catch (Exception ex) { fillIssues += "Name fill failed | "; }

            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
            catch (Exception ex) { fillIssues += "Email fill failed | "; }

            // ===== File upload (SAFE) =====
            System.out.println("➡️ [MedantaPharmacyUpload] Uploading file...");

            File f = new File(filePath);
            boolean fileExists = f.exists();
            boolean fileProbablySet = false;

            if (!fileExists) {
                uploadIssues = "Local file missing: " + filePath;
            } else {
                try {
                    WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);

                    // sendKeys upload
                    fileInput.sendKeys(f.getAbsolutePath());

                    try { Thread.sleep(800); } catch (Exception ignored) {}

                    // value may be masked in some browsers, but we try
                    try {
                        String v = fileInput.getAttribute("value");
                        if (v != null && !v.trim().isEmpty()) fileProbablySet = true;
                    } catch (Exception ignored) {}

                } catch (Exception ex) {
                    uploadIssues = "File upload failed: " + ex.getClass().getSimpleName() + " | " + ex.getMessage();
                }
            }

            // ✅ capture inputs ALWAYS (before submit)
            inputs = "Name=" + safeGetValue(nameBy)
                    + " | Email=" + safeGetValue(emailBy)
                    + " | File=" + (fileExists ? (fileProbablySet ? "SELECTED" : "SELECTED(unknown)") : "MISSING(" + filePath + ")")
                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim())
                    + (uploadIssues.isBlank() ? "" : " | UploadIssues=" + uploadIssues.trim());

            // If file missing => don’t click submit
            if (!fileExists) {
                status = "❌ VALIDATION_FAIL";
                fieldErrors = uploadIssues;
                globalErrors = collectGlobalErrors();
                info = "Upload file not found on machine";
                successSeen = false;

            } else {

                // ===== Submit (SAFE) =====
                System.out.println("➡️ [MedantaPharmacyUpload] Clicking submit...");

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
                    successSeen = waitForFlashPresence(successBy, 5000)
                            || waitForFlashPresence(successFallbackBy, 9000);
                }

                fieldErrors = collectAllValidationErrors();
                globalErrors = collectGlobalErrors();

                // ===== Decide Status =====
                if (!submitClicked) {
                    status = "❌ FORM_NOT_SUBMITTED";
                    successSeen = false;
                    if (info == null || info.isBlank()) info = "Submit not clicked";
                }
                else if (successSeen) {
                    status = "✅ PASS";
                    info = "Submitted"
                            + (fillIssues.isBlank() ? "" : (" | " + fillIssues.trim()))
                            + (uploadIssues.isBlank() ? "" : (" | " + uploadIssues.trim()));
                }
                else if (fieldErrors != null && !fieldErrors.isBlank()) {
                    status = "❌ VALIDATION_FAIL";
                    info = fillIssues.isBlank() ? "Validation errors" : fillIssues.trim();
                }
                else if (globalErrors != null && !globalErrors.isBlank()) {
                    status = "❌ GLOBAL_FAIL";
                    info = fillIssues.isBlank() ? "Global error shown" : fillIssues.trim();
                }
                else {
                    status = "⚠ UNKNOWN";
                    info = fillIssues.isBlank() ? "No success/error signal detected" : fillIssues.trim();
                }
            }

        } catch (Exception e) {
            status = "❌ EXCEPTION";
            info = e.getClass().getSimpleName() + " | " + e.getMessage();

        } finally {

            try {
                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
            } catch (Exception ignored) {
                debug = "Debug not available";
            }

            System.out.println("============== MEDANTA PHARMACY UPLOAD FORM RESULT ==============");
            System.out.println("STATUS         : " + status);
            System.out.println("SUBMIT CLICKED : " + submitClicked);
            System.out.println("SUCCESS        : " + successSeen);
            System.out.println("INPUTS         : " + inputs);
            System.out.println("FIELD ERRORS   : " + (fieldErrors == null ? "" : fieldErrors));
            System.out.println("GLOBAL ERRORS  : " + (globalErrors == null ? "" : globalErrors));
            System.out.println("INFO           : " + info);
            System.out.println("DEBUG          : " + debug);
            System.out.println("=================================================================");

            // writeFormResult expects serverInfo param -> we pass info there
            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
        }

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

                // slowType already clears + JS fallback
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
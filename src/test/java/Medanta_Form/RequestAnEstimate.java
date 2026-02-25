package Medanta_Form;

import java.io.File;
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

public class RequestAnEstimate extends BaseClass {

	 @Test(priority = 1)
	    public void RequestAnEstimatePage_PriceEstimateForm() {

	        int row = 24;

	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        String info = "";
	        boolean successSeen = false;
	        boolean submitClicked = false;
	        String debug = "";
	        String fillIssues = "";

	        try {

	            String url = "https://www.medanta.org/international-patient/services/request-an-estimate";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            // Scroll slightly
	            try { js.executeScript("window.scrollBy(0,210)"); } catch (Exception ignored) {}

	            // ===== Locators =====
	            By fnameBy      = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
	            By lnameBy      = By.xpath("//input[@placeholder='Enter Your Last Name']");
	            By emailBy      = By.xpath("//input[@placeholder='Enter Your E-mail']");
	            By mobileBy     = By.xpath("//input[@placeholder='Enter Your Mobile No.']");
	            By countryBy    = By.xpath("//select[@class='inputbox']");
	            By departmentBy = By.xpath("(//select[@class='inputbox'])[2]");
	            By fileBy       = By.xpath("(//input[@type='file'])[2]");
	            By submitBy     = By.xpath("(//button[@type='submit'])[3]");

	            // success locators
	            By successBy = By.xpath("//div[contains(text(),'Thank you')]");
	            By successFallbackBy = By.xpath(
	                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	            );

	            // ===== Test Data =====
	            String expFName  = "Dipesh";
	            String expLName  = "Singh";
	            String expEmail  = "dipesh@yopmail.com";
	            String expMobile = "9876543210";

	            // ===== File upload path =====
	            String filePath = System.getProperty("user.dir")
	                    + File.separator + "SampleDocs"
	                    + File.separator + "upload.docx";

	            System.out.println("➡️ [RequestAnEstimate] Opening page...");

	            // ensure first field visible (safe)
	            try {
	                WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(fnameBy));
	                scrollToElement(first);
	            } catch (Exception ignored) {}

	            System.out.println("➡️ [RequestAnEstimate] Filling form...");

	            // ===== SAFE FILL =====
	            try { typeAndEnsureValue(wait, js, fnameBy, expFName); }
	            catch (Exception ex) { fillIssues += "FirstName fill failed | "; }

	            try { typeAndEnsureValue(wait, js, lnameBy, expLName); }
	            catch (Exception ex) { fillIssues += "LastName fill failed | "; }

	            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
	            catch (Exception ex) { fillIssues += "Email fill failed | "; }

	            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
	            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

	            // ===== SAFE value wipe protection =====
	            try { ensureValueStillPresent(fnameBy, expFName); }
	            catch (Exception ex) { fillIssues += "FirstName value wiped | "; }

	            try { ensureValueStillPresent(lnameBy, expLName); }
	            catch (Exception ex) { fillIssues += "LastName value wiped | "; }

	            try { ensureValueStillPresent(emailBy, expEmail); }
	            catch (Exception ex) { fillIssues += "Email value wiped | "; }

	            try { ensureValueStillPresent(mobileBy, expMobile); }
	            catch (Exception ex) { fillIssues += "Mobile value wiped | "; }

	            // ===== Dropdowns (SAFE) =====
	            try { selectByIndexAndEnsure(wait, countryBy, 8); }
	            catch (Exception ex) { fillIssues += "Country dropdown failed | "; }

	            try { selectByVisibleTextAndEnsure(wait, departmentBy, "Obstetrics"); }
	            catch (Exception ex) { fillIssues += "Department dropdown failed | "; }

	            String countryVal = safeGetSelectedText(wait, countryBy);
	            String deptVal = safeGetSelectedText(wait, departmentBy);

	            // ===== File upload (SAFE) =====
	            String fileInfo = "NOT_SELECTED";
	            File f = new File(filePath);

	            if (!f.exists()) {
	                fileInfo = "MISSING(" + filePath + ")";
	            } else {
	                try {
	                    WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
	                    fileInput.sendKeys(f.getAbsolutePath());
	                    fileInfo = "SELECTED";
	                } catch (Exception e) {
	                    fileInfo = "UPLOAD_FAILED(" + e.getClass().getSimpleName() + ")";
	                    fillIssues += "File upload failed | ";
	                }
	            }

	            // ✅ capture inputs BEFORE submit (ALWAYS)
	            inputs = "FirstName=" + safeGetValue(fnameBy)
	                    + " | LastName=" + safeGetValue(lnameBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | Mobile=" + safeGetValue(mobileBy)
	                    + " | Country=" + countryVal
	                    + " | Department=" + deptVal
	                    + " | File=" + fileInfo
	                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim());

	            // If file missing -> do not submit
	            if (fileInfo.startsWith("MISSING")) {
	                status = "❌ VALIDATION_FAIL";
	                fieldErrors = "Upload file not found on machine: " + filePath;
	                info = "Local file missing";
	                submitClicked = false;
	                successSeen = false;

	            } else {

	                // ===== Submit (SAFE) =====
	                System.out.println("➡️ [RequestAnEstimate] Clicking submit...");

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
	                    info = "Submit click failed: " + clickEx.getClass().getSimpleName() + " | " + clickEx.getMessage();
	                }

	                // ===== Detect outcomes =====
	                if (submitClicked) {
	                    successSeen = waitForFlashPresence(successBy, 4500)
	                            || waitForFlashPresence(successFallbackBy, 9000);
	                }

	                fieldErrors = collectAllValidationErrors();
	                globalErrors = collectGlobalErrors();

	                // ===== Decide Status =====
	                if (!submitClicked) {
	                    status = "❌ FORM_NOT_SUBMITTED";
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

	            System.out.println("============== REQUEST AN ESTIMATE FORM RESULT ==============");
	            System.out.println("STATUS         : " + status);
	            System.out.println("SUBMIT CLICKED : " + submitClicked);
	            System.out.println("SUCCESS        : " + successSeen);
	            System.out.println("INPUTS         : " + inputs);
	            System.out.println("FIELD ERRORS   : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS  : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("INFO           : " + info);
	            System.out.println("DEBUG          : " + debug);
	            System.out.println("============================================================");

	            // serverInfo column me info pass
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
	        }

	        if (!status.contains("PASS")) {
	            Assert.fail("Request An Estimate form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= Helpers ================= */

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

	    private void selectByVisibleTextAndEnsure(WebDriverWait wait, By selectLocator, String visibleText) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
	                Select s = new Select(dd);
	                s.selectByVisibleText(visibleText);

	                String selected = s.getFirstSelectedOption().getText();
	                if (selected != null && selected.trim().equalsIgnoreCase(visibleText.trim())) return;

	            } catch (StaleElementReferenceException ignored) {
	            } catch (Exception ignored) {
	            }
	        }
	        throw new RuntimeException("Dropdown selection did not persist for locator: " + selectLocator + " text='" + visibleText + "'");
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
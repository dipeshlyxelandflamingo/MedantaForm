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

	        // ✅ Make these available to finally block (so Excel ALWAYS writes)
	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        String serverInfo = "";
	        boolean successSeen = false;
	        String debug = "";

	        boolean network5xx = false;

	        try {

	            String url = "https://www.medanta.org/international-patient/services/request-an-estimate";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            // Scroll slightly
	            js.executeScript("window.scrollBy(0,210)");

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
	            WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(fnameBy));
	            scrollToElement(first);

	            System.out.println("➡️ [RequestAnEstimate] Filling form...");

	            typeAndEnsureValue(wait, js, fnameBy, expFName);
	            typeAndEnsureValue(wait, js, lnameBy, expLName);
	            typeAndEnsureValue(wait, js, emailBy, expEmail);
	            typeAndEnsureValue(wait, js, mobileBy, expMobile);

	            // ⭐ value wipe protection (text fields)
	            ensureValueStillPresent(fnameBy, expFName);
	            ensureValueStillPresent(lnameBy, expLName);
	            ensureValueStillPresent(emailBy, expEmail);
	            ensureValueStillPresent(mobileBy, expMobile);

	            // ===== Dropdowns =====
	            selectByIndexAndEnsure(wait, countryBy, 8);

	            // ✅ Safer: use VisibleText instead of value (value can differ)
	            selectByVisibleTextAndEnsure(wait, departmentBy, "Obstetrics");

	            String countryVal = safeGetSelectedText(wait, countryBy);
	            String deptVal = safeGetSelectedText(wait, departmentBy);

	            // ===== File upload (with existence check) =====
	            String fileInfo = "NOT_SELECTED";
	            File f = new File(filePath);

	            if (!f.exists()) {
	                // if file missing, mark as validation fail but continue to finally (Excel write)
	                fileInfo = "MISSING(" + filePath + ")";
	            } else {
	                try {
	                    WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
	                    fileInput.sendKeys(f.getAbsolutePath());
	                    fileInfo = "SELECTED";
	                } catch (Exception e) {
	                    fileInfo = "UPLOAD_FAILED(" + e.getClass().getSimpleName() + ")";
	                }
	            }

	            // ✅ capture inputs BEFORE submit
	            inputs = "FirstName=" + safeGetValue(fnameBy)
	                    + " | LastName=" + safeGetValue(lnameBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | Mobile=" + safeGetValue(mobileBy)
	                    + " | Country=" + countryVal
	                    + " | Department=" + deptVal
	                    + " | File=" + fileInfo;

	            // If file missing -> directly validation fail (no point submitting)
	            if (fileInfo.startsWith("MISSING")) {
	                status = "❌ VALIDATION_FAIL";
	                fieldErrors = "Upload file not found on machine: " + filePath;
	                serverInfo = "Local file missing";
	                return; // finally will still run + Excel write
	            }

	            // ===== Submit =====
	            System.out.println("➡️ [RequestAnEstimate] Clicking submit...");
	            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);

	            try { Thread.sleep(800); } catch (Exception ignored) {}

	            // ✅ clear perf logs before submit (post-submit only check)
	            clearPerformanceLogs();

	            try {
	                submitBtn.click();
	            } catch (Exception e) {
	                js.executeScript("arguments[0].click();", submitBtn);
	            }

	            // ===== Detect outcomes =====
	            successSeen =
	                    waitForFlashPresence(successBy, 4500) || waitForFlashPresence(successFallbackBy, 9000);

	            network5xx = waitForNetwork5xx(9000);

	            fieldErrors = collectAllValidationErrors();
	            globalErrors = collectGlobalErrors();

	            // ===== Decide Status =====
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

	        } catch (Exception e) {

	            // set exception status only if still unknown
	            if (status == null || status.trim().isEmpty() || status.equals("⚠ UNKNOWN")) {
	                status = "❌ EXCEPTION";
	                serverInfo = e.getClass().getSimpleName() + " | " + e.getMessage();
	            }

	            // Optional: if you have 500 page detector
	            if (isServer500Like()) {
	                status = "❌ SERVER_FAIL (PAGE 500)";
	                serverInfo = "500 page detected during flow";
	            }

	        } finally {

	            // always compute debug safely
	            try {
	                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
	            } catch (Exception ignored) {
	                debug = "Debug not available";
	            }

	            // ===== PRINT =====
	            System.out.println("============== REQUEST AN ESTIMATE FORM RESULT ==============");
	            System.out.println("STATUS        : " + status);
	            System.out.println("SUCCESS       : " + successSeen);
	            System.out.println("NETWORK 5XX   : " + network5xx);
	            System.out.println("INPUTS        : " + inputs);
	            System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("SERVER INFO   : " + serverInfo);
	            System.out.println("DEBUG         : " + debug);
	            System.out.println("============================================================");

	            // ✅ Excel ALWAYS writes
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);
	        }

	        // ✅ Fail AFTER excel write
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
	        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " text='" + visibleText + "'");
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
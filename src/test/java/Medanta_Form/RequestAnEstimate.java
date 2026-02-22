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

	        String url = "https://www.medanta.org/international-patient/services/request-an-estimate";
	        driver.navigate().to(url);

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        // Scroll slightly (same as your logic)
	        js.executeScript("window.scrollBy(0,210)", "");

	        // ===== Locators (same as your code) =====
	        By fnameBy      = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
	        By lnameBy      = By.xpath("//input[@placeholder='Enter Your Last Name']");
	        By emailBy      = By.xpath("//input[@placeholder='Enter Your E-mail']");
	        By mobileBy     = By.xpath("//input[@placeholder='Enter Your Mobile No.']");
	        By countryBy    = By.xpath("//select[@class='inputbox']");
	        By departmentBy = By.xpath("(//select[@class='inputbox'])[2]");
	        By fileBy       = By.xpath("(//input[@type='file'])[2]");
	        By submitBy     = By.xpath("(//button[@type='submit'])[3]");
	        By successBy    = By.xpath("//div[contains(text(),'Thank you')]");

	        By successFallbackBy = By.xpath(
	                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                        + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	        );

	        // ===== Test Data =====
	        String expFName   = "Dipesh";
	        String expLName   = "Singh";
	        String expEmail   = "dipesh@yopmail.com";
	        String expMobile  = "9876543210";

	        // ===== File upload (OS safe path) =====
	        String filePath = System.getProperty("user.dir")
	                + File.separator + "SampleDocs"
	                + File.separator + "upload.docx";

	        System.out.println("➡️ [RequestAnEstimate] Opening page...");
	        wait.until(ExpectedConditions.visibilityOfElementLocated(fnameBy));

	        System.out.println("➡️ [RequestAnEstimate] Filling form...");
	        typeAndEnsureValue(wait, js, fnameBy, expFName);
	        typeAndEnsureValue(wait, js, lnameBy, expLName);
	        typeAndEnsureValue(wait, js, emailBy, expEmail);
	        typeAndEnsureValue(wait, js, mobileBy, expMobile);

	        // value wipe protection (for text fields)
	        ensureValueStillPresent(fnameBy, expFName);
	        ensureValueStillPresent(lnameBy, expLName);
	        ensureValueStillPresent(emailBy, expEmail);
	        ensureValueStillPresent(mobileBy, expMobile);

	        // ===== Dropdowns (ensure selection) =====
	        selectByIndexAndEnsure(wait, countryBy, 8);
	        selectByValueAndEnsure(wait, departmentBy, "Obstetrics");

	        try { Thread.sleep(800); } catch (Exception ignored) {}

	        // ===== File upload =====
	        try {
	            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
	            js.executeScript("arguments[0].scrollIntoView({block:'center'});", fileInput);
	            fileInput.sendKeys(filePath);
	        } catch (Exception e) {
	            System.out.println("⚠️ [RequestAnEstimate] File upload failed/skipped: " + e.getMessage());
	        }

	        // capture inputs BEFORE submit
	        String inputs =
	                "FirstName=" + safeGetValue(fnameBy)
	                        + " | LastName=" + safeGetValue(lnameBy)
	                        + " | Email=" + safeGetValue(emailBy)
	                        + " | Mobile=" + safeGetValue(mobileBy)
	                        + " | CountryIndex=8"
	                        + " | Department=Obstetrics"
	                        + " | File=" + filePath;

	        // ===== Submit =====
	        System.out.println("➡️ [RequestAnEstimate] Clicking submit...");
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
	                waitForFlashPresence(successBy, 4500) || waitForFlashPresence(successFallbackBy, 9000);

	        boolean network5xx = waitForNetwork5xx(9000);

	        String fieldErrors  = collectAllValidationErrors();
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

	        // ===== Excel (E → L) =====
	        writeFormResult(row, status, inputs, fieldErrors, globalErrors, serverInfo, successSeen, debug);

	        if (!status.contains("PASS")) {
	            Assert.fail("Request An Estimate form failed -> " + status + " | " + debug);
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
	        Assert.fail("Value did not persist for locator: " + locator + " expected='" + value + "'");
	    }

	    private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
	                Select s = new Select(dd);
	                s.selectByIndex(index);
	                String selected = s.getFirstSelectedOption().getText().trim();
	                if (selected != null && !selected.isEmpty()) return;
	            } catch (StaleElementReferenceException ignored) {
	            } catch (Exception ignored) {
	            }
	        }
	        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
	    }

	    private void selectByValueAndEnsure(WebDriverWait wait, By selectLocator, String value) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
	                Select s = new Select(dd);
	                s.selectByValue(value);
	                String selectedVal = s.getFirstSelectedOption().getAttribute("value");
	                if (selectedVal != null && selectedVal.equals(value)) return;
	            } catch (StaleElementReferenceException ignored) {
	            } catch (Exception ignored) {
	            }
	        }
	        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " value=" + value);
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
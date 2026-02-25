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

public class MedantaPharmacy_RequestACallbackForm extends BaseClass {

	
	 @Test(priority = 1)
	    public void MedantaPharmacyPage_RequestACallbackForm() {

	        int row = 21;

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

	            String url = "https://www.medanta.org/medanta-pharmacy";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            // ===== Locators =====
	            By nameBy = By.name("name");
	            By mobileBy = By.name("mobile");
	            By emailBy = By.name("email");
	            By messageBy = By.xpath("//textarea[@class='inputbox']");
	            By submitBy = By.xpath("(//button[@type='submit'])[3]");

	            // success (specific + fallback)
	            By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");
	            By successFallbackBy = By.xpath(
	                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	            );

	            // ===== Test Data =====
	            String expName = "Dipesh";
	            String expMobile = "9876543210";
	            String expEmail = "dipesh@yopmail.com";
	            String expMsg = "Test";

	            System.out.println("➡️ [MedantaPharmacy] Opening page...");

	            // wait first field & scroll (safe)
	            try {
	                WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	                scrollToElement(first);
	            } catch (Exception ignored) {}

	            System.out.println("➡️ [MedantaPharmacy] Filling form...");

	            // ===== SAFE FILL =====
	            try { typeAndEnsureValue(wait, js, nameBy, expName); }
	            catch (Exception ex) { fillIssues += "Name fill failed | "; }

	            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
	            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

	            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
	            catch (Exception ex) { fillIssues += "Email fill failed | "; }

	            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
	            catch (Exception ex) { fillIssues += "Message fill failed | "; }

	            // optional wipe protection (safe, no crash)
	            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(messageBy, expMsg); } catch (Exception ignored) {}

	            // ✅ Capture inputs ALWAYS
	            inputs = "Name=" + safeGetValue(nameBy)
	                    + " | Mobile=" + safeGetValue(mobileBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | Message=" + safeGetValue(messageBy)
	                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim());

	            // ===== Submit =====
	            System.out.println("➡️ [MedantaPharmacy] Clicking submit...");

	            try {
	                WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	                js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
	                try { Thread.sleep(600); } catch (Exception ignored) {}

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

	            // ===== Detect success =====
	            if (submitClicked) {
	                successSeen = waitForFlashPresence(successBy, 5000)
	                        || waitForFlashPresence(successFallbackBy, 9000);
	            }

	            // ✅ Always collect errors
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

	        } catch (Exception e) {
	            status = "❌ EXCEPTION";
	            info = e.getClass().getSimpleName() + " | " + e.getMessage();

	        } finally {

	            try {
	                debug = driver.getCurrentUrl() + " | " + driver.getTitle();
	            } catch (Exception ignored) {
	                debug = "Debug not available";
	            }

	            System.out.println("============== MEDANTA PHARMACY FORM RESULT ==============");
	            System.out.println("STATUS         : " + status);
	            System.out.println("SUBMIT CLICKED : " + submitClicked);
	            System.out.println("SUCCESS        : " + successSeen);
	            System.out.println("INPUTS         : " + inputs);
	            System.out.println("FIELD ERRORS   : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS  : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("INFO           : " + info);
	            System.out.println("DEBUG          : " + debug);
	            System.out.println("==========================================================");

	            // writeFormResult expects serverInfo param -> we pass info there
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
	        }

	        if (!status.contains("PASS")) {
	            Assert.fail("Medanta Pharmacy form failed -> " + status + " | " + debug);
	        }
	    }

	    /* ================= SAFE TYPE ================= */

	    private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
	        for (int attempt = 1; attempt <= 3; attempt++) {
	            try {
	                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
	                js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

	                try { el.click(); } catch (Exception ignored) {}

	                // slowType already does strong clear + JS fallback
	                slowType(el, value);

	                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

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
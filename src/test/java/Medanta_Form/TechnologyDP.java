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

public class TechnologyDP extends BaseClass {

	
	 @Test(priority = 1)
	    public void TechnologyDetailPage_QueryForm() {

	        int row = 26;

	        String status = "⚠ UNKNOWN";
	        String inputs = "";
	        String fieldErrors = "";
	        String globalErrors = "";
	        boolean successSeen = false;
	        boolean submitClicked = false;
	        String fillIssues = "";
	        String debug = "";
	        String info = ""; // ✅ add this

	        try {

	            String url = "https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/radiology/technology/ct-scan-on-wheels";
	            driver.navigate().to(url);

	            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	            JavascriptExecutor js = (JavascriptExecutor) driver;

	            By nameBy    = By.name("name");
	            By mobileBy  = By.name("mobile");
	            By emailBy   = By.name("email");
	            By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
	            By submitBy  = By.xpath("(//button[@type='submit'])[3]");

	            // ✅ better: exact success class/text bhi add kar do (flash issue)
	            By thankYouBy = By.xpath("//*[contains(@class,'successmsg') and contains(.,'Thank you')]");
	            By successFallbackBy = By.xpath(
	                    "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
	                            + "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]"
	            );

	            String expName   = "Test";
	            String expMobile = "9876543210";
	            String expEmail  = "wakemedantatest@gmail.com";
	            String expMsg    = "Testing the form Please ignore";

	            System.out.println("➡️ [TechnologyDP] Opening page...");

	            WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
	            scrollToElement(first);

	            System.out.println("➡️ [TechnologyDP] Filling form...");

	            try { typeAndEnsureValue(wait, js, nameBy, expName); }
	            catch (Exception ex) { fillIssues += "Name fill failed | "; }

	            try { typeAndEnsureValue(wait, js, mobileBy, expMobile); }
	            catch (Exception ex) { fillIssues += "Mobile fill failed | "; }

	            try { typeAndEnsureValue(wait, js, emailBy, expEmail); }
	            catch (Exception ex) { fillIssues += "Email fill failed | "; }

	            try { typeAndEnsureValue(wait, js, messageBy, expMsg); }
	            catch (Exception ex) { fillIssues += "Message fill failed | "; }

	            try { ensureValueStillPresent(nameBy, expName); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(mobileBy, expMobile); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(emailBy, expEmail); } catch (Exception ignored) {}
	            try { ensureValueStillPresent(messageBy, expMsg); } catch (Exception ignored) {}

	            inputs = "Name=" + safeGetValue(nameBy)
	                    + " | Mobile=" + safeGetValue(mobileBy)
	                    + " | Email=" + safeGetValue(emailBy)
	                    + " | Message=" + safeGetValue(messageBy)
	                    + (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues);

	            System.out.println("➡️ [TechnologyDP] Clicking submit...");

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
	                info = "Submit click failed: " + clickEx.getClass().getSimpleName();
	            }

	            if (submitClicked) {
	                // ✅ flash fast hai, isliye polling fast rakho
	                successSeen = waitForFlashPresence(thankYouBy, 9000) || waitForFlashPresence(successFallbackBy, 9000);
	            }

	            fieldErrors = collectAllValidationErrors();
	            globalErrors = collectGlobalErrors();

	            if (!submitClicked) {
	                status = "❌ FORM_NOT_SUBMITTED";
	                if (info == null || info.isBlank()) info = "Submit not clicked";
	            } else if (successSeen) {
	                status = "✅ PASS";
	                info = "Submitted"; // ✅ IMPORTANT
	            } else if (fieldErrors != null && !fieldErrors.isBlank()) {
	                status = "❌ VALIDATION_FAIL";
	                info = "Validation errors";
	            } else if (globalErrors != null && !globalErrors.isBlank()) {
	                status = "❌ GLOBAL_ERROR";
	                info = "Global error shown";
	            } else {
	                status = "⚠ UNKNOWN";
	                info = "No success/error signal detected";
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

	            System.out.println("============== TECHNOLOGY DP FORM RESULT ==============");
	            System.out.println("STATUS         : " + status);
	            System.out.println("SUBMIT CLICKED : " + submitClicked);
	            System.out.println("SUCCESS        : " + successSeen);
	            System.out.println("INPUTS         : " + inputs);
	            System.out.println("FIELD ERRORS   : " + (fieldErrors == null ? "" : fieldErrors));
	            System.out.println("GLOBAL ERRORS  : " + (globalErrors == null ? "" : globalErrors));
	            System.out.println("INFO           : " + info);
	            System.out.println("DEBUG          : " + debug);
	            System.out.println("=======================================================");

	            // ✅ PASS ho to Excel me "Submitted" jayega
	            writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
	        }

	        if (!status.contains("PASS")) {
	            Assert.fail("Technology DP form failed -> " + status + " | " + debug);
	        }
	    }

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
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

public class MedantaLabs extends BaseClass {

	@Test(priority = 1)
	public void MedantaLabsPage_RequestACallbackForm() {

		int row = 20;

		String status = "⚠ UNKNOWN";
		String inputs = "";
		String fieldErrors = "";
		String globalErrors = "";
		String info = "";
		boolean successSeen = false;
		String debug = "";

		boolean submitClicked = false;
		String fillIssues = "";

		WebDriverWait wait = null;
		JavascriptExecutor js = null;

		try {

			String url = "https://www.medanta.org/medanta-labs";
			driver.navigate().to(url);

			wait = new WebDriverWait(driver, Duration.ofSeconds(15));
			js = (JavascriptExecutor) driver;

			// ===== Locators =====
			By nameBy = By.name("name");
			By mobileBy = By.name("mobile");
			By emailBy = By.name("email");
			By messageBy = By.xpath("//textarea[@placeholder='Enter Your Message']");
			By submitBy = By.xpath("(//button[@type='submit'])[3]");

			// success element + fallback
			By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");
			By successFallbackBy = By.xpath(
					"//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'thank you') "
							+ "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'successfully') "
							+ "or contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submitted')]");

			// ===== Test Data =====
			String expName = "Dipesh";
			String expMobile = "9876543210";
			String expEmail = "dipesh@yopmail.com";
			String expMsg = "Test";

			System.out.println("➡️ [MedantaLabs] Opening page...");

			// ensure first field visible (don’t crash here)
			try {
				WebElement firstField = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
				scrollToElement(firstField);
			} catch (Exception ignored) {
			}

			System.out.println("➡️ [MedantaLabs] Filling form...");

			// ===== SAFE FILL (never stop test mid-way) =====
			try {
				typeAndEnsureValue(wait, js, nameBy, expName);
			} catch (Exception ex) {
				fillIssues += "Name fill failed | ";
			}

			try {
				typeAndEnsureValue(wait, js, mobileBy, expMobile);
			} catch (Exception ex) {
				fillIssues += "Mobile fill failed | ";
			}

			try {
				typeAndEnsureValue(wait, js, emailBy, expEmail);
			} catch (Exception ex) {
				fillIssues += "Email fill failed | ";
			}

			try {
				typeAndEnsureValue(wait, js, messageBy, expMsg);
			} catch (Exception ex) {
				fillIssues += "Message fill failed | ";
			}

			// optional wipe protection (don’t fail)
			try {
				ensureValueStillPresent(nameBy, expName);
			} catch (Exception ignored) {
				fillIssues += "Name value wiped | ";
			}
			try {
				ensureValueStillPresent(mobileBy, expMobile);
			} catch (Exception ignored) {
				fillIssues += "Mobile value wiped | ";
			}
			try {
				ensureValueStillPresent(emailBy, expEmail);
			} catch (Exception ignored) {
				fillIssues += "Email value wiped | ";
			}
			try {
				ensureValueStillPresent(messageBy, expMsg);
			} catch (Exception ignored) {
				fillIssues += "Message value wiped | ";
			}

			// ✅ inputs ALWAYS captured (pass/fail both)
			inputs = "Name=" + safeGetValue(nameBy)
					+ " | Mobile=" + safeGetValue(mobileBy)
					+ " | Email=" + safeGetValue(emailBy)
					+ " | Message=" + safeGetValue(messageBy)
					+ (fillIssues.isBlank() ? "" : " | FillIssues=" + fillIssues.trim());

			// ===== Submit (ALWAYS attempt) =====
			System.out.println("➡️ [MedantaLabs] Clicking submit...");
			try {
				WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
				js.executeScript("arguments[0].scrollIntoView({block:'center'});", submitBtn);
				try {
					Thread.sleep(400);
				} catch (Exception ignored) {
				}

				
				

				try {
					submitBtn.click();
				} catch (Exception e) {
					js.executeScript("arguments[0].click();", submitBtn);
				}

				submitClicked = true;

			} catch (Exception ex) {
				submitClicked = false;
				fillIssues += "Submit click failed | ";
			}

			// ===== Detect outcomes =====
			successSeen = submitClicked
					&& (waitForFlashPresence(successBy, 4500) || waitForFlashPresence(successFallbackBy, 9000));

			// ✅ Always collect errors (after submit attempt)
			fieldErrors = collectAllValidationErrors();
			globalErrors = collectGlobalErrors();

			String issues = (fillIssues == null) ? "" : fillIssues.trim();
			if (issues.endsWith("|"))
				issues = issues.substring(0, issues.length() - 1).trim();

			// ===== Decide status =====
			if (successSeen) {
				status = "✅ PASS";
				info = issues.isBlank() ? "Submitted" : ("Submitted | " + issues);
			} else if (!submitClicked) {
				status = "❌ FORM_NOT_SUBMITTED";
				info = issues.isBlank() ? "Submit not clicked" : issues;
			} else if (fieldErrors != null && !fieldErrors.isBlank()) {
				status = "❌ VALIDATION_FAIL";
				info = issues.isBlank() ? "Validation errors" : issues;
			} else if (globalErrors != null && !globalErrors.isBlank()) {
				status = "❌ GLOBAL_FAIL";
				info = issues.isBlank() ? "Global error shown" : issues;
			} else {
				status = "⚠ UNKNOWN";
				info = issues.isBlank() ? "No success/error signal detected" : issues;
			}

		} catch (Exception e) {

			status = "❌ FORM_NOT_SUBMITTED";
			info = "Flow crashed: " + e.getClass().getSimpleName() + " | " + e.getMessage();

		} finally {

			try {
				debug = driver.getCurrentUrl() + " | " + driver.getTitle();
			} catch (Exception ignored) {
				debug = "Debug not available";
			}

			System.out.println("============== MEDANTA LABS FORM RESULT ==============");
			System.out.println("STATUS        : " + status);
			System.out.println("SUBMIT CLICKED: " + submitClicked);
			System.out.println("SUCCESS       : " + successSeen);
			System.out.println("INPUTS        : " + inputs);
			System.out.println("FIELD ERRORS  : " + (fieldErrors == null ? "" : fieldErrors));
			System.out.println("GLOBAL ERRORS : " + (globalErrors == null ? "" : globalErrors));
			System.out.println("INFO          : " + info);
			System.out.println("DEBUG         : " + debug);
			System.out.println("======================================================");

			// ✅ Excel ALWAYS writes
			writeFormResult(row, status, inputs, fieldErrors, globalErrors, info, successSeen, debug);
		}

		// ✅ Fail AFTER excel write
		if (!status.contains("PASS")) {
			Assert.fail("Medanta Labs form failed -> " + status + " | " + debug);
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
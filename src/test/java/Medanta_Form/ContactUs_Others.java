package Medanta_Form;

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

public class ContactUs_Others extends BaseClass {

	 @Test(priority = 1)
	    public void ContactUsPage_OthersForm() {

	        driver.navigate().to("https://www.medanta.org/contact-us");

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

	        // -------- Wait loader --------
	        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));

	        // -------- Open Others Form --------
	        WebElement otherFormBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
	        otherFormBtn.click();

	        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

	        // -------- By locators (same as your code) --------
	        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[5]");
	        By emailBy = By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]");
	        By hospitalBy = By.xpath("(//select[@name='hospital'])[2]");
	        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]");
	        By messageBy = By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]");
	        By submitBy = By.xpath("(//button[@type='submit'])[5]");
	        By successBy = By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]");

	        // -------- Fill form (SAFE TYPE) --------
	        typeAndEnsureValue(wait, js, nameBy, "dipesh");
	        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

	        // Dropdown (ensure)
	        selectByIndexAndEnsure(wait, hospitalBy, 1);
	        String hospitalVal = safeGetSelectedText(wait, hospitalBy);

	        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
	        typeAndEnsureValue(wait, js, messageBy, "Test");

	        // -------- Safety validation (fresh fetch) --------
	        Assert.assertFalse(driver.findElement(nameBy).getAttribute("value").isEmpty(), "Name is empty");
	        Assert.assertFalse(driver.findElement(emailBy).getAttribute("value").isEmpty(), "Email is empty");
	        Assert.assertFalse(driver.findElement(mobileBy).getAttribute("value").isEmpty(), "Mobile is empty");
	        Assert.assertFalse(driver.findElement(messageBy).getAttribute("value").isEmpty(), "Message is empty");

	        // -------- Submit --------
	        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
	        try { Thread.sleep(800); } catch (Exception ignored) {}
	        try {
	            submitBtn.click();
	        } catch (Exception e) {
	            js.executeScript("arguments[0].click();", submitBtn);
	        }

	        try {
	            // -------- PASS --------
	            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

	            writeExcel(7, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	            System.out.println("✅ Others Form – PASS");

	        } catch (Exception e) {
	            // -------- FAIL --------
	            String nameVal = safeGetValue(nameBy);
	            String emailVal = safeGetValue(emailBy);
	            String mobileVal = safeGetValue(mobileBy);
	            String messageVal = safeGetValue(messageBy);

	            StringBuilder errorMsg = new StringBuilder();

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "(//input[@placeholder='Enter Your Name'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "(//input[@placeholder='Enter Your E-mail'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "(//input[@placeholder='Enter Your Phone Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(
	                        By.xpath("(//select[@name='hospital'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Hospital Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            try {
	                WebElement err = driver.findElement(By.xpath(
	                        "(//textarea[@placeholder='Enter Your Message Here'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
	                if (err.isDisplayed())
	                    errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
	            } catch (Exception ignored) {}

	            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
	                    + " | Hospital=" + hospitalVal + " | Message=" + messageVal + " | Errors => " + errorMsg;

	            writeExcel(7, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	            writeExcel(7, 5, finalResult);

	            System.out.println(finalResult);
	            Assert.fail("Others form validation failed: " + finalResult);
	        }
	    }

	    // ✅ same helper (paste in every form for now)
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

	    // ✅ dropdown helper
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
	            return v == null ? "" : v;
	        } catch (Exception e) {
	            return "";
	        }
	    }
	}
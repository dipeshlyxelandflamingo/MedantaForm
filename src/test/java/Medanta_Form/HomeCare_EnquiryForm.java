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

public class HomeCare_EnquiryForm extends BaseClass{
	
	@Test(priority = 1)
    public void HomeCarePage_EnquiryForm() {

        driver.navigate().to("https://www.medanta.org/home-care");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,2000)");

        // -------- By locators (same as your code) --------
        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[4]");
        By mobileBy = By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]");
        By emailBy = By.xpath("(//input[@placeholder='Enter Your Email'])[4]");
        By locationBy = By.xpath("//select[@placeholder='Select Location']");
        By submitBy = By.xpath("(//button[@type='submit'])[4]");
        By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

        // -------- Fill form (SAFE TYPE) --------
        typeAndEnsureValue(wait, js, nameBy, "Dipesh");
        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

        // dropdown
        selectByIndexAndEnsure(wait, locationBy, 1);
        String locationVal = safeGetSelectedText(wait, locationBy);

        // -------- Submit form --------
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        try {
            submitBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", submitBtn);
        }

        // ===== Try-catch: success/fail =====
        try {
            // -------- PASS --------
            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

            writeExcel(15, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ HomeCare Enquiry Form – PASS");

        } catch (Exception e) {
            // -------- FAIL: capture values and errors --------
            String nameVal = safeGetValue(nameBy);
            String mobileVal = safeGetValue(mobileBy);
            String emailVal = safeGetValue(emailBy);

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(By.xpath(
                        "(//input[@placeholder='Enter Your Name'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "(//input[@placeholder='Enter Your Mobile Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "(//input[@placeholder='Enter Your Email'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Location Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
                    + " | Location=" + locationVal + " | Errors => " + errorMsg;

            writeExcel(15, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(15, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("HomeCare Enquiry form validation failed: " + finalResult);
        }
    }

    // ✅ text/textarea helper
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

    // ✅ dropdown helper (index)
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
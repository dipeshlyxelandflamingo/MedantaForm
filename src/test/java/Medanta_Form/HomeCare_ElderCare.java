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

public class HomeCare_ElderCare extends BaseClass {

	@Test(priority = 1)
    public void HomeCare_ElderCareProgram_QueryForm() throws Throwable {

        driver.navigate().to("https://www.medanta.org/home-care-service-program/elder-care-program");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // -------- By locators (same as your code) --------
        By nameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
        By mobileBy = By.xpath("(//input[@type='number'])[3]");
        By emailBy = By.xpath("(//input[@type='email'])[2]");
        By submitBy = By.xpath("(//button[@type='submit'])[3]");
        By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

        // -------- Scroll and fill fields --------
        WebElement nameForScroll = wait.until(ExpectedConditions.visibilityOfElementLocated(nameBy));
        scrollToElement(nameForScroll);

        typeAndEnsureValue(wait, js, nameBy, "Dipesh");

        try { Thread.sleep(1000); } catch (Exception ignored) {}

        typeAndEnsureValue(wait, js, mobileBy, "9876543210");
        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

        // -------- Wait before submit --------
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
        try { Thread.sleep(800); } catch (Exception ignored) {}

        try {
            submitBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", submitBtn);
        }

        // -------- Try-catch for success/fail starts with success message --------
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

            writeExcel(14, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Elder Care Program Form – PASS");

        } catch (Exception e) {
            // -------- FAIL: capture entered values and errors --------
            String nameVal = safeGetValue(nameBy);
            String mobileVal = safeGetValue(mobileBy);
            String emailVal = safeGetValue(emailBy);

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(By.xpath(
                        "(//input[@placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("(//input[@type='number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("(//input[@type='email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            // NOTE: you had Location error block but there is no location dropdown in this form code.
            // Keeping your original block is ok, but it may always be empty. Not changing it to avoid side-effects.
            try {
                WebElement err = driver.findElement(By.xpath(
                        "//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Location Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
                    + " | Errors => " + errorMsg;

            writeExcel(14, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(14, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("Elder Care Program validation failed: " + finalResult);
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
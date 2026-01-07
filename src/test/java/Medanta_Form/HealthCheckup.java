package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class HealthCheckup extends BaseClass {


    @Test(priority = 1)
    public void HealthCheckup_RequestACallbackForm() {
        driver.navigate().to("https://www.medanta.org/healthcheckup/gurugram-hospital");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

       
            // ===== Locate fields =====
            WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
            WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.name("mobile")));
            WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
            WebElement message = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@class='inputbox']")));
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));

            // ===== Scroll to first field =====
            scrollToElement(name);

            // ===== Fill form slowly =====
            slowType(name, "Dipesh");
            slowType(mobile, "9876543210");
            slowType(email, "dipesh@yopmail.com");
            slowType(message, "Test");

            // ===== Submit form =====
            submitBtn.click();
try {
            // ===== PASS: Wait for success message =====
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you')]")));

            writeExcel(13, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Health Checkup – PASS");

        } catch (Exception e) {
            // ===== FAIL: Capture entered values safely =====
            String nameVal = safeGetValue(By.name("name"));
            String mobileVal = safeGetValue(By.name("mobile"));
            String emailVal = safeGetValue(By.name("email"));
            String messageVal = safeGetValue(By.xpath("//textarea[@class='inputbox']"));

            StringBuilder errorMsg = new StringBuilder();
            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}
            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}
            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='email']/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}
            try {
                WebElement err = driver.findElement(By.xpath("//textarea[@class='inputbox']/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
                    + " | Message=" + messageVal
                    + " | Errors => " + errorMsg;

            writeExcel(13, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(13, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("Health Checkup form validation failed: " + finalResult);
        }
    }

    // ===== Helper: safely get field value =====
    private String safeGetValue(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() ? element.getAttribute("value") : "";
        } catch (Exception e) {
            return "";
        }
    }
}
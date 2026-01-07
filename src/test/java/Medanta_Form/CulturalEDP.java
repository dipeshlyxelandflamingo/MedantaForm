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

public class CulturalEDP extends BaseClass {
	
    

    @Test(priority = 1)
    public void CulturalEventDetailPage_QueryForm() {

        driver.navigate().to("https://www.medanta.org/careers/events/cultural-events");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // -------- Locate fields --------
        WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='name']")));
        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='mobile']")));
        WebElement email = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]")));
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[@type='submit'])[3]")));

        // -------- Scroll and fill fields --------
        scrollToElement(name);
        slowType(name, "Test");
        slowType(mobile, "9876543210");
        slowType(email, "wakemedantatest@gmail.com");

        // -------- Wait before submit --------
        try { Thread.sleep(800); } catch (Exception ignored) {}
        submitBtn.click();

        // -------- Try-catch for success/fail --------
        try {
            // -------- PASS --------
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(9, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Cultural Event Query Form – PASS");

        } catch (Exception e) {
            // -------- FAIL --------
            String nameVal = name.getAttribute("value");
            String mobileVal = mobile.getAttribute("value");
            String emailVal = email.getAttribute("value");

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
                WebElement err = driver.findElement(By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
                    + " | Errors => " + errorMsg;

            writeExcel(9, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(9, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("Cultural Event Query form validation failed: " + finalResult);
        }
    }
}
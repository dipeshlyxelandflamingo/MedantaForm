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

public class ElderCare_RequestACallbackForm extends BaseClass {

	

    @Test(priority = 1)
    public void ElderCareProgramPage_RequestACallbackForm() {

        driver.navigate().to("https://www.medanta.org/elder-care-program");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

       

        // -------- Locate fields --------
        WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.name("mobile")));
        WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
        WebElement message = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@class='inputbox']")));
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));

        // -------- Scroll first field into view --------
        scrollToElement(name);

        // -------- Fill form --------
        slowType(name, "Dipesh");
        slowType(mobile, "9876543210");
        slowType(email, "dipesh@yopmail.com");
        slowType(message, "Test");

        // -------- Submit --------
        try { Thread.sleep(800); } catch (Exception ignored) {}
        submitBtn.click();

        // -------- Try-catch for success/fail --------
        try {
            // -------- PASS --------
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(11, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ ElderCare_RequestACallbackForm – PASS");

        } catch (Exception e) {
            // -------- FAIL --------
            String nameVal = name.getAttribute("value");
            String mobileVal = mobile.getAttribute("value");
            String emailVal = email.getAttribute("value");
            String messageVal = message.getAttribute("value");

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='name']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='mobile']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("//textarea[@class='inputbox']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
                    + " | Message=" + messageVal
                    + " | Errors => " + errorMsg;

            writeExcel(11, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(11, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("ElderCare_RequestACallbackForm validation failed: " + finalResult);
        }
    }
}
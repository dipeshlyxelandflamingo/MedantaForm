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

public class BecomeAPartner extends BaseClass {

	

    @Test(priority = 1)
    public void BecomeAPartnerPage_BusinessModelForm() {

        driver.navigate().to("https://www.medanta.org/medanta-labs/become-a-partner");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
       
        // ---------- Scroll to form ----------
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,2600)");

        // ---------- Locate fields ----------
        WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='text'])[4]")));
        WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='email'])[2]")));
        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='text'])[5]")));
        WebElement address = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='text'])[6]")));
        WebElement pin = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='text'])[7]")));
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));
       
       

        // ---------- Fill form (SLOW TYPE) ----------
        slowType(name, "Dipesh");
        slowType(email, "dipesh@yopmail.com");
        slowType(mobile, "9876543210");
        slowType(address, "Test");
        slowType(pin, "123456");

        // ---------- Safety check ----------
        Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name empty");
        Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email empty");
        Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile empty");
        Assert.assertFalse(address.getAttribute("value").isEmpty(), "Address empty");
        Assert.assertFalse(pin.getAttribute("value").isEmpty(), "Pin empty");

        // ---------- Submit button ----------
        
        try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // UI settle
        submitBtn.click();

        try {
            // ---------- PASS ----------
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Thank you')]")));

            writeExcel(3, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Become a Partner – PASS");

        } catch (Exception e) {
            // ---------- FAIL ----------
            String nameVal = name.getAttribute("value");
            String emailVal = email.getAttribute("value");
            String mobileVal = mobile.getAttribute("value");
            String addressVal = address.getAttribute("value");
            String pinVal = pin.getAttribute("value");

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[4]/following-sibling::span"));
                if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[5]/following-sibling::span"));
                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[6]/following-sibling::span"));
                if (err.isDisplayed()) errorMsg.append("Address Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath("(//input[@type='text'])[7]/following-sibling::span"));
                if (err.isDisplayed()) errorMsg.append("Pin Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult =
                    "Name=" + nameVal +
                    " | Email=" + emailVal +
                    " | Mobile=" + mobileVal +
                    " | Address=" + addressVal +
                    " | Pincode=" + pinVal +
                    " | Errors => " + errorMsg;

            writeExcel(3, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(3, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("Become a Partner form validation failed");
        }
    }
    
}
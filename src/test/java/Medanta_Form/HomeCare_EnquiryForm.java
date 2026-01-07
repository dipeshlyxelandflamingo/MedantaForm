package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

        // -------- Locate fields --------
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//input[@placeholder='Enter Your Name'])[4]")));
        WebElement mobileInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]")));
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//input[@placeholder='Enter Your Email'])[4]")));
        WebElement locationDD = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//select[@placeholder='Select Location']")));
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[@type='submit'])[4]")));

        // -------- Scroll to form --------
        

        // -------- Fill form using slow typing --------
        slowType(nameInput, "Dipesh");
        slowType(mobileInput, "9876543210");
        slowType(emailInput, "dipesh@yopmail.com");
        new Select(locationDD).selectByIndex(1); // default location

        // -------- Submit form --------
       try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        submitBtn.click();
        // ===== Try-catch: success/fail =====
        try {
            // -------- PASS: wait for thank you message --------
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(15, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ HomeCare Enquiry Form – PASS");

        } catch (Exception e) {
            // -------- FAIL: capture values and errors --------
            String nameVal = nameInput.getAttribute("value");
            String mobileVal = mobileInput.getAttribute("value");
            String emailVal = emailInput.getAttribute("value");

            String locationVal = "";
            try {
                locationVal = new Select(locationDD).getFirstSelectedOption().getText();
            } catch (Exception ignored) {}

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
}
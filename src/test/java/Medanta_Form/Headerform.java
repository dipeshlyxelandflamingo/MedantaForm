package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class Headerform extends BaseClass {
	 // Excel helper
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if (sheet.getRow(rowNum) == null)
                sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }

    @Test
    public void Header_RequestACallBackForm() throws Exception {

        driver.navigate().to("https://www.medanta.org");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        driver.findElement(By.xpath("//span[normalize-space()='Request Call Back']")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Thread.sleep(3000);
        // Fill form (INTENTIONALLY INVALID MOBILE)
        driver.findElement(By.xpath("//input[@id='fname']"))
                .sendKeys("Test");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile Number']"))
                .sendKeys("9876543210"); 
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@id='lname' and @placeholder='Enter Your Email']"))
                .sendKeys("wakemedantatest@gmail.com");
        Thread.sleep(3000);
        driver.findElement(By.xpath("//button[@class='submitpopupbt']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // SUCCESS CASE
            WebElement successMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(12, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
            System.out.println("✅ Header Request Call Back PASS");

        } catch (Exception e) {

            System.out.println("❌ Header Request Call Back FAIL");

            // Capture entered values
            String nameVal = driver.findElement(By.xpath("//input[@id='fname']"))
                    .getAttribute("value");

            String mobileVal = driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile Number']"))
                    .getAttribute("value");

            String emailVal = driver.findElement(
                    By.xpath("//input[@id='lname' and @placeholder='Enter Your Email']"))
                    .getAttribute("value");

            // Capture validation errors
            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement nameErr = driver.findElement(
                        By.xpath("//input[@id='fname']/following-sibling::span[contains(@class,'errmsg')]"));
                if (nameErr.isDisplayed())
                    errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement mobileErr = driver.findElement(
                        By.xpath("//input[@placeholder='Enter Your Mobile Number']/following-sibling::span[contains(@class,'errmsg')]"));
                if (mobileErr.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            // ✅ EMAIL ERROR (NEW ADDITION)
            try {
                WebElement emailErr = driver.findElement(
                        By.xpath("//input[@id='lname']/following-sibling::span[contains(@class,'errmsg')]"));
                if (emailErr.isDisplayed())
                    errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            // Final result
            String finalResult =
                    "Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Errors => " + errorMsg;

            writeExcel(12, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(12, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Header Request Call Back validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
    
}
package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class MedantaLabs extends BaseClass {
	
	 // Excel writer helper
    public void writeExcel(int row, int col, String value) {
        try {
            if (sheet.getRow(row) == null)
                sheet.createRow(row);
            sheet.getRow(row).createCell(col).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void MedantaLabsPage_RequestACallbackForm() throws Throwable {

        driver.navigate().to("https://www.medanta.org/medanta-labs");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Fill form (INTENTIONALLY INVALID MOBILE)
        driver.findElement(By.name("name")).sendKeys("Dipesh");
        Thread.sleep(1000);
        driver.findElement(By.name("mobile")).sendKeys("9876543210"); 
        Thread.sleep(1000);
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']")).sendKeys("Test");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            System.out.println("✅ Medanta Labs – Request Callback PASS");
            writeExcel(20, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ Medanta Labs – Request Callback FAIL");

            // Capture entered values
            String nameVal = driver.findElement(By.name("name")).getAttribute("value");
            String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
            String emailVal = driver.findElement(By.name("email")).getAttribute("value");
            String messageVal = driver.findElement(
                    By.xpath("//textarea[@placeholder='Enter Your Message']")).getAttribute("value");

            // Capture validation errors
            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(
                        By.xpath("//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("//textarea[@placeholder='Enter Your Message']/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            // Final result
            String finalResult =
                    "Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Message=" + messageVal +
                    " | Errors => " + errorMsg;

            writeExcel(20, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(20, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Medanta Labs validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
}
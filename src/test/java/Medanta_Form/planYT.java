package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class planYT extends BaseClass {

	// Common Excel writer
    public void writeExcel(int row, int col, String value) {
        try {
            if (sheet.getRow(row) == null)
                sheet.createRow(row);
            sheet.getRow(row).createCell(col).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }

    // ================== TC_01 : Plan Your Trip – Query Form ==================
    @Test(priority = 1)
    public void PlanYourTripPage_QueryForm() throws Throwable {

        driver.navigate().to("https://www.medanta.org/plan-your-trip");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Fill form (INTENTIONALLY INVALID MOBILE)
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]"))
                .sendKeys("Test");
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]"))
                .sendKeys("9876543210"); 
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[2]"))
                .sendKeys("wakemedantatest@gmail.com");

        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            System.out.println("✅ Plan Your Trip – Query Form PASS");
            writeExcel(23, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ Plan Your Trip – Query Form FAIL");

            // Capture entered values
            String nameVal = driver.findElement(
                    By.xpath("(//input[@placeholder='Enter Your Name'])[3]"))
                    .getAttribute("value");

            String mobileVal = driver.findElement(
                    By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]"))
                    .getAttribute("value");

            String emailVal = driver.findElement(
                    By.xpath("(//input[@placeholder='Enter Your Email'])[2]"))
                    .getAttribute("value");

            // Capture field-wise error messages
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
                        By.xpath("(//input[@placeholder='Enter Your Email'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult =
                    "Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Errors => " + errorMsg;

            writeExcel(23, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(23, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Plan Your Trip validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
}
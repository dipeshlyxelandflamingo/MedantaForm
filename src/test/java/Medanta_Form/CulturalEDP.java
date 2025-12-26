package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class CulturalEDP extends BaseClass {

	 // Helper method to write Excel
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
    public void CulturalEventDetailPage_QueryForm() throws InterruptedException {

        driver.navigate().to("https://www.medanta.org/careers/events/cultural-events");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Fill form (INTENTIONALLY INVALID MOBILE)
        driver.findElement(By.xpath("//input[@name='name']"))
                .sendKeys("Test");

        driver.findElement(By.xpath("//input[@name='mobile']"))
                .sendKeys("9876543210"); 

        driver.findElement(By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]"))
                .sendKeys("wakemedantatest@gmail.com");

        Thread.sleep(1500);
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // SUCCESS CASE
            WebElement successMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(9, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
            System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ Cultural Event Query Form FAIL");

            // Capture entered values
            String nameVal = driver.findElement(By.xpath("//input[@name='name']"))
                    .getAttribute("value");

            String mobileVal = driver.findElement(By.xpath("//input[@name='mobile']"))
                    .getAttribute("value");

            String emailVal = driver.findElement(
                    By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]"))
                    .getAttribute("value");

            // Capture validation errors
            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement nameErr = driver.findElement(
                        By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'errmsg')]"));
                if (nameErr.isDisplayed())
                    errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement mobileErr = driver.findElement(
                        By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'errmsg')]"));
                if (mobileErr.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement emailErr = driver.findElement(
                        By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]/following-sibling::span[contains(@class,'errmsg')]"));
                if (emailErr.isDisplayed())
                    errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            // Final result string
            String finalResult =
                    "Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Errors => " + errorMsg;

            writeExcel(9, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(9, 5, finalResult);
            Thread.sleep(3000);

            Assert.fail("Cultural Event Query form validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
}
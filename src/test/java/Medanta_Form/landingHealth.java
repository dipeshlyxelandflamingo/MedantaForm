package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class landingHealth extends BaseClass {

	 // Excel helper method
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if (sheet.getRow(rowNum) == null)
                sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void LandingHealth_Page_QueryForm() throws Exception {

        driver.navigate().to("https://www.medanta.org/ehc/hishealth-checkup/L1gz");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        Thread.sleep(3000);
        driver.findElement(By.name("name")).sendKeys("Test");
        Thread.sleep(1000);
        driver.findElement(By.name("mobile")).sendKeys("9876543210"); 
        Thread.sleep(1000);
        driver.findElement(By.name("email")).sendKeys("wakemedantatest@gmail.com");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"))
                .sendKeys("Testing the form Please ignore");
        Thread.sleep(3000);
        driver.findElement(By.xpath("(//button[@type='submit'])[2]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Success message
            WebElement successMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'Your query has been Successfully Submitted')]")));

            System.out.println("✅ Landing Health Query Form PASS");
            writeExcel(19, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ Landing Health Query Form FAIL");

            // Capture entered values
            String nameVal = driver.findElement(By.name("name")).getAttribute("value");
            String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
            String emailVal = driver.findElement(By.name("email")).getAttribute("value");
            String msgVal = driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']")).getAttribute("value");

            // Capture validation errors if any
            StringBuilder errorMsg = new StringBuilder();
            
            
            try {
                WebElement nameErr = driver.findElement(
                        By.xpath("//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (nameErr.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement mobileErr = driver.findElement(
                        By.xpath("//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (mobileErr.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement emailErr = driver.findElement(
                        By.xpath("//input[@name='email']/following-sibling::span[contains(@class,'errmsg')]"));
                if (emailErr.isDisplayed())
                    errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult =
                    "   Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Errors => " + errorMsg;

            writeExcel(19, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(19, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Landing Health Query form failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
}
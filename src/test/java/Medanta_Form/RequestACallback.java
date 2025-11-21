package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class RequestACallback extends BaseClass {

	  // Helper method to safely write to Excel
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if(sheet.getRow(rowNum) == null) sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Error writing to Excel: " + e.getMessage());
        }
    }

    @Test(priority=1)
    public void TelemedicinePage_RequestACallbackForm() {
        driver.navigate().to("https://www.medanta.org/tele-medicine");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.name("name")).sendKeys("Dipesh");
        driver.findElement(By.name("mobile")).sendKeys("9876543210");
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        driver.findElement(By.xpath("//textarea[@class='inputbox']")).sendKeys("Test");
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            boolean isMsgVisible = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//div[contains(@class,'successmsg') or contains(text(),'Thank you')]"),
                    "Thank you"
            ));

            Assert.assertTrue(isMsgVisible, "Telemedicine Request A Callback form submission failed");
            writeExcel(23, 4, "PASS!");
            System.out.println("Test 1 PASS");

        } catch (Exception e) {
            writeExcel(23, 4, "FAIL!");
            throw new RuntimeException(e);
        }
    }

    @Test(priority=2)
    public void Air_Ambulance() {
        driver.navigate().to("https://www.medanta.org/air-ambulance");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.name("name")).sendKeys("Dipesh");
        driver.findElement(By.name("mobile")).sendKeys("9876543210");
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        driver.findElement(By.xpath("//textarea[@class='inputbox']")).sendKeys("Test");
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            boolean isMsgVisible = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//div[contains(@class,'successmsg') or contains(text(),'Thank you')]"),
                    "Thank you"
            ));

            Assert.assertTrue(isMsgVisible, "Air Ambulance form submission failed");
            writeExcel(24, 4, "PASS!");
            System.out.println("Test 2 PASS");

        } catch (Exception e) {
            writeExcel(24, 4, "FAIL!");
            throw new RuntimeException(e);
        }
    }

    @Test(priority=3)
    public void Health_Checkup() {
        driver.navigate().to("https://www.medanta.org/healthcheckup/gurugram-hospital");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.name("name")).sendKeys("Dipesh");
        driver.findElement(By.name("mobile")).sendKeys("9876543210");
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        driver.findElement(By.xpath("//textarea[@class='inputbox']")).sendKeys("Test");
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            boolean isMsgVisible = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//div[contains(@class,'successmsg') or contains(text(),'Thank you')]"),
                    "Thank you"
            ));

            Assert.assertTrue(isMsgVisible, "Health Checkup form submission failed");
            writeExcel(25, 4, "PASS!");
            System.out.println("Test 3 PASS");

        } catch (Exception e) {
            writeExcel(25, 4, "FAIL!");
            throw new RuntimeException(e);
        }
    }
}
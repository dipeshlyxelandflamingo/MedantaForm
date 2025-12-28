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

public class ContactUs_Enquiry extends BaseClass {

	 // Helper method to write to Excel
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if(sheet.getRow(rowNum) == null) sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Error writing to Excel: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void ContactUsPage_EnquiryForm() throws Exception {
        driver.navigate().to("https://www.medanta.org/contact-us");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        Thread.sleep(3000);
        // Fill form
        driver.findElement(By.name("name")).sendKeys("dipesh");
        Thread.sleep(1000);
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='mobile']")).sendKeys("9876543210");
        Thread.sleep(1000);

        WebElement DD = driver.findElement(By.name("hospital"));
        Select Hospital = new Select(DD);
        Hospital.selectByIndex(1);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message Here']")).sendKeys("Test");
        Thread.sleep(1000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)", "");
        Thread.sleep(3000);
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));
            writeExcel(5, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
            System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");
        } catch (Exception e) {
            System.out.println("❌ Enquiry Form FAIL");

            // Capture values
            String nameVal = driver.findElement(By.name("name")).getAttribute("value");
            String emailVal = driver.findElement(By.name("email")).getAttribute("value");
            String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
            String LocationHospital = Hospital.getFirstSelectedOption().getText();
            String messageVal = driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message Here']")).getAttribute("value");

            // Capture errors
            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement nameErr = driver.findElement(By.xpath("(//input[@name='name']/following-sibling::div[contains(@class,'errmsg')])[1]"));
                if(nameErr.isDisplayed()) errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            
            
            try {
                WebElement emailErr = driver.findElement(By.xpath("(//input[@name='email']/following-sibling::div[contains(@class,'errmsg')])[1]"));
                if(emailErr.isDisplayed()) errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement mobileErr = driver.findElement(By.xpath("(//input[@name='mobile']/following-sibling::div[contains(@class,'errmsg')])[1]"));
                if(mobileErr.isDisplayed()) errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            
            
            try {
                WebElement locationErr = driver.findElement(By.xpath("(//select[@name='hospital'])[1]/following-sibling::div[contains(@class,'errmsg')])[1]"));
                if(locationErr.isDisplayed()) errorMsg.append("Location Error: ").append(locationErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            
            
            
            
            try {
                WebElement messageValErr = driver.findElement(By.xpath("(//textarea[@placeholder='Enter Your Message Here']/following-sibling::div[contains(@class,'errmsg')])[1]"));
                if(messageValErr.isDisplayed()) errorMsg.append("Msg Error: ").append(messageValErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            

           Thread.sleep(3000);

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Location=" + LocationHospital + " | Message=" + messageVal + " | Errors => " + errorMsg;
            writeExcel(5, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(5, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Enquiry form validation failed: " + finalResult);
            Thread.sleep(3000);
            Thread.sleep(3000);
        }
    }

    
}
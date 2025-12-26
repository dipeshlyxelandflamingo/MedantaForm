package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class ContactUs_Feedback extends BaseClass{
	
	
	
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
    public void ContactUsPage_FeedbackForm() throws Exception {
    	
    	 driver.navigate().to("https://www.medanta.org/contact-us");
         driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    	
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
        WebElement feedbackBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
        feedbackBtn.click();

        driver.findElement(By.name("feedback_message")).sendKeys("Test");
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).sendKeys("Dipesh");
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]")).sendKeys("9876543210"); // Invalid
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]")).sendKeys("dipesh@yopmail.com");
        driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement successMsg = wait1.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//div[contains(@class,'successmsg') and contains(text(),'Thank you')])[1]")));
            writeExcel(6, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
            System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");
        } catch (Exception e) {
            System.out.println("❌ Feedback Form FAIL");

            String nameVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).getAttribute("value");
            String mobileVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]")).getAttribute("value");
            String emailVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]")).getAttribute("value");
            String msgVal = driver.findElement(By.name("feedback_message")).getAttribute("value");

            StringBuilder errorMsg = new StringBuilder();
            
            try {
                WebElement feedbackErr = driver.findElement(By.xpath("//textarea[@name='feedback_message']/following-sibling::div[contains(@class,'errmsg')]"));
                if(feedbackErr.isDisplayed()) errorMsg.append("Feedback Error: ").append(feedbackErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            
            try {
                WebElement nameErr = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
                if(nameErr.isDisplayed()) errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            try {
                WebElement mobileErr = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
                if(mobileErr.isDisplayed()) errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            try {
                WebElement emailErr = driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
                if(emailErr.isDisplayed()) errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
            } catch (Exception ignored) {}
            
            
            Thread.sleep(3000);

            String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message=" + msgVal + " | Errors => " + errorMsg;
            writeExcel(6, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(6, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Feedback form validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
    
   

}

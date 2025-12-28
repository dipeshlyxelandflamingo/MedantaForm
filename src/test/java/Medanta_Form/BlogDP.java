package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class BlogDP extends BaseClass {

	 @Test(priority = 1)
	    public void BlogDetailPage_QueryForm() throws Throwable {

	        driver.navigate().to(
	                "https://www.medanta.org/patient-education-blog/omicron-variant-key-facts-you-need-to-know-in-2025");
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	        Thread.sleep(3000);
	        // ---------- Fill Form (INTENTIONALLY INVALID) ----------
	        driver.findElement(
	                By.xpath("(//input[@type='text' and @placeholder='Enter Your Name'])[3]"))
	                .sendKeys("Test");
	        Thread.sleep(1000);
	        
	        driver.findElement(
	                By.xpath("(//input[@type='number' and @placeholder='Enter Your Mobile Number'])[2]"))
	                .sendKeys("9876543210"); 
	        Thread.sleep(1000);
	        driver.findElement(
	                By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]"))
	                .sendKeys("wakemedantatest@gmail.com");
	        Thread.sleep(3000);
	        driver.findElement(By.xpath("//button[@class='theme-button submitpopupbt']")).click();

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	        try {
	            // ---------- PASS CHECK ----------
	            WebElement thankYouMsg = wait.until(
	                    ExpectedConditions.visibilityOfElementLocated(
	                            By.xpath("//div[contains(text(),'Thank you')]")));

	            sheet.getRow(4).createCell(4).setCellValue("✅ FORM SUBMITTED SUCCSESSFULLY!");
	            System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");

	        } catch (Exception e) {

	            System.out.println("❌ BLOG FORM FAIL");

	            // ---------- Get filled values ----------
	            String nameVal = driver.findElement(
	                    By.xpath("(//input[@type='text' and @placeholder='Enter Your Name'])[3]"))
	                    .getAttribute("value");

	            String mobileVal = driver.findElement(
	                    By.xpath("(//input[@type='number' and @placeholder='Enter Your Mobile Number'])[2]"))
	                    .getAttribute("value");

	            String emailVal = driver.findElement(
	                    By.xpath("(//input[@type='email' and @placeholder='Enter Your Email'])[2]"))
	                    .getAttribute("value");

	         // ---------- Capture Errors ----------
	            StringBuilder errorMsg = new StringBuilder();

	         // Name
	            try {
	                String nameError = driver.findElement(
	                        By.xpath("//input[@placeholder='Enter Your Name']"))
	                        .getAttribute("validationMessage");

	                if (nameError != null && !nameError.isEmpty()) {
	                    errorMsg.append("Name Error: ").append(nameError).append(" | ");
	                }
	            } catch (Exception ignored) {}

	            // Mobile
	            try {
	                WebElement mobileErr = driver.findElement(
	                    By.xpath("//input[@placeholder='Enter Your Mobile Number']/following-sibling::div[contains(@class,'errmsg')]")
	                );
	                if (mobileErr.isDisplayed()) {
	                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
	                }
	            } catch (Exception ignored) {}

	            // Email
	            try {
	                WebElement emailErr = driver.findElement(
	                    By.xpath("//input[@placeholder='Enter Your Email']/following-sibling::div[contains(@class,'errmsg')]")
	                );
	                if (emailErr.isDisplayed()) {
	                    errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
	                }
	            } catch (Exception ignored) {}

	            // ---------- Final Excel Message ----------
	            String finalResult =
	                    "Name=" + nameVal +
	                    " | Mobile=" + mobileVal +
	                    " | Email=" + emailVal +
	                    " | Errors => " + errorMsg;

	            sheet.getRow(4).createCell(4).setCellValue("❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
	            sheet.getRow(4).createCell(5).setCellValue(finalResult);
	            Thread.sleep(3000);
	            System.out.println(finalResult);
	            Assert.fail("Blog query form validation failed");
	            Thread.sleep(3000);
	        }
	    }
	}
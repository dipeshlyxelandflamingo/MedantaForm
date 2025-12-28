package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class AilmentDP extends BaseClass {

	@Test(priority = 1)
	public void AilmentDetailPage_RequestCallBackForm() throws InterruptedException {

	    driver.navigate().to(
	        "https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/orthopaedics/disease/achilles-tendon-injury-symptoms-causes-treatment-recovery"
	    );

	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	    Thread.sleep(3000);
	    // ---------- Fill Form ----------
	    driver.findElement(By.name("name")).sendKeys("dipesh");
	    Thread.sleep(1000);
	    driver.findElement(By.name("mobile")).sendKeys("9876543211");
	    Thread.sleep(1000);
	    driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
	    Thread.sleep(1000);
	    driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"))
	          .sendKeys("test message");
	    Thread.sleep(1000);

	    Thread.sleep(3000);
	    driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        // ✅ ONLY PASS CHECK
	        WebElement thankYouMsg = wait.until(
	            ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//div[contains(text(),'Thank you')]")
	            )
	        );

	        // ---------- PASS ----------
	        sheet.getRow(1).createCell(4).setCellValue("✅ FORM SUBMITTED SUCCSESSFULLY!");
	        System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");

	    } catch (Exception e) {

	        // ---------- FAIL ----------
	        System.out.println("❌ FORM FAIL");

	     // ---------- Get filled values ----------
	        String nameVal = driver.findElement(By.name("name")).getAttribute("value");
	        String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
	        String emailVal = driver.findElement(By.name("email")).getAttribute("value");
	        String msgVal = driver.findElement(
	                By.xpath("//textarea[@placeholder='Enter Your Message']"))
	                .getAttribute("value");

	        // ---------- Get errors ----------
	        StringBuilder errorMsg = new StringBuilder();

	        // ✅ Name error (DOM)
	        try {
	            WebElement nameErrorEle = driver.findElement(
	                By.xpath("//input[@name='name']/following-sibling::span[@class='error errmsg']"));
	            if (nameErrorEle.isDisplayed()) {
	                errorMsg.append("Name Error: ")
	                        .append(nameErrorEle.getText())
	                        .append(" | ");
	            }
	        } catch (Exception e1) {}

	        // ✅ Mobile error (DOM)
	        try {
	            WebElement mobileErrorEle = driver.findElement(
	                By.xpath("//input[@name='mobile']/following-sibling::span[@class='error errmsg']"));
	            if (mobileErrorEle.isDisplayed()) {
	                errorMsg.append("Mobile Error: ")
	                        .append(mobileErrorEle.getText())
	                        .append(" | ");
	            }
	        } catch (Exception e2) {}

	        // ✅ Email error (browser validation)
	        String emailError = driver.findElement(By.name("email"))
	                .getAttribute("validationMessage");
	        if (emailError != null && !emailError.isEmpty()) {
	            errorMsg.append("Email Error: ")
	                    .append(emailError)
	                    .append(" | ");
	        }

	        // ---------- Final Excel message ----------
	        String finalResult =
	                "   Name=" + nameVal +
	                " | Mobile=" + mobileVal +
	                " | Email=" + emailVal +
	                " | Message=" + msgVal +
	                " | Errors => " + errorMsg.toString();
	        
	        sheet.getRow(1).createCell(4).setCellValue("❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
	        sheet.getRow(1).createCell(5).setCellValue(finalResult);
	        Thread.sleep(3000);
	        System.out.println(finalResult);
	        Assert.fail("Form validation failed");
	        Thread.sleep(3000);
	        
	        
	    }
	    
	}
	
}
package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class AilmentDP extends BaseClass {

	
	@Test(priority = 1)
	public void AilmentDetailPage_RequestCallBackForm() {

	    driver.navigate().to(
	            "https://www.medanta.org/hospitals-near-me/gurugram-hospital/speciality/orthopaedics/disease/achilles-tendon-injury-symptoms-causes-treatment-recovery");

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    JavascriptExecutor js = (JavascriptExecutor) driver;

	    // ---------- Wait for form ----------
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));

	    // ---------- Locate fields (clickable) ----------
	    WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
	    WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.name("mobile")));
	    WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));
	    WebElement message = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("//textarea[@placeholder='Enter Your Message']")));

	    // ---------- Scroll to form ----------
	    js.executeScript("arguments[0].scrollIntoView({block:'center'});", name);

	    // ---------- Fill Form (SLOW TYPE) ----------
	    slowType(name, "dipesh");
	    slowType(mobile, "9876543210");
	    slowType(email, "dipesh@yopmail.com");
	    slowType(message, "test message");

	    // ---------- Safety check (NO EARLY SUBMIT) ----------
	    Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name empty");
	    Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile empty");
	    Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email empty");
	    Assert.assertFalse(message.getAttribute("value").isEmpty(), "Message empty");

	    // ---------- Submit ONLY after all fields ----------
	    WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("(//button[@type='submit'])[3]")));

	    try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // UI settle
	    submitBtn.click();

	    try {
	        // ---------- PASS condition ----------
	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//*[contains(text(),'Thank you')]")));

	        writeExcel(1, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	        System.out.println("✅ FORM SUBMITTED SUCCESSFULLY!");

	    } catch (Exception e) {

	        System.out.println("❌ FORM FAIL");

	        // ---------- Get filled values ----------
	        String nameVal = name.getAttribute("value");
	        String mobileVal = mobile.getAttribute("value");
	        String emailVal = email.getAttribute("value");
	        String msgVal = message.getAttribute("value");

	        // ---------- Get errors ----------
	        StringBuilder errorMsg = new StringBuilder();

	        try {
	            WebElement nameError = driver.findElement(
	                    By.xpath("//input[@name='name']/following-sibling::span[contains(@class,'error')]"));
	            if (nameError.isDisplayed()) {
	                errorMsg.append("Name Error: ").append(nameError.getText()).append(" | ");
	            }
	        } catch (Exception ignored) {
	        }

	        try {
	            WebElement mobileError = driver.findElement(
	                    By.xpath("//input[@name='mobile']/following-sibling::span[contains(@class,'error')]"));
	            if (mobileError.isDisplayed()) {
	                errorMsg.append("Mobile Error: ").append(mobileError.getText()).append(" | ");
	            }
	        } catch (Exception ignored) {
	        }

	        String emailError = email.getAttribute("validationMessage");
	        if (emailError != null && !emailError.isEmpty()) {
	            errorMsg.append("Email Error: ").append(emailError).append(" | ");
	        }

	        String finalResult = "Name=" + nameVal +
	                " | Mobile=" + mobileVal +
	                " | Email=" + emailVal +
	                " | Message=" + msgVal +
	                " | Errors => " + errorMsg;

	        // ---------- Write to Excel ----------
	        writeExcel(1, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	        writeExcel(1, 5, finalResult);

	        System.out.println(finalResult);
	        Assert.fail("Form validation failed");
	    }
	}
	
}
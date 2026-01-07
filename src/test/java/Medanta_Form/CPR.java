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

public class CPR extends BaseClass {

	

	@Test(priority = 1)
	public void CPRPage_EnrollnowForm() {

	    driver.navigate().to("https://www.medanta.org/cpr");

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    JavascriptExecutor js = (JavascriptExecutor) driver;

	    // -------- Locate fields safely --------
	    WebElement name = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));
	    WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]")));
	    WebElement email = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("(//input[@placeholder='Enter Your Email'])[3]")));
	    WebElement locationDD = wait.until(ExpectedConditions.visibilityOfElementLocated(
	            By.xpath("//select[@placeholder='Select Location']")));

	    // -------- Scroll first field into view --------
	    scrollToElement(name);

	    // -------- Fill form (SLOW TYPE) --------
	    slowType(name, "Test");
	    Select location = new Select(locationDD);
	    location.selectByVisibleText("Gurugram");
	    slowType(mobile, "9876543210");
	    slowType(email, "wakemedantatest@gmail.com");

	    

	    // -------- Safety validation --------
	    Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name is empty");
	    Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile is empty");
	    Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email is empty");

	    // -------- Submit --------
	    WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
	            By.xpath("(//button[@type='submit'])[3]")));
	    try { Thread.sleep(800); } catch (Exception ignored) {}
	    submitBtn.click();

	    try {
	        // -------- PASS --------
	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

	        writeExcel(8, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
	        System.out.println("✅ CPR Enroll Form – PASS");

	    } catch (Exception e) {
	        // -------- FAIL --------
	        String nameVal = name.getAttribute("value");
	        String mobileVal = mobile.getAttribute("value");
	        String emailVal = email.getAttribute("value");
	        String locationVal = location.getFirstSelectedOption().getText();

	        StringBuilder errorMsg = new StringBuilder();

	        try {
	            WebElement err = driver.findElement(By.xpath(
	                    "(//input[@placeholder='Enter Your Name'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
	            if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        try {
	            WebElement err = driver.findElement(By.xpath(
	                    "(//input[@placeholder='Enter Your Mobile Number'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
	            if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        try {
	            WebElement err = driver.findElement(By.xpath(
	                    "(//input[@placeholder='Enter Your Email'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
	            if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        try {
	            WebElement err = driver.findElement(By.xpath(
	                    "//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
	            if (err.isDisplayed()) errorMsg.append("Location Error: ").append(err.getText()).append(" | ");
	        } catch (Exception ignored) {}

	        String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal +
	                " | Location=" + locationVal + " | Errors => " + errorMsg;

	        writeExcel(8, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
	        writeExcel(8, 5, finalResult);

	        System.out.println(finalResult);
	        Assert.fail("CPR Enroll form validation failed: " + finalResult);
	    }
	}
}
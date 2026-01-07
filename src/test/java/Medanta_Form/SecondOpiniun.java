package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class SecondOpiniun extends BaseClass {

	

	@Test(priority = 1)
	public void SecondOpinionPage_RequestACallbackForm() {

		 driver.navigate().to("https://www.medanta.org/second-opinion");

		    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		    // ===== Locate fields =====
		    WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
		    WebElement mobileField = driver.findElement(By.name("mobile"));
		    WebElement emailField = driver.findElement(By.name("email"));
		    WebElement messageField = driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message']"));
		    WebElement fileInput = driver.findElement(By.xpath("(//input[@type='file'])[2]"));
		    WebElement submitBtn = driver.findElement(By.xpath("(//button[@type='submit'])[3]"));

		    // ===== Fill form using slowType =====
		    slowType(nameField, "Dipesh");
		    slowType(mobileField, "9876543210");
		    slowType(emailField, "dipesh@yopmail.com");
		    slowType(messageField, "Test");
		    
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // File upload
		    String filePath = System.getProperty("user.dir") + "/SampleDocs/upload.docx";
		    fileInput.sendKeys(filePath);
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    // Submit
		    submitBtn.click();

		    // ===== Try-catch starts from Thank You message =====
		    try {
		        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you')]")));

		        writeExcel(25, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
		        System.out.println("✅ Second Opinion – Request A Callback PASS");

		    } catch (Exception e) {
		        System.out.println("❌ Second Opinion – Request A Callback FAIL");

		        // Capture field values
		        String nameVal = nameField.getAttribute("value");
		        String mobileVal = mobileField.getAttribute("value");
		        String emailVal = emailField.getAttribute("value");
		        String messageVal = messageField.getAttribute("value");

		        // Capture errors
		        StringBuilder errorMsg = new StringBuilder();
		        try {
		            WebElement err = driver.findElement(
		                    By.xpath("//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(
		                    By.xpath("//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(
		                    By.xpath("//input[@name='email']/following-sibling::div[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        try {
		            WebElement err = driver.findElement(
		                    By.xpath("//textarea[@placeholder='Enter Your Message']/following-sibling::div[contains(@class,'errmsg')]"));
		            if (err.isDisplayed()) errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
		        } catch (Exception ignored) {}

		        String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
		                + " | Message=" + messageVal + " | Errors => " + errorMsg;

		        writeExcel(25, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
		        writeExcel(25, 5, finalResult);
		        System.out.println(finalResult);
		        Assert.fail("Second Opinion validation failed: " + finalResult);
		    }
		}
}
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

	@Test(priority = 1)
	public void ContactUsPage_EnquiryForm() {

		driver.navigate().to("https://www.medanta.org/contact-us");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ---------- Locate fields ----------
		WebElement name = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));
		WebElement email = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your E-mail'])[1]")));
		WebElement hospitalDD = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@placeholder='Select Hospital']")));
		WebElement mobile = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[1]")));
		WebElement message = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[1]")));
		// ---------- Submit ----------
				WebElement submitBtn = wait
						.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));
		// ---------- Fill Form ----------
		scrollToElement(name);
		slowType(name, "dipesh");
		slowType(email, "dipesh@yopmail.com");
		new Select(hospitalDD).selectByIndex(1);
		slowType(mobile, "9876543210");
		slowType(message, "Automation test enquiry");

		// ---------- Safety validation ----------
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email is empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(message.getAttribute("value").isEmpty(), "Message is empty");

		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		submitBtn.click();

		try {
			// ---------- PASS ----------
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));
			writeExcel(5, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ FORM SUBMITTED SUCCESSFULLY!");

		} catch (Exception e) {
			// ---------- FAIL ----------
			StringBuilder errorMsg = new StringBuilder();

			// Name validation
			String nameErr = name.getAttribute("validationMessage");
			if (nameErr != null && !nameErr.isEmpty())
				errorMsg.append("Name Error: ").append(nameErr).append(" | ");

			// Mobile validation
			try {
				WebElement mobileErr = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Phone Number']/following-sibling::div[contains(@class,'errmsg')]"));
				if (mobileErr.isDisplayed())
					errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			// Email validation
			try {
				WebElement emailErr = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your E-mail']/following-sibling::div[contains(@class,'errmsg')]"));
				if (emailErr.isDisplayed())
					errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = String.format("Name=%s | Mobile=%s | Email=%s | Errors => %s",
					name.getAttribute("value"), mobile.getAttribute("value"), email.getAttribute("value"), errorMsg);

			writeExcel(5, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(5, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Contact Us form validation failed");
		}
	}
}
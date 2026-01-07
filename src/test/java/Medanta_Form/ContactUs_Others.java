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

public class ContactUs_Others extends BaseClass {

	@Test(priority = 1)
	public void ContactUsPage_OthersForm() {

		driver.navigate().to("https://www.medanta.org/contact-us");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// -------- Wait loader --------
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
		
		
		
		// -------- Open Others Form --------
		WebElement otherFormBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
		otherFormBtn.click();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// -------- Locate fields --------
		WebElement name = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Name'])[5]")));
		WebElement email = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]")));
		
		
		
		WebElement hospitalDD = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//select[@name='hospital'])[2]")));
		WebElement mobile = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]")));
		
		
		WebElement message = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]")));

		

		// -------- Fill form (SLOW TYPE) --------
		slowType(name, "dipesh");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		slowType(email, "dipesh@yopmail.com");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Select hospital = new Select(hospitalDD);
		hospital.selectByIndex(1);
		slowType(mobile, "9876543210");
		slowType(message, "Test");

		// -------- Safety validation --------
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email is empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(message.getAttribute("value").isEmpty(), "Message is empty");

		// -------- Submit --------
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[5]")));
		try {
			Thread.sleep(800);
		} catch (Exception ignored) {
		}
		submitBtn.click();

		try {
			// -------- PASS --------
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(@class,'successmsg') and contains(text(),'Thank you')]")));

			writeExcel(7, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Others Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = name.getAttribute("value");
			String emailVal = email.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String hospitalVal = hospital.getFirstSelectedOption().getText();
			String messageVal = message.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your E-mail'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Phone Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(
						By.xpath("(//select[@name='hospital'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Hospital Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"(//textarea[@placeholder='Enter Your Message Here'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Message Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal
					+ " | Hospital=" + hospitalVal + " | Message=" + messageVal + " | Errors => " + errorMsg;

			writeExcel(7, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(7, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Others form validation failed: " + finalResult);
		}
	}
}
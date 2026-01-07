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

public class BlogDP extends BaseClass {

	@Test(priority = 1)
	public void BlogDetailPage_QueryForm() {

		driver.navigate().to(
				"https://www.medanta.org/patient-education-blog/omicron-variant-key-facts-you-need-to-know-in-2025");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// ---------- Locate fields ----------
		WebElement name = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//label[normalize-space()='Name *']/parent::div/input)[2]")));
		WebElement mobile = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]")));
		WebElement email = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//label[normalize-space()='Email *']/parent::div/input)[2]")));

		// ---------- Scroll first field into view ----------
		scrollToElement(name);

		// ---------- Fill form (SLOW TYPE) ----------
		slowType(name, "Test");
		slowType(mobile, "9876543210");
		slowType(email, "dipesh@yopmail.com");

		// ---------- Safety check ----------
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email empty");

		// ---------- Submit ----------
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[3]")));
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // UI settle
		submitBtn.click();

		try {
			// ---------- PASS ----------
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Thank you')]")));

			writeExcel(4, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ BLOG FORM SUBMITTED SUCCESSFULLY!");

		} catch (Exception e) {
			// ---------- FAIL ----------
			String nameVal = name.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String emailVal = email.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			try {
				String nameErr = name.getAttribute("validationMessage");
				if (nameErr != null && !nameErr.isEmpty())
					errorMsg.append("Name Error: ").append(nameErr).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Mobile Number']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@placeholder='Enter Your Email']/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(4, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(4, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Blog query form validation failed");
		}
	}

}
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

public class AirAmbulancePage extends BaseClass {

	@Test(priority = 1)
	public void AirAmbulance_RequestACallbackForm() {

		driver.navigate().to("https://www.medanta.org/air-ambulance");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// ---------- Wait & locate fields (clickable) ----------
		WebElement name = wait.until(
				ExpectedConditions.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")));

		WebElement mobile = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[2]")));

		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.name("email")));

		WebElement message = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//textarea[@class='inputbox']")));

		// ---------- Scroll to form ----------
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", name);

		// ---------- Fill form (SLOW TYPE) ----------
		slowType(name, "Dipesh");
		slowType(mobile, "9876543210");
		slowType(email, "dipesh@yopmail.com");
		slowType(message, "Test");

		// ---------- Safety check (NO early submit) ----------
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email empty");
		Assert.assertFalse(message.getAttribute("value").isEmpty(), "Message empty");

		// ---------- Submit ONLY after all fields ----------
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@class='theme-button'])[2]")));

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

			writeExcel(2, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Air Ambulance – PASS");

		} catch (Exception e) {

			// ---------- FAIL ----------
			String nameVal = name.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String emailVal = email.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@name='name']/ancestor::div[contains(@class,'field')]//span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//input[@name='mobile']/ancestor::div[contains(@class,'field')]//span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver
						.findElement(By.xpath("//input[@name='email']/following::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Errors => "
					+ errorMsg;

			writeExcel(2, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(2, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Air Ambulance validation failed");
		}
	}

}
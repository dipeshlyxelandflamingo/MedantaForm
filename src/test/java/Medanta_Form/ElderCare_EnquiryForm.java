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

public class ElderCare_EnquiryForm extends BaseClass {

	@Test(priority = 1)
	public void ElderCareProgramPage_EnquiryForm() {

		driver.navigate().to("https://www.medanta.org/elder-care-program");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		// -------- Optional hard scroll --------
		((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1500)");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// -------- Locate fields --------
		WebElement name = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='text'])[6]")));
		WebElement mobile = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='number'])[5]")));
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//input[@type='email'])[4]")));
		WebElement cityDD = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//select[@class='inputbox'])[2]")));
		WebElement modeDD = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//select[@class='inputbox'])[3]")));
		WebElement checkbox = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='checkbox']")));
		WebElement submitBtn = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='submit'])[5]")));

		// -------- Scroll and fill fields --------
		scrollToElement(name);
		slowType(name, "Dipesh");
		slowType(mobile, "9876543210");
		slowType(email, "dipesh@yopmail.com");
		new Select(cityDD)
		.selectByIndex(1);
		new Select(modeDD).selectByIndex(1);
		checkbox.click();

		// -------- Safety validation --------
		Assert.assertFalse(name.getAttribute("value").isEmpty(), "Name is empty");
		Assert.assertFalse(mobile.getAttribute("value").isEmpty(), "Mobile is empty");
		Assert.assertFalse(email.getAttribute("value").isEmpty(), "Email is empty");

		// -------- Wait before submit --------
		try {
			Thread.sleep(1000);
		} catch (Exception ignored) {
		}
		submitBtn.click();

		// -------- Try-catch for success/fail --------
		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(10, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ ElderCare Program Enquiry Form – PASS");

		} catch (Exception e) {
			// -------- FAIL --------
			String nameVal = name.getAttribute("value");
			String mobileVal = mobile.getAttribute("value");
			String emailVal = email.getAttribute("value");
			String cityVal = new Select(cityDD).getFirstSelectedOption().getText();
			String modeVal = new Select(modeDD).getFirstSelectedOption().getText();

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='text'])[6]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='number'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='email'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//select[@class='inputbox'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("City Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//select[@class='inputbox'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mode Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | City="
					+ cityVal + " | Mode=" + modeVal + " | Errors => " + errorMsg;

			writeExcel(10, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(10, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("ElderCare Program Enquiry form validation failed: " + finalResult);
		}
	}
}
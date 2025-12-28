package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class ContactUs_Others extends BaseClass {

	// Helper method to write to Excel
	public void writeExcel(int rowNum, int cellNum, String value) {
		try {
			if (sheet.getRow(rowNum) == null)
				sheet.createRow(rowNum);
			sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
		} catch (Exception e) {
			System.out.println("Error writing to Excel: " + e.getMessage());
		}
	}

	@Test(priority = 1)
	public void ContactUsPage_OthersForm() throws Exception {

		driver.navigate().to("https://www.medanta.org/contact-us");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));

		WebElement otherFormBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
		otherFormBtn.click();
		
		 Thread.sleep(3000);
		// Fill form (intentionally invalid mobile)
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[5]")).sendKeys("dipesh");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]")).sendKeys("dipesh@yopmail.com");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]")).sendKeys("9876543210");
		Thread.sleep(1000);

		WebElement DD1 = driver.findElement(By.xpath("(//select[@name='hospital'])[2]"));
		Select hospital1 = new Select(DD1);
		hospital1.selectByIndex(1);
		Thread.sleep(2000);

		driver.findElement(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]")).sendKeys("Test");
		 Thread.sleep(3000);
		driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();
		

		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			WebElement successMsg = wait2.until(
				    ExpectedConditions.visibilityOfElementLocated(
				        By.xpath("(//div[contains(@class,'successmsg') and contains(normalize-space(),'Thank you for filling the form')])[1]")
				    )
				);

			writeExcel(7, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
			System.out.println("✅ FORM SUBMITTED SUCCSESSFULLY!");
		} catch (Exception e) {
			System.out.println("❌ Others Form FAIL");

			// Capture values
			String nameVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[5]"))
					.getAttribute("value");
			String emailVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]"))
					.getAttribute("value");
			String mobileVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]"))
					.getAttribute("value");
			String messageVal = driver.findElement(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]"))
					.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			try {
				WebElement nameErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Name'])[5]/following-sibling::div[contains(@class,'errmsg')]"));
				if (nameErr.isDisplayed())
					errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement emailErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your E-mail'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (emailErr.isDisplayed())
					errorMsg.append("Email Error: ").append(emailErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement mobileErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Phone Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (mobileErr.isDisplayed())
					errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement hospitalErr = driver.findElement(
						By.xpath("(//select[@name='hospital'])[2]/following-sibling::div[contains(@class,'errmsg')]"));
				if (hospitalErr.isDisplayed())
					errorMsg.append("Hospital Error: ").append(hospitalErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement MsgErr = driver.findElement(By.xpath(
						"(//input[@placeholder='Enter Your Phone Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
				if (MsgErr.isDisplayed())
					errorMsg.append("Msg Error: ").append(MsgErr.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			Thread.sleep(3000);

			String finalResult = "Name=" + nameVal + " | Mobile=" + mobileVal + " | Email=" + emailVal + " | Message="
					+ messageVal + " | Errors => " + errorMsg;
			writeExcel(7, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
			writeExcel(7, 5, finalResult);
			  Thread.sleep(3000);
			Assert.fail("Others form validation failed: " + finalResult);
			  Thread.sleep(3000);
		}
	}

}

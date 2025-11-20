package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import Base.BaseClass;

public class ContactUs extends BaseClass {

	@Test(priority = 1)
	public void ContactUsPage_EnquiryForm() throws Exception {

		driver.navigate().to("https://www.medanta.org/contact-us");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("name")).sendKeys("dipesh");
		Thread.sleep(1000);
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		Thread.sleep(1000);
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		Thread.sleep(1000);

		WebElement DD = driver.findElement(By.name("hospital"));
		Select Hospital = new Select(DD);
		Hospital.selectByIndex(1);

		driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message Here']")).sendKeys("Test");

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,200)", "");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		Thread.sleep(2000);

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(5).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(5).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test(priority = 2)
	public void ContactUsPage_FeedbackForm() throws Exception {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
		WebElement emt = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
		emt.click();

		driver.findElement(By.name("feedback_message")).sendKeys("Test");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).sendKeys("Dipesh");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]")).sendKeys("9876543210");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]")).sendKeys("dipesh@yopmail.com");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();
		Thread.sleep(3000);

		// Explicit wait for the success message
		WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt1 = wait1.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg1 = emt1.getText();
			if (msg1.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(6).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(6).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test(priority = 3)
	public void ContactUsPage_OthersForm() throws Exception {
		// driver.findElement(By.id("other-form")).click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));

		WebElement emt1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
		Thread.sleep(1000);
		emt1.click();

		// driver.findElement(By.id("other-form")).click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[5]")).sendKeys("dipesh");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]")).sendKeys("dipesh@yopmail.com");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]")).sendKeys("9876543210");
		Thread.sleep(1000);
		WebElement DD1 = driver.findElement(By.xpath("(//select[@name='hospital'])[2]"));
		Select hospital1 = new Select(DD1);
		hospital1.selectByIndex(1);
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]")).sendKeys("Test");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();
		Thread.sleep(1000);

		// Explicit wait for the success message
		WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt2 = wait2.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg2 = emt2.getText();
			if (msg2.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(7).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(7).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

}

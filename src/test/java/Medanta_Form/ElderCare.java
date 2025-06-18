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

public class ElderCare extends BaseClass {

	@Test
	public void TC_01() {
		driver.navigate().to("https://www.medanta.org/elder-care-program");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("name")).sendKeys("Dipesh");
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//textarea [@class='inputbox']")).sendKeys("Test");
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// Explicit wait for the success message
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(19).createCell(5).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(19).createCell(5).setCellValue("PASS!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

	@Test
	public void TC_02() throws Throwable {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1550)", "");

		driver.findElement(By.linkText("Enroll Now")).click();
		driver.findElement(By.xpath("(//input[@type='text'])[5]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='number'])[4]")).sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@type='email'])[3]")).sendKeys("dipesh@yopmail.com");
		WebElement DD = driver.findElement(By.xpath("//select[@class='inputbox']"));
		Select s = new Select(DD);
		s.selectByIndex(1);

		driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//a[@id='closeModal'])[2]")).click();
		Thread.sleep(15000);

	}

	@Test
	public void TC_03() throws Exception {
		Thread.sleep(10000);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,450)", "");

		driver.findElement(By.xpath("(//input[@type='text'])[6]")).sendKeys("Dipesh");
		driver.findElement(By.xpath("(//input[@type='number'])[5]")).sendKeys("9876543210");
		driver.findElement(By.xpath("(//input[@type='email'])[4]")).sendKeys("dipesh@yopmail.com");
		WebElement city = driver.findElement(By.xpath("(//select[@class='inputbox'])[2]"));
		Select selectcity = new Select(city);
		selectcity.selectByIndex(1);

		WebElement mode = driver.findElement(By.xpath("(//select[@class='inputbox'])[2]"));
		Select PreferedMode = new Select(mode);
		PreferedMode.selectByIndex(1);

		driver.findElement(By.xpath("//input[@type='checkbox']")).click();

		driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();

		// Explicit wait for the success message
		WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt1 = wait1.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));

			// Validate the success message
			String msg1 = emt1.getText();
			if (msg1.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(21).createCell(5).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(21).createCell(5).setCellValue("PASS!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}

	}

}

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

public class Jobs extends BaseClass {

	@Test
	public void TC_01() throws Exception {
		driver.navigate().to("https://www.medanta.org/careers");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,3200)", "");
		
		driver.findElement(By.xpath("(//a[@class='theme-button'])[2]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='full_name']")).sendKeys("dipesh");
		driver.findElement(By.xpath("//input[@name='mobile_no']")).sendKeys("9876543210");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("dipesh@yopmail.com");
		
		WebElement LocationDD = driver.findElement(By.xpath("//select[@name='hospital']"));
		Select location= new Select(LocationDD);
		location.selectByIndex(1);
		
		driver.findElement(By.xpath("//input[@id='filename']")).sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//div[contains(text(), 'Thank you for filling the form.')]")));

			String msg = emt.getText();
			System.out.println("Extracted message: " + msg);

			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(26).createCell(5).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(26).createCell(5).setCellValue("FAIL!");
			}
		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}

	}
}
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

public class CareerJob extends BaseClass {
	

	@Test
	public void TC_01() throws Exception {

		driver.navigate().to("https://www.medanta.org/jobs/gurgaon-pharmacist-haryana-registration/form");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.name("first_name")).sendKeys("Dipesh");
		driver.findElement(By.name("last_name")).sendKeys("Sing");
		driver.findElement(By.xpath("(//input[@type='email'])[2]")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.name("mobile")).sendKeys("9876543210");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@placeholder='Enter Your City']")).sendKeys("noida");

		WebElement Statedropdown = driver.findElement(By.name("state"));
		Select state = new Select(Statedropdown);
		state.selectByValue("Uttar Pradesh");

		driver.findElement(By.id("dob")).sendKeys("2000-06-12");

		WebElement GenderDD = driver.findElement(By.name("gender"));
		Select Gender = new Select(GenderDD);
		Gender.selectByIndex(1);

		driver.findElement(By.xpath("(//input[@name='selectedFreshOrExp'])[2]")).click();
		driver.findElement(By.xpath("(//button[@type='button'])[8]")).click();
		Thread.sleep(3000);

	}

	@Test
	public void TC_02() throws Exception {

		driver.findElement(By.xpath("(//input[@class='inputbox'])[5]")).sendKeys("Abcd");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[6]")).sendKeys("10000");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[7]")).sendKeys("20000");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[8]")).sendKeys("30");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[9]")).sendKeys("Associate");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[10]")).sendKeys("Abcd");
		driver.findElement(By.xpath("(//input[@class='inputbox'])[11]")).sendKeys("3");
		Thread.sleep(2000);
		driver.findElement(By.xpath("(//button[@type='button'])[9]")).click();
		Thread.sleep(3000);

	}

	@Test
	public void TC_03() throws Exception {

		driver.findElement(By.xpath("//input[@placeholder='Enter Institution']")).sendKeys("Abcd");
		driver.findElement(By.xpath("//input[@placeholder='Enter Degree']")).sendKeys("Abcd");
		driver.findElement(By.xpath("//input[@placeholder='Enter Year of Completion']")).sendKeys("2010");
		driver.findElement(By.xpath("//input[@placeholder='Enter Percentage']")).sendKeys("70");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@id='resume'])[2]")).sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");
		Thread.sleep(1000);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,200)", "");

		// driver.findElement(By.xpath("//span[@id='recaptcha-anchor']")).click();

		Thread.sleep(10000);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();
		
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			WebElement emt = wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(), 'You have already applied')]")));

			// Validate the success message
			String msg = emt.getText();
			if (msg.contains("Thank you")) {
				System.out.println("PASS");
				sheet.getRow(25).createCell(5).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(25).createCell(5).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}
	
		

}



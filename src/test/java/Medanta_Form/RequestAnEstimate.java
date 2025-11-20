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

public class RequestAnEstimate extends BaseClass {

	@Test(priority=1)
	public void RequestAnEstimatePage_PriceEstimateForm() throws Exception {

		driver.navigate().to("https://www.medanta.org/international-patient/services/request-an-estimate");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,210)", "");

		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("dipesh");
		driver.findElement(By.xpath("//input[@placeholder='Enter Your Last Name']")).sendKeys("Sing");
		driver.findElement(By.xpath("//input[@placeholder='Enter Your E-mail']")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile No.']")).sendKeys("9876543210");

		WebElement countrydd = driver.findElement(By.xpath("//select[@class='inputbox']"));
		Select country = new Select(countrydd);
		country.selectByIndex(8);

		WebElement departmentdd = driver.findElement(By.xpath("(//select[@class='inputbox'])[2]"));
		Select department = new Select(departmentdd);
		department.selectByValue("Obstetrics");

		driver.findElement(By.xpath("(//input[@type='file'])[2]")).sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");
		Thread.sleep(3000);

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
				sheet.getRow(27).createCell(4).setCellValue("PASS!");
			} else {
				System.out.println("FAIL");
				sheet.getRow(27).createCell(4).setCellValue("FAIL!");
			}

		} catch (Exception e) {
			System.out.println("Element not found or timeout occurred: " + e.getMessage());
		}
	}

}

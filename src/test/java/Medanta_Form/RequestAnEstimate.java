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

public class RequestAnEstimate extends BaseClass {

	// ================= Excel Helper =================
	public void writeExcel(int row, int col, String value) {
		try {
			if (sheet.getRow(row) == null)
				sheet.createRow(row);
			sheet.getRow(row).createCell(col).setCellValue(value);
		} catch (Exception e) {
			System.out.println("Excel write error: " + e.getMessage());
		}
	}

	@Test(priority = 1)
	public void RequestAnEstimatePage_PriceEstimateForm() throws InterruptedException {

		driver.navigate().to("https://www.medanta.org/international-patient/services/request-an-estimate");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,210)", "");

		 Thread.sleep(3000);
		driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]")).sendKeys("Dipesh");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@placeholder='Enter Your Last Name']")).sendKeys("Singh");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@placeholder='Enter Your E-mail']")).sendKeys("dipesh@yopmail.com");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile No.']")).sendKeys("9876543210"); 
		Thread.sleep(1000);

		Select country = new Select(driver.findElement(By.xpath("//select[@class='inputbox']")));
		country.selectByIndex(8);
		Thread.sleep(1000);
		Select department = new Select(driver.findElement(By.xpath("(//select[@class='inputbox'])[2]")));
		department.selectByValue("Obstetrics");
		Thread.sleep(1000);
		driver.findElement(By.xpath("(//input[@type='file'])[2]")).sendKeys("C:\\Users\\LYXELANDFLAMINGO\\upload.docx");
		 Thread.sleep(3000);
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

		// ================= Validation =================
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you')]")));

			System.out.println("✅ Request An Estimate PASS");
			writeExcel(24, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

		} catch (Exception e) {

			// ===== Capture entered values =====
			String fnameVal = driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[3]"))
					.getAttribute("value");
			String lnameVal = driver.findElement(By.xpath("//input[@placeholder='Enter Your Last Name']"))
					.getAttribute("value");
			String mobileVal = driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile No.']"))
					.getAttribute("value");
			String emailVal = driver.findElement(By.xpath("//input[@placeholder='Enter Your E-mail']"))
					.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();

			// ===== Field wise errors =====
			try {
				WebElement err = driver.findElement(By.xpath(
						"//label[contains(text(),'First Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("First Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			try {
				WebElement err = driver.findElement(By.xpath(
						"//label[contains(text(),'Last Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Last Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			
			try {
				WebElement err = driver.findElement(By.xpath("//label[contains(text(),'E-mail')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			
			
			
			
			try {
				WebElement err = driver.findElement(By.xpath(
						"//label[contains(text(),'Mobile')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			

			// ===== Final result =====
			String finalResult = "FirstName=" + fnameVal + " | LastName=" + lnameVal + " | Mobile=" + mobileVal
					+ " | Email=" + emailVal + " | Errors => " + errorMsg;

			System.out.println("❌ Request An Estimate FAIL");

			writeExcel(24, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
			writeExcel(24, 5, finalResult);
			  Thread.sleep(3000);
			Assert.fail("Request An Estimate validation failed: " + finalResult);
			  Thread.sleep(3000);
		}
	}
}

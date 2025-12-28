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

public class HomeCare_EnquiryForm extends BaseClass{
	
	// Excel helper
		public void writeExcel(int rowNum, int cellNum, String value) {
			try {
				if (sheet.getRow(rowNum) == null)
					sheet.createRow(rowNum);
				sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
			} catch (Exception e) {
				System.out.println("Excel write error: " + e.getMessage());
			}
		}
		@Test(priority = 1)
		public void HomeCarePage_EnquiryForm() throws Throwable {
			
			driver.navigate().to("https://www.medanta.org/home-care");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.scrollBy(0,2000)", "");
			 Thread.sleep(3000);
			driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).sendKeys("Dipesh");
			 Thread.sleep(1000);
			driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]")).sendKeys("9876543210"); 
			 Thread.sleep(1000);
			driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[4]")).sendKeys("dipesh@yopmail.com");
			 Thread.sleep(1000);
			Select location = new Select(driver.findElement(By.xpath("//select[@placeholder='Select Location']")));
			location.selectByIndex(0); // ❌ default
			 Thread.sleep(3000);
			driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			try {
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

				writeExcel(15, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

			} catch (Exception e) {

				StringBuilder errorMsg = new StringBuilder();

				try {
					WebElement err = driver.findElement(By.xpath(
							"(//input[@placeholder='Enter Your Name'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
					if (err.isDisplayed())
						errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
				} catch (Exception ignored) {
				}

				try {
					WebElement err = driver.findElement(By.xpath(
							"(//input[@placeholder='Enter Your Mobile Number'])[3]/following-sibling::div[contains(@class,'errmsg')]"));
					if (err.isDisplayed())
						errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
				} catch (Exception ignored) {
				}

				try {
					WebElement err = driver.findElement(By.xpath(
							"(//input[@placeholder='Enter Your Email'])[4]/following-sibling::div[contains(@class,'errmsg')]"));
					if (err.isDisplayed())
						errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
				} catch (Exception ignored) {
				}

				try {
					WebElement err = driver.findElement(By.xpath(
							"//select[@placeholder='Select Location']/following-sibling::div[contains(@class,'errmsg')]"));
					if (err.isDisplayed())
						errorMsg.append("Location Error: ").append(err.getText()).append(" | ");
				} catch (Exception ignored) {
				}

				String finalResult = "Name="
						+ driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).getAttribute("value")
						+ " | Mobile="
						+ driver.findElement(By.xpath("(//input[@placeholder='Enter Your Mobile Number'])[3]"))
								.getAttribute("value")
						+ " | Email="
						+ driver.findElement(By.xpath("(//input[@placeholder='Enter Your Email'])[4]"))
								.getAttribute("value")
						+ " | Location=" + location.getFirstSelectedOption().getText() + " | Errors => " + errorMsg;

				writeExcel(15, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
				writeExcel(15, 5, finalResult);
				  Thread.sleep(3000);
				Assert.fail(finalResult);
				  Thread.sleep(3000);
			} 
		}


}

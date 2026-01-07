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

public class MedantaPharmacy_UploadForm extends BaseClass {

	

	@Test(priority = 1)
	public void MedantaPharmacyPage_UploadForm() {

		driver.navigate().to("https://www.medanta.org/medanta-pharmacy");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Scroll down to form
		js.executeScript("window.scrollBy(0,800)");
		try {
			Thread.sleep(2000);
		} catch (Exception ignored) {
		}

		WebElement nameInput = driver.findElement(By.xpath("(//input[@type='text'])[5]"));
		WebElement emailInput = driver.findElement(By.xpath("(//input[@type='email'])[3]"));
		WebElement fileInput = driver.findElement(By.xpath("(//input[@type='file'])[2]"));
		WebElement submitBtn = driver.findElement(By.xpath("(//button[@type='submit'])[4]"));

		// -------- Fill form with slow typing --------
		slowType(nameInput, "Dipesh");
		slowType(emailInput, "dipesh@yopmail.com");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filePath = System.getProperty("user.dir") + "\\SampleDocs\\upload.docx";
		fileInput.sendKeys(filePath);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Submit form
		submitBtn.click();

		// ===== Try-catch for success/fail (EDP style) =====
		try {
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

			writeExcel(22, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
			System.out.println("✅ Medanta Pharmacy – Upload Form PASS");

		} catch (Exception e) {

			String nameVal = nameInput.getAttribute("value");
			String emailVal = emailInput.getAttribute("value");

			StringBuilder errorMsg = new StringBuilder();
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='text'])[5]/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='email'])[3]/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}
			try {
				WebElement err = driver.findElement(
						By.xpath("(//input[@type='file'])[2]/following-sibling::span[contains(@class,'errmsg')]"));
				if (err.isDisplayed())
					errorMsg.append("File Error: ").append(err.getText()).append(" | ");
			} catch (Exception ignored) {
			}

			String finalResult = "Name=" + nameVal + " | Email=" + emailVal + " | File=NOT_UPLOADED | Errors => "
					+ errorMsg;

			writeExcel(22, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
			writeExcel(22, 5, finalResult);

			System.out.println(finalResult);
			Assert.fail("Upload Form validation failed: " + finalResult);
		}
	}
}
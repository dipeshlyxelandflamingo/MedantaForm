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

public class MedantaPharmacy_UploadForm extends BaseClass{
	
	

	// Common Excel writer
    public void writeExcel(int row, int col, String value) {
        try {
            if (sheet.getRow(row) == null)
                sheet.createRow(row);
            sheet.getRow(row).createCell(col).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Excel write error: " + e.getMessage());
        }
    }

    // ================== TC_02 : Upload Form ==================
    @Test(priority = 1)
    public void MedantaPharmacyPage_UploadForm() throws Exception {

    	driver.navigate().to("https://www.medanta.org/medanta-pharmacy");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    	
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,800)", "");

        // Fill form (INTENTIONALLY SKIP FILE / INVALID)
        driver.findElement(By.xpath("(//input[@type='text'])[5]")).sendKeys("Dipesh");
        driver.findElement(By.xpath("(//input[@type='email'])[3]")).sendKeys("dipesh@yopmail.com");
     // 1️⃣ Locate the file input
        WebElement fileInput = driver.findElement(By.xpath("(//input[@type='file'])[2]"));

        // 2️⃣ File path (relative to project)
        String filePath = System.getProperty("user.dir") + "\\SampleDocs\\upload.docx";

        // 3️⃣ Upload file
        fileInput.sendKeys(filePath);


        Thread.sleep(2000);
        driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            System.out.println("✅ Medanta Pharmacy – Upload Form PASS");
            writeExcel(22, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ Medanta Pharmacy – Upload Form FAIL");

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(
                        By.xpath("//label[text()='Name *']/following-sibling::input/../span[contains(@class,'errmsg')])[2]"));
                if (err.isDisplayed())
                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("//label[text()='Email *']/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("//label[contains(text(),'Upload Prescription')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("File Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult =
                    "Name=" + driver.findElement(By.xpath("(//input[@type='text'])[5]")).getAttribute("value") +
                    " | Email=" + driver.findElement(By.xpath("(//input[@type='email'])[3]")).getAttribute("value") +
                    " | File=NOT_UPLOADED" +
                    " | Errors => " + errorMsg;

            writeExcel(22, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            writeExcel(22, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("Upload Form validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }
}


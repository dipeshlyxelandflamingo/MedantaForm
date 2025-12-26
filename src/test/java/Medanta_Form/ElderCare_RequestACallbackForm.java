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

public class ElderCare_RequestACallbackForm extends BaseClass {

	
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

    // ===================== Request a Callback =====================
    @Test(priority = 1)
    public void ElderCareProgramPage_RequestACallbackForm() throws Exception {

        driver.navigate().to("https://www.medanta.org/elder-care-program");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.findElement(By.name("name")).sendKeys("Dipesh");
        driver.findElement(By.name("mobile")).sendKeys("9876543210"); 
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        driver.findElement(By.xpath("//textarea[@class='inputbox']")).sendKeys("Test");

        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement successMsg = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(text(),'Thank you for filling the form')]")));

            writeExcel(11, 4, "✅ FORM SUBMITTED SUCCSESSFULLY!");
            System.out.println("✅ ElderCare_RequestACallbackForm Request Callback PASS");

        } catch (Exception e) {

            System.out.println("❌ ElderCare_RequestACallbackForm Request Callback FAIL");

            String nameVal = driver.findElement(By.name("name")).getAttribute("value");
            String mobileVal = driver.findElement(By.name("mobile")).getAttribute("value");
            String emailVal = driver.findElement(By.name("email")).getAttribute("value");
            String msgVal = driver.findElement(By.xpath("//textarea[@class='inputbox']")).getAttribute("value");

            StringBuilder errorMsg = new StringBuilder();
            
            
            try {
                WebElement nameErr = driver.findElement(
                        By.xpath("//input[@name='name']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (nameErr.isDisplayed())
                    errorMsg.append("Name Error: ").append(nameErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement mobileErr = driver.findElement(
                        By.xpath("//input[@name='mobile']/ancestor::div[contains(@class,'field')]/span[contains(@class,'errmsg')]"));
                if (mobileErr.isDisplayed())
                    errorMsg.append("Mobile Error: ").append(mobileErr.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult =
                    "Name=" + nameVal +
                    " | Mobile=" + mobileVal +
                    " | Email=" + emailVal +
                    " | Message=" + msgVal +
                    " | Errors => " + errorMsg;

            writeExcel(11, 4, "❌ FORM NOT SUBMITTED SUCCSESSFULLY! FAIL");
            Thread.sleep(3000);
            writeExcel(11, 5, finalResult);
            Thread.sleep(3000);
            Assert.fail("ElderCare_RequestACallbackForm Request Callback validation failed: " + finalResult);
            Thread.sleep(3000);
        }
    }

   
  
}
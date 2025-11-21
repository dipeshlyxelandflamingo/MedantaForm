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

public class ElderCare extends BaseClass {

	 // Helper method to safely write to Excel
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if(sheet.getRow(rowNum) == null) sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Error writing to Excel: " + e.getMessage());
        }
    }

    @Test(priority=1)
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
            boolean isMsgVisible = wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//div[contains(@class,'successmsg')]"), "Thank you"
            ));

            Assert.assertTrue(isMsgVisible, "Request A Callback form submission failed");
            writeExcel(9, 4, "PASS!");
            System.out.println("Test 1 PASS");

        } catch (Exception e) {
            writeExcel(9, 4, "FAIL!");
            throw e;
        }
    }

    @Test(priority=2)
    public void ElderCareProgramPage_EnrollNowForm() throws Throwable {
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

        // Success indicator: Modal disappears
       // WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
       
        
    }

    @Test(priority=3)
    public void ElderCareProgramPage_EnquiryForm() throws Exception {
        Thread.sleep(1000);
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

        //WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        
    }
}
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

public class ContactUs extends BaseClass {

	 // Helper method to safely write to Excel
    public void writeExcel(int rowNum, int cellNum, String value) {
        try {
            if(sheet.getRow(rowNum) == null) sheet.createRow(rowNum);
            sheet.getRow(rowNum).createCell(cellNum).setCellValue(value);
        } catch (Exception e) {
            System.out.println("Error writing to Excel: " + e.getMessage());
        }
    }

    @Test(priority = 1)
    public void ContactUsPage_EnquiryForm() throws Exception {
        driver.navigate().to("https://www.medanta.org/contact-us");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.name("name")).sendKeys("dipesh");
        Thread.sleep(1000);
        driver.findElement(By.name("email")).sendKeys("dipesh@yopmail.com");
        Thread.sleep(1000);
        driver.findElement(By.name("mobile")).sendKeys("9876543210");
        Thread.sleep(1000);

        WebElement DD = driver.findElement(By.name("hospital"));
        Select Hospital = new Select(DD);
        Hospital.selectByIndex(1);

        driver.findElement(By.xpath("//textarea[@placeholder='Enter Your Message Here']")).sendKeys("Test");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,200)", "");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement emt = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("//div[contains(text(), 'Thank you for filling the form')]")));
            Thread.sleep(1000);

            String msg = emt.getText().trim();
            try {
                Assert.assertTrue(msg.toLowerCase().contains("thank you"), "Form submission failed");
                writeExcel(4, 4, "PASS!");
                System.out.println("Test 1 PASS");
            } catch (AssertionError e) {
                writeExcel(4, 4, "FAIL!");
                throw e; // re-throw to mark TestNG test as fail
            }

        } catch (Exception e) {
            writeExcel(4, 4, "FAIL!");
            throw e; // re-throw to fail test
        }
    }

    @Test(priority = 2)
    public void ContactUsPage_FeedbackForm() throws Exception {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));
        WebElement emt = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedback-form")));
        emt.click();

        driver.findElement(By.name("feedback_message")).sendKeys("Test");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[4]")).sendKeys("Dipesh");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[2]")).sendKeys("9876543210");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[2]")).sendKeys("dipesh@yopmail.com");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//button[@type='submit'])[4]")).click();
        Thread.sleep(3000);

        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement emt1 = wait1.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("(//div[contains(@class,'successmsg') and contains(text(),'Thank you')])[2]")));
            Thread.sleep(1000);

            String msg1 = emt1.getText().trim();
            try {
                Assert.assertTrue(msg1.toLowerCase().contains("thank you"), "Form submission failed");
                writeExcel(5, 4, "PASS!");
                System.out.println("Test 2 PASS");
            } catch (AssertionError e) {
                writeExcel(5, 4, "FAIL!");
                throw e;
            }

        } catch (Exception e) {
            writeExcel(5, 4, "FAIL!");
            throw e;
        }
    }

    @Test(priority = 3)
    public void ContactUsPage_OthersForm() throws Exception {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".form-loader.loading")));

        WebElement emt1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("other-form")));
        Thread.sleep(1000);
        emt1.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Name'])[5]")).sendKeys("dipesh");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your E-mail'])[3]")).sendKeys("dipesh@yopmail.com");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//input[@placeholder='Enter Your Phone Number'])[3]")).sendKeys("9876543210");
        Thread.sleep(1000);
        WebElement DD1 = driver.findElement(By.xpath("(//select[@name='hospital'])[2]"));
        Select hospital1 = new Select(DD1);
        hospital1.selectByIndex(1);
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//textarea[@placeholder='Enter Your Message Here'])[2]")).sendKeys("Test");
        Thread.sleep(1000);
        driver.findElement(By.xpath("(//button[@type='submit'])[5]")).click();
        Thread.sleep(1000);

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement emt2 = wait2.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("(//div[contains(@class,'successmsg') and contains(text(),'Thank you')])[3]")));
            Thread.sleep(1000);

            String msg2 = emt2.getText().trim();
            try {
                Assert.assertTrue(msg2.toLowerCase().contains("thank you"), "Form submission failed");
                writeExcel(6, 4, "PASS!");
                System.out.println("Test 3 PASS");
            } catch (AssertionError e) {
                writeExcel(6, 4, "FAIL!");
                throw e;
            }

        } catch (Exception e) {
            writeExcel(6, 4, "FAIL!");
            throw e;
        }
    }

}
package Medanta.Medanta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import common.base;

import java.time.Duration;

public class Headerform extends base {

    @Test
    public void TC_01() {
        driver.navigate().to("https://www.medanta.org");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.findElement(By.xpath("//span[normalize-space()='Request Call Back']")).click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.findElement(By.xpath("//input[@id='fname']")).sendKeys("Saurabh Test");
        driver.findElement(By.xpath("//input[@placeholder='Enter Your Mobile Number']")).sendKeys("9876543210");
        driver.findElement(By.xpath("//input[@id='lname' and @placeholder='Enter Your Email']")).sendKeys("wakemedantatest@gmail.com");
        driver.findElement(By.xpath("//button[@class='submitpopupbt']")).click();

        validateSuccessMessage();
    }

    public void validateSuccessMessage() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    try {
	        WebElement emt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
	                "//div[contains(text(), 'Thank you for filling the form.')]"))); 

	        String msg = emt.getText();
	        System.out.println("Extracted message: " + msg); 

	        if (msg.contains("Thank you")) {
	            System.out.println("PASS");
	        } else {
	            System.out.println("FAIL");
	        }
	    } catch (Exception e) {
	        System.out.println("Element not found or timeout occurred: " + e.getMessage());
	    }

}
}
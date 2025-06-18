package Medanta.Medanta;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import common.base;

import java.time.Duration;

public class Jobs extends base {

	@Test
	public void TC_01() {
		driver.navigate().to("https://www.medanta.org/careers");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.xpath("//a[normalize-space()='Submit Your Resume']")).click();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		driver.findElement(By.xpath("//input[@placeholder='Full Name']")).sendKeys("Saurabh Test");
		driver.findElement(By.xpath("//input[@placeholder='Phone Number']")).sendKeys("9876543210");
		driver.findElement(By.xpath("//input[@placeholder='Email']"))
				.sendKeys("wakemedantatest@gmail.com");
		Select topicDropdown = new Select(driver.findElement(By.xpath("//select[@placeholder='Select Your Location']")));
        topicDropdown.selectByVisibleText("Medanta Gurugram");	
        // File upload process
        WebElement uploadElement = driver.findElement(By.xpath("//input[@id='filename']"));
        uploadElement.sendKeys("C:\\Users\\LyxelFlamingo\\Downloads\\dummy.pdf"); // Set the correct path for the PDF file

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.findElement(By.xpath("(//button[@type='submit'])[3]")).click();

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
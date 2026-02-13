package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import Base.BaseClass;

public class RequestAnEstimate extends BaseClass {

	
	@Test(priority = 1)
    public void RequestAnEstimatePage_PriceEstimateForm() {

        driver.navigate().to("https://www.medanta.org/international-patient/services/request-an-estimate");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll slightly (same as your logic)
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,210)", "");

        // ===== By locators (same as your code) =====
        By fnameBy = By.xpath("(//input[@placeholder='Enter Your Name'])[3]");
        By lnameBy = By.xpath("//input[@placeholder='Enter Your Last Name']");
        By emailBy = By.xpath("//input[@placeholder='Enter Your E-mail']");
        By mobileBy = By.xpath("//input[@placeholder='Enter Your Mobile No.']");
        By countryBy = By.xpath("//select[@class='inputbox']");
        By departmentBy = By.xpath("(//select[@class='inputbox'])[2]");
        By fileBy = By.xpath("(//input[@type='file'])[2]");
        By submitBy = By.xpath("(//button[@type='submit'])[3]");
        By successBy = By.xpath("//div[contains(text(),'Thank you')]");

        // ===== Fill form (SAFE TYPE) =====
        typeAndEnsureValue(wait, js, fnameBy, "Dipesh");
        typeAndEnsureValue(wait, js, lnameBy, "Singh");
        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");
        typeAndEnsureValue(wait, js, mobileBy, "9876543210");

        // ===== Dropdowns (ensure selection) =====
        selectByIndexAndEnsure(wait, countryBy, 8);
        selectByValueAndEnsure(wait, departmentBy, "Obstetrics");

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // ===== File upload =====
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));
        String filePath = System.getProperty("user.dir") + "\\SampleDocs\\upload.docx";
        fileInput.sendKeys(filePath);

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // ===== Submit =====
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
        try {
            submitBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", submitBtn);
        }

        // ====== TRY-CATCH starts from thank you message =====
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

            writeExcel(24, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Request An Estimate PASS");

        } catch (Exception e) {

            System.out.println("❌ Request An Estimate FAIL");

            String fnameVal = safeGetValue(fnameBy);
            String lnameVal = safeGetValue(lnameBy);
            String emailVal = safeGetValue(emailBy);
            String mobileVal = safeGetValue(mobileBy);

            StringBuilder errorMsg = new StringBuilder();

            try {
                WebElement err = driver.findElement(By.xpath(
                        "//label[contains(text(),'First Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("First Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "//label[contains(text(),'Last Name')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Last Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "//label[contains(text(),'E-mail')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(By.xpath(
                        "//label[contains(text(),'Mobile')]/following-sibling::input/../span[contains(@class,'errmsg')]"));
                if (err.isDisplayed()) errorMsg.append("Mobile Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "FirstName=" + fnameVal + " | LastName=" + lnameVal + " | Mobile=" + mobileVal
                    + " | Email=" + emailVal + " | Errors => " + errorMsg;

            writeExcel(24, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(24, 5, finalResult);
            System.out.println(finalResult);
            Assert.fail("Request An Estimate validation failed: " + finalResult);
        }
    }

    // ✅ text helper
    private void typeAndEnsureValue(WebDriverWait wait, JavascriptExecutor js, By locator, String value) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);

                try { el.click(); } catch (Exception ignored) {}
                try { el.clear(); } catch (Exception ignored) {}

                slowType(el, value);

                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                String actual = el.getAttribute("value");
                if (actual != null && actual.trim().equals(value)) return;

            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        Assert.fail("Value did not persist for locator: " + locator + " expected='" + value + "'");
    }

    // ✅ dropdown helpers
    private void selectByIndexAndEnsure(WebDriverWait wait, By selectLocator, int index) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
                Select s = new Select(dd);
                s.selectByIndex(index);
                String selected = s.getFirstSelectedOption().getText().trim();
                if (selected != null && !selected.isEmpty()) return;
            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " index=" + index);
    }

    private void selectByValueAndEnsure(WebDriverWait wait, By selectLocator, String value) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement dd = wait.until(ExpectedConditions.visibilityOfElementLocated(selectLocator));
                Select s = new Select(dd);
                s.selectByValue(value);
                String selectedVal = s.getFirstSelectedOption().getAttribute("value");
                if (selectedVal != null && selectedVal.equals(value)) return;
            } catch (StaleElementReferenceException ignored) {
            } catch (Exception ignored) {
            }
        }
        Assert.fail("Dropdown selection did not persist for locator: " + selectLocator + " value=" + value);
    }

    private String safeGetValue(By locator) {
        try {
            WebElement el = driver.findElement(locator);
            String v = el.getAttribute("value");
            return v == null ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }
}
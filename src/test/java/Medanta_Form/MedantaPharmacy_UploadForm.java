package Medanta_Form;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
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

        // Scroll down to form (same as your logic)
        js.executeScript("window.scrollBy(0,800)");
        try { Thread.sleep(2000); } catch (Exception ignored) {}

        // -------- By locators (same as your code) --------
        By nameBy = By.xpath("(//input[@type='text'])[5]");
        By emailBy = By.xpath("(//input[@type='email'])[3]");
        By fileBy = By.xpath("(//input[@type='file'])[2]");
        By submitBy = By.xpath("(//button[@type='submit'])[4]");
        By successBy = By.xpath("//div[contains(text(),'Thank you for filling the form')]");

        // -------- Fill form (SAFE TYPE) --------
        typeAndEnsureValue(wait, js, nameBy, "Dipesh");
        typeAndEnsureValue(wait, js, emailBy, "dipesh@yopmail.com");

        // -------- File upload (keep your path logic) --------
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(fileBy));

        String filePath = System.getProperty("user.dir") + "\\SampleDocs\\upload.docx";
        fileInput.sendKeys(filePath);

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        // -------- Submit --------
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(submitBy));
        try {
            submitBtn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", submitBtn);
        }

        // ===== Try-catch for success/fail (EDP style) =====
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successBy));

            writeExcel(22, 4, "✅ FORM SUBMITTED SUCCESSFULLY!");
            System.out.println("✅ Medanta Pharmacy – Upload Form PASS");

        } catch (Exception e) {

            String nameVal = safeGetValue(nameBy);
            String emailVal = safeGetValue(emailBy);

            StringBuilder errorMsg = new StringBuilder();
            try {
                WebElement err = driver.findElement(
                        By.xpath("(//input[@type='text'])[5]/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Name Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("(//input[@type='email'])[3]/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("Email Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            try {
                WebElement err = driver.findElement(
                        By.xpath("(//input[@type='file'])[2]/following-sibling::span[contains(@class,'errmsg')]"));
                if (err.isDisplayed())
                    errorMsg.append("File Error: ").append(err.getText()).append(" | ");
            } catch (Exception ignored) {}

            String finalResult = "Name=" + nameVal + " | Email=" + emailVal + " | File=NOT_UPLOADED | Errors => "
                    + errorMsg;

            writeExcel(22, 4, "❌ FORM NOT SUBMITTED SUCCESSFULLY! FAIL");
            writeExcel(22, 5, finalResult);

            System.out.println(finalResult);
            Assert.fail("Upload Form validation failed: " + finalResult);
        }
    }

    // ✅ same helper (paste in every form for now)
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
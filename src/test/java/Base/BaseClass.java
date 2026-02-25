package Base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	protected WebDriver driver;

    protected FileInputStream file;
    protected XSSFWorkbook workbook;
    protected XSSFSheet sheet;
    protected DataFormatter formatter;

    protected static final String EXCEL_PATH = System.getProperty("user.dir")
            + "/src/test/resources/formsautomation.xlsx";

    /* ================= BEFORE SUITE ================= */
    @BeforeSuite
    public void writeExecutionDateOnce() {

        try (FileInputStream fis = new FileInputStream(EXCEL_PATH); XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sh = wb.getSheet("Sheet1");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String runDate = sdf.format(new Date());

            int rowIndex = 32; // Row 33 (0-based)
            int colIndex = 1;  // Column B (0-based)

            if (sh.getRow(rowIndex) == null) sh.createRow(rowIndex);
            sh.getRow(rowIndex).createCell(colIndex).setCellValue(runDate);

            try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
                wb.write(fos);
            }

            System.out.println("📅 Execution date written in Excel");

        } catch (Exception e) {
            System.err.println("❌ Execution date write failed: " + e.getMessage());
        }
    }

    /* ================= BEFORE CLASS ================= */
    @BeforeClass
    public void openBrowser() {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(getChromeOptions());

        // ✅ Jenkins stability: rely on explicit waits in tests
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        loadExcel();
    }

    /* ================= CHROME OPTIONS ================= */
    private ChromeOptions getChromeOptions() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-blink-features=AutomationControlled");

        if (isLinux()) {
            System.out.println("🔹 Jenkins/Linux → Headless Chrome");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-features=VizDisplayCompositor");
        } else {
            System.out.println("🔹 Windows → Normal Chrome");
            options.addArguments("start-maximized");
        }

        return options;
    }

    private boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    /* ================= EXCEL LOAD ================= */
    private void loadExcel() {
        try {
            file = new FileInputStream(EXCEL_PATH);
            workbook = new XSSFWorkbook(file);

            // ✅ close stream to avoid file-lock issues
            file.close();
            file = null;

            sheet = workbook.getSheet("Sheet1");
            formatter = new DataFormatter();
            System.out.println("✅ Excel loaded");
        } catch (Exception e) {
            throw new RuntimeException("❌ Excel load failed: " + e.getMessage());
        }
    }

    /* ================= EXCEL WRITE ================= */
    public synchronized void writeExcel(int row, int col, String value) {
        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sh = wb.getSheet("Sheet1");
            if (sh.getRow(row) == null) sh.createRow(row);
            sh.getRow(row).createCell(col).setCellValue(value);

            try (FileOutputStream out = new FileOutputStream(EXCEL_PATH)) {
                wb.write(out);
            }

        } catch (Exception e) {
            System.err.println("❌ Excel write failed: " + e.getMessage());
        }
    }

    /* ================= UTIL METHODS ================= */

    public void slowType(WebElement element, String text) {

        if (text == null) text = "";

        try { element.click(); } catch (Exception ignored) {}

        // 1) Strong clear
        try {
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
        } catch (Exception ignored) {}

        // 2) Try native typing
        try {
            element.sendKeys(text);
        } catch (Exception ignored) {
            try {
                for (char c : text.toCharArray()) {
                    element.sendKeys(String.valueOf(c));
                    try { Thread.sleep(60); } catch (InterruptedException ignored2) {}
                }
            } catch (Exception ignored2) {}
        }

        // 3) Validate
        String v = "";
        try {
            v = element.getAttribute("value");
            if (v == null) v = "";
        } catch (Exception ignored) {}

        if (v.trim().equals(text.trim())) return;

        // 4) JS fallback + events
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "var el=arguments[0]; var val=arguments[1];" +
                            "el.focus();" +
                            "el.value = val;" +
                            "el.dispatchEvent(new Event('input', {bubbles:true}));" +
                            "el.dispatchEvent(new Event('change', {bubbles:true}));" +
                            "el.dispatchEvent(new Event('blur', {bubbles:true}));",
                    element, text
            );
        } catch (Exception ignored) {}
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public String nowStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    /* ================= THANK YOU FLASH DETECT ================= */
    public boolean waitForFlashPresence(By locator, int maxMillis) {
        long end = System.currentTimeMillis() + maxMillis;

        while (System.currentTimeMillis() < end) {
            try {
                for (WebElement el : driver.findElements(locator)) {
                    // element present => count it success (displayed check optional)
                    if (el != null) return true;
                }
            } catch (Exception ignored) {}

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    /* ================= FIELD ERRORS (MULTIPLE) ================= */
    public String collectAllValidationErrors() {
        StringBuilder sb = new StringBuilder();
        try {
            for (WebElement e : driver.findElements(By.xpath(
                    "//*[contains(@class,'errmsg') or contains(@class,'error') or contains(@class,'invalid')][normalize-space()!='']"))) {
                if (e.isDisplayed()) {
                    String t = e.getText();
                    if (t != null && !t.trim().isEmpty())
                        sb.append(t.trim()).append(" | ");
                }
            }
        } catch (Exception ignored) {}
        return sb.toString().trim();
    }

    /* ================= GLOBAL ERRORS (TOAST/ALERT) ================= */
    public String collectGlobalErrors() {
        StringBuilder sb = new StringBuilder();
        try {
            for (WebElement e : driver.findElements(By.xpath(
                    "//*[@role='alert' or contains(@class,'toast') or contains(@class,'alert') or contains(@class,'notification')][normalize-space()!='']"))) {
                if (e.isDisplayed()) {
                    String t = e.getText();
                    if (t != null && !t.trim().isEmpty())
                        sb.append(t.trim()).append(" | ");
                }
            }
        } catch (Exception ignored) {}
        return sb.toString().trim();
    }

    /* ================= VALUE LOST DETECT + REFILL ================= */
    public boolean ensureValueStillPresent(By locator, String expected) {
        try {
            WebElement el = driver.findElement(locator);

            String actual = el.getAttribute("value");
            if (actual == null) actual = "";
            if (expected.equals(actual.trim())) return true;

            slowType(el, expected);

            String after = el.getAttribute("value");
            return after != null && expected.equals(after.trim());

        } catch (Exception e) {
            return false;
        }
    }

    public void ensureValueStillPresentStrict(By locator, String expected) {
        boolean ok = ensureValueStillPresent(locator, expected);
        if (!ok) throw new RuntimeException("Value wiped / not present for: " + locator + " expected='" + expected + "'");
    }

    /* ================= WRITE RESULT (E to L) ================= */
    public void writeFormResult(int row, String status, String inputs, String fieldErrors, String globalErrors,
                                String serverInfo, boolean thankYouSeen, String debugUrlTitle) {
        writeExcel(row, 4, status);                 // E
        writeExcel(row, 5, inputs);                 // F
        writeExcel(row, 6, fieldErrors);            // G
        writeExcel(row, 7, globalErrors);           // H
        writeExcel(row, 8, serverInfo);             // I
        writeExcel(row, 9, String.valueOf(thankYouSeen)); // J
        writeExcel(row, 10, debugUrlTitle);         // K
        writeExcel(row, 11, nowStamp());            // L
    }

    /* ================= AFTER CLASS ================= */
    @AfterClass(alwaysRun = true)
    public void tearDown() {

        try {
            if (file != null) file.close();
            if (workbook != null) workbook.close();
        } catch (IOException e) {
            System.err.println("❌ Excel close issue: " + e.getMessage());
        }

        if (driver != null) {
            driver.quit();
        }

        System.out.println("✅ Test finished cleanly");
    }
}
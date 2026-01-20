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
import org.openqa.selenium.JavascriptExecutor;
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

    // ✅ Single source for Excel location
    protected static final String EXCEL_PATH =
            System.getProperty("user.dir") + "/src/test/resources/formsautomation.xlsx";

    /* ================= BEFORE SUITE ================= */
    @BeforeSuite
    public void writeExecutionDateOnce() {

        try (FileInputStream fis = new FileInputStream(EXCEL_PATH);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sh = wb.getSheet("Sheet1");

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String runDate = sdf.format(new Date());

            int rowIndex = 32; // Row 33
            int colIndex = 1;  // Column B

            if (sh.getRow(rowIndex) == null)
                sh.createRow(rowIndex);

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

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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
            sheet = workbook.getSheet("Sheet1");
            formatter = new DataFormatter();
            System.out.println("✅ Excel loaded");
        } catch (Exception e) {
            throw new RuntimeException("❌ Excel load failed: " + e.getMessage());
        }
    }

    /* ================= EXCEL WRITE ================= */
    public synchronized void writeExcel(int row, int col, String value) {

        try {
            if (sheet.getRow(row) == null)
                sheet.createRow(row);

            sheet.getRow(row).createCell(col).setCellValue(value);

            try (FileOutputStream out = new FileOutputStream(EXCEL_PATH)) {
                workbook.write(out);
            }

        } catch (Exception e) {
            System.err.println("❌ Excel write failed: " + e.getMessage());
        }
    }

    /* ================= UTIL METHODS ================= */
    public void slowType(WebElement element, String text) {
        element.clear();
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {}
        }
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
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
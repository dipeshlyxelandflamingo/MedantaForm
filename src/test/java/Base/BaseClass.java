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

	public WebDriver driver;
	public FileInputStream file;
	public XSSFWorkbook workbook;
	public XSSFSheet sheet;
	public DataFormatter formatter;

	// ✅ EXCEL PATH (SINGLE SOURCE OF TRUTH)
	private final String EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/forms automation.xlsx";

	// ================= EXECUTION DATE (ONCE PER SUITE) =================
	@BeforeSuite
	public void writeExecutionDateOnce() {

		try {
			FileInputStream fis = new FileInputStream(EXCEL_PATH);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sh = wb.getSheet("Sheet1");

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String runDate = sdf.format(new Date());

			int rowIndex = 32; // Excel Row 33
			int colIndex = 1; // Column B

			if (sh.getRow(rowIndex) == null)
				sh.createRow(rowIndex);

			sh.getRow(rowIndex).createCell(colIndex).setCellValue(runDate);

			fis.close();

			try (FileOutputStream fos = new FileOutputStream(EXCEL_PATH)) {
				wb.write(fos);
			}
			wb.close();

			System.out.println("📅 Execution date written once in Excel");

		} catch (Exception e) {
			System.err.println("❌ Execution date write failed: " + e.getMessage());
		}
	}

	// ================= BEFORE CLASS =================
	@BeforeClass
	public void OpenBrowser() {

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-notifications");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-extensions");

		// ===== OS based execution =====
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			System.out.println("🔹 Jenkins / Linux detected → Headless ON");
			options.addArguments("--headless=new");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-gpu");
			options.addArguments("--window-size=1920,1080");
		} else {
			System.out.println("🔹 Windows detected → Headed browser");
			options.addArguments("start-maximized");
		}

		driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

		// ================= EXCEL SETUP =================
		try {
			file = new FileInputStream(EXCEL_PATH);
			workbook = new XSSFWorkbook(file);
			sheet = workbook.getSheet("Sheet1");
			formatter = new DataFormatter();
			System.out.println("✅ Excel loaded successfully");
		} catch (Exception e) {
			throw new RuntimeException("❌ Excel file issue: " + e.getMessage());
		}
	}

	// ================= SLOW TYPE HELPER =================
	public void slowType(WebElement element, String text) {
		element.click();
		element.clear();
		for (char c : text.toCharArray()) {
			element.sendKeys(String.valueOf(c));
			try {
				Thread.sleep(120);
			} catch (InterruptedException e) {
			}
		}
	}

	// ================= SCROLL HELPER =================
	public void scrollToElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
	}

	// ================= EXCEL WRITE (STATUS / ERROR) =================
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

	// ================= AFTER CLASS =================
	@AfterClass(alwaysRun = true)
	public void TearDown() {

		try {
			if (file != null)
				file.close();
			if (workbook != null)
				workbook.close();
		} catch (IOException e) {
			System.err.println("❌ Excel close issue: " + e.getMessage());
		}

		if (driver != null) {
			driver.quit();
		}

		System.out.println("✅ Test finished → Browser closed & Excel saved");
	}
}
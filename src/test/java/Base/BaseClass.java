package Base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

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

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	public WebDriver driver;
	public FileInputStream file;
	public XSSFWorkbook workbook;
	public XSSFSheet sheet;
	public DataFormatter formatter;

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
			String path = System.getProperty("user.dir") + "/forms automation.xlsx";
			file = new FileInputStream(path);
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
				Thread.sleep(120); // human typing speed
			} catch (InterruptedException e) {
			}
		}
	}

	// ================= SCROLL HELPER =================
	public void scrollToElement(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
	}

	// ================= EXCEL WRITE (JENKINS SAFE) =================
	public synchronized void writeExcel(int row, int col, String value) {
		try {
			if (sheet.getRow(row) == null)
				sheet.createRow(row);

			sheet.getRow(row).createCell(col).setCellValue(value);

			String path = System.getProperty("user.dir") + "/forms automation.xlsx";
			try (FileOutputStream out = new FileOutputStream(path)) {
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
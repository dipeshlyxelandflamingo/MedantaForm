package Base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	public WebDriver driver;
	public FileInputStream file;
	public FileOutputStream fileOut;
	public XSSFWorkbook workbook;
	public XSSFSheet sheet;
	public DataFormatter formatter;

	@BeforeClass
	public void Openbrowser() {

		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("https://www.medanta.org");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		try {
			file = new FileInputStream("MedantaExcel/forms automation.xlsx");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sheet = workbook.getSheet("Sheet1");
		formatter = new DataFormatter();
		System.out.println("Sheet name: " + sheet.getSheetName());
	}

	//@AfterClass
	public void TearDown() {
		try {
			fileOut = new FileOutputStream("MedantaExcel/forms automation.xlsx");
			workbook.write(fileOut);
		} catch (Exception e) {
			System.err.println("Error while writing to Excel file: " + e.getMessage());

		}
		driver.quit();
	}

}

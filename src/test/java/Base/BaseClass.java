package Base;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("start-maximized");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.7444.134 Safari/537.36");

        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.get("https://www.medanta.org");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            String path = System.getProperty("user.dir") + "/forms automation.xlsx";
            file = new FileInputStream(path);
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheet("Sheet1");
            formatter = new DataFormatter();
            System.out.println("Sheet name: " + sheet.getSheetName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void TearDown() {
        try {
            if (file != null) file.close(); // Close input stream first

            String path = System.getProperty("user.dir") + "/forms automation.xlsx";
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            System.out.println("Excel Updated Successfully!");
        } catch (Exception e) {
            System.err.println("Error while writing to Excel file: " + e.getMessage());
        } finally {
            try {
                if (fileOut != null) fileOut.close();
                if (workbook != null) workbook.close();
            } catch (Exception ex) {
                System.err.println("Error while closing workbook/file: " + ex.getMessage());
            }
            if (driver != null) driver.quit();
        }
    }
}

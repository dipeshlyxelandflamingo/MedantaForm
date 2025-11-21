@BeforeClass
public void Openbrowser() {

    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();

    // Anti-detection setup
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("start-maximized");
    options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/142.0.7444.134 Safari/537.36");

    // Jenkins/Linux safe mode
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

    // ❗❗ THIS WAS THE REAL PROBLEM — FIXED NOW
    driver = new ChromeDriver(options);

    driver.get("https://www.medanta.org");
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    try {
        file = new FileInputStream("MedantaExcel/forms automation.xlsx");
        workbook = new XSSFWorkbook(file);
    } catch (Exception e) {
        e.printStackTrace();
    }

    sheet = workbook.getSheet("Sheet1");
    formatter = new DataFormatter();
    System.out.println("Sheet name: " + sheet.getSheetName());
}

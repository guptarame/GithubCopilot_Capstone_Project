package Github_Copilot.base;

import Github_Copilot.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setupDriver() {
        driver = createDriver(TestConfig.browser(), TestConfig.headless());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        driver.manage().window().maximize();
    }

    @AfterEach
    void teardownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver createDriver(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield new FirefoxDriver(options);
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
                yield new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}


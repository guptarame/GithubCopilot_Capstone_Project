package Github_Copilot.base;

import Github_Copilot.data.TestData;
import Github_Copilot.config.TestConfig;
import Github_Copilot.listeners.TestLifecycleListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.nio.file.Path;
import java.time.Duration;

@ExtendWith(TestLifecycleListener.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setupDriver() {
        driver = createDriver(TestConfig.browser(), TestConfig.headless());
        TestLifecycleListener.registerDriver(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TestData.DEFAULT_IMPLICIT_WAIT_SECONDS));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.pageLoadTimeout()));
        driver.manage().window().maximize();
    }

    @AfterEach
    void teardownDriver() {
        if (driver != null) {
            TestLifecycleListener.clearDriver();
            driver.quit();
        }
    }

    protected WebDriver createDriver(String browser, boolean headless) {
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

    protected WebDriver createChromeDriver(boolean headless, Path userDataDir) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        if (userDataDir != null) {
            options.addArguments("--user-data-dir=" + userDataDir.toAbsolutePath());
        }
        options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
        return new ChromeDriver(options);
    }
}


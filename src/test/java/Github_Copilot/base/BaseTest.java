package Github_Copilot.base;

import Github_Copilot.config.TestConfig;
import Github_Copilot.listeners.TestLifecycleListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.Dimension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.time.Duration;

@ExtendWith(TestLifecycleListener.class)
public abstract class BaseTest {

    protected WebDriver driver;
    private Path chromeProfileDir;

    @BeforeEach
    void setupDriver(TestInfo testInfo) throws IOException {
        boolean rememberMeTest = testInfo.getDisplayName().contains("Remember me");
        if (rememberMeTest && "chrome".equals(TestConfig.browser())) {
            chromeProfileDir = Files.createTempDirectory("remember-me-profile-");
            driver = createChromeDriver(TestConfig.headless(), chromeProfileDir);
        } else {
            driver = createDriver(TestConfig.browser(), TestConfig.headless());
        }
        TestLifecycleListener.registerDriver(driver);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.pageLoadTimeout()));
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        if (!TestConfig.headless()) {
            driver.manage().window().maximize();
        } else {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    @AfterEach
    void teardownDriver() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            TestLifecycleListener.clearDriver();
            deleteChromeProfile();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected Path getChromeProfileDir() {
        return chromeProfileDir;
    }

    private void deleteChromeProfile() {
        if (chromeProfileDir == null) {
            return;
        }
        try (var paths = Files.walk(chromeProfileDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // Best-effort cleanup; the test result must remain authoritative.
                }
            });
        } catch (IOException ignored) {
            // Best-effort cleanup for browser-created profile files.
        } finally {
            chromeProfileDir = null;
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

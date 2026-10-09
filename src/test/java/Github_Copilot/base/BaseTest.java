package Github_Copilot.base;

import Github_Copilot.config.TestConfig;
import Github_Copilot.listeners.ExtentReportExtension;
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

@ExtendWith({TestLifecycleListener.class, ExtentReportExtension.class})
public abstract class BaseTest {

    protected WebDriver driver;
    private Path persistentProfileDir;

    @BeforeEach
    void setupDriver(TestInfo testInfo) throws IOException {
        boolean rememberMeTest = testInfo.getDisplayName().toLowerCase().contains("remember me");
        if (rememberMeTest) {
            persistentProfileDir = Files.createTempDirectory("remember-me-profile-");
            driver = createDriver(TestConfig.browser(), TestConfig.headless(), persistentProfileDir);
        } else {
            driver = createDriver(TestConfig.browser(), TestConfig.headless());
        }
        TestLifecycleListener.registerDriver(driver);
    }

    @AfterEach
    void teardownDriver() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            TestLifecycleListener.clearDriver();
            deletePersistentProfile();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected Path getPersistentProfileDir() {
        return persistentProfileDir;
    }

    protected WebDriver restartDriver(Path profileDir) {
        if (driver != null) {
            driver.quit();
            TestLifecycleListener.clearDriver();
        }
        driver = createDriver(TestConfig.browser(), TestConfig.headless(), profileDir);
        TestLifecycleListener.registerDriver(driver);
        return driver;
    }

    private void deletePersistentProfile() {
        if (persistentProfileDir == null) {
            return;
        }
        try (var paths = Files.walk(persistentProfileDir)) {
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
            persistentProfileDir = null;
        }
    }

    protected WebDriver createDriver(String browser, boolean headless) {
        return createDriver(browser, headless, null);
    }

    protected WebDriver createDriver(String browser, boolean headless, Path userDataDir) {
        WebDriver webDriver;
        return switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                if (userDataDir != null) {
                    options.addArguments("-profile", userDataDir.toAbsolutePath().toString());
                }
                webDriver = new FirefoxDriver(options);
                configureDriver(webDriver, headless);
                yield webDriver;
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                if (userDataDir != null) {
                    options.addArguments("--user-data-dir=" + userDataDir.toAbsolutePath());
                }
                options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
                webDriver = new ChromeDriver(options);
                configureDriver(webDriver, headless);
                yield webDriver;
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private void configureDriver(WebDriver webDriver, boolean headless) {
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.pageLoadTimeout()));
        webDriver.manage().timeouts().implicitlyWait(Duration.ZERO);
        if (!headless) {
            webDriver.manage().window().maximize();
        } else {
            webDriver.manage().window().setSize(new Dimension(1920, 1080));
        }
    }

    protected WebDriver createChromeDriver(boolean headless, Path userDataDir) {
        return createDriver("chrome", headless, userDataDir);
    }
}

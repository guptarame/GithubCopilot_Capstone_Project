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
        try {
            if (rememberMeTest) {
                persistentProfileDir = createPersistentProfileDirectory();
            }
            driver = persistentProfileDir == null
                    ? createDriver(TestConfig.browser(), TestConfig.headless())
                    : createDriver(TestConfig.browser(), TestConfig.headless(), persistentProfileDir);
            TestLifecycleListener.registerDriver(driver);
        } catch (IOException | RuntimeException | Error failure) {
            WebDriver partiallyStartedDriver = driver;
            driver = null;
            quitDriverSafely(partiallyStartedDriver, "WebDriver cleanup failed after test setup failure.");
            clearDriverRegistrationSafely();
            deletePersistentProfile();
            throw failure;
        }
    }

    @AfterEach
    void teardownDriver() {
        WebDriver activeDriver = driver;
        driver = null;
        try {
            quitDriverSafely(activeDriver, "WebDriver quit failed during test cleanup.");
        } finally {
            try {
                clearDriverRegistrationSafely();
            } finally {
                deletePersistentProfile();
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    protected Path getPersistentProfileDir() {
        return persistentProfileDir;
    }

    protected WebDriver restartDriver(Path profileDir) {
        WebDriver previousDriver = driver;
        driver = null;
        clearDriverRegistrationSafely();
        quitDriverSafely(previousDriver, "WebDriver quit failed during Remember Me restart.");

        try {
            WebDriver restartedDriver = createDriver(TestConfig.browser(), TestConfig.headless(), profileDir);
            driver = restartedDriver;
            TestLifecycleListener.registerDriver(restartedDriver);
            return restartedDriver;
        } catch (RuntimeException | Error failure) {
            WebDriver partiallyStartedDriver = driver;
            driver = null;
            quitDriverSafely(partiallyStartedDriver, "WebDriver cleanup failed after restart failure.");
            clearDriverRegistrationSafely();
            deletePersistentProfile();
            throw failure;
        }
    }

    private void deletePersistentProfile() {
        Path profileDir = persistentProfileDir;
        persistentProfileDir = null;
        if (profileDir == null) {
            return;
        }

        boolean deletionFailed = false;
        try (var paths = Files.walk(profileDir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                try {
                    deleteProfileEntry(path);
                } catch (Throwable failure) {
                    deletionFailed = true;
                }
            }
        } catch (Throwable failure) {
            deletionFailed = true;
        }
        if (deletionFailed) {
            warnSafely("Temporary browser profile cleanup failed; local residual files may require authorized removal.");
        }
    }

    protected Path createPersistentProfileDirectory() throws IOException {
        return Files.createTempDirectory("remember-me-profile-");
    }

    protected void deleteProfileEntry(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    protected WebDriver createDriver(String browser, boolean headless) {
        return createDriver(browser, headless, null);
    }

    protected WebDriver createDriver(String browser, boolean headless, Path userDataDir) {
        WebDriver webDriver;
        return switch (browser) {
            case "firefox" -> {
                setupDriverManager(browser);
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                if (userDataDir != null) {
                    options.addArguments("-profile", userDataDir.toAbsolutePath().toString());
                }
                webDriver = newFirefoxDriver(options);
                configureAndReturn(webDriver, headless);
                yield webDriver;
            }
            case "chrome" -> {
                setupDriverManager(browser);
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                if (userDataDir != null) {
                    options.addArguments("--user-data-dir=" + userDataDir.toAbsolutePath());
                }
                options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=1920,1080");
                webDriver = newChromeDriver(options);
                configureAndReturn(webDriver, headless);
                yield webDriver;
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    protected void setupDriverManager(String browser) {
        switch (browser) {
            case "firefox" -> WebDriverManager.firefoxdriver().setup();
            case "chrome" -> WebDriverManager.chromedriver().setup();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    protected WebDriver newFirefoxDriver(FirefoxOptions options) {
        return new FirefoxDriver(options);
    }

    protected WebDriver newChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

    private WebDriver configureAndReturn(WebDriver webDriver, boolean headless) {
        try {
            configureDriver(webDriver, headless);
            return webDriver;
        } catch (RuntimeException | Error failure) {
            quitDriverSafely(webDriver, "WebDriver cleanup failed after startup configuration failure.");
            throw failure;
        }
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

    private static void quitDriverSafely(WebDriver webDriver, String warning) {
        if (webDriver == null) {
            return;
        }
        try {
            webDriver.quit();
        } catch (Throwable cleanupFailure) {
            warnSafely(warning);
        }
    }

    private static void clearDriverRegistrationSafely() {
        try {
            TestLifecycleListener.clearDriver();
        } catch (Throwable cleanupFailure) {
            warnSafely("WebDriver lifecycle registration cleanup failed.");
        }
    }

    private static void warnSafely(String warning) {
        try {
            Github_Copilot.utils.LogUtil.warn(warning);
        } catch (Throwable ignored) {
            // Diagnostics must not replace the original test/setup outcome.
        }
    }

    protected WebDriver createChromeDriver(boolean headless, Path userDataDir) {
        return createDriver("chrome", headless, userDataDir);
    }
}

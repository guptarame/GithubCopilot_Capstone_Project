package Github_Copilot.listeners;

import Github_Copilot.utils.LogUtil;
import Github_Copilot.utils.ScreenshotUtil;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class TestLifecycleListener implements BeforeEachCallback, AfterTestExecutionCallback {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static void registerDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void clearDriver() {
        DRIVER.remove();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        LogUtil.info("Starting test: " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        String testName = context.getDisplayName();

        if (failure.isPresent()) {
            LogUtil.error("Test failed: " + testName + " -> " + failure.get().getMessage());
            WebDriver driver = DRIVER.get();
            if (driver != null) {
                ScreenshotUtil.capture(driver, testName);
            }
        } else {
            LogUtil.pass("Test passed: " + testName);
        }

        LogUtil.info("Finished test: " + testName);
    }
}


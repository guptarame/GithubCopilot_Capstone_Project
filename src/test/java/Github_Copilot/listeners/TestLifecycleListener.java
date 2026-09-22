package Github_Copilot.listeners;

import Github_Copilot.utils.LogUtil;
import Github_Copilot.utils.ScreenshotUtil;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
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

        if (failure.isPresent() && failure.get() instanceof TestAbortedException) {
            LogUtil.warn("Test skipped: " + testName + " -> " + failure.get().getMessage());
            LogUtil.info("Finished test: " + testName);
            return;
        }

        if (failure.isPresent()) {
            LogUtil.error("Test failed: " + testName, failure.get());
            try {
                WebDriver driver = DRIVER.get();
                if (driver != null) {
                    ScreenshotUtil.capture(driver, testName);
                }
            } catch (RuntimeException diagnosticFailure) {
                LogUtil.error("Failure diagnostics could not be captured", diagnosticFailure);
            }
        } else {
            LogUtil.pass("Test passed: " + testName);
        }

        LogUtil.info("Finished test: " + testName);
    }
}

package Github_Copilot.listeners;

import Github_Copilot.config.TestConfig;
import Github_Copilot.utils.LogUtil;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.TestAbortedException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class TestLifecycleListener implements BeforeEachCallback, AfterTestExecutionCallback {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Instant> STARTED_AT = new ThreadLocal<>();

    public static void registerDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void clearDriver() {
        DRIVER.remove();
    }

    public static WebDriver currentDriver() {
        return DRIVER.get();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        STARTED_AT.set(Instant.now());
        LogUtil.info("Starting test: " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Throwable> failure = context.getExecutionException();
        String testName = context.getDisplayName();

        if (failure.isPresent() && failure.get() instanceof TestAbortedException) {
            LogUtil.warn("Test skipped: " + testName + " -> " + failure.get().getMessage());
            logFinished(testName);
            return;
        }

        if (failure.isPresent()) {
            LogUtil.error("Test failed: " + testName, failure.get());
            if (DRIVER.get() != null && !TestConfig.allowFailureScreenshots()) {
                LogUtil.warn("Failure screenshot skipped because ALLOW_FAILURE_SCREENSHOTS is not enabled.");
            }
        } else {
            LogUtil.pass("Test passed: " + testName);
        }

        logFinished(testName);
    }

    private void logFinished(String testName) {
        Instant startedAt = STARTED_AT.get();
        if (startedAt != null) {
            LogUtil.info("Finished test: " + testName + " in " + Duration.between(startedAt, Instant.now()).toMillis() + " ms");
            STARTED_AT.remove();
        } else {
            LogUtil.info("Finished test: " + testName);
        }
    }
}

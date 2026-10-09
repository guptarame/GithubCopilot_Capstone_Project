package Github_Copilot.listeners;

import Github_Copilot.config.TestConfig;
import Github_Copilot.utils.LogUtil;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class TestLifecycleListener implements BeforeEachCallback, TestWatcher {

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
    public void testSuccessful(ExtensionContext context) {
        recordOutcome(context, TestOutcome.PASSED, null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        recordOutcome(context, TestOutcome.FAILED, cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        recordOutcome(context, TestOutcome.SKIPPED, cause);
    }

    private void recordOutcome(ExtensionContext context, TestOutcome outcome, Throwable failure) {
        String testName = context.getDisplayName();
        if (outcome == TestOutcome.SKIPPED) {
            LogUtil.warn("Test skipped: " + testName + " -> " + failure.getMessage());
        } else if (outcome == TestOutcome.FAILED) {
            LogUtil.error("Test failed: " + testName, failure);
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
        clearDriver();
    }
}

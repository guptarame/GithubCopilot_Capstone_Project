package Github_Copilot.listeners;

import Github_Copilot.config.TestConfig;
import Github_Copilot.utils.LogUtil;
import Github_Copilot.utils.ScreenshotUtil;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ExtentReportExtension implements BeforeEachCallback, AfterTestExecutionCallback,
    AfterAllCallback, TestWatcher {

    private static final String REPORT_PATH_PROPERTY = "extentReportPath";
    private static final String DEFAULT_REPORT_PATH = "target/extent-reports/ExtentReport.html";
    private static final ExtentReports REPORT = createReport();
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        ExtentTest test = REPORT.createTest(context.getDisplayName());
        test.assignCategory(context.getRequiredTestClass().getSimpleName());
        CURRENT_TEST.set(test);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            return;
        }

        if (TestOutcome.from(context.getExecutionException()) == TestOutcome.FAILED) {
            attachFailureScreenshot(test, context.getDisplayName());
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        finishTest("Test passed");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        finishTest("Test failed; see the Surefire report for diagnostic details.");
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            test.skip("Test skipped");
        }
        CURRENT_TEST.remove();
    }

    private void finishTest(String result) {
        ExtentTest test = CURRENT_TEST.get();
        if (test != null) {
            if (result.equals("Test passed")) {
                test.pass(result);
            } else {
                test.fail(result);
            }
        }
        CURRENT_TEST.remove();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        REPORT.flush();
    }

    private static ExtentReports createReport() {
        try {
            Path reportPath = Path.of(System.getProperty(REPORT_PATH_PROPERTY, DEFAULT_REPORT_PATH));
            Files.createDirectories(reportPath.toAbsolutePath().getParent());
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath.toFile());
            ExtentReports extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Browser", TestConfig.browser());
            extentReports.setSystemInfo("Headless", Boolean.toString(TestConfig.headless()));
            return extentReports;
        } catch (Exception exception) {
            throw new ExceptionInInitializerError("Unable to initialize the Extent report.");
        }
    }

    private static void attachFailureScreenshot(ExtentTest test, String testName) {
        if (!TestConfig.allowFailureScreenshots()) {
            return;
        }

        try {
            Path screenshot = ScreenshotUtil.capture(TestLifecycleListener.currentDriver(), testName);
            if (screenshot == null) {
                return;
            }

            Path reportPath = Path.of(System.getProperty(REPORT_PATH_PROPERTY, DEFAULT_REPORT_PATH))
                    .toAbsolutePath();
            Path relativeScreenshot = reportPath.getParent().relativize(screenshot.toAbsolutePath());
            test.addScreenCaptureFromPath(relativeScreenshot.toString().replace("\\", "/"));
        } catch (RuntimeException exception) {
            LogUtil.warn("Failure screenshot diagnostics could not be attached to the Extent report.");
        }
    }
}
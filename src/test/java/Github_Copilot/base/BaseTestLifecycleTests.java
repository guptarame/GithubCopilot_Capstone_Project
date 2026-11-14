package Github_Copilot.base;

import Github_Copilot.listeners.TestLifecycleListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseTestLifecycleTests {

    private static final String BROWSER_PROPERTY = "browser";
    private static final String HEADLESS_PROPERTY = "headless";
    private static final String PAGE_LOAD_PROPERTY = "pageLoadTimeout";

    private String previousBrowser;
    private String previousHeadless;
    private String previousPageLoadTimeout;
    private final List<Harness> harnesses = new ArrayList<>();

    @BeforeEach
    void setConfiguration() {
        previousBrowser = System.getProperty(BROWSER_PROPERTY);
        previousHeadless = System.getProperty(HEADLESS_PROPERTY);
        previousPageLoadTimeout = System.getProperty(PAGE_LOAD_PROPERTY);
        System.setProperty(BROWSER_PROPERTY, "chrome");
        System.setProperty(HEADLESS_PROPERTY, "true");
        System.clearProperty(PAGE_LOAD_PROPERTY);
    }

    @AfterEach
    void restoreConfiguration() {
        for (Harness harness : harnesses) {
            harness.failProfileDeletion = false;
            harness.stop();
            try {
                deleteTree(harness.createdProfile);
            } catch (IOException ignored) {
                // A failed test cleanup must not replace the test's primary outcome.
            }
        }
        restoreProperty(BROWSER_PROPERTY, previousBrowser);
        restoreProperty(HEADLESS_PROPERTY, previousHeadless);
        restoreProperty(PAGE_LOAD_PROPERTY, previousPageLoadTimeout);
        TestLifecycleListener.clearDriver();
    }

    @Test
    void configurationFailureQuitsConstructedDriverAndPreservesPrimaryFailure() {
        RuntimeException configurationFailure = new IllegalStateException("synthetic configuration failure");
        RuntimeException quitFailure = new IllegalStateException("synthetic quit failure");
        DriverDouble constructed = new DriverDouble(configurationFailure, quitFailure);
        Harness harness = newHarness(constructed);

        ByteArrayOutputStream captured = captureOutput(() ->
                assertSame(configurationFailure, assertThrows(RuntimeException.class,
                        harness::createChromeForTest)));

        assertEquals(1, constructed.quitCalls.get());
        assertTrue(captured.toString().contains("startup configuration failure"));
        assertFalse(captured.toString().contains("synthetic configuration failure"));
        assertFalse(captured.toString().contains("synthetic quit failure"));
    }

    @Test
    void setupFailureAfterProfileCreationAttemptsDriverAndProfileCleanup() {
        RuntimeException configurationFailure = new IllegalStateException("synthetic setup failure");
        RuntimeException quitFailure = new IllegalStateException("synthetic quit failure");
        DriverDouble constructed = new DriverDouble(configurationFailure, quitFailure);
        Harness harness = newHarness(constructed);

        assertSame(configurationFailure, assertThrows(RuntimeException.class,
                () -> harness.start(rememberMeTestInfo())));

        assertEquals(1, constructed.quitCalls.get());
        assertNotNullPathIsDeleted(harness.createdProfile);
        assertNull(harness.getDriver());
        assertNull(harness.profile());
        assertNull(TestLifecycleListener.currentDriver());
    }

    @Test
    void successfulRestartUsesSameProfileAndTeardownRecursivelyRemovesIt() throws IOException {
        DriverDouble initial = new DriverDouble();
        DriverDouble restarted = new DriverDouble();
        Harness harness = newHarness(initial, restarted);

        harness.start(rememberMeTestInfo());
        Path profile = harness.profile();
        Path nestedData = Files.createDirectories(profile.resolve("nested")).resolve("session-data");
        Files.writeString(nestedData, "synthetic profile content");

        WebDriver restartedDriver = harness.restart(profile);

        assertSame(restarted.driver, restartedDriver);
        assertEquals(1, initial.quitCalls.get());
        assertSame(restarted.driver, TestLifecycleListener.currentDriver());
        assertTrue(profileOption(harness.lastChromeOptions).contains(profile.toAbsolutePath().toString()));
        assertTrue(Files.exists(nestedData));

        harness.stop();

        assertEquals(1, restarted.quitCalls.get());
        assertFalse(Files.exists(profile));
        assertNull(harness.getDriver());
        assertNull(harness.profile());
        assertNull(TestLifecycleListener.currentDriver());
    }

    @Test
    void teardownQuitFailureDoesNotPreventListenerOrProfileCleanup() throws IOException {
        RuntimeException quitFailure = new IllegalStateException("synthetic teardown failure");
        DriverDouble driver = new DriverDouble(null, quitFailure);
        Harness harness = newHarness(driver);
        harness.start(rememberMeTestInfo());
        Path profile = harness.profile();
        Files.writeString(profile.resolve("session-data"), "synthetic profile content");

        ByteArrayOutputStream captured = captureOutput(harness::stop);

        assertEquals(1, driver.quitCalls.get());
        assertTrue(captured.toString().contains("quit failed during test cleanup"));
        assertFalse(captured.toString().contains("synthetic teardown failure"));
        assertFalse(captured.toString().contains(profile.toString()));
        assertFalse(Files.exists(profile));
        assertNull(harness.getDriver());
        assertNull(harness.profile());
        assertNull(TestLifecycleListener.currentDriver());
    }

    @Test
    void restartFailureClearsRegistrationAndRemovesTemporaryProfile() throws IOException {
        DriverDouble initial = new DriverDouble();
        RuntimeException restartFailure = new IllegalStateException("synthetic restart failure");
        DriverDouble failedRestart = new DriverDouble(restartFailure, null);
        Harness harness = newHarness(initial, failedRestart);
        harness.start(rememberMeTestInfo());
        Path profile = harness.profile();
        Files.writeString(profile.resolve("session-data"), "synthetic profile content");

        assertSame(restartFailure, assertThrows(RuntimeException.class, () -> harness.restart(profile)));

        assertEquals(1, initial.quitCalls.get());
        assertEquals(1, failedRestart.quitCalls.get());
        assertFalse(Files.exists(profile));
        assertNull(harness.getDriver());
        assertNull(harness.profile());
        assertNull(TestLifecycleListener.currentDriver());
    }

    @Test
    void configuredPageLoadTimeoutAndZeroImplicitWaitAreApplied() throws IOException {
        System.setProperty(PAGE_LOAD_PROPERTY, "17");
        DriverDouble driver = new DriverDouble();
        Harness harness = newHarness(driver);

        harness.start(nonRememberMeTestInfo());

        assertEquals(Duration.ofSeconds(17), driver.pageLoadTimeout);
        assertEquals(Duration.ZERO, driver.implicitWait);
        harness.stop();
    }

    @Test
    void profileDeletionFailureWarnsWithoutPathOrRawExceptionAndDoesNotMaskCleanup() throws IOException {
        Harness harness = newHarness(new DriverDouble());
        harness.failProfileDeletion = true;
        harness.start(rememberMeTestInfo());
        Path profile = harness.profile();
        Files.writeString(profile.resolve("session-data"), "synthetic profile content");

        ByteArrayOutputStream captured = captureOutput(harness::stop);
        String warning = captured.toString();

        assertTrue(warning.contains("Temporary browser profile cleanup failed"));
        assertFalse(warning.contains(profile.toString()));
        assertFalse(warning.contains("synthetic file deletion detail"));
        assertNull(harness.profile());
        assertNull(harness.getDriver());
        assertNull(TestLifecycleListener.currentDriver());
        deleteTree(profile);
    }

    private static String profileOption(ChromeOptions options) {
        Object chromeOptions = options.getCapability("goog:chromeOptions");
        if (!(chromeOptions instanceof java.util.Map<?, ?> capabilities)) {
            return "";
        }
        Object arguments = capabilities.get("args");
        return arguments == null ? "" : arguments.toString();
    }

    private Harness newHarness(DriverDouble... drivers) {
        Harness harness = new Harness(drivers);
        harnesses.add(harness);
        return harness;
    }

    private static TestInfo rememberMeTestInfo() {
        return testInfo("REG-REMEMBER-RESTART: Remember Me lifecycle regression");
    }

    private static TestInfo nonRememberMeTestInfo() {
        return testInfo("ordinary lifecycle regression");
    }

    private static TestInfo testInfo(String displayName) {
        return proxy(TestInfo.class, (proxy, method, args) -> {
            if (method.getName().equals("getDisplayName")) {
                return displayName;
            }
            return null;
        });
    }

    private static ByteArrayOutputStream captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try (PrintStream replacement = new PrintStream(captured)) {
            System.setOut(replacement);
            action.run();
        } finally {
            System.setOut(original);
        }
        return captured;
    }

    private static void assertNotNullPathIsDeleted(Path path) {
        assertTrue(path != null);
        assertFalse(Files.exists(path));
    }

    private static void deleteTree(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            return;
        }
        try (var entries = Files.walk(path)) {
            for (Path entry : entries.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(entry);
            }
        }
    }

    private static void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
    }

    private static final class Harness extends BaseTest {
        private final Deque<DriverDouble> drivers = new ArrayDeque<>();
        private ChromeOptions lastChromeOptions;
        private Path createdProfile;
        private boolean failProfileDeletion;

        private Harness(DriverDouble... drivers) {
            this.drivers.addAll(List.of(drivers));
        }

        @Override
        protected Path createPersistentProfileDirectory() throws IOException {
            createdProfile = super.createPersistentProfileDirectory();
            return createdProfile;
        }

        @Override
        protected void setupDriverManager(String browser) {
            // Browser-driver resolution is deliberately excluded from lifecycle unit tests.
        }

        @Override
        protected WebDriver newChromeDriver(ChromeOptions options) {
            lastChromeOptions = options;
            return drivers.removeFirst().driver;
        }

        @Override
        protected void deleteProfileEntry(Path path) throws IOException {
            if (failProfileDeletion) {
                throw new IOException("synthetic file deletion detail");
            }
            super.deleteProfileEntry(path);
        }

        private void start(TestInfo testInfo) throws IOException {
            setupDriver(testInfo);
        }

        private void stop() {
            teardownDriver();
        }

        private WebDriver restart(Path profile) {
            return restartDriver(profile);
        }

        private WebDriver createChromeForTest() {
            return createDriver("chrome", true);
        }

        private Path profile() {
            return getPersistentProfileDir();
        }
    }

    private static final class DriverDouble {
        private final AtomicInteger quitCalls = new AtomicInteger();
        private final RuntimeException configurationFailure;
        private final RuntimeException quitFailure;
        private final WebDriver driver;
        private Duration pageLoadTimeout;
        private Duration implicitWait;

        private DriverDouble() {
            this(null, null);
        }

        private DriverDouble(RuntimeException configurationFailure, RuntimeException quitFailure) {
            this.configurationFailure = configurationFailure;
            this.quitFailure = quitFailure;
            this.driver = proxy(WebDriver.class, (driverProxy, method, args) -> {
                if (method.getName().equals("manage")) {
                    return optionsProxy();
                }
                if (method.getName().equals("quit")) {
                    quitCalls.incrementAndGet();
                    if (this.quitFailure != null) {
                        throw this.quitFailure;
                    }
                    return null;
                }
                return objectMethod(driverProxy, method.getName(), args);
            });
        }

        private WebDriver.Options optionsProxy() {
            return proxy(WebDriver.Options.class, (options, method, args) -> {
                if (method.getName().equals("timeouts")) {
                    return timeoutsProxy();
                }
                if (method.getName().equals("window")) {
                    return windowProxy();
                }
                return objectMethod(options, method.getName(), args);
            });
        }

        private WebDriver.Timeouts timeoutsProxy() {
            return proxy(WebDriver.Timeouts.class, (timeouts, method, args) -> {
                if (method.getName().equals("pageLoadTimeout")) {
                    pageLoadTimeout = (Duration) args[0];
                    failConfigurationIfRequested();
                    return timeouts;
                }
                if (method.getName().equals("implicitlyWait")) {
                    implicitWait = (Duration) args[0];
                    return timeouts;
                }
                return objectMethod(timeouts, method.getName(), args);
            });
        }

        private WebDriver.Window windowProxy() {
            return proxy(WebDriver.Window.class, (window, method, args) ->
                    objectMethod(window, method.getName(), args));
        }

        private void failConfigurationIfRequested() {
            if (configurationFailure != null) {
                throw configurationFailure;
            }
        }
    }

    private static Object objectMethod(Object proxy, String method, Object[] args) {
        return switch (method) {
            case "toString" -> proxy.getClass().getInterfaces()[0].getSimpleName() + " double";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == (args == null ? null : args[0]);
            default -> null;
        };
    }
}

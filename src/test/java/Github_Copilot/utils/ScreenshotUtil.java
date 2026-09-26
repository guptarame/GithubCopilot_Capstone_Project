package Github_Copilot.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() {
    }

    public static Path capture(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot)) {
            LogUtil.warn("Screenshot skipped because the driver does not support screenshots.");
            return null;
        }

        try {
            redactSensitiveFields(driver);
            Path directory = Paths.get("target", "screenshots");
            Files.createDirectories(directory);

            String fileName = sanitize(testName) + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path destination = directory.resolve(fileName);
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination);
            LogUtil.info("Saved screenshot to " + destination.toAbsolutePath());
            return destination;
        } catch (IOException | RuntimeException ex) {
            LogUtil.error("Unable to capture screenshot", ex);
            return null;
        } finally {
            restoreSensitiveFields(driver);
        }
    }

    private static void redactSensitiveFields(WebDriver driver) {
        if (driver instanceof JavascriptExecutor javascriptExecutor) {
            javascriptExecutor.executeScript("""
                    document.querySelectorAll('input[type="password"], input[type="email"], input[name*="user"], input[id*="user"], input[name*="email"], input[id*="email"]')
                        .forEach(function (element) {
                            element.dataset.screenshotRedacted = element.value;
                            element.value = '[REDACTED]';
                        });
                    """);
        }
    }

    private static void restoreSensitiveFields(WebDriver driver) {
        if (driver instanceof JavascriptExecutor javascriptExecutor) {
            javascriptExecutor.executeScript("""
                    document.querySelectorAll('[data-screenshot-redacted]').forEach(function (element) {
                        element.value = element.dataset.screenshotRedacted;
                        delete element.dataset.screenshotRedacted;
                    });
                    """);
        }
    }

    private static String sanitize(String value) {
        return value == null || value.isBlank()
                ? "test"
                : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

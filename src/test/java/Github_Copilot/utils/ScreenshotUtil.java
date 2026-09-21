package Github_Copilot.utils;

import org.openqa.selenium.OutputType;
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
            Path directory = Paths.get("target", "screenshots");
            Files.createDirectories(directory);

            String fileName = sanitize(testName) + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path destination = directory.resolve(fileName);
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination);
            LogUtil.info("Saved screenshot to " + destination.toAbsolutePath());
            return destination;
        } catch (IOException ex) {
            LogUtil.error("Unable to capture screenshot: " + ex.getMessage());
            return null;
        }
    }

    private static String sanitize(String value) {
        return value == null || value.isBlank()
                ? "test"
                : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}


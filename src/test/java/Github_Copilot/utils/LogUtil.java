package Github_Copilot.utils;

import Github_Copilot.config.TestConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class LogUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogUtil() {
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void step(String message) {
        log("STEP", message);
    }

    public static void pass(String message) {
        log("PASS", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Throwable throwable) {
        String detail = throwable == null
                ? "unknown error"
                : throwable.getClass().getSimpleName();
        log("ERROR", message + " [" + detail + "]");
    }

    private static void log(String level, String message) {
        System.out.printf("[%s] [%s] %s%n", LocalDateTime.now().format(FORMATTER), level, redact(message));
    }

    private static String redact(String message) {
        String redacted = message == null ? "" : message;
        for (String secret : List.of(TestConfig.validUsername(), TestConfig.validPassword())) {
            if (secret != null && !secret.isBlank()) {
                redacted = redacted.replace(secret, "[REDACTED]");
            }
        }
        return redacted;
    }
}

package Github_Copilot.config;

import Github_Copilot.data.TestData;

public final class TestConfig {

    private TestConfig() {
    }

    public static String baseUrl() {
        return fromSystemOrEnv("baseUrl", "BASE_URL", "https://askomdch.com/account/");
    }

    public static String browser() {
        return fromSystemOrEnv("browser", "BROWSER", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(fromSystemOrEnv("headless", "HEADLESS", "false"));
    }

    public static int pageLoadTimeout() {
        String value = fromSystemOrEnv("pageLoadTimeout", "PAGE_LOAD_TIMEOUT", String.valueOf(TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS));
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS;
        }
    }

    public static String validUsername() {
        return fromSystemOrEnv("validUsername", "LOGIN_VALID_USERNAME", "");
    }

    public static String validPassword() {
        return fromSystemOrEnv("validPassword", "LOGIN_VALID_PASSWORD", "");
    }

    public static String invalidPassword() {
        return fromSystemOrEnv("invalidPassword", "LOGIN_INVALID_PASSWORD", "invalid-password");
    }

    public static String unknownUsername() {
        return fromSystemOrEnv("unknownUsername", "LOGIN_UNKNOWN_USERNAME", "unknown_user_not_registered@example.com");
    }

    private static String fromSystemOrEnv(String systemKey, String envKey, String defaultValue) {
        String systemValue = System.getProperty(systemKey);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}


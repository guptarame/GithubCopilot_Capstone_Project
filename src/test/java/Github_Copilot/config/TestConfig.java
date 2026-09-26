package Github_Copilot.config;

import Github_Copilot.data.TestData;

import java.net.URI;
import java.net.URISyntaxException;

public final class TestConfig {

    private TestConfig() {
    }

    public static String baseUrl() {
        String url = fromSystemOrEnv("baseUrl", "BASE_URL", "https://askomdch.com/account/");
        validateTransport(url);
        return url;
    }

    public static String browser() {
        return fromSystemOrEnv("browser", "BROWSER", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(fromSystemOrEnv("headless", "HEADLESS", "false"));
    }

    public static int pageLoadTimeout() {
        String value = fromSystemOrEnv("pageLoadTimeout", "PAGE_LOAD_TIMEOUT_SECONDS",
                String.valueOf(TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS));
        try {
            int timeout = Integer.parseInt(value.trim());
            return timeout > 0 && timeout <= 60 ? timeout : TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS;
        } catch (NumberFormatException ignored) {
            return TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS;
        }
    }

    public static String validUsername() {
        return fromSystemOrEnv("validUsername", "LOGIN_VALID_USERNAME", "ramesh7272");
    }

    public static String validPassword() {
        return fromSystemOrEnv("validPassword", "LOGIN_VALID_PASSWORD", "ramesh7272");
    }

    public static String invalidPassword() {
        return fromSystemOrEnv("invalidPassword", "LOGIN_INVALID_PASSWORD", "invalid-password");
    }

    public static String unknownUsername() {
        return fromSystemOrEnv("unknownUsername", "LOGIN_UNKNOWN_USERNAME", "unknown_user_not_registered@example.com");
    }

    public static boolean requireCredentialTests() {
        return Boolean.parseBoolean(fromSystemOrEnv("requireCredentialTests", "REQUIRE_CREDENTIAL_TESTS", "false"));
    }

    public static boolean allowInsecureLocalBaseUrl() {
        return Boolean.parseBoolean(fromSystemOrEnv("allowInsecureLocalBaseUrl", "ALLOW_INSECURE_LOCAL_BASE_URL", "false"));
    }

    public static boolean allowFailureScreenshots() {
        return Boolean.parseBoolean(fromSystemOrEnv("allowFailureScreenshots", "ALLOW_FAILURE_SCREENSHOTS", "false"));
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

    private static void validateTransport(String url) {
        try {
            URI uri = new URI(url);
            if ("https".equalsIgnoreCase(uri.getScheme())) {
                return;
            }

            if (allowInsecureLocalBaseUrl() && "http".equalsIgnoreCase(uri.getScheme()) && isLocalHost(uri.getHost())) {
                return;
            }

            throw new IllegalArgumentException("baseUrl must use HTTPS. HTTP is allowed only for explicit local runs.");
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("baseUrl must be a valid URI.", ex);
        }
    }

    private static boolean isLocalHost(String host) {
        return host != null && (host.equalsIgnoreCase("localhost")
                || host.equals("127.0.0.1")
                || host.equals("::1"));
    }
}

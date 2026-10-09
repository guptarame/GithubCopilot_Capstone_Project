package Github_Copilot.config;

import Github_Copilot.data.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestConfigTests {

    @Test
    void credentialsAreEmptyWhenEnvironmentValuesAreAbsentOrBlank() {
        assertEquals("", TestConfig.environmentCredential(null));
        assertEquals("", TestConfig.environmentCredential("  "));
        assertFalse(TestConfig.credentialsPresent("", "provided"));
        assertFalse(TestConfig.credentialsPresent("provided", ""));
        assertFalse(TestConfig.credentialsPresent(null, null));
    }

    @Test
    void credentialsAreAvailableOnlyWhenBothEnvironmentValuesArePresent() {
        assertEquals("provided", TestConfig.environmentCredential("provided"));
        assertTrue(TestConfig.credentialsPresent("provided", "provided"));
    }

    @Test
    void missingCredentialsFailWhenRequiredAndSkipWhenOptional() {
        assertEquals(TestConfig.CredentialStatus.MISSING_REQUIRED,
                TestConfig.credentialStatus(false, true));
        assertEquals(TestConfig.CredentialStatus.OPTIONAL_SKIP,
                TestConfig.credentialStatus(false, false));
        assertEquals(TestConfig.CredentialStatus.AVAILABLE,
                TestConfig.credentialStatus(true, true));
    }

    @Test
    void nonSecretSettingsUseSystemThenEnvironmentThenDefaultPrecedence() {
        assertEquals("system", TestConfig.resolveSetting("system", "environment", "default"));
        assertEquals("environment", TestConfig.resolveSetting(" ", "environment", "default"));
        assertEquals("default", TestConfig.resolveSetting(null, " ", "default"));
    }

    @Test
    void pageLoadTimeoutAcceptsOnlySafePositiveValues() {
        assertEquals(1, TestConfig.parsePageLoadTimeout("1"));
        assertEquals(60, TestConfig.parsePageLoadTimeout("60"));
        assertEquals(TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS, TestConfig.parsePageLoadTimeout("0"));
        assertEquals(TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS, TestConfig.parsePageLoadTimeout("61"));
        assertEquals(TestData.DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS, TestConfig.parsePageLoadTimeout("not-a-number"));
    }

    @Test
    void baseUrlRequiresHttpsExceptOptedInLocalHttp() {
        TestConfig.validateTransport("https://example.test/account/", false);
        TestConfig.validateTransport("http://localhost:8080/account/", true);

        assertThrows(IllegalArgumentException.class,
                () -> TestConfig.validateTransport("http://example.test/account/", true));
        assertThrows(IllegalArgumentException.class,
                () -> TestConfig.validateTransport("http://localhost:8080/account/", false));
        assertThrows(IllegalArgumentException.class,
            () -> TestConfig.validateTransport("https:relative", false));
        assertThrows(IllegalArgumentException.class,
                () -> TestConfig.validateTransport("not a URI", false));
    }
}
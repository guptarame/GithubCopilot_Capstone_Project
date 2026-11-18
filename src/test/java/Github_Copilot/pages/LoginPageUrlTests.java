package Github_Copilot.pages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageUrlTests {

    @Test
    void acceptsConfiguredAccountHomeWithEquivalentTrailingSlashAndDefaultPort() {
        assertTrue(LoginPage.isDashboardHomeUrl(
                "https://askomdch.com:443/account/", "https://askomdch.com/account"));
    }

    @Test
    void rejectsAccountSubpagesAsDashboardHome() {
        assertFalse(LoginPage.isDashboardHomeUrl(
                "https://askomdch.com/account/orders/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isDashboardHomeUrl(
                "https://askomdch.com/account/edit-account/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isDashboardHomeUrl(
            "https://askomdch.com/account/?orders", "https://askomdch.com/account/"));
    }

    @Test
    void rejectsDashboardHomeFragmentMismatch() {
        assertFalse(LoginPage.isDashboardHomeUrl(
                "https://askomdch.com/account/#orders", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isDashboardHomeUrl(
                "https://askomdch.com/account/#orders", "https://askomdch.com/account/#profile"));
    }

    @Test
    void rejectsDifferentOriginsAndMalformedUrls() {
        assertFalse(LoginPage.isDashboardHomeUrl(
                "https://other.example/account/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isDashboardHomeUrl("not a URL", "https://askomdch.com/account/"));
    }

    @Test
    void loginPageIdentityAllowsOnlyTrailingSlashAndDefaultPortNormalization() {
        assertTrue(LoginPage.isConfiguredPageUrl(
                "https://askomdch.com:443/account/", "https://askomdch.com/account"));
        assertFalse(LoginPage.isConfiguredPageUrl(
                "https://askomdch.com/login/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isConfiguredPageUrl(
                "https://askomdch.com/account/orders/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isConfiguredPageUrl(
                "https://other.example/account/", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isConfiguredPageUrl(
                "https://askomdch.com/account/?redirect=elsewhere", "https://askomdch.com/account/"));
        assertFalse(LoginPage.isConfiguredPageUrl(
                "https://askomdch.com/account/#details", "https://askomdch.com/account/"));
    }
}

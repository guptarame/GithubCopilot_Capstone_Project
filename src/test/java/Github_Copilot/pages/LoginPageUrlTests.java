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
}

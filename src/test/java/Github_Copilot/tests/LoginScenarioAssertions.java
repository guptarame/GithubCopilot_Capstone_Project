package Github_Copilot.tests;

import Github_Copilot.data.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LoginScenarioAssertions {

    private LoginScenarioAssertions() {
    }

    static void assertInvalidPasswordOutcome(String message, boolean loggedIn, boolean loginFormVisible) {
        assertTrue(containsAny(message.toLowerCase(), TestData.INVALID_PASSWORD_MESSAGE_TOKENS),
                "Expected an incorrect-password style error message.");
        assertAll(
                () -> assertFalse(loggedIn, "Invalid login must not authenticate the user."),
                () -> assertTrue(loginFormVisible, "Invalid login should remain on the login page.")
        );
    }

    static void assertCustomerIdentifyingWelcome(String dashboardText, String expectedIdentity) {
        assertTrue(matchesCustomerWelcome(dashboardText, expectedIdentity),
                "Expected the dashboard welcome to identify the configured account.");
    }

        static void assertRememberMeSession(String dashboardText, boolean logoutVisible) {
        assertAll(
            () -> assertTrue(dashboardText != null && !dashboardText.isBlank(),
                "Expected account dashboard content after browser restart."),
            () -> assertTrue(logoutVisible, "Expected a visible logout control after browser restart.")
        );
        }

    static boolean matchesCustomerWelcome(String dashboardText, String expectedIdentity) {
        if (dashboardText == null || expectedIdentity == null || expectedIdentity.isBlank()) {
            return false;
        }

        String normalizedDashboard = dashboardText.strip();
        if (!normalizedDashboard.regionMatches(true, 0, "Hello ", 0, "Hello ".length())) {
            return false;
        }

        String greeting = normalizedDashboard.substring("Hello ".length());
        int identityEnd = greeting.indexOf('(');
        int lineEnd = greeting.indexOf('\n');
        if (lineEnd >= 0 && (identityEnd < 0 || lineEnd < identityEnd)) {
            identityEnd = lineEnd;
        }
        int carriageReturn = greeting.indexOf('\r');
        if (carriageReturn >= 0 && (identityEnd < 0 || carriageReturn < identityEnd)) {
            identityEnd = carriageReturn;
        }
        if (identityEnd >= 0) {
            greeting = greeting.substring(0, identityEnd);
        }

        return greeting.strip().equalsIgnoreCase(expectedIdentity.strip());
    }

    private static boolean containsAny(String text, List<String> tokens) {
        return tokens.stream().anyMatch(text::contains);
    }
}
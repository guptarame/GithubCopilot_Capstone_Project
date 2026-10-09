package Github_Copilot.tests;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.MultipleFailuresError;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginScenarioAssertionsTests {

    @Test
    void invalidPasswordAssertionRejectsMissingOrInsufficientEvidence() {
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", false, true));
        assertThrows(AssertionFailedError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "", false, true));
        assertThrows(MultipleFailuresError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", true, true));
        assertThrows(MultipleFailuresError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", false, false));
    }

    @Test
    void customerWelcomeRequiresTheConfiguredIdentityWithoutExposingItInDiagnostics() {
        String expectedIdentity = "synthetic test identity";

        assertDoesNotThrow(() -> LoginScenarioAssertions.assertCustomerIdentifyingWelcome(
                "Hello SYNTHETIC TEST IDENTITY (not synthetic test identity? Sign out)", expectedIdentity));

        AssertionFailedError genericGreeting = assertThrows(AssertionFailedError.class,
                () -> LoginScenarioAssertions.assertCustomerIdentifyingWelcome("Hello there", expectedIdentity));
        AssertionFailedError mismatchedGreeting = assertThrows(AssertionFailedError.class,
                () -> LoginScenarioAssertions.assertCustomerIdentifyingWelcome("Hello someone else", expectedIdentity));
        assertFalse(genericGreeting.getMessage().contains(expectedIdentity));
        assertFalse(mismatchedGreeting.getMessage().contains(expectedIdentity));
    }

        @Test
        void rememberMeSessionRequiresDashboardContentAndVisibleLogout() {
                assertDoesNotThrow(() -> LoginScenarioAssertions.assertRememberMeSession(
                                "Orders and account details", true));
                assertThrows(MultipleFailuresError.class,
                                () -> LoginScenarioAssertions.assertRememberMeSession("", true));
                assertThrows(MultipleFailuresError.class,
                                () -> LoginScenarioAssertions.assertRememberMeSession("Orders and account details", false));
        }
}
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
                "Incorrect password", false, true, true));
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Error: The password you entered for the username [account] is incorrect", false, true, true));
        assertThrows(AssertionFailedError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "", false, true, true));
        assertThrows(MultipleFailuresError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", true, true, true));
        assertThrows(MultipleFailuresError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", false, false, true));
        assertThrows(MultipleFailuresError.class, () -> LoginScenarioAssertions.assertInvalidPasswordOutcome(
                "Incorrect password", false, true, false));
    }

    @Test
    void unknownUserAssertionAcceptsOnlyAccountSpecificUnregisteredSemantics() {
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertUnknownUserOutcome(
                "Unknown username.", false, true));
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertUnknownUserOutcome(
                "This account does not exist.", false, true));
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertUnknownUserOutcome(
                "Email is not registered.", false, true));

        for (String unrelatedMessage : new String[] {
                "", "Not found.", "Invalid username.", "Incorrect password.",
                "The requested resource was not found.", "The service is temporarily unavailable."
        }) {
            assertThrows(AssertionFailedError.class,
                    () -> LoginScenarioAssertions.assertUnknownUserOutcome(unrelatedMessage, false, true));
        }
    }

    @Test
    void unknownUserAssertionRequiresUnauthenticatedStateAndRetainedFormWithoutEchoingFeedback() {
        assertThrows(MultipleFailuresError.class,
                () -> LoginScenarioAssertions.assertUnknownUserOutcome("Account does not exist.", true, true));
        assertThrows(MultipleFailuresError.class,
                () -> LoginScenarioAssertions.assertUnknownUserOutcome("Account does not exist.", false, false));

        String sensitiveSyntheticFeedback =
                "Account customer.private@example.invalid does not exist.";
        AssertionFailedError failure = assertThrows(AssertionFailedError.class,
                () -> LoginScenarioAssertions.assertUnknownUserOutcome(sensitiveSyntheticFeedback, false, true));
        assertFalse(failure.getMessage().contains("customer.private@example.invalid"));
        assertFalse(failure.getMessage().contains(sensitiveSyntheticFeedback));
    }

    @Test
    void myAccountDestinationAssertionRejectsMissingDashboardSignal() {
        assertDoesNotThrow(() -> LoginScenarioAssertions.assertMyAccountDashboard(true));
        assertThrows(AssertionFailedError.class,
                () -> LoginScenarioAssertions.assertMyAccountDashboard(false));
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
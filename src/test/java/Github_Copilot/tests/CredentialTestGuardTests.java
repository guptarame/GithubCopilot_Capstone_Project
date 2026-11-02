package Github_Copilot.tests;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CredentialTestGuardTests {

    @Test
    void missingRequiredCredentialsFailWithoutExposingValues() {
        assertThrows(AssertionFailedError.class,
                () -> CredentialTestGuard.requireCredentials("", "", true, "valid-login"));
    }

    @Test
    void missingOptionalCredentialsAbortAndPresentCredentialsProceed() {
        assertThrows(TestAbortedException.class,
                () -> CredentialTestGuard.requireCredentials("", "", false, "valid-login"));
        assertDoesNotThrow(() -> CredentialTestGuard.requireCredentials("provided", "provided", true, "valid-login"));
    }

    @Test
    void missingKnownUsernameFailsOrAbortsAccordingToRequirement() {
        assertThrows(AssertionFailedError.class,
                () -> CredentialTestGuard.requireUsername("", true, "invalid-password"));
        assertThrows(TestAbortedException.class,
                () -> CredentialTestGuard.requireUsername("", false, "invalid-password"));
    }

        @Test
        void missingExpectedWelcomeIdentityFailsOrAbortsAccordingToRequirement() {
        AssertionFailedError requiredFailure = assertThrows(AssertionFailedError.class,
            () -> CredentialTestGuard.requireExpectedWelcomeIdentity("", true, "valid-login"));
        assertFalse(requiredFailure.getMessage().contains("identity-value"));
        assertThrows(TestAbortedException.class,
            () -> CredentialTestGuard.requireExpectedWelcomeIdentity("", false, "valid-login"));
        assertDoesNotThrow(() -> CredentialTestGuard.requireExpectedWelcomeIdentity(
            "identity-value", true, "valid-login"));
        }
}
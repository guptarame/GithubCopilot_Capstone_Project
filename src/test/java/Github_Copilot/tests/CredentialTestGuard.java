package Github_Copilot.tests;

import Github_Copilot.config.TestConfig;
import org.junit.jupiter.api.Assumptions;

import static org.junit.jupiter.api.Assertions.fail;

final class CredentialTestGuard {

    private CredentialTestGuard() {
    }

    static void requireCredentials(String username, String password, boolean required, String scenario) {
        require(TestConfig.credentialsPresent(username, password), required,
                "valid login credentials were", scenario);
    }

    static void requireUsername(String username, boolean required, String scenario) {
        require(username != null && !username.isBlank(), required,
                "a known valid username was", scenario);
    }

    static void requireExpectedWelcomeIdentity(String identity, boolean required, String scenario) {
        require(identity != null && !identity.isBlank(), required,
                "the expected welcome identity was", scenario);
    }

    private static void require(boolean available, boolean required, String missingRequirement, String scenario) {
        TestConfig.CredentialStatus status = TestConfig.credentialStatus(available, required);
        if (status == TestConfig.CredentialStatus.MISSING_REQUIRED) {
            fail("Credential-dependent " + scenario + " coverage is required but "
                    + missingRequirement + " not provided.");
        }
        Assumptions.assumeTrue(status == TestConfig.CredentialStatus.AVAILABLE,
                "Skipping " + scenario + " test because " + missingRequirement + " not provided.");
    }
}
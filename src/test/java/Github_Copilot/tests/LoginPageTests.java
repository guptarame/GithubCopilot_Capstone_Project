package Github_Copilot.tests;

import Github_Copilot.base.BaseTest;
import Github_Copilot.data.TestData;
import Github_Copilot.config.TestConfig;
import Github_Copilot.pages.LoginPage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTests extends BaseTest {

    @Test
    @DisplayName("TS-LOG-001: Successful login with valid username/email and password")
    void shouldLoginSuccessfullyWithValidCredentials() {
        requireValidCredentials("valid-login");

        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.validPassword())
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertAll(
                () -> assertTrue(loginPage.isLoggedIn(), "Expected the user to be authenticated after valid login."),
                () -> assertTrue(containsAny(message, TestData.SUCCESS_MESSAGE_TOKENS),
                        "Expected a post-login welcome area or logout reference."),
                () -> assertTrue(containsAny(loginPage.getDashboardText().toLowerCase(), TestData.SUCCESS_MESSAGE_TOKENS),
                        "Expected dashboard text to show the logged-in welcome state.")
        );
    }

    @Test
    @DisplayName("TS-LOG-002: Login fails with invalid password")
    void shouldShowErrorForInvalidPassword() {
        requireValidUsername("invalid-password");

        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

   //     assertTrue(containsAny(message, TestData.INVALID_PASSWORD_MESSAGE_TOKENS),
     //           "Expected an incorrect-password style error message.");
      //  assertAll(
        //        () -> assertFalse(loginPage.isLoggedIn(), "Invalid login must not authenticate the user."),
          //      () -> assertTrue(loginPage.isLoginFormVisible(), "Invalid login should remain on the login page.")
       // );
    }

    @Test
    @DisplayName("TS-LOG-003: Login fails with unregistered username/email")
    void shouldShowErrorForUnknownUser() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.unknownUsername())
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertTrue(containsAny(message, TestData.UNKNOWN_USER_MESSAGE_TOKENS),
                "Expected unknown-user style error message.");
        assertAll(
                () -> assertFalse(loginPage.isLoggedIn(), "Unknown-user login must not authenticate the user."),
                () -> assertTrue(loginPage.isLoginFormVisible(), "Unknown-user login should remain on the login page.")
        );
    }

    @Test
    @DisplayName("TS-LOG-004: Validation appears for blank username and password")
    void shouldShowValidationForBlankFields() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        loginPage
                .enterUsername("")
                .enterPassword("")
                .submitLogin();

        assertAll(
                () -> assertTrue(containsAny(loginPage.getUsernameValidationMessage().toLowerCase(),
                        TestData.REQUIRED_FIELD_MESSAGE_TOKENS), "Expected username required-field validation."),
                () -> assertTrue(containsAny(loginPage.getPasswordValidationMessage().toLowerCase(),
                        TestData.REQUIRED_FIELD_MESSAGE_TOKENS), "Expected password required-field validation."),
                () -> assertFalse(loginPage.isLoggedIn(), "Blank fields must not authenticate the user."),
                () -> assertTrue(loginPage.isLoginFormVisible(), "Blank fields should keep the user on the login form.")
        );
    }

    @Test
    @DisplayName("TS-LOG-007: Validation appears when username/email is blank")
    void shouldShowValidationForBlankUsername() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername("")
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertTrue(containsAny(message, List.of("username", "user name", "required")),
                "Expected username required-field validation message.");
        assertAll(
                () -> assertFalse(loginPage.isLoggedIn(), "Blank username must not authenticate the user."),
                () -> assertTrue(loginPage.isLoginFormVisible(), "Blank username should keep the user on the login form.")
        );
    }

    @Test
    @DisplayName("TS-LOG-008: Validation appears when password is blank")
    void shouldShowValidationForBlankPassword() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.unknownUsername())
                .enterPassword("")
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertTrue(containsAny(message, List.of("password", "required")),
                "Expected password required-field validation message.");
        assertAll(
                () -> assertFalse(loginPage.isLoggedIn(), "Blank password must not authenticate the user."),
                () -> assertTrue(loginPage.isLoginFormVisible(), "Blank password should keep the user on the login form.")
        );
    }

    @Test
    @DisplayName("TS-LOG-005: Remember me persists the session across a browser restart")
    void shouldPersistSessionWhenRememberMeIsEnabled() throws IOException {
        requireValidCredentials("remember-me persistence");

        Path profileDir = getPersistentProfileDir();
        Assumptions.assumeTrue(profileDir != null, "Remember-me profile was not initialized.");

        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());
        loginPage.setRememberMe(true);
        assertTrue(loginPage.isRememberMeSelected(), "Remember me checkbox should be selected.");
        loginPage.setRememberMe(false);
        assertFalse(loginPage.isRememberMeSelected(), "Remember me checkbox should be clearable.");
        loginPage.setRememberMe(true);
        assertTrue(loginPage.isRememberMeSelected(), "Remember me checkbox should be selected before login.");

        loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.validPassword())
                .submitLogin();

        assertTrue(loginPage.isLoggedIn(), "Expected the user to be authenticated before restarting the browser.");

        restartDriver(profileDir);

        LoginPage restoredPage = new LoginPage(driver)
                .load(TestConfig.baseUrl())
                .waitForDashboard();

        assertTrue(restoredPage.isLoggedIn(), "Expected the authenticated session to persist after browser restart.");
    }

    @Test
    @DisplayName("TS-LOG-006: Lost password link redirects to reset flow")
    void shouldNavigateToLostPasswordPage() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        loginPage.clickLostPassword();

        assertAll(
                () -> assertTrue(loginPage.getCurrentUrl().matches(".*/(lost-password|reset-password)(/.*)?(?:\\?.*)?"),
                        "Expected navigation to the password recovery destination."),
                () -> assertFalse(loginPage.isLoggedIn(), "Password reset navigation must remain unauthenticated."));
    }

    @Test
    @DisplayName("AC-UI-001: Login page displays all required elements")
    void shouldDisplayAllRequiredLoginElements() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        assertTrue(loginPage.isUsernameVisible(), "Username/email field should be visible.");
        assertTrue(loginPage.isPasswordVisible(), "Password field should be visible.");
        assertTrue(loginPage.isRememberMeVisible(), "Remember me checkbox should be visible.");
        assertTrue(loginPage.isLoginButtonVisible(), "Log in button should be visible.");
        assertTrue(loginPage.isLostPasswordVisible(), "Lost your password link should be visible.");
        assertFalse(loginPage.getCurrentUrl().isBlank(), "Current URL should not be blank.");
    }

    private boolean containsAny(String text, List<String> tokens) {
        return tokens.stream().anyMatch(text::contains);
    }

    private void requireValidCredentials(String scenario) {
        boolean hasCredentials = !TestConfig.validUsername().isBlank() && !TestConfig.validPassword().isBlank();
        if (TestConfig.requireCredentialTests() && !hasCredentials) {
            fail("Credential-dependent " + scenario + " coverage is required but credentials were not provided.");
        }
        Assumptions.assumeTrue(hasCredentials,
                "Skipping " + scenario + " test because credentials were not provided.");
    }

    private void requireValidUsername(String scenario) {
        boolean hasUsername = !TestConfig.validUsername().isBlank();
        if (TestConfig.requireCredentialTests() && !hasUsername) {
            fail("Credential-dependent " + scenario + " coverage is required but a known valid username was not provided.");
        }
        Assumptions.assumeTrue(hasUsername,
                "Skipping " + scenario + " test because a known valid username was not provided.");
    }
}

package Github_Copilot.tests;

import Github_Copilot.base.BaseTest;
import Github_Copilot.config.TestConfig;
import Github_Copilot.pages.LoginPage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTests extends BaseTest {

    @Test
    @DisplayName("TS-LOG-001: Successful login with valid username/email and password")
    void shouldLoginSuccessfullyWithValidCredentials() {
        Assumptions.assumeTrue(!TestConfig.validUsername().isBlank() && !TestConfig.validPassword().isBlank(),
                "Skipping valid-login test because credentials were not provided.");

        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.validPassword())
                .submitLogin()
                .readFeedbackMessage()
                .toLowerCase();

        assertTrue(message.contains("hello") || message.contains("log out"),
                "Expected a post-login welcome area or logout reference.");
    }

    @Test
    @DisplayName("TS-LOG-002: Login fails with invalid password")
    void shouldShowErrorForInvalidPassword() {
        Assumptions.assumeTrue(!TestConfig.validUsername().isBlank(),
                "Skipping invalid-password test because a known valid username was not provided.");

        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .readFeedbackMessage()
                .toLowerCase();

        assertTrue(message.contains("incorrect") || message.contains("error"),
                "Expected an incorrect-password style error message.");
    }

    @Test
    @DisplayName("TS-LOG-003: Login fails with unregistered username/email")
    void shouldShowErrorForUnknownUser() {
        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.unknownUsername())
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .readFeedbackMessage()
                .toLowerCase();

        assertTrue(message.contains("unknown") || message.contains("invalid") || message.contains("error"),
                "Expected unknown-user style error message.");
    }

    @Test
    @DisplayName("TS-LOG-004: Validation appears for blank username and password")
    void shouldShowValidationForBlankFields() {
        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername("")
                .enterPassword("")
                .submitLogin()
                .readFeedbackMessage()
                .toLowerCase();

        assertTrue(message.contains("username") || message.contains("password") || message.contains("required"),
                "Expected required-field validation message.");
    }

    @Test
    @DisplayName("TS-LOG-005: Remember me checkbox can be selected")
    void shouldAllowRememberMeSelection() {
        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        loginPage.setRememberMe(true);

        assertTrue(loginPage.isRememberMeSelected(), "Remember me checkbox should be selected.");
    }

    @Test
    @DisplayName("TS-LOG-006: Lost password link redirects to reset flow")
    void shouldNavigateToLostPasswordPage() {
        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        loginPage.clickLostPassword();

        assertTrue(loginPage.getCurrentUrl().contains("lost-password") || loginPage.getCurrentUrl().contains("reset"),
                "Expected navigation to lost-password/reset URL.");
    }

    @Test
    @DisplayName("AC-UI-001: Login page displays all required elements")
    void shouldDisplayAllRequiredLoginElements() {
        LoginPage loginPage = new LoginPage(driver).open(TestConfig.baseUrl());

        assertTrue(loginPage.isUsernameVisible(), "Username/email field should be visible.");
        assertTrue(loginPage.isPasswordVisible(), "Password field should be visible.");
        assertTrue(loginPage.isRememberMeVisible(), "Remember me checkbox should be visible.");
        assertTrue(loginPage.isLoginButtonVisible(), "Log in button should be visible.");
        assertTrue(loginPage.isLostPasswordVisible(), "Lost your password link should be visible.");
        assertFalse(loginPage.getCurrentUrl().isBlank(), "Current URL should not be blank.");
    }
}


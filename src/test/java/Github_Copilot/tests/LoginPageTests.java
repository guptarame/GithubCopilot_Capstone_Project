package Github_Copilot.tests;

import Github_Copilot.base.BaseTest;
import Github_Copilot.data.TestData;
import Github_Copilot.config.TestConfig;
import Github_Copilot.listeners.TestLifecycleListener;
import Github_Copilot.pages.LoginPage;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTests extends BaseTest {

    @Test
    @DisplayName("TS-LOG-001: Successful login with valid username/email and password")
    void shouldLoginSuccessfullyWithValidCredentials() {
        Assumptions.assumeTrue(!TestConfig.validUsername().isBlank() && !TestConfig.validPassword().isBlank(),
                "Skipping valid-login test because credentials were not provided.");

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
        Assumptions.assumeTrue(!TestConfig.validUsername().isBlank(),
                "Skipping invalid-password test because a known valid username was not provided.");

        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.invalidPassword())
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertTrue(containsAny(message, TestData.ERROR_MESSAGE_TOKENS),
                "Expected an incorrect-password style error message.");
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

        assertTrue(containsAny(message, List.of("unknown", "invalid", "error", "not")),
                "Expected unknown-user style error message.");
    }

    @Test
    @DisplayName("TS-LOG-004: Validation appears for blank username and password")
    void shouldShowValidationForBlankFields() {
        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());

        String message = loginPage
                .enterUsername("")
                .enterPassword("")
                .submitLogin()
                .getFeedbackMessage()
                .toLowerCase();

        assertTrue(containsAny(message, List.of("username", "password", "required", "error")),
                "Expected required-field validation message.");
    }

    @Test
    @DisplayName("TS-LOG-005: Remember me persists the session across a browser restart")
    void shouldPersistSessionWhenRememberMeIsEnabled() throws IOException {
        Assumptions.assumeTrue("chrome".equals(TestConfig.browser()),
                "Skipping remember-me persistence test because it requires Chrome with a persistent profile.");
        Assumptions.assumeTrue(!TestConfig.validUsername().isBlank() && !TestConfig.validPassword().isBlank(),
                "Skipping remember-me persistence test because credentials were not provided.");

        Path profileDir = Files.createTempDirectory("remember-me-profile");

        LoginPage loginPage = new LoginPage(driver).openPage(TestConfig.baseUrl());
        loginPage.setRememberMe(true);
        assertTrue(loginPage.isRememberMeSelected(), "Remember me checkbox should be selected.");

        loginPage
                .enterUsername(TestConfig.validUsername())
                .enterPassword(TestConfig.validPassword())
                .submitLogin();

        assertTrue(loginPage.isLoggedIn(), "Expected the user to be authenticated before restarting the browser.");

        driver.quit();

        driver = createChromeDriver(TestConfig.headless(), profileDir);
        TestLifecycleListener.registerDriver(driver);

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

        assertTrue(loginPage.getCurrentUrl().contains("lost-password") || loginPage.getCurrentUrl().contains("reset"),
                "Expected navigation to lost-password/reset URL.");
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
}


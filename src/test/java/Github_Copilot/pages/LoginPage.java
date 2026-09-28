package Github_Copilot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;

public class LoginPage extends BasePage {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By rememberMeCheckbox = By.id("rememberme");
    private final By loginButton = By.name("login");
    private final By lostPasswordLink = By.linkText("Lost your password?");
    private final By dashboardContent = By.cssSelector("div.woocommerce-MyAccount-content");
    private final By errorBanner = By.cssSelector("ul.woocommerce-error, div.woocommerce-notices-wrapper .woocommerce-error");
    private final By logoutLink = By.linkText("Log out");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage openPage(String url) {
        super.navigateTo(url);
        waitForVisible(usernameField);
        return this;
    }

    public LoginPage load(String url) {
        super.navigateTo(url);
        return this;
    }

    public boolean isUsernameVisible() {
        return isVisible(usernameField);
    }

    public boolean isPasswordVisible() {
        return isVisible(passwordField);
    }

    public boolean isRememberMeVisible() {
        return isVisible(rememberMeCheckbox);
    }

    public boolean isLoginButtonVisible() {
        return isVisible(loginButton);
    }

    public boolean isLostPasswordVisible() {
        return isVisible(lostPasswordLink);
    }

    public boolean isLoginFormVisible() {
        return isUsernameVisible() && isPasswordVisible() && isLoginButtonVisible();
    }

    public boolean isUnauthenticated() {
        return !isLoggedIn();
    }

    public LoginPage enterUsername(String value) {
        type(usernameField, value);
        return this;
    }

    public LoginPage enterPassword(String value) {
        type(passwordField, value);
        return this;
    }

    public void setRememberMe(boolean enabled) {
        if (driver.findElement(rememberMeCheckbox).isSelected() != enabled) {
            click(rememberMeCheckbox);
        }
    }

    public boolean isRememberMeSelected() {
        return driver.findElement(rememberMeCheckbox).isSelected();
    }

    public LoginPage submitLogin() {
        click(loginButton);
        waitForSubmissionOutcome();
        return this;
    }

    public String getCurrentUrl() {
        return currentUrl();
    }

    public String getFeedbackMessage() {
        String error = textOfFirstVisible(errorBanner);
        if (!error.isBlank()) {
            return error;
        }

        if (anyVisible(dashboardContent)) {
            return textOf(dashboardContent);
        }

        return getValidationMessage();
    }

    public String readFeedbackMessage() {
        return getFeedbackMessage();
    }

    public String getValidationMessage() {
        String usernameValidation = validationMessage(usernameField);
        return !usernameValidation.isBlank() ? usernameValidation : validationMessage(passwordField);
    }

    public String getUsernameValidationMessage() {
        return validationMessage(usernameField);
    }

    public String getPasswordValidationMessage() {
        return validationMessage(passwordField);
    }

    public boolean isLoggedIn() {
        String dashboardText = anyVisible(dashboardContent) ? textOf(dashboardContent) : "";
        String lowerText = dashboardText.toLowerCase();
        return anyVisible(logoutLink) || lowerText.contains("hello") || lowerText.contains("log out");
    }

    public String getDashboardText() {
        return anyVisible(dashboardContent) ? textOf(dashboardContent) : "";
    }

    public LoginPage waitForDashboard() {
        waitForCondition(webDriver -> isLoggedIn());
        return this;
    }

    public LoginPage waitForLoginForm() {
        waitForCondition(webDriver -> isLoginFormVisible());
        return this;
    }

    public void clickLostPassword() {
        click(lostPasswordLink);
        waitForCondition(webDriver -> isPasswordRecoveryUrl(webDriver.getCurrentUrl()));
    }

    private void waitForSubmissionOutcome() {
        wait.until(ExpectedConditions.refreshed(webDriver -> anyVisible(dashboardContent)
                || anyVisible(logoutLink)
                || hasScopedError()
                || hasNativeValidationMessage()));
    }

    private boolean hasScopedError() {
        return !textOfFirstVisible(errorBanner).isBlank();
    }

    private boolean isPasswordRecoveryUrl(String url) {
        try {
            String path = URI.create(url).getPath().toLowerCase();
            return path.matches(".*/(lost-password|reset-password)(/.*)?");
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private boolean hasNativeValidationMessage() {
        triggerNativeValidation(usernameField);
        triggerNativeValidation(passwordField);
        return !getValidationMessage().isBlank();
    }

    private String validationMessage(By locator) {
        String validationMessage = driver.findElement(locator).getAttribute("validationMessage");
        if (validationMessage != null && !validationMessage.isBlank()) {
            return validationMessage;
        }
        return hasScopedError() ? textOfFirstVisible(errorBanner) : "";
    }

    private void triggerNativeValidation(By locator) {
        if (driver instanceof JavascriptExecutor javascriptExecutor) {
            javascriptExecutor.executeScript("arguments[0].reportValidity();", driver.findElement(locator));
        }
    }
}

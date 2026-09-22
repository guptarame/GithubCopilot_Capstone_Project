package Github_Copilot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By rememberMeCheckbox = By.id("rememberme");
    private final By loginButton = By.name("login");
    private final By lostPasswordLink = By.linkText("Lost your password?");
    private final By dashboardContent = By.cssSelector("div.woocommerce-MyAccount-content");
    private final By errorBanner = By.cssSelector("ul.woocommerce-error, div.woocommerce-notices-wrapper");
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
        if (anyVisible(errorBanner)) {
            return textOfFirstVisible(errorBanner);
        }

        if (anyVisible(dashboardContent)) {
            return textOf(dashboardContent);
        }

        return "";
    }

    public String readFeedbackMessage() {
        return getFeedbackMessage();
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
        wait.until(webDriver -> isLoggedIn());
        return this;
    }

    public void clickLostPassword() {
        click(lostPasswordLink);
        waitForUrlContaining("lost-password", "reset");
    }

    private void waitForSubmissionOutcome() {
        wait.until(webDriver -> anyVisible(errorBanner)
                || anyVisible(dashboardContent)
                || anyVisible(logoutLink)
                || hasNativeValidationMessage());
    }

    private boolean hasNativeValidationMessage() {
        return hasValidationMessage(usernameField) || hasValidationMessage(passwordField);
    }

    private boolean hasValidationMessage(By locator) {
        String validationMessage = driver.findElement(locator).getAttribute("validationMessage");
        return validationMessage != null && !validationMessage.isBlank();
    }
}

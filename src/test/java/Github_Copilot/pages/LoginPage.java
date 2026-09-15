package Github_Copilot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By rememberMeCheckbox = By.id("rememberme");
    private final By loginButton = By.name("login");
    private final By lostPasswordLink = By.linkText("Lost your password?");
    private final By messageBanner = By.cssSelector("ul.woocommerce-error, div.woocommerce-MyAccount-content, div.woocommerce-notices-wrapper");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public LoginPage open(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        return this;
    }

    public boolean isUsernameVisible() {
        return driver.findElement(usernameField).isDisplayed();
    }

    public boolean isPasswordVisible() {
        return driver.findElement(passwordField).isDisplayed();
    }

    public boolean isRememberMeVisible() {
        return driver.findElement(rememberMeCheckbox).isDisplayed();
    }

    public boolean isLoginButtonVisible() {
        return driver.findElement(loginButton).isDisplayed();
    }

    public boolean isLostPasswordVisible() {
        return driver.findElement(lostPasswordLink).isDisplayed();
    }

    public LoginPage enterUsername(String value) {
        WebElement username = driver.findElement(usernameField);
        username.clear();
        username.sendKeys(value);
        return this;
    }

    public LoginPage enterPassword(String value) {
        WebElement password = driver.findElement(passwordField);
        password.clear();
        password.sendKeys(value);
        return this;
    }

    public LoginPage setRememberMe(boolean enabled) {
        WebElement checkbox = driver.findElement(rememberMeCheckbox);
        if (checkbox.isSelected() != enabled) {
            checkbox.click();
        }
        return this;
    }

    public boolean isRememberMeSelected() {
        return driver.findElement(rememberMeCheckbox).isSelected();
    }

    public LoginPage submitLogin() {
        driver.findElement(loginButton).click();
        return this;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String readFeedbackMessage() {
        wait.until(anyLoginResultVisible());
        return driver.findElements(messageBanner)
                .stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse("");
    }

    public LoginPage clickLostPassword() {
        driver.findElement(lostPasswordLink).click();
        return this;
    }

    private ExpectedCondition<Boolean> anyLoginResultVisible() {
        return webDriver -> {
            if (webDriver == null) {
                return false;
            }

            try {
                return webDriver.findElements(messageBanner)
                        .stream()
                        .anyMatch(element -> {
                            try {
                                return element.isDisplayed() && !element.getText().isBlank();
                            } catch (StaleElementReferenceException ignored) {
                                return false;
                            }
                        });
            } catch (StaleElementReferenceException ignored) {
                return false;
            }
        };
    }
}



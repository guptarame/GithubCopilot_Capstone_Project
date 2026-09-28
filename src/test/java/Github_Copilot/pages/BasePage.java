package Github_Copilot.pages;

import Github_Copilot.data.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestData.DEFAULT_WAIT_TIMEOUT_SECONDS));
    }

    protected void navigateTo(String url) {
        driver.get(url);
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(By locator, String value) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected boolean isVisible(By locator) {
        try {
            return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
        } catch (StaleElementReferenceException ignored) {
            return false;
        }
    }

    protected String textOf(By locator) {
        return waitForVisible(locator).getText();
    }

    protected String textOfFirstVisible(By... locators) {
        return Arrays.stream(locators)
                .flatMap(locator -> driver.findElements(locator).stream())
                .map(element -> {
                    try {
                        return element.isDisplayed() ? element.getText() : "";
                    } catch (StaleElementReferenceException ignored) {
                        return "";
                    }
                })
                .filter(text -> !text.isBlank())
                .findFirst()
                .orElse("");
    }

    protected boolean anyVisible(By... locators) {
        return Arrays.stream(locators).anyMatch(this::isVisible);
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected boolean waitForCondition(java.util.function.Function<WebDriver, Boolean> condition) {
        try {
            wait.until(condition);
            return true;
        } catch (TimeoutException ignored) {
            return false;
        }
    }

    protected void waitForUrlContaining(String... urlParts) {
        wait.until(webDriver -> Arrays.stream(urlParts)
                .anyMatch(part -> webDriver.getCurrentUrl().contains(part)));
    }

    protected void waitForUrlMatching(String regex) {
        wait.until(webDriver -> webDriver.getCurrentUrl().matches(regex));
    }
}

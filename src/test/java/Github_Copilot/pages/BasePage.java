package Github_Copilot.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
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
        return !driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed();
    }

    protected String textOf(By locator) {
        return waitForVisible(locator).getText();
    }

    protected String textOfFirstVisible(By... locators) {
        return Arrays.stream(locators)
                .filter(this::isVisible)
                .map(locator -> driver.findElement(locator).getText())
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
}



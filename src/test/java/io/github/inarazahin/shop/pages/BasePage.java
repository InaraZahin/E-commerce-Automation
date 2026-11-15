package io.github.inarazahin.shop.pages;

import io.github.inarazahin.shop.config.Config;
import io.github.inarazahin.shop.pages.components.Header;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Shared behaviour for page objects.
 *
 * <p>Conventions: every interaction goes through an explicit wait (no {@code Thread.sleep},
 * no implicit wait); locators use stable ids, names and data attributes, never list indexes or
 * layout-dependent XPaths; and page objects never assert - tests do.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = waitFor(driver);
    }

    /**
     * A wait that also retries on stale elements: after a form post or an AJAX refresh the page
     * replaces nodes, and a condition should simply be re-evaluated against the new DOM.
     */
    public static WebDriverWait waitFor(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Config.timeout());
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }

    public Header header() {
        return new Header(driver);
    }

    protected void navigateTo(String path) {
        driver.get(Config.baseUrl() + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Types into a field and confirms the value stuck (retrying if the node was re-rendered). */
    protected void type(By locator, String text) {
        wait.until(d -> {
            WebElement field = d.findElement(locator);
            if (!field.isDisplayed()) {
                return false;
            }
            field.clear();
            field.sendKeys(text);
            return text.equals(field.getDomProperty("value"));
        });
    }

    /**
     * Replaces a field's value by selecting its text and typing over it. Needed for spinner
     * inputs whose script immediately resets an emptied field, so clear() + type appends.
     */
    protected void overwrite(WebElement field, String text) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); arguments[0].select();", field);
        field.sendKeys(text);
    }

    protected void check(By locator) {
        WebElement box = driver.findElement(locator);
        if (!box.isSelected()) {
            // Theme-styled checkboxes hide the native input behind a custom widget.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", box);
        }
    }

    protected void selectByText(By locator, String text) {
        new Select(visible(locator)).selectByVisibleText(text);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    /** HTML5 constraint-validation message of a form field ("" when the field is valid). */
    public String validationMessage(By field) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", driver.findElement(field));
    }

    /** Parses a displayed price such as "€1,234.50" or "$19.12" into an exact amount. */
    protected static BigDecimal money(String displayed) {
        String digits = displayed.replaceAll("[^0-9.,]", "").replace(",", "");
        return new BigDecimal(digits);
    }
}

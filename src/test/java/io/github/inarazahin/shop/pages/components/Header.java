package io.github.inarazahin.shop.pages.components;

import io.github.inarazahin.shop.pages.SearchResultsPage;
import io.github.inarazahin.shop.pages.BasePage;
import io.qameta.allure.Step;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The storefront header: account links, cart summary and the search box. */
public class Header {

    private static final By USER_INFO = By.id("_desktop_user_info");
    private static final By SIGN_IN = By.cssSelector("#_desktop_user_info .user-info > a:not(.account):not(.logout)");
    private static final By ACCOUNT_NAME = By.cssSelector("#_desktop_user_info a.account");
    private static final By SIGN_OUT = By.cssSelector("#_desktop_user_info a.logout");
    private static final By CART = By.cssSelector("#_desktop_cart .blockcart");
    private static final By SEARCH = By.cssSelector("#search_widget input[name='s']");
    private static final Pattern CART_COUNT = Pattern.compile("\\((\\d+)\\)");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Header(WebDriver driver) {
        this.driver = driver;
        this.wait = BasePage.waitFor(driver);
    }

    public boolean isSignedIn() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(USER_INFO));
        return !driver.findElements(SIGN_OUT).isEmpty();
    }

    public String accountName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_NAME)).getText().trim();
    }

    @Step("Sign out")
    public void signOut() {
        wait.until(ExpectedConditions.elementToBeClickable(SIGN_OUT)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGN_IN));
    }

    /** Number of items shown on the header cart badge, e.g. "Cart (2)". */
    public int cartCount() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(CART)).getText();
        Matcher matcher = CART_COUNT.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    /** Waits until the header cart badge shows {@code expected} items (it updates via AJAX). */
    public void waitForCartCount(int expected) {
        wait.until(d -> cartCount() == expected);
    }

    @Step("Search for \"{term}\"")
    public SearchResultsPage search(String term) {
        var box = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH));
        box.clear();
        box.sendKeys(term, Keys.ENTER);
        return new SearchResultsPage(driver).waitUntilLoaded();
    }
}

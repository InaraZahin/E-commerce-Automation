package io.github.inarazahin.shop.pages;

import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Pages under "My account" that the suite uses. */
public final class AccountPages {

    private AccountPages() {
    }

    /** "Your personal information" - name, email and password changes. */
    public static class IdentityPage extends BasePage {

        public static final By FIRST_NAME = By.cssSelector("#customer-form #field-firstname");
        private static final By CURRENT_PASSWORD = By.cssSelector("#customer-form #field-password");
        private static final By NEW_PASSWORD = By.cssSelector("#customer-form #field-new_password");
        private static final By TERMS = By.cssSelector("#customer-form input[name='psgdpr']");
        private static final By PRIVACY = By.cssSelector("#customer-form input[name='customer_privacy']");
        private static final By SAVE = By.cssSelector("#customer-form button[type='submit']");
        private static final By SUCCESS = By.cssSelector("#notifications .alert-success");
        private static final By ERROR = By.cssSelector("#notifications .alert-danger, #customer-form .has-error .help-block");

        public IdentityPage(WebDriver driver) {
            super(driver);
        }

        @Step("Open personal information")
        public IdentityPage open() {
            navigateTo("/identity");
            visible(FIRST_NAME);
            return this;
        }

        public IdentityPage changeFirstName(String firstName) {
            type(FIRST_NAME, firstName);
            return this;
        }

        public IdentityPage changePassword(String newPassword) {
            type(NEW_PASSWORD, newPassword);
            return this;
        }

        /** Every change must be confirmed with the current password and the consents. */
        @Step("Save personal information")
        public void save(String currentPassword) {
            type(CURRENT_PASSWORD, currentPassword);
            check(TERMS);
            check(PRIVACY);
            click(SAVE);
            wait.until(d -> isDisplayed(SUCCESS) || isDisplayed(ERROR));
        }

        public String successMessage() {
            return text(SUCCESS);
        }

        public String errorMessage() {
            return text(ERROR);
        }

        public String firstName() {
            return visible(FIRST_NAME).getDomProperty("value");
        }
    }

    /** "Order history". */
    public static class OrderHistoryPage extends BasePage {

        private static final By MAIN = By.id("main");
        private static final By ORDER_ROWS = By.cssSelector("#content table tbody tr, #content .orders .order");

        public OrderHistoryPage(WebDriver driver) {
            super(driver);
        }

        @Step("Open order history")
        public OrderHistoryPage open() {
            navigateTo("/order-history");
            visible(MAIN);
            return this;
        }

        public List<String> orders() {
            return driver.findElements(ORDER_ROWS).stream().filter(WebElement::isDisplayed)
                    .map(WebElement::getText).toList();
        }

        public String pageText() {
            return text(MAIN);
        }
    }
}

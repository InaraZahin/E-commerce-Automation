package io.github.inarazahin.shop.pages;

import io.github.inarazahin.shop.data.Customer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage extends BasePage {

    public static final By FIRST_NAME = By.cssSelector("#customer-form #field-firstname");
    public static final By LAST_NAME = By.cssSelector("#customer-form #field-lastname");
    public static final By EMAIL = By.cssSelector("#customer-form #field-email");
    public static final By PASSWORD = By.cssSelector("#customer-form #field-password");
    public static final By TERMS_CONSENT = By.cssSelector("#customer-form input[name='psgdpr']");
    public static final By PRIVACY_CONSENT = By.cssSelector("#customer-form input[name='customer_privacy']");
    private static final By SUBMIT = By.cssSelector("#customer-form [data-link-action='save-customer']");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the registration page")
    public RegistrationPage open() {
        navigateTo("/registration");
        return waitUntilLoaded();
    }

    RegistrationPage waitUntilLoaded() {
        visible(FIRST_NAME);
        return this;
    }

    @Step("Fill in the registration form for {customer.email}")
    public RegistrationPage fill(Customer customer) {
        type(FIRST_NAME, customer.firstName());
        type(LAST_NAME, customer.lastName());
        type(EMAIL, customer.email());
        type(PASSWORD, customer.password());
        return this;
    }

    @Step("Accept the required consents")
    public RegistrationPage acceptConsents() {
        check(TERMS_CONSENT);
        check(PRIVACY_CONSENT);
        return this;
    }

    @Step("Submit the registration form")
    public void submit() {
        click(SUBMIT);
    }

    /** Fills, consents and submits; waits until the new customer is signed in. */
    public HomePage register(Customer customer) {
        fill(customer).acceptConsents().submit();
        wait.until(d -> header().isSignedIn());
        return new HomePage(driver);
    }

    /** Server-side validation message shown under a field (waits for it to appear). */
    public String fieldError(By field) {
        WebElement group = wait.until(d -> {
            WebElement input = d.findElement(field);
            WebElement formGroup = input.findElement(By.xpath("ancestor::div[contains(@class,'form-group')]"));
            return formGroup.getAttribute("class").contains("has-error") ? formGroup : null;
        });
        return group.findElement(By.cssSelector(".help-block")).getText().trim();
    }

    public boolean isDisplayed() {
        return isDisplayed(FIRST_NAME) && driver.getCurrentUrl().contains("/registration");
    }
}

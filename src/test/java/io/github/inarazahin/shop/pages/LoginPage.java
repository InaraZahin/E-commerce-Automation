package io.github.inarazahin.shop.pages;

import io.github.inarazahin.shop.data.Customer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public static final By EMAIL = By.cssSelector("#login-form #field-email");
    public static final By PASSWORD = By.cssSelector("#login-form #field-password");
    private static final By SUBMIT = By.id("submit-login");
    private static final By ERROR = By.cssSelector("#content .alert-danger");
    private static final By FORGOT_PASSWORD = By.cssSelector("#login-form a[href*='password-recovery']");
    private static final By CREATE_ACCOUNT = By.cssSelector("#content a[href*='registration']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the sign-in page")
    public LoginPage open() {
        navigateTo("/login");
        visible(EMAIL);
        return this;
    }

    @Step("Sign in as {email}")
    public void submit(String email, String password) {
        type(EMAIL, email);
        type(PASSWORD, password);
        click(SUBMIT);
    }

    /** Signs in and waits until the storefront shows the customer as signed in. */
    public HomePage signInAs(Customer customer) {
        submit(customer.email(), customer.password());
        wait.until(d -> header().isSignedIn());
        return new HomePage(driver);
    }

    public String errorMessage() {
        return text(ERROR);
    }

    public boolean isDisplayed() {
        return isDisplayed(EMAIL) && driver.getCurrentUrl().contains("/login");
    }

    @Step("Open 'Forgot your password?'")
    public PasswordRecoveryPage forgotPassword() {
        click(FORGOT_PASSWORD);
        return new PasswordRecoveryPage(driver).waitUntilLoaded();
    }

    @Step("Open 'Create account'")
    public RegistrationPage createAccount() {
        click(CREATE_ACCOUNT);
        return new RegistrationPage(driver).waitUntilLoaded();
    }
}

package io.github.inarazahin.shop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PasswordRecoveryPage extends BasePage {

    private static final By EMAIL = By.cssSelector("#main input#email");
    private static final By SEND = By.id("send-reset-link");
    private static final By SUCCESS = By.cssSelector("#main .ps-alert-success");

    public PasswordRecoveryPage(WebDriver driver) {
        super(driver);
    }

    PasswordRecoveryPage waitUntilLoaded() {
        visible(EMAIL);
        return this;
    }

    @Step("Request a password reset for {email}")
    public void requestReset(String email) {
        type(EMAIL, email);
        click(SEND);
        visible(SUCCESS);
    }

    /** The confirmation shown after a reset request. */
    public String confirmation() {
        return text(SUCCESS);
    }
}

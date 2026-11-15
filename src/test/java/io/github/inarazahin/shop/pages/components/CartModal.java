package io.github.inarazahin.shop.pages.components;

import io.github.inarazahin.shop.pages.CartPage;
import io.github.inarazahin.shop.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** The "Product successfully added to your shopping cart" dialog. */
public class CartModal {

    private static final By MODAL = By.id("blockcart-modal");
    private static final By TITLE = By.cssSelector("#blockcart-modal .modal-title");
    private static final By PRODUCT_NAME = By.cssSelector("#blockcart-modal .product-name");
    private static final By DETAILS = By.cssSelector("#blockcart-modal .modal-body");
    private static final By CHECKOUT = By.cssSelector("#blockcart-modal .cart-content-btn a");
    private static final By CONTINUE = By.cssSelector("#blockcart-modal .cart-content-btn button");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartModal(WebDriver driver) {
        this.driver = driver;
        this.wait = BasePage.waitFor(driver);
    }

    public CartModal waitUntilOpen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL));
        return this;
    }

    public String title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE)).getText().trim();
    }

    public String productName() {
        return driver.findElement(PRODUCT_NAME).getText().trim();
    }

    /** All text in the dialog body, e.g. "Size: M", "Quantity: 2". */
    public String details() {
        return driver.findElement(DETAILS).getText();
    }

    @Step("Proceed to checkout from the cart dialog")
    public CartPage proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT)).click();
        return new CartPage(driver).waitUntilLoaded();
    }

    @Step("Continue shopping")
    public void continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }
}

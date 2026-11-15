package io.github.inarazahin.shop.pages;

import io.qameta.allure.Step;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

    private static final By SUMMARY = By.cssSelector(".cart-summary");
    private static final By LINES = By.cssSelector(".cart-overview .cart-item");
    private static final By LINE_NAME = By.cssSelector(".product-line-info a.label");
    private static final By LINE_QUANTITY = By.cssSelector("input.js-cart-line-product-quantity");
    private static final By LINE_TOTAL = By.cssSelector(".product-price strong, span.product-price");
    private static final By LINE_REMOVE = By.cssSelector("a.remove-from-cart");
    private static final By PRODUCTS_SUBTOTAL = By.cssSelector("#cart-subtotal-products .value");
    private static final By SHIPPING = By.cssSelector("#cart-subtotal-shipping .value");
    private static final By TOTAL = By.cssSelector(".cart-summary .cart-total .value");
    private static final By EMPTY = By.cssSelector(".cart-overview .no-items");
    private static final By CHECKOUT = By.cssSelector(".cart-detailed-actions a.btn-primary");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the cart")
    public CartPage open() {
        navigateTo("/cart?action=show");
        return waitUntilLoaded();
    }

    public CartPage waitUntilLoaded() {
        visible(SUMMARY);
        return this;
    }

    public List<String> productNames() {
        return driver.findElements(LINES).stream()
                .map(line -> line.findElement(LINE_NAME).getText().trim())
                .toList();
    }

    public int quantityOf(String productName) {
        return Integer.parseInt(line(productName).findElement(LINE_QUANTITY).getDomProperty("value"));
    }

    public BigDecimal lineTotalOf(String productName) {
        return money(line(productName).findElement(LINE_TOTAL).getText());
    }

    public BigDecimal productsSubtotal() {
        return money(text(PRODUCTS_SUBTOTAL));
    }

    public BigDecimal shipping() {
        return money(text(SHIPPING));
    }

    public BigDecimal total() {
        return money(text(TOTAL));
    }

    @Step("Change quantity of {productName} to {quantity}")
    public CartPage changeQuantity(String productName, int quantity) {
        BigDecimal before = productsSubtotal();
        WebElement field = line(productName).findElement(LINE_QUANTITY);
        overwrite(field, String.valueOf(quantity));
        field.sendKeys(Keys.ENTER);
        // Totals are recalculated via AJAX.
        wait.until(d -> productsSubtotal().compareTo(before) != 0);
        return this;
    }

    @Step("Remove {productName} from the cart")
    public CartPage remove(String productName) {
        line(productName).findElement(LINE_REMOVE).click();
        wait.until(d -> !productNames().contains(productName));
        return this;
    }

    public boolean isEmpty() {
        return isDisplayed(EMPTY);
    }

    public String emptyMessage() {
        return text(EMPTY);
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(CHECKOUT);
        return new CheckoutPage(driver).waitUntilLoaded();
    }

    private WebElement line(String productName) {
        return wait.until(d -> d.findElements(LINES).stream()
                .filter(l -> l.findElement(LINE_NAME).getText().trim().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null));
    }
}

package io.github.inarazahin.shop.pages;

import io.github.inarazahin.shop.pages.components.CartModal;
import io.qameta.allure.Step;
import java.math.BigDecimal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductPage extends BasePage {

    /** A demo-catalogue product with size and colour options. */
    public static final String HUMMINGBIRD_TSHIRT = "/men/1-1-hummingbird-printed-t-shirt.html";

    private static final By NAME = By.cssSelector("#main h1");
    private static final By PRICE = By.cssSelector(".product-prices .current-price-value");
    private static final By SIZE = By.id("group_1");
    private static final By QUANTITY = By.id("quantity_wanted");
    private static final By ADD_TO_CART = By.cssSelector("[data-button-action='add-to-cart']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open product page {path}")
    public ProductPage open(String path) {
        navigateTo(path);
        return waitUntilLoaded();
    }

    ProductPage waitUntilLoaded() {
        visible(ADD_TO_CART);
        return this;
    }

    public String name() {
        return text(NAME);
    }

    public BigDecimal price() {
        return money(text(PRICE));
    }

    @Step("Select size {size}")
    public ProductPage selectSize(String size) {
        selectByText(SIZE, size);
        // The variant change reloads the product block via AJAX and then records the chosen
        // combination in the URL fragment (e.g. "#/2-size-m/8-color-white").
        String fragment = "-size-" + size.toLowerCase();
        wait.until(d -> d.getCurrentUrl().contains(fragment));
        visible(ADD_TO_CART);
        return this;
    }

    @Step("Set quantity to {quantity}")
    public ProductPage setQuantity(int quantity) {
        WebElement field = visible(QUANTITY);
        overwrite(field, String.valueOf(quantity));
        wait.until(d -> String.valueOf(quantity).equals(visible(QUANTITY).getDomProperty("value")));
        return this;
    }

    @Step("Add to cart")
    public CartModal addToCart() {
        click(ADD_TO_CART);
        return new CartModal(driver).waitUntilOpen();
    }
}

package io.github.inarazahin.shop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final By FEATURED_PRODUCTS = By.cssSelector("#content .product-miniature");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the home page")
    public HomePage open() {
        navigateTo("/");
        visible(FEATURED_PRODUCTS);
        return this;
    }
}

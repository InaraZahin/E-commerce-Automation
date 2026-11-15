package io.github.inarazahin.shop.pages;

import io.qameta.allure.Step;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchResultsPage extends BasePage {

    private static final By RESULTS = By.id("js-product-list-top");
    private static final By TILES = By.cssSelector("#js-product-list .product-miniature");
    // Tile titles are truncated by the theme ("The best is yet to come'..."); the image alt holds the full name.
    private static final By TILE_IMAGE = By.cssSelector(".product-thumbnail img");
    private static final By TILE_LINK = By.cssSelector(".product-title a");
    private static final By TOTAL = By.cssSelector("#js-product-list-top .total-products");
    private static final Pattern COUNT = Pattern.compile("(\\d+) products?");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Search for \"{term}\" via URL")
    public SearchResultsPage open(String term) {
        navigateTo("/search?s=" + java.net.URLEncoder.encode(term, java.nio.charset.StandardCharsets.UTF_8));
        return waitUntilLoaded();
    }

    public SearchResultsPage waitUntilLoaded() {
        visible(RESULTS);
        return this;
    }

    public List<String> productNames() {
        return allVisible(TILES).stream().map(SearchResultsPage::fullName).toList();
    }

    /** The count stated in "There are N products.". */
    public int statedTotal() {
        Matcher matcher = COUNT.matcher(text(TOTAL));
        if (!matcher.find()) {
            throw new IllegalStateException("No product count in: " + text(TOTAL));
        }
        return Integer.parseInt(matcher.group(1));
    }

    private static String fullName(WebElement tile) {
        return tile.findElement(TILE_IMAGE).getAttribute("alt").trim();
    }

    @Step("Open product \"{name}\" from the results")
    public ProductPage openProduct(String name) {
        allVisible(TILES).stream()
                .filter(tile -> fullName(tile).equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No result named " + name))
                .findElement(TILE_LINK)
                .click();
        return new ProductPage(driver).waitUntilLoaded();
    }
}

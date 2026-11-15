package io.github.inarazahin.shop.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderConfirmationPage extends BasePage {

    private static final By HEADING = By.cssSelector("#content-hook_order_confirmation h3");
    private static final By DETAILS = By.id("order-details");
    private static final Pattern REFERENCE = Pattern.compile("Order reference:\\s*([A-Z]+)");

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    OrderConfirmationPage waitUntilLoaded() {
        wait.until(d -> d.getCurrentUrl().contains("order-confirmation"));
        visible(HEADING);
        return this;
    }

    public String heading() {
        return text(HEADING);
    }

    public String details() {
        return text(DETAILS);
    }

    public String orderReference() {
        Matcher matcher = REFERENCE.matcher(details());
        if (!matcher.find()) {
            throw new IllegalStateException("No order reference in: " + details());
        }
        return matcher.group(1);
    }
}

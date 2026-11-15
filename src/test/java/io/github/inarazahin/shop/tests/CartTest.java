package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.pages.CartPage;
import io.github.inarazahin.shop.pages.HomePage;
import io.github.inarazahin.shop.pages.ProductPage;
import io.github.inarazahin.shop.pages.components.CartModal;
import io.qameta.allure.Feature;
import java.math.BigDecimal;
import org.testng.annotations.Test;

@Feature("Cart")
public class CartTest extends BaseTest {

    private static final String TSHIRT = "Hummingbird printed t-shirt";

    @Test(groups = SMOKE, description = "A new visitor starts with an empty cart")
    public void cartStartsEmpty() {
        CartPage cart = new CartPage(driver()).open();

        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.emptyMessage()).isEqualTo("There are no more items in your cart");
        assertThat(cart.header().cartCount()).isZero();
    }

    @Test(groups = SMOKE, description = "Adding a product confirms the chosen size and quantity")
    public void addProductWithOptions() {
        ProductPage product = new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT)
                .selectSize("M")
                .setQuantity(2);

        CartModal modal = product.addToCart();

        assertThat(modal.title()).contains("Product successfully added to your shopping cart");
        assertThat(modal.productName()).isEqualToIgnoringCase(TSHIRT);
        assertThat(modal.details()).contains("Size: M").contains("Quantity: 2");
        modal.continueShopping();
        product.header().waitForCartCount(2);
    }

    @Test(groups = REGRESSION, description = "Line total and subtotal equal unit price x quantity")
    public void totalsMatchPriceTimesQuantity() {
        ProductPage product = new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT).setQuantity(3);
        BigDecimal unitPrice = product.price();

        CartPage cart = product.addToCart().proceedToCheckout();

        BigDecimal expected = unitPrice.multiply(BigDecimal.valueOf(3));
        assertThat(cart.quantityOf(TSHIRT)).isEqualTo(3);
        assertThat(cart.lineTotalOf(TSHIRT)).isEqualByComparingTo(expected);
        assertThat(cart.productsSubtotal()).isEqualByComparingTo(expected);
        assertThat(cart.total()).isEqualByComparingTo(expected.add(cart.shipping()));
    }

    @Test(groups = REGRESSION, description = "Changing the quantity in the cart recalculates totals")
    public void changeQuantityInCart() {
        ProductPage product = new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT);
        BigDecimal unitPrice = product.price();
        CartPage cart = product.addToCart().proceedToCheckout();

        cart.changeQuantity(TSHIRT, 4);

        assertThat(cart.quantityOf(TSHIRT)).isEqualTo(4);
        assertThat(cart.productsSubtotal()).isEqualByComparingTo(unitPrice.multiply(BigDecimal.valueOf(4)));
        cart.header().waitForCartCount(4);
    }

    @Test(groups = REGRESSION, description = "Removing the only product empties the cart")
    public void removeProduct() {
        CartPage cart = new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT)
                .addToCart()
                .proceedToCheckout();

        cart.remove(TSHIRT);

        assertThat(cart.isEmpty()).isTrue();
        cart.header().waitForCartCount(0);
    }

    @Test(groups = REGRESSION, description = "The cart survives page reloads and navigation")
    public void cartSurvivesNavigation() {
        new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT).setQuantity(2).addToCart().continueShopping();

        driver().navigate().refresh();
        new HomePage(driver()).open().header().waitForCartCount(2);

        CartPage cart = new CartPage(driver()).open();
        assertThat(cart.productNames()).containsExactly(TSHIRT);
        assertThat(cart.quantityOf(TSHIRT)).isEqualTo(2);
    }
}

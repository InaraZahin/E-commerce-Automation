package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.data.Customer;
import io.github.inarazahin.shop.data.StorefrontClient;
import io.github.inarazahin.shop.data.TestData;
import io.github.inarazahin.shop.pages.AccountPages.OrderHistoryPage;
import io.github.inarazahin.shop.pages.CheckoutPage;
import io.github.inarazahin.shop.pages.CheckoutPage.Payment;
import io.github.inarazahin.shop.pages.LoginPage;
import io.github.inarazahin.shop.pages.OrderConfirmationPage;
import io.github.inarazahin.shop.pages.ProductPage;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Feature("Checkout")
public class CheckoutTest extends BaseTest {

    private CheckoutPage checkoutWithOneProduct() {
        return new ProductPage(driver()).open(ProductPage.HUMMINGBIRD_TSHIRT)
                .addToCart()
                .proceedToCheckout()
                .proceedToCheckout();
    }

    @DataProvider(name = "payments", parallel = true)
    public Object[][] payments() {
        return new Object[][] {{Payment.BANK_WIRE}, {Payment.CHECK}, {Payment.CASH_ON_DELIVERY}};
    }

    @Test(groups = SMOKE, dataProvider = "payments",
            description = "A signed-in customer can place an order with each payment method")
    public void customerPlacesOrder(Payment payment) {
        Customer customer = StorefrontClient.registerNewCustomer();
        new LoginPage(driver()).open().signInAs(customer);

        OrderConfirmationPage confirmation = checkoutWithOneProduct()
                .enterAddress(TestData.usAddress())
                .confirmAddress()
                .confirmShipping()
                .choosePayment(payment)
                .acceptTerms()
                .placeOrder();

        assertThat(confirmation.heading()).containsIgnoringCase("Your order is confirmed");
        String reference = confirmation.orderReference();
        assertThat(reference).matches("[A-Z]{9}");
        assertThat(new OrderHistoryPage(driver()).open().orders())
                .as("order history")
                .anySatisfy(row -> assertThat(row).contains(reference));
    }

    @Test(groups = SMOKE, description = "A guest can check out without an account")
    public void guestCheckout() {
        OrderConfirmationPage confirmation = checkoutWithOneProduct()
                .continueAsGuest(TestData.newCustomer())
                .enterAddress(TestData.usAddress())
                .confirmAddress()
                .confirmShipping()
                .choosePayment(Payment.BANK_WIRE)
                .acceptTerms()
                .placeOrder();

        assertThat(confirmation.heading()).containsIgnoringCase("Your order is confirmed");
    }

    @Test(groups = REGRESSION, description = "An order cannot be placed before accepting the terms")
    public void termsMustBeAccepted() {
        Customer customer = StorefrontClient.registerNewCustomer();
        new LoginPage(driver()).open().signInAs(customer);

        CheckoutPage checkout = checkoutWithOneProduct()
                .enterAddress(TestData.usAddress())
                .confirmAddress()
                .confirmShipping()
                .choosePayment(Payment.BANK_WIRE);

        assertThat(checkout.isPlaceOrderEnabled()).as("before accepting terms").isFalse();
        checkout.acceptTerms();
        assertThat(checkout.isPlaceOrderEnabled()).as("after accepting terms").isTrue();
    }

    @Test(groups = REGRESSION, description = "The delivery address is required")
    public void addressIsRequired() {
        Customer customer = StorefrontClient.registerNewCustomer();
        new LoginPage(driver()).open().signInAs(customer);

        CheckoutPage checkout = checkoutWithOneProduct().confirmAddress();

        assertThat(checkout.validationMessage(CheckoutPage.ADDRESS1)).isNotBlank();
        assertThat(checkout.currentStep()).isEqualTo("checkout-addresses-step");
    }
}

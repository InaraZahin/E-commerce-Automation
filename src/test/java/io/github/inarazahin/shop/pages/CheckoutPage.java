package io.github.inarazahin.shop.pages;

import io.github.inarazahin.shop.data.Address;
import io.github.inarazahin.shop.data.Customer;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The one-page checkout: personal information, addresses, shipping and payment steps. */
public class CheckoutPage extends BasePage {

    public enum Payment {
        BANK_WIRE("Pay by bank wire"),
        CHECK("Pay by Check"),
        CASH_ON_DELIVERY("Pay by Cash on Delivery");

        private final String label;

        Payment(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    private static final By CURRENT_STEP = By.cssSelector("section.checkout-step.-current");

    // Personal information (guest)
    private static final By GUEST_FIRST_NAME = By.cssSelector("#checkout-guest-form #field-firstname");
    private static final By GUEST_LAST_NAME = By.cssSelector("#checkout-guest-form #field-lastname");
    private static final By GUEST_EMAIL = By.cssSelector("#checkout-guest-form #field-email");
    private static final By GUEST_TERMS = By.cssSelector("#checkout-guest-form input[name='psgdpr']");
    private static final By GUEST_PRIVACY = By.cssSelector("#checkout-guest-form input[name='customer_privacy']");
    private static final By GUEST_CONTINUE = By.cssSelector("#checkout-guest-form button[name='continue']");

    // Addresses
    public static final By ADDRESS1 = By.cssSelector("#delivery-address #field-address1");
    private static final By CITY = By.cssSelector("#delivery-address #field-city");
    private static final By POSTCODE = By.cssSelector("#delivery-address #field-postcode");
    private static final By COUNTRY = By.cssSelector("#delivery-address #field-id_country");
    private static final By STATE = By.cssSelector("#delivery-address #field-id_state");
    private static final By CONFIRM_ADDRESS = By.cssSelector("#delivery-address button[name='confirm-addresses']");

    // Shipping
    private static final By CONFIRM_DELIVERY = By.cssSelector("button[name='confirmDeliveryOption']");

    // Payment
    private static final By TERMS_OF_SERVICE = By.id("conditions_to_approve[terms-and-conditions]");
    private static final By PLACE_ORDER = By.cssSelector("#payment-confirmation button[type='submit']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    CheckoutPage waitUntilLoaded() {
        visible(CURRENT_STEP);
        return this;
    }

    /** Id of the active step, e.g. "checkout-addresses-step". */
    public String currentStep() {
        return visible(CURRENT_STEP).getAttribute("id");
    }

    @Step("Continue as guest {customer.email}")
    public CheckoutPage continueAsGuest(Customer customer) {
        type(GUEST_FIRST_NAME, customer.firstName());
        type(GUEST_LAST_NAME, customer.lastName());
        type(GUEST_EMAIL, customer.email());
        check(GUEST_TERMS);
        check(GUEST_PRIVACY);
        click(GUEST_CONTINUE);
        return waitForStep("checkout-addresses-step");
    }

    @Step("Enter delivery address in {address.city}")
    public CheckoutPage enterAddress(Address address) {
        type(ADDRESS1, address.street());
        type(CITY, address.city());
        type(POSTCODE, address.postcode());
        selectByText(COUNTRY, address.country());
        // Changing country re-renders the form (states differ per country).
        wait.until(d -> isDisplayed(STATE));
        selectByText(STATE, address.state());
        return this;
    }

    @Step("Confirm the address")
    public CheckoutPage confirmAddress() {
        click(CONFIRM_ADDRESS);
        return this;
    }

    @Step("Confirm the shipping method")
    public CheckoutPage confirmShipping() {
        waitForStep("checkout-delivery-step");
        click(CONFIRM_DELIVERY);
        return waitForStep("checkout-payment-step");
    }

    @Step("Choose payment: {payment}")
    public CheckoutPage choosePayment(Payment payment) {
        By option = By.xpath("//section[@id='checkout-payment-step']//label[normalize-space()='"
                + payment.label() + "']");
        click(option);
        return this;
    }

    @Step("Accept the terms of service")
    public CheckoutPage acceptTerms() {
        check(TERMS_OF_SERVICE);
        return this;
    }

    public boolean isPlaceOrderEnabled() {
        return visible(PLACE_ORDER).isEnabled()
                && !visible(PLACE_ORDER).getAttribute("class").contains("disabled");
    }

    @Step("Place the order")
    public OrderConfirmationPage placeOrder() {
        wait.until(d -> isPlaceOrderEnabled());
        click(PLACE_ORDER);
        return new OrderConfirmationPage(driver).waitUntilLoaded();
    }

    private CheckoutPage waitForStep(String stepId) {
        wait.until(d -> stepId.equals(currentStep()));
        return this;
    }
}

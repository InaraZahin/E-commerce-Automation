package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.data.Customer;
import io.github.inarazahin.shop.data.StorefrontClient;
import io.github.inarazahin.shop.data.TestData;
import io.github.inarazahin.shop.pages.HomePage;
import io.github.inarazahin.shop.pages.LoginPage;
import io.github.inarazahin.shop.pages.RegistrationPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("Registration")
public class RegistrationTest extends BaseTest {

    @Test(groups = SMOKE, description = "A visitor can create an account and is signed in")
    public void registerNewAccount() {
        Customer customer = TestData.newCustomer();

        HomePage home = new LoginPage(driver()).open().createAccount().register(customer);

        assertThat(home.header().accountName()).isEqualTo(customer.fullName());
    }

    @Test(groups = REGRESSION, description = "The new account can sign in again after signing out")
    public void newAccountCanSignInAgain() {
        Customer customer = TestData.newCustomer();
        HomePage home = new RegistrationPage(driver()).open().register(customer);
        home.header().signOut();

        new LoginPage(driver()).open().signInAs(customer);

        assertThat(home.header().accountName()).isEqualTo(customer.fullName());
    }

    @Test(groups = REGRESSION, description = "An email address can only be registered once")
    public void duplicateEmailIsRejected() {
        Customer existing = StorefrontClient.registerNewCustomer();
        Customer duplicate = TestData.newCustomer();
        RegistrationPage registration = new RegistrationPage(driver()).open();

        registration.fill(new Customer(duplicate.firstName(), duplicate.lastName(),
                existing.email(), duplicate.password())).acceptConsents().submit();

        assertThat(registration.fieldError(RegistrationPage.EMAIL))
                .contains("already used");
        assertThat(registration.header().isSignedIn()).isFalse();
    }

    @Test(groups = REGRESSION, description = "Names containing digits are rejected")
    public void nameWithDigitsIsRejected() {
        Customer customer = TestData.newCustomer();
        RegistrationPage registration = new RegistrationPage(driver()).open();

        registration.fill(customer.withFirstName("Alex2")).acceptConsents().submit();

        assertThat(registration.fieldError(RegistrationPage.FIRST_NAME)).isEqualTo("Invalid format.");
        assertThat(registration.header().isSignedIn()).isFalse();
    }

    @Test(groups = REGRESSION, description = "A weak password is blocked before submission")
    public void weakPasswordIsRejected() {
        Customer customer = TestData.newCustomer().withPassword("abc");
        RegistrationPage registration = new RegistrationPage(driver()).open();

        registration.fill(customer).acceptConsents().submit();

        // The password policy is enforced in the browser (pattern + strength meter).
        assertThat(registration.validationMessage(RegistrationPage.PASSWORD)).isNotBlank();
        assertThat(registration.isDisplayed()).isTrue();
        assertThat(registration.header().isSignedIn()).isFalse();
    }

    @Test(groups = REGRESSION, description = "The required consents must be accepted")
    public void consentsAreRequired() {
        RegistrationPage registration = new RegistrationPage(driver()).open();

        registration.fill(TestData.newCustomer()).submit();

        assertThat(registration.validationMessage(RegistrationPage.TERMS_CONSENT)).isNotBlank();
        assertThat(registration.isDisplayed()).isTrue();
        assertThat(registration.header().isSignedIn()).isFalse();
    }
}

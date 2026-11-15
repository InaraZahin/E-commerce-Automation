package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.data.Customer;
import io.github.inarazahin.shop.data.StorefrontClient;
import io.github.inarazahin.shop.data.TestData;
import io.github.inarazahin.shop.pages.AccountPages.IdentityPage;
import io.github.inarazahin.shop.pages.AccountPages.OrderHistoryPage;
import io.github.inarazahin.shop.pages.HomePage;
import io.github.inarazahin.shop.pages.LoginPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

@Feature("My account")
public class AccountTest extends BaseTest {

    private Customer signedInCustomer() {
        Customer customer = StorefrontClient.registerNewCustomer();
        new LoginPage(driver()).open().signInAs(customer);
        return customer;
    }

    @Test(groups = REGRESSION, description = "A customer can update their name")
    public void updateName() {
        Customer customer = signedInCustomer();
        IdentityPage identity = new IdentityPage(driver()).open();

        identity.changeFirstName("Renamed");
        identity.save(customer.password());

        assertThat(identity.successMessage()).contains("Information successfully updated");
        assertThat(identity.header().accountName()).isEqualTo("Renamed " + customer.lastName());
    }

    @Test(groups = REGRESSION, description = "Changes require the correct current password")
    public void wrongCurrentPasswordIsRejected() {
        signedInCustomer();
        IdentityPage identity = new IdentityPage(driver()).open();

        identity.changePassword(TestData.strongPassword());
        identity.save("Not-The-Current-1!");

        assertThat(identity.errorMessage()).isNotBlank();
    }

    @Test(groups = REGRESSION, description = "After a password change only the new password works")
    public void changePassword() {
        Customer customer = signedInCustomer();
        String newPassword = TestData.strongPassword();
        IdentityPage identity = new IdentityPage(driver()).open();

        identity.changePassword(newPassword);
        identity.save(customer.password());
        assertThat(identity.successMessage()).contains("Information successfully updated");
        identity.header().signOut();

        LoginPage login = new LoginPage(driver()).open();
        login.submit(customer.email(), customer.password());
        assertThat(login.errorMessage()).as("old password").isEqualTo("Authentication failed.");

        login.signInAs(customer.withPassword(newPassword));
        assertThat(new HomePage(driver()).header().isSignedIn()).as("new password").isTrue();
    }

    @Test(groups = REGRESSION, description = "A new customer has no orders")
    public void newCustomerHasNoOrders() {
        signedInCustomer();

        OrderHistoryPage history = new OrderHistoryPage(driver()).open();

        assertThat(history.orders()).isEmpty();
        assertThat(history.pageText()).contains("You have not placed any orders.");
    }
}

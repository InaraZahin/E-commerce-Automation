package io.github.inarazahin.shop.tests;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.inarazahin.shop.data.Customer;
import io.github.inarazahin.shop.data.StorefrontClient;
import io.github.inarazahin.shop.data.TestData;
import io.github.inarazahin.shop.pages.HomePage;
import io.github.inarazahin.shop.pages.LoginPage;
import io.github.inarazahin.shop.pages.PasswordRecoveryPage;
import io.qameta.allure.Feature;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Feature("Authentication")
public class AuthenticationTest extends BaseTest {

    @Test(groups = SMOKE, description = "A registered customer can sign in")
    public void signInWithValidCredentials() {
        Customer customer = StorefrontClient.registerNewCustomer();

        new LoginPage(driver()).open().signInAs(customer);

        HomePage home = new HomePage(driver());
        assertThat(home.header().isSignedIn()).isTrue();
        assertThat(home.header().accountName()).isEqualTo(customer.fullName());
    }

    @DataProvider(name = "rejectedCredentials", parallel = true)
    public Object[][] rejectedCredentials() {
        return new Object[][] {
            {"registered email, wrong password", true, "Wrong-Pass-123!"},
            {"unregistered email", false, "Any-Pass-1234!"},
        };
    }

    @Test(groups = REGRESSION, dataProvider = "rejectedCredentials",
            description = "Sign-in is rejected with invalid credentials")
    public void signInIsRejected(String scenario, boolean registered, String password) {
        String email = registered ? StorefrontClient.registerNewCustomer().email() : TestData.uniqueEmail();
        LoginPage login = new LoginPage(driver()).open();

        login.submit(email, password);

        assertThat(login.errorMessage()).as(scenario).isEqualTo("Authentication failed.");
        assertThat(login.header().isSignedIn()).as(scenario).isFalse();
    }

    @Test(groups = REGRESSION, description = "The browser blocks submitting empty or malformed fields")
    public void requiredAndFormatValidation() {
        LoginPage login = new LoginPage(driver()).open();
        List<String> problems = new ArrayList<>();

        login.submit("", "Some-Pass-123!");
        problems.add(login.validationMessage(LoginPage.EMAIL));

        login.submit("not-an-email", "Some-Pass-123!");
        problems.add(login.validationMessage(LoginPage.EMAIL));

        login.submit(TestData.uniqueEmail(), "");
        problems.add(login.validationMessage(LoginPage.PASSWORD));

        // Each attempt is stopped by HTML5 validation before reaching the server.
        assertThat(problems).allSatisfy(message -> assertThat(message).isNotBlank());
        assertThat(login.isDisplayed()).isTrue();
    }

    @Test(groups = SMOKE, description = "A signed-in customer can sign out")
    public void signOut() {
        Customer customer = StorefrontClient.registerNewCustomer();
        HomePage home = new LoginPage(driver()).open().signInAs(customer);

        home.header().signOut();

        assertThat(home.header().isSignedIn()).isFalse();
        driver().navigate().refresh();
        assertThat(home.header().isSignedIn()).as("still signed out after reload").isFalse();
    }

    @Test(groups = REGRESSION, description = "The session is shared across browser tabs")
    public void sessionIsKeptInNewTab() {
        Customer customer = StorefrontClient.registerNewCustomer();
        new LoginPage(driver()).open().signInAs(customer);

        driver().switchTo().newWindow(org.openqa.selenium.WindowType.TAB);
        HomePage secondTab = new HomePage(driver()).open();

        assertThat(secondTab.header().accountName()).isEqualTo(customer.fullName());
    }

    @Test(groups = REGRESSION, description = "Password recovery accepts an email and confirms the request")
    public void passwordRecovery() {
        Customer customer = StorefrontClient.registerNewCustomer();
        PasswordRecoveryPage recovery = new LoginPage(driver()).open().forgotPassword();

        recovery.requestReset(customer.email());

        assertThat(recovery.confirmation())
                .contains("you will receive a link to reset your password")
                .contains(customer.email());
    }
}

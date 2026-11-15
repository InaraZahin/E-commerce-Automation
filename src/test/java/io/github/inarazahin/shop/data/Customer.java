package io.github.inarazahin.shop.data;

/** A storefront customer account. */
public record Customer(String firstName, String lastName, String email, String password) {

    public String fullName() {
        return firstName + " " + lastName;
    }

    public Customer withPassword(String newPassword) {
        return new Customer(firstName, lastName, email, newPassword);
    }

    public Customer withFirstName(String newFirstName) {
        return new Customer(newFirstName, lastName, email, password);
    }
}

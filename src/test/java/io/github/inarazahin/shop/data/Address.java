package io.github.inarazahin.shop.data;

/** A delivery address. {@code country} and {@code state} are the visible option labels. */
public record Address(String street, String city, String postcode, String country, String state) {
}

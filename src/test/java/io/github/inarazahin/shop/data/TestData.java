package io.github.inarazahin.shop.data;

import java.util.Locale;
import java.util.UUID;
import net.datafaker.Faker;

/**
 * Generates fresh, unique test data, so tests never share accounts, never collide on re-runs
 * and never need data written back into the repository.
 */
public final class TestData {

    private static final Faker FAKER = new Faker(Locale.US);

    private TestData() {
    }

    public static Customer newCustomer() {
        // PrestaShop only accepts letters, spaces, apostrophes and dots in names.
        String first = lettersOnly(FAKER.name().firstName());
        String last = lettersOnly(FAKER.name().lastName());
        return new Customer(first, last, uniqueEmail(), strongPassword());
    }

    public static String uniqueEmail() {
        return "qa+" + UUID.randomUUID().toString().substring(0, 12) + "@example.com";
    }

    /** Satisfies PrestaShop's password policy (length and strength score). */
    public static String strongPassword() {
        return "Qa-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14) + "!9";
    }

    public static Address usAddress() {
        return new Address(FAKER.address().streetAddress(), "Springfield", "62704", "United States", "Illinois");
    }

    private static String lettersOnly(String value) {
        String cleaned = value.replaceAll("[^A-Za-z ]", "");
        return cleaned.isBlank() ? "Test" : cleaned;
    }
}

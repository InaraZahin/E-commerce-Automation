package io.github.inarazahin.shop.data;

import io.github.inarazahin.shop.config.Config;
import io.qameta.allure.Step;
import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Prepares test preconditions over plain HTTP instead of through the browser. Registering a
 * customer this way takes milliseconds and keeps UI time for the behaviour actually under test.
 */
public final class StorefrontClient {

    private StorefrontClient() {
    }

    /** Registers a brand-new customer account and returns it. */
    @Step("Register customer {customer.email} via HTTP")
    public static Customer register(Customer customer) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("id_gender", "1");
        form.put("firstname", customer.firstName());
        form.put("lastname", customer.lastName());
        form.put("email", customer.email());
        form.put("password", customer.password());
        form.put("psgdpr", "1");
        form.put("customer_privacy", "1");
        form.put("submitCreate", "1");

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        try {
            // Load the form first to obtain a session cookie, like a browser would.
            client.send(HttpRequest.newBuilder(uri("/registration")).GET().build(),
                    HttpResponse.BodyHandlers.discarding());
            HttpResponse<String> response = client.send(
                    HttpRequest.newBuilder(uri("/registration"))
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(encode(form)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            // Success redirects away from the form; a validation error re-renders it with 200.
            if (response.statusCode() != 302) {
                throw new IllegalStateException("Registration of " + customer.email()
                        + " failed with HTTP " + response.statusCode());
            }
            return customer;
        } catch (IOException e) {
            throw new IllegalStateException("Shop not reachable at " + Config.baseUrl(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    public static Customer registerNewCustomer() {
        return register(TestData.newCustomer());
    }

    private static URI uri(String path) {
        return URI.create(Config.baseUrl() + path);
    }

    private static String encode(Map<String, String> form) {
        return form.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}

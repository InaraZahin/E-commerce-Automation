package io.github.inarazahin.shop.config;

import java.time.Duration;
import java.util.Optional;

/**
 * Run-time settings. Each value is read from a JVM system property first
 * ({@code -Dbase.url=...}), then an environment variable ({@code BASE_URL}), then a default,
 * so nothing environment-specific is hardcoded in the tests.
 */
public final class Config {

    private Config() {
    }

    /** Storefront root, without a trailing slash. */
    public static String baseUrl() {
        return get("base.url", "http://localhost:8080").replaceAll("/+$", "");
    }

    /** chrome, firefox or edge. */
    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    /** Selenium Grid URL. When set, browsers run remotely instead of on this machine. */
    public static Optional<String> remoteUrl() {
        return Optional.ofNullable(get("selenium.remote.url", null)).filter(url -> !url.isBlank());
    }

    /** Maximum time an explicit wait polls for a condition. */
    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds", "15")));
    }

    private static String get(String property, String defaultValue) {
        String fromProperty = System.getProperty(property);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }
        String fromEnv = System.getenv(property.toUpperCase().replace('.', '_'));
        return fromEnv != null && !fromEnv.isBlank() ? fromEnv : defaultValue;
    }
}

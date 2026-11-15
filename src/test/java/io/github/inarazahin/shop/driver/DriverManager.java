package io.github.inarazahin.shop.driver;

import org.openqa.selenium.WebDriver;

/** One browser per test thread, so tests can run in parallel without sharing state. */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver start() {
        WebDriver driver = DriverFactory.create();
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver driver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No browser started for this thread");
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}

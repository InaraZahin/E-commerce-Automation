package io.github.inarazahin.shop.driver;

import io.github.inarazahin.shop.config.Config;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates a configured browser. Drivers are resolved automatically by Selenium Manager,
 * so no driver binaries live in the repository.
 */
public final class DriverFactory {

    private static final Dimension WINDOW = new Dimension(1440, 900);

    private DriverFactory() {
    }

    public static WebDriver create() {
        Capabilities options = options(Config.browser(), Config.headless());
        WebDriver driver = Config.remoteUrl()
                .map(url -> remote(url, options))
                .orElseGet(() -> local(options));
        // Explicit waits only: an implicit wait would silently slow down every negative check.
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().window().setSize(WINDOW);
        return driver;
    }

    private static Capabilities options(String browser, boolean headless) {
        return switch (browser) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--disable-dev-shm-usage", "--disable-search-engine-choice-screen");
                yield options;
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                yield options;
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                yield options;
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver local(Capabilities options) {
        if (options instanceof ChromeOptions chrome) {
            return new ChromeDriver(chrome);
        }
        if (options instanceof FirefoxOptions firefox) {
            return new FirefoxDriver(firefox);
        }
        return new EdgeDriver((EdgeOptions) options);
    }

    private static WebDriver remote(String url, Capabilities options) {
        try {
            return new RemoteWebDriver(URI.create(url).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid selenium.remote.url: " + url, e);
        }
    }
}

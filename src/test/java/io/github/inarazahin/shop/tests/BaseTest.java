package io.github.inarazahin.shop.tests;

import io.github.inarazahin.shop.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test method gets its own fresh browser (and therefore its own session and cart), so
 * tests are independent of each other and of execution order, and can run in parallel.
 */
public abstract class BaseTest {

    public static final String SMOKE = "smoke";
    public static final String REGRESSION = "regression";

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverManager.start();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser(ITestResult result) {
        try {
            if (!result.isSuccess() && DriverManager.isStarted()) {
                attachFailureEvidence(DriverManager.driver());
            }
        } finally {
            DriverManager.quit();
        }
    }

    protected WebDriver driver() {
        return DriverManager.driver();
    }

    private static void attachFailureEvidence(WebDriver driver) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.attachment("Screenshot", "image/png", new ByteArrayInputStream(png),
                    AttachmentOptions.withFileExtension(".png"));
            Allure.attachment("Page source", "text/html", driver.getPageSource(),
                    AttachmentOptions.withFileExtension(".html"));
            Allure.attachment("URL", driver.getCurrentUrl());
        } catch (RuntimeException e) {
            // Evidence is best effort: never let it mask the real failure.
            System.err.println("Could not capture failure evidence: " + e);
        }
    }
}

# E-commerce Automation

[![E2E](../../actions/workflows/e2e.yml/badge.svg)](../../actions/workflows/e2e.yml)

End-to-end tests for an online store, written in **Java** with **Selenium WebDriver**, **TestNG**
and **Allure**. The suite tests a real [PrestaShop](https://prestashop.com) storefront, the
platform behind the classic *automationpractice.com* demo shop. The store runs locally in Docker
with the standard demo catalogue, so the tests are reproducible on any machine and in CI.

## What it covers

35 test runs (30 test methods, some data-driven) across the full shopping journey:

| Area | Tests | Highlights |
|---|---|---|
| Authentication | 7 | Valid sign-in, wrong password, unknown email, browser-side validation, sign-out, session shared across tabs, password recovery |
| Registration | 6 | New account, re-login, duplicate email, invalid name, weak password, required consents |
| Search | 6 | Header search, relevance and result count (3 terms), case-insensitivity, open product from results |
| Cart | 6 | Empty cart, size and quantity options, totals = price × quantity, quantity change, removal, cart survives navigation |
| Checkout | 6 | Order with each payment method (3), guest checkout, terms must be accepted, address required |
| My account | 4 | Update name, wrong current password, password change, empty order history |

Every test is tagged `smoke` (10 critical-path tests) or `regression`.

## Quick start

Requirements: JDK 17+, Maven, Docker, Chrome.

```bash
docker compose up -d --wait
```

The first start installs PrestaShop and takes a few minutes. The shop is then at
http://localhost:8080.

```bash
mvn test
```

```bash
mvn test -Dgroups=smoke
```

```bash
docker compose down -v
```

View the Allure report (requires the [Allure CLI](https://allurereport.org/docs/install/)):

```bash
allure serve target/allure-results
```

## Configuration

Settings are read from JVM system properties, then environment variables, then defaults:

| Property | Environment variable | Default |
|---|---|---|
| `base.url` | `BASE_URL` | `http://localhost:8080` |
| `browser` (`chrome`, `firefox`, `edge`) | `BROWSER` | `chrome` |
| `headless` | `HEADLESS` | `true` |
| `selenium.remote.url` (Selenium Grid) | `SELENIUM_REMOTE_URL` | not set: local browser |
| `timeout.seconds` | `TIMEOUT_SECONDS` | `15` |

For example, to watch the tests in Firefox: `mvn test -Dbrowser=firefox -Dheadless=false`.

Browser drivers are resolved automatically by Selenium Manager. No driver binaries are kept in
the repository.

## How the suite is designed

**Independent tests.** Each test method gets a fresh browser and freshly generated data (unique
emails, random names). No test depends on another, on execution order, or on data left by a
previous run. That is why TestNG runs them in parallel (4 threads), and why any single test can
be run on its own.

**Fast preconditions.** When a test needs an existing customer, the account is registered with a
plain HTTP request (`StorefrontClient`) instead of through the browser. Browser time is spent
only on the behaviour under test.

**Explicit waits only.** There are no `Thread.sleep` calls and no implicit wait. Every
interaction waits for the specific condition it needs: an element to be clickable, an AJAX total
to change, the URL fragment of the chosen product variant. Waits also retry on stale elements,
because PrestaShop re-renders parts of the page after AJAX calls.

**Stable locators.** Elements are found by ids, names and data attributes
(`[data-button-action='add-to-cart']`), never by list position or layout-dependent XPath.

**Exact assertions.** Prices are parsed into `BigDecimal` and compared numerically, never as
strings or doubles. Assertions use AssertJ, so failures read clearly.

**Assertions only in tests.** Page objects describe what a user can do. Tests decide what is
correct.

**Evidence on failure.** A failed test attaches a screenshot, the page source and the URL to the
Allure report. Page-object actions are annotated with `@Step`, so the report reads as a sequence
of user actions.

## Project structure

```
docker-compose.yml                 PrestaShop 8.2 + MariaDB, demo catalogue
docker/post-install/               store settings applied after install (e.g. disable email)
src/test/java/io/github/inarazahin/shop/
  config/Config.java               settings from properties / environment
  driver/                          DriverFactory (local or Grid), DriverManager (one browser per thread)
  data/                            Customer, Address, TestData (Datafaker), StorefrontClient (HTTP setup)
  pages/                           page objects + components (Header, CartModal)
  tests/                           BaseTest + one class per feature area
src/test/resources/testng.xml      suite: parallel methods, 4 threads
.github/workflows/e2e.yml          CI: start shop, run suite, publish Allure report
```

## Continuous integration

`.github/workflows/e2e.yml` runs on every push and pull request, nightly, and on demand (with an
optional group filter). It starts the shop with Docker Compose, runs the suite in the runner's
Chrome, and uploads the Allure HTML report and the Surefire results as a build artifact.

## Notes about the store

- **Search is fuzzy.** PrestaShop returns "similar" products even for nonsense terms, so there is
  no "no results" test. The search tests assert that results are relevant to the term.
- **Email is switched off** in the test store (`docker/post-install`), because the container has
  no mail server. Without that, password recovery for a registered customer fails with
  *"An error occurred while sending the email"*.

## History

Version 1 of this project tested *automationpractice.com*, which is no longer online. It used
Gradle, ran its 51 tests as one ordered user journey with fixed sleeps, and kept test accounts in
a JSON file the tests rewrote. Version 2 is a rewrite around a self-hosted store, independent
parallel tests, explicit waits and CI.

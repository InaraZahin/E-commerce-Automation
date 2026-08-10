# E-Commerce Automation Testing Framework

This project delivers end-to-end test automation for an [E-commerce website](http://automationpractice.com/), built using **Selenium WebDriver** with **TestNG** as the testing framework.

## Modules Covered

The automation covers the following core modules/pages:

- SignUp
- Login
- Search
- Cart
- Checkout

## Test Design

A total of **51 test cases** were developed across these modules, organized into test suites that include both **positive and negative scenarios**. The test flow follows a **state-transition design**, simulating a real user journey — from browsing to completing a product purchase.

On any test failure, a **screenshot is automatically captured** at the point of failure for easier debugging.


## Tech Stack

| Category | Tool/Technology |
|---|---|
| Automation Tool | Selenium WebDriver |
| IDE | IntelliJ IDEA |
| Build Tool | Gradle |
| Language | Java |
| Testing Framework | TestNG |
| Reporting | Allure |

## Prerequisites

- Install **JDK 11**, **Gradle**, and **Allure**
- Set up environment variables for JDK 11, Gradle, and Allure

## Setup & Installation

1. Clone and unzip the repository
2. Open the project directory
3. Open `build.gradle` in IntelliJ IDEA (double-click to launch)
4. Wait for the project to build successfully
5. Open the Terminal and run the test scripts

## Running the Tests

Run the automation suite with:

```bash
gradle clean test
```

This will launch a browser and begin executing the automated tests.

## Viewing the Allure Report

After the test run completes, generate and view the report with:

```bash
allure generate allure-results --clean -o allure-report
allure serve allure-results
```

## Reports & Suite Overview

Below is a snapshot of the Allure summary report:

<img width="1860" height="894" alt="185918539-c40ea3e9-dd3f-4e56-b223-adcf80fcbf25" src="https://github.com/user-attachments/assets/0928bbe9-a150-4e95-8592-4c0bfd1b4462" />


Here's a breakdown of the test suites included in this project:

<img width="1863" height="922" alt="185918375-8ac0df03-ee09-4215-a6df-ecdbcf9fd5cd" src="https://github.com/user-attachments/assets/68ecd4e4-3e4d-4073-b61f-7a69e06f9cc3" />


## Project Walkthrough

- 🎥 **Full project walkthrough:** [Watch video](https://drive.google.com/file/d/1fx4bMzsdBhugkUjqPKAI1z9UCwFo8W_-/view?usp=sharing)
- 🎥 **Checkout module sanity test demo:** [Watch video](https://drive.google.com/file/d/1nsk8-EKik-BnvjvH4mSwOwV7COD7dsas/view?usp=sharing)

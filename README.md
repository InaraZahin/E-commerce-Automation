This project delivers end-to-end test automation for an E-commerce website, built using Selenium WebDriver with TestNG as the testing framework.

The automation covers the following core modules/pages:

SignUp
Login
Search
Cart
Checkout

A total of 51 test cases were developed across these modules, organized into test suites that include both positive and negative scenarios. The test flow follows a state-transition design, simulating a real user journey — from browsing to completing a product purchase. On any test failure, a screenshot is automatically captured at the point of failure for easier debugging.


Tech Stack:

Automation Tool: Selenium WebDriver
IDE: IntelliJ IDEA
Build Tool: Gradle
Language: Java
Testing Framework: TestNG

Setup Requirements:

Install JDK 11, Gradle, and Allure
Set up environment variables for JDK 11, Gradle, and Allure
Clone and unzip the repository
Open the project directory
Open "build.gradle" in IntelliJ IDEA (double-click to launch)
Wait for the project to build successfully
Open the Terminal and run the test scripts

To run the automation suite:

gradle clean test
This will launch a browser and begin executing the automated tests.

To generate and view the Allure report after the run:

allure generate allure-results --clean -o allure-report
allure serve allure-results

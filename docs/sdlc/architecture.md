# Architecture Document

**Feature:** Selenium Login Automation for Customer Authentication
**Source:** docs/sdlc/requirements.md
**Date:** 2026-09-26
**Agent:** architecture-agent

---

## 1. Architecture Overview
The solution is a lightweight Java + Selenium + JUnit 5 test suite built around the Page Object Model (POM). It centralizes browser lifecycle control, waits, configuration, and test data so each scenario stays readable and independent. The framework targets the WooCommerce My Account login page and validates both successful and unsuccessful authentication behavior using browser automation driven by Maven and JUnit.

---

## 2. Component Responsibilities

| Component | Responsibility | Inputs | Outputs | Key Classes |
|---|---|---|---|---|
| Test Configuration | Reads defaults and overrides from system properties and environment variables | `baseUrl`, browser, headless toggle, timeouts, credentials | Resolved runtime settings | `TestConfig` |
| Browser Lifecycle | Initializes and tears down WebDriver instances with isolated profiles | Browser choice, headless flag, profile directory | Browser session and cleanup | `BaseTest` |
| Page Object Layer | Encapsulates selectors and page actions | WebDriver and locators | Reusable UI interactions | `BasePage`, `LoginPage` |
| Test Data | Defines expected success/error tokens and common test values | Scenario names and validation strings | Assertion inputs | `TestData` |
| Reporting | Produces lifecycle events and enhanced diagnostics | Test events, screenshots, driver references | Logs and reports | `TestLifecycleListener`, `ExtentReportExtension` |
| Test Suite | Executes login scenarios and acts as acceptance coverage | WebDriver-driven flows | Pass/fail results | `LoginPageTests` |

---

## 3. Directory Structure

```text
src/
  main/java/
    Github_Copilot/Main.java
  test/java/
    Github_Copilot/
      base/
        BaseTest.java
      config/
        TestConfig.java
      data/
        TestData.java
      listeners/
        TestLifecycleListener.java
        ExtentReportExtension.java
      pages/
        BasePage.java
        LoginPage.java
      tests/
        LoginPageTests.java
        LoginPageRunner.java
      utils/
        LogUtil.java
        ScreenshotUtil.java
```

---

## 4. Test Execution Flow

```text
Maven/JUnit start
  -> TestConfig resolves browser / URLs / credentials
  -> BaseTest creates WebDriver (Chrome or Firefox)
  -> LoginPage opens the My Account page
  -> Page object performs actions (type, submit, click, wait)
  -> Assertions validate authenticated or rejected states
  -> Listener/extension captures logs and reports
  -> Browser closes and profile cleanup runs
```

Key flow details:
- `BaseTest` handles setup and teardown per scenario.
- `LoginPage` encapsulates visibility, validation messages, logout detection, and lost-password navigation.
- Tests use bounded explicit waits via `WebDriverWait` rather than fixed sleeps.
- Remember Me scenarios use a temporary persistent profile directory for session validation.

---

## 5. Technology and Configuration Decisions
- Java 21 for modern language and tooling support.
- Selenium 4.x for stable WebDriver APIs and browser automation.
- WebDriverManager to simplify driver provisioning.
- JUnit 5 for structured tests and assertions.
- Maven Surefire for test execution and reporting.
- Config values read from system properties first, then environment variables, then sane defaults.

Example decision flow:
- `baseUrl` falls back to `https://askomdch.com/account/`
- `browser` defaults to `chrome`
- `headless` defaults to `false`
- `pageLoadTimeout` uses a safe bounded integer value
- Credentials fall back to environment variables `LOGIN_VALID_USERNAME`, `LOGIN_VALID_PASSWORD`

---

## 6. Error Handling, Security, Scalability, and Success Criteria

### Error Handling
- Test actions fail fast with explicit assertions on user-visible outcomes.
- Lifecycle listeners capture start/finish/failure details.
- Screenshots and logs are supported for diagnostic visibility.

### Security
- Credentials are not stored as literal values in the source tree.
- Environment variables or system properties are used to supply account data.
- Sensitive values should remain outside generated reports and logs.

### Scalability
- The framework is intentionally sequential for V1, reducing complexity while preserving maintainability.
- Future parallel execution can be introduced without redesigning the page layer.

### Success Criteria
The architecture is successful when it covers the login flow scenarios required by the PRD, supports both Chrome and Firefox, maintains test readability, and remains easy to extend without overengineering.

---

## 7. Traceability
- Requirements covered: FR-1 through FR-8, NFR-1 through NFR-5, and user stories US-1 through US-5
- Existing implementation reviewed: `BaseTest`, `TestConfig`, `BasePage`, `LoginPage`, and `LoginPageTests`
- Next Stage: Design Review

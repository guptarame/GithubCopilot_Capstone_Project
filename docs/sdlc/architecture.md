# Selenium Test Automation Framework Architecture

**Project:** Selenium Login Automation Capstone  
**Feature:** US-AUTH-002 — Customer Login & Authentication
**Based on:** `docs/sdlc/requirements.md`, existing test implementation
**Date:** 2026-09-22
**Agent:** architecture-agent

## Architecture Overview

The project uses a small JUnit 5/Selenium 4 framework organized around a per-test
WebDriver lifecycle and Page Object Model (POM). Tests express authentication
intent through `LoginPage`; configuration, waits, diagnostics, and test data
remain centralized so the six login scenarios can run independently on Chrome or
Firefox. Maven Surefire is the baseline reporting mechanism, with screenshots
captured for failures.

## System Components

| Component | Responsibility | Inputs / outputs | Key classes and dependencies |
| --- | --- | --- | --- |
| **Driver and test lifecycle** | Create the configured browser before each test, apply timeouts, and quit it afterward. Keep tests isolated and sequential. | Browser/headless settings → `WebDriver` available to the test | `base.BaseTest`; Selenium 4, WebDriverManager, JUnit 5 |
| **Page objects and synchronization** | Own locators and business actions; wait for visible/clickable elements instead of sleeping. | Driver and page URL → login actions, page state, messages | `pages.BasePage`, `pages.LoginPage`; Selenium `WebDriverWait` |
| **Configuration and test data** | Resolve runtime settings and credentials without putting secrets in source control; centralize deterministic negative data and message tokens. | System properties or environment variables → typed settings/test values | `config.TestConfig`, `data.TestData`; Java standard library |
| **Scenario tests** | Map each requirement to an independent, readable test and make assertions about UI, URL, authentication, and feedback. Skip credential-dependent tests when credentials are absent. | Configured driver, page objects, test data → JUnit pass/fail/skip | `tests.LoginPageTests`; JUnit 5 assertions and assumptions |
| **Diagnostics and reporting** | Log lifecycle events and capture a timestamped screenshot after a failed test; preserve the original failure. | JUnit extension context and current driver → console logs and `target/screenshots/*.png` | `listeners.TestLifecycleListener`, `utils.LogUtil`, `utils.ScreenshotUtil`; JUnit extension API |

### Component contracts

- `BaseTest.setupDriver()` calls `createDriver(browser, headless)`, applies the
  one-second implicit wait and 30-second (configurable) page-load timeout, and
  registers the driver with the lifecycle extension.
- `BasePage` exposes common navigation, visibility, click, typing, URL, and
  explicit-wait behavior. `LoginPage` contains stable locators (`id`,
  `name`, and targeted CSS/link text) and methods such as
  `openPage`, `submitLogin`, `isLoggedIn`, `getFeedbackMessage`,
  `setRememberMe`, and `clickLostPassword`.
- `TestConfig` currently resolves **system property → environment variable →
  default**. No properties file is required for the current implementation.
- `TestLifecycleListener` runs through `@ExtendWith` on `BaseTest`, so a failure
  is logged and screenshotted before `@AfterEach` closes the browser.

## Directory Structure

```text
src/test/java/Github_Copilot/
├── base/       BaseTest.java
├── config/     TestConfig.java
├── data/       TestData.java
├── listeners/  TestLifecycleListener.java
├── pages/      BasePage.java, LoginPage.java
├── tests/      LoginPageTests.java
└── utils/      LogUtil.java, ScreenshotUtil.java
docs/sdlc/requirements.md
pom.xml
```

No additional framework layer is required for the current scope. A new page
object should be added under `pages` only when a scenario crosses a distinct
page boundary (for example, a future reset-password flow).

## Requirements and Scenario Coverage

| Requirement / scenario | Architectural coverage |
| --- | --- |
| FR-01 / AC-1: form and required controls | `LoginPage` stable locators and `AC-UI-001` visibility assertions |
| FR-02 / AC-2: blank-field validation | `TS-LOG-004`; feedback read through `LoginPage.getFeedbackMessage()` |
| FR-03 / AC-3: successful authentication and dashboard | `TS-LOG-001`; `isLoggedIn`, dashboard text, welcome/logout tokens |
| FR-04 / AC-4: invalid password/unknown user remain on login | `TS-LOG-002` and `TS-LOG-003`; error-banner feedback assertions |
| FR-05 / AC-5: Remember me persistence | `TS-LOG-005`; Chrome persistent profile, restart, and dashboard check |
| FR-06 / AC-6: password recovery navigation | `TS-LOG-006`; link action and `lost-password`/`reset` URL assertion |
| NFR-01 security | HTTPS default URL; valid credentials only from environment/system properties; no password logging |
| NFR-02 reliability | Fresh browser per test, explicit waits, deterministic test data, no test ordering |
| NFR-03 compatibility | Driver factory supports Chrome and Firefox; headless mode is configurable |
| NFR-04 maintainability | POM and stable `id`/`name` locators isolate DOM changes |
| NFR-05 observability | Business assertions, lifecycle logs, Surefire XML, and failure screenshots |

The valid-login and invalid-password scenarios are skipped with a clear reason
when required valid credentials are not supplied. The unknown-user, blank-field,
UI, and reset-link scenarios do not require a real account.

## Configuration and Data Flow

```text
mvn test / -D overrides
          │
          ▼
TestConfig: system property → environment variable → safe default
          │
          ├── browser, headless, baseUrl, pageLoadTimeout
          └── valid/invalid/unknown login data
          │
          ▼
BaseTest → WebDriverFactory logic → ChromeDriver or FirefoxDriver
          │
          ▼
LoginPage (explicit waits + locators) → JUnit assertions
          │                                      │
          └── failure → listener → log + target/screenshots ──┐
                                                               ▼
                                             Surefire XML in target/surefire-reports
```

Supported examples:

```powershell
mvn test
mvn test -Dbrowser=firefox -Dheadless=true
mvn test -DbaseUrl=https://askomdch.com/account/
```

For a successful login, provide `LOGIN_VALID_USERNAME` and
`LOGIN_VALID_PASSWORD` (or the corresponding `-DvalidUsername` and
`-DvalidPassword` values). Optional values are `LOGIN_INVALID_PASSWORD` and
`LOGIN_UNKNOWN_USERNAME`. Credentials must be supplied by the local shell or
CI secret store and must not be committed or included in logs.

## Test Execution Flow

```text
Maven Surefire discovers **/*Tests.java
  → JUnit creates LoginPageTests instance
  → TestLifecycleListener logs start
  → BaseTest @BeforeEach creates/configures driver
  → Test opens account URL through LoginPage
  → Page object performs actions using explicit waits
  → Test asserts expected form, URL, success, validation, or error state
  → On failure: listener logs and captures screenshot
  → BaseTest @AfterEach quits driver
  → Surefire writes XML reports and Maven returns pass/fail status
```

Each test receives a new browser. The Remember me test is intentionally
Chrome-specific: it uses a temporary persistent profile, authenticates, quits,
reopens with that profile, and verifies the dashboard. It is skipped on Firefox
or without valid credentials rather than weakening the other scenarios.

## Technology Decisions

| Concern | Choice | Rationale |
| --- | --- | --- |
| Language | Java 21 | Matches `pom.xml`, strong typing, modern LTS runtime |
| Browser automation | Selenium 4.25.0 | Cross-browser WebDriver API and explicit waits |
| Driver management | WebDriverManager 6.1.0 | Removes manual driver binary setup |
| Test framework | JUnit Jupiter 5.11.3 | Lifecycle, assumptions, extensions, and readable tests |
| Build/discovery | Maven 3.9+ and Surefire 3.5.0 | Reproducible dependency/test execution and XML reports |
| Assertions | JUnit 5 native assertions | Already used; sufficient for current scenarios |

The existing one-second implicit wait is retained for compatibility, but state
changes and post-submit outcomes must use `WebDriverWait` in page objects.
`Thread.sleep()` and broad retry loops are not part of the design.

## Error Handling and Reliability

- Unsupported browsers fail immediately with `IllegalArgumentException`.
- Missing credential inputs use JUnit assumptions for credential-dependent
  scenarios; this is reported as skipped, not as a false pass.
- Element and navigation timing issues fail through explicit wait timeouts with
  the test name and screenshot available for diagnosis.
- Unexpected WebDriver/test exceptions are not swallowed. Teardown attempts to
  quit the driver, while the listener preserves the failure result.
- Feedback assertions should use meaningful token sets because the live site's
  theme/plugin wording may vary slightly; they must still assert the correct
  page state and outcome.
- Tests run sequentially in the initial design. Parallel execution is deferred
  until driver isolation, profile handling, and CI capacity are verified.

## Security and Operational Constraints

- The default and documented target URL use HTTPS; non-production URLs must be
  explicitly supplied.
- Never hardcode or print valid passwords. Avoid putting credentials in
  screenshots or command history; use CI secret variables where possible.
- Screenshots are diagnostic artifacts under `target` and should be retained
  only according to CI policy.
- Execution depends on network access to the live application and installed
  Chrome/Firefox binaries. Live-site changes can affect selectors and wording.

## Success Criteria and Traceability

The implementation is ready for review when all six `TS-LOG` scenarios and the
required-control acceptance check map to the components above, tests are
independent, Chrome and Firefox startup are configurable, and failed tests
produce actionable logs/screenshots without exposing secrets. This document is
traceable to `requirements.md`, `pom.xml`, and the current
`BaseTest`, `TestConfig`, `BasePage`, `LoginPage`, `LoginPageTests`,
`TestLifecycleListener`, `LogUtil`, and `ScreenshotUtil` implementations.

**Next stage:** design review, followed by implementation planning and
verification.
**Suggested commit message:** `[Architecture] Propose Selenium test automation architecture`

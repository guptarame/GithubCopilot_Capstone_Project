# Architecture - Selenium Automation (Java + Maven)

## Technology Choices
- Language: Java 21
- Build: Maven
- Test Framework: JUnit 5
- UI Automation: Selenium WebDriver 4
- Driver Provisioning: WebDriverManager

## High-Level Components
- `config.TestConfig`
  - Reads runtime settings from system properties/environment variables.
  - Controls base URL, browser, headless mode, and credentials.
- `base.BaseTest`
  - Creates and tears down WebDriver per test.
  - Applies common browser setup and timeouts.
- `pages.LoginPage`
  - Encapsulates locators and actions for the My Account login UI.
  - Exposes page behavior methods used by tests.
- `tests.LoginPageTests`
  - Implements acceptance scenarios TS-LOG-001 to TS-LOG-006.
  - Performs assertions against login behavior.

## Data Flow
1. Maven starts JUnit tests.
2. `BaseTest` builds WebDriver with settings from `TestConfig`.
3. Tests call `LoginPage.open()` and perform scenario actions.
4. `LoginPage` methods interact with DOM and return visible messages/URLs.
5. JUnit assertions validate expected outcomes.

## Key Design Decisions
- Use Page Object Model to isolate selectors from test assertions.
- Keep successful-login credentials outside repo via environment variables.
- Use explicit waits to reduce flaky test behavior.
- Keep tests independent and deterministic where possible.

## Risks and Mitigations
- External site instability: use resilient assertions and wait strategy.
- Credential availability: skip valid-login scenario if env vars are missing.
- UI text changes: assert key phrases instead of exact long strings.


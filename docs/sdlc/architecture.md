# Selenium Test Automation Framework Architecture

**Project:** Selenium Login Automation Capstone  
**Feature:** US-AUTH-002 — Login & Authentication  
**Stack:** Java 21, Maven, JUnit 5, Selenium 4, WebDriverManager  
**Existing Codebase:** `BaseTest`, `TestConfig`, `LoginPage`, `LoginPageTests`  
**Date:** 2026-09-21

## 1. Objective and Scope

The goal is to keep the current Selenium framework practical, stable, and easy to extend for login-related testing. The architecture should support positive and negative authentication scenarios, work across supported browsers, and remain compatible with the existing Maven/JUnit structure.

**In scope**
- Login page validation for valid and invalid credentials
- Reusable browser setup and teardown
- Config-driven execution for local and CI runs
- Maintainable page objects and assertions
- Basic reporting and failure diagnostics

**Out of scope**
- Full framework rewrite
- Remote grid/cloud execution
- Advanced BDD layer
- Heavy custom reporting stack

## 2. Proposed Framework Layers / Components

### 2.1 Base Test Layer
**Responsibility:** Manage WebDriver lifecycle, browser options, and test-level setup/cleanup.

**Current fit:** `BaseTest`  
**Recommended behavior:**
- Create driver in `@BeforeEach`
- Quit driver in `@AfterEach`
- Centralize browser selection and common timeouts
- Avoid shared state between tests

**Dependencies:** Selenium WebDriver, WebDriverManager, JUnit 5

### 2.2 Page Object Layer
**Responsibility:** Encapsulate page locators and user actions.

**Current fit:** `LoginPage`  
**Recommended behavior:**
- Keep UI locators private to the page class
- Expose business methods such as `open()`, `login(username, password)`, `getErrorMessage()`, and `isLoaded()`
- Return page objects or self for fluent usage where useful

**Dependencies:** WebDriver, WebDriverWait

### 2.3 Configuration Layer
**Responsibility:** Resolve runtime settings and test credentials from properties/env/system values.

**Current fit:** `TestConfig`  
**Recommended behavior:**
- Resolve values in this order: system properties, environment variables, defaults
- Keep sensitive values out of source control
- Provide safe defaults for local execution

**Suggested config keys**
- `baseUrl`
- `browser`
- `headless`
- `validUsername`
- `validPassword`
- `invalidPassword`
- `unknownUsername`

### 2.4 Test Layer
**Responsibility:** Hold scenario-focused tests with clear assertions.

**Current fit:** `LoginPageTests`  
**Recommended behavior:**
- One scenario per test method
- Use Arrange-Act-Assert style
- Keep test methods readable and short
- Prefer descriptive method names and `@DisplayName`

### 2.5 Utility Layer
**Responsibility:** Provide shared helpers only where repetition exists.

**Recommended utilities**
- Wait helper for explicit waits
- Screenshot helper for failures
- Small assertion helper only if repeated logic appears

**Rule:** Do not add utilities unless the same logic appears in multiple places.

### 2.6 Reporting / Listener Layer
**Responsibility:** Capture failures and support diagnostics.

**Recommended approach**
- Use JUnit 5 extensions only if needed
- Keep initial setup simple
- Capture screenshots on failure if test instability appears
- Rely on Surefire reports for baseline reporting

## 3. Page Object and Configuration Strategy

### Page Object Strategy
The login page should remain the primary abstraction for UI interaction. Locators should stay in `LoginPage`, not in test classes.

**Recommended page object methods**
- `open()`
- `enterUsername(String username)`
- `enterPassword(String password)`
- `submit()`
- `getFeedbackMessage()`
- `isLoginFormVisible()`

This keeps the test code focused on intent rather than DOM details.

### Configuration Strategy
`TestConfig` should remain the single source of truth for execution settings and credentials.

**Credential strategy**
- Use real valid credentials only through environment variables
- Keep invalid credentials deterministic and non-sensitive
- Avoid hardcoding secrets in tests or properties files

**Example**
- Valid login: env-provided username/password
- Invalid login: valid username + known bad password
- Unknown user: clearly fake email + any password

## 4. Locator Strategy and Synchronization Approach

### Locator Strategy
Prefer stable locators in this order:
1. `id`
2. `name`
3. `cssSelector`
4. `xpath` only when necessary

**Guidelines**
- Avoid brittle absolute XPath
- Use page-specific locators
- Prefer semantic locators tied to labels, ids, or stable attributes

### Synchronization Strategy
Use explicit waits for UI state changes.

**Recommended**
- `WebDriverWait` for element visibility/clickability
- Wait for feedback message after submit
- Avoid `Thread.sleep()`
- Keep implicit wait minimal or avoid mixing with heavy explicit waits

**Synchronization targets**
- Login form ready
- Submit button clickable
- Validation/error message visible
- Successful navigation or post-login state ready

## 5. Data / Configuration Strategy

### Valid Credentials
- Supplied externally via environment variables
- Example:
  - `LOGIN_VALID_USERNAME`
  - `LOGIN_VALID_PASSWORD`

### Invalid Credentials
- Can be hardcoded or centrally defined
- Examples:
  - invalid password
  - unknown username
  - blank values for field validation

### Recommended test data grouping
- `positive` login data
- `negative` login data
- `validation` data for empty fields or malformed inputs

### Storage guidance
- Keep lightweight data in `TestConfig`
- Add test data classes only if scenarios expand
- Avoid spreadsheet or complex data frameworks at this stage

## 6. Test Case Grouping / Tagging Recommendations

Use JUnit 5 tags to support selective execution.

**Recommended tags**
- `smoke` — core login success path
- `negative` — invalid credential scenarios
- `regression` — complete login coverage
- `auth` — authentication-related suite grouping

**Suggested usage**
- Smoke: valid login only
- Regression: valid + invalid + boundary cases
- Negative: only failure scenarios

This allows CI and local execution to target the right scope without changing code.

## 7. Error Handling / Assertion Strategy

### Error Handling
- Let unexpected failures fail fast
- Catch exceptions only when adding value to diagnostics
- Capture screenshots on failure if needed
- Re-throw after logging so test results remain accurate

### Assertion Strategy
- Use JUnit 5 assertions as the default
- Keep assertions close to the business outcome
- Prefer one main assertion per scenario where possible
- Include meaningful failure messages

**Examples**
- Verify success message or logout state after valid login
- Verify error message for invalid credentials
- Verify field validation for blank submission

## 8. CI / Execution Considerations

### Local Execution
- Default browser: Chrome
- Allow browser override via system property
- Support headless mode for quick validation

### CI Execution
- Run headless by default
- Use environment variables for credentials
- Keep execution deterministic and isolated
- Publish Surefire reports as build artifacts

### Maven execution examples
- `mvn test`
- `mvn test -Dbrowser=firefox`
- `mvn test -Dheadless=true`

### Reliability considerations
- Use fixed, explicit waits
- Keep tests independent
- Avoid order dependency
- Limit retries unless flakiness is proven

## 9. Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| Flaky UI timing | Test instability | Use explicit waits and stable locators |
| Hardcoded credentials | Security and maintenance issue | Use env vars for valid credentials |
| Brittle locators | Frequent failures | Prefer id/name/css over XPath |
| Shared test state | Order-dependent failures | Reinitialize browser per test |
| Over-engineering | Slower delivery | Keep current layered design small |
| CI environment differences | Browser inconsistencies | Use headless-safe options and consistent timeouts |

## 10. Short Implementation Roadmap

### Phase 1: Stabilize Existing Structure
- Keep `BaseTest`, `TestConfig`, `LoginPage`, `LoginPageTests`
- Confirm setup/teardown is consistent
- Standardize waits and browser options

### Phase 2: Improve Page Object Quality
- Refine `LoginPage` locators
- Add explicit wait-based page methods
- Ensure tests use only page methods, not raw WebDriver calls

### Phase 3: Strengthen Configuration
- Finalize system property and environment variable precedence
- Document required credentials and browser options
- Add safe defaults for local runs

### Phase 4: Organize Test Execution
- Add JUnit 5 tags
- Group smoke and negative login tests
- Update Maven Surefire configuration if needed

### Phase 5: Add Diagnostics
- Add screenshot capture on failure if the suite grows
- Keep reporting simple and Maven-native

## 11. Summary

This architecture keeps the current project structure intact while making it more maintainable and CI-friendly. It relies on the existing `BaseTest`, `TestConfig`, `LoginPage`, and `LoginPageTests` classes, adds only minimal supporting structure, and uses standard Selenium/JUnit/Maven patterns for reliable login automation.

# Selenium Login Automation Architecture

## Context and Overview

This Stage 2 proposal defines the test architecture for `US-AUTH-002` and the six scenarios in [requirements.md](requirements.md). The system under test is the My Account login page at `https://askomdch.com/account/`; execution requires network access and a registered account for credential-dependent scenarios.

The project is a Java 21, Maven, Selenium 4, JUnit 5 test suite. Preserve its existing package layout and dependencies. Each test gets a fresh local WebDriver through `BaseTest`; page objects own selectors and user actions; `TestConfig` supplies runtime settings; JUnit extensions provide lifecycle logging, failure diagnostics, and Extent reporting. Tests run sequentially. No application implementation, remote grid, or parallel execution is proposed.

Configured browser choices and CI jobs are not proof that either browser has been exercised successfully. Runtime support is limited to the current Chrome and Firefox driver paths, subject to installed browsers, WebDriverManager resolution, operating system, and CI environment. Record actual verification evidence in the later verification stage.

## Components

| Component / key classes | Responsibility | Inputs | Outputs | Dependencies |
| --- | --- | --- | --- | --- |
| Test configuration: `TestConfig` | Resolve and validate URL, browser, headless mode, timeouts, credentials, and diagnostic flags. | Maven system properties, environment variables, documented defaults. | Validated settings or a clear configuration error. | `TestData` defaults; Java URI APIs. |
| Test data: `TestData` | Keep wait defaults and message-match tokens separate from test logic. | Project-defined constants. | Timeout values and accepted message tokens. | Java collections. |
| Driver lifecycle: `BaseTest` | Create/configure the browser before each test, register it for listeners, and quit it afterward; provide a profile-preserving restart for Remember Me. | `TestConfig`, JUnit test metadata. | Isolated `WebDriver`; temporary profile only for Remember Me test. | Selenium Chrome/Firefox drivers; WebDriverManager; lifecycle listener. |
| Page abstraction: `BasePage` | Provide shared navigation, explicit-wait, visibility, typing, click, and URL helpers. | `WebDriver`, locators, conditions. | UI operations and observed state. | Selenium `WebDriverWait`; `TestData` wait default. |
| Login page: `LoginPage` | Encapsulate login controls, selectors, form actions, visible feedback, login state, and recovery navigation. | Driver; configured login URL; test inputs. | Page state, feedback text, and navigation result. | `BasePage`; Selenium locators and expected conditions. |
| Scenario tests: `LoginPageTests` | Express independent assertions for TS-LOG-001 through TS-LOG-008 and required control visibility (`AC-UI-001`). | `TestConfig`, `TestData`, `LoginPage`, `CredentialTestGuard`, `LoginScenarioAssertions`. | JUnit pass, fail, or assumption-aborted result. | JUnit Jupiter assertions and assumptions. |
| Credential guard: `CredentialTestGuard` (+ `CredentialTestGuardTests`) | Centralize the fail-vs-skip decision for credential-dependent scenarios so a missing secret cannot silently pass. | Candidate username/password, `requireCredentialTests()` flag, scenario label. | JUnit hard failure (`MISSING_REQUIRED`) or `Assumptions.assumeTrue` skip (`OPTIONAL_SKIP`); never a pass without the condition being met. | `TestConfig.CredentialStatus`; JUnit `Assumptions`. |
| Scenario assertion helper: `LoginScenarioAssertions` (+ `LoginScenarioAssertionsTests`) | Hold the shared invalid-password outcome assertion (error token, unauthenticated state, form visibility) so the negative scenario cannot pass with disabled checks. | Feedback message, login state, form-visibility flags. | JUnit assertion pass/fail. | `TestData` message tokens; JUnit assertions. |
| Suite entry: `LoginPageRunner` | Provide an explicit JUnit Platform suite for `LoginPageTests`. | Selected test class. | Suite execution. | JUnit Platform Suite. |
| Lifecycle diagnostics: `TestLifecycleListener`, `TestOutcome`, `LogUtil` (+ `TestLifecycleTests`) | Record test start, duration, result (`PASSED`/`FAILED`/`SKIPPED`, distinguishing `TestAbortedException` skips from failures), and redacted diagnostic messages. | JUnit callbacks and current driver registration. | Console log entries. | JUnit extensions; `TestConfig`. |
| Configuration unit tests: `TestConfigTests` | Verify non-secret precedence, timeout bounds, transport validation, and credential-status logic without exercising a browser. | `TestConfig` static methods and package-private helpers. | Fast JUnit pass/fail feedback independent of WebDriver/network. | JUnit Jupiter assertions. |
| Screenshot and report: `ScreenshotUtil`, `ExtentReportExtension` | On failure, optionally capture redacted screenshots, record outcomes, and flush the HTML report. | JUnit result, current driver, opt-in flag, report path property. | `target/screenshots/` images and `target/extent-reports/ExtentReport.html` by default. | Selenium screenshot APIs; ExtentReports. |
| Build and CI: Maven Surefire, `selenium-login.yml` | Compile and discover tests; CI runs serial Chrome/Firefox matrix jobs in headless mode and uploads available reports. | Maven configuration, CI secrets, workflow inputs. | Test results and uploaded artifacts when a workflow is run. | Maven; Java 21; GitHub Actions. |

## Directory Structure

```text
src/test/java/Github_Copilot/
  base/BaseTest.java
  config/TestConfig.java
  config/TestConfigTests.java            unit tests for config precedence/validation
  data/TestData.java
  listeners/ExtentReportExtension.java
  listeners/TestLifecycleListener.java
  listeners/TestOutcome.java             pass/fail/skip classification used by the listener
  listeners/TestLifecycleTests.java      unit test for outcome classification
  pages/BasePage.java
  pages/LoginPage.java
  tests/LoginPageRunner.java
  tests/LoginPageTests.java
  tests/CredentialTestGuard.java         fail-vs-skip guard for credential-dependent scenarios
  tests/CredentialTestGuardTests.java
  tests/LoginScenarioAssertions.java     shared invalid-password outcome assertion
  tests/LoginScenarioAssertionsTests.java
  utils/LogUtil.java
  utils/ScreenshotUtil.java
docs/sdlc/
  requirements.md
  architecture.md
  design-review.md
  impl-plan.md
  verification-report.md
target/                         generated Surefire, Extent, and screenshot output
```

The `CredentialTestGuard`, `LoginScenarioAssertions`, `TestOutcome`, and the package-local `*Tests` unit-test classes were added during Stage 4/5 implementation to close Stage 3 design-review conditions (credential-sourcing safety and effective invalid-password assertions). They are documented here so the architecture stays current with the verified implementation; no further new component is proposed by this revision.

No `src/test/resources` files were found in the inspected project. Keep scenario data in the existing `TestData` / `TestConfig` abstractions unless test resources become necessary; do not add a data framework for the current scenarios.

## Configuration and Technology Decisions

Use existing project versions and dependencies: Java 21, Maven, Selenium 4, WebDriverManager, JUnit Jupiter / Platform Suite, ExtentReports, and Maven Surefire. Do not add Hamcrest or another reporting, driver, or configuration library for this scope. Surefire 3.5.0 currently includes `**/*Tests.java`, so `mvn test` discovers `LoginPageTests` (browser-driven scenarios) together with the browser-independent `TestConfigTests`, `CredentialTestGuardTests`, `LoginScenarioAssertionsTests`, and `TestLifecycleTests`. Run `mvn -Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests test` for fast, driver-free feedback. The suite class `LoginPageRunner` is an explicit suite entry and does not match that include by its `*Runner` name.

For non-secret settings, preserve the implemented precedence:

```text
nonblank Java system property -> nonblank environment variable -> documented default
```

Current examples include `baseUrl` / `BASE_URL` / HTTPS site default, `browser` / `BROWSER` / `chrome`, `headless` / `HEADLESS` / `false`, and page-load timeout / `PAGE_LOAD_TIMEOUT_SECONDS` / 10 seconds. Invalid timeout values fall back to the default; only HTTPS is accepted except explicitly enabled local HTTP. Implicit wait remains zero; page-load timeout and page-object explicit waits are bounded (currently 10 seconds by default).

Credentials require stricter treatment than ordinary settings: supply `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD` only through environment variables or CI secrets; never place them in source, committed files, logs, screenshots, or command-line arguments. `TestConfig.validUsername()` / `validPassword()` now read exclusively from `System.getenv(...)` with no system-property path and no non-empty fallback value, so an absent environment variable yields an empty string rather than a usable default (Stage 3 Condition 1, closed and confirmed in Stage 6 verification). `CredentialTestGuard` turns that absence into either a hard JUnit failure (when `requireCredentialTests()` is true) or an explicit `Assumptions.assumeTrue` skip (when false); a skipped test is never reported as a pass. Keep safe invalid-test values (`invalidPassword`, `unknownUsername`) configurable through the ordinary system-property/environment/default precedence, since those are not secrets. Set `REQUIRE_CREDENTIAL_TESTS=true` / `requireCredentialTests=true` in CI so missing credentials fail visibly instead of silently skipping.

Other existing opt-ins are `ALLOW_FAILURE_SCREENSHOTS` (default false) and `ALLOW_INSECURE_LOCAL_BASE_URL` (default false). `extentReportPath` is a Java system property for the report destination. The report extension currently reports browser/headless system-property values as metadata; ensure metadata does not expose secrets.

## Test Execution Flow

1. `mvn test` compiles the project and Surefire selects every class matching `**/*Tests.java`, which currently includes `LoginPageTests` plus the driver-free `TestConfigTests`, `CredentialTestGuardTests`, `LoginScenarioAssertionsTests`, and `TestLifecycleTests`. CI uses `mvn --batch-mode test`, Java 21, serial Chrome/Firefox matrix jobs, and `-Dheadless=true -DrequireCredentialTests=true`.
2. JUnit runs `TestLifecycleListener` and `ExtentReportExtension` callbacks for every discovered test; `BaseTest` creates a fresh Chrome or Firefox driver using WebDriverManager only for the browser-driven `LoginPageTests` (the four `*Tests` unit-test classes exercise `TestConfig`, `CredentialTestGuard`, `LoginScenarioAssertions`, and `TestOutcome` directly, without a `WebDriver`). `BaseTest` applies page-load timeout, zero implicit wait, and viewport/headless settings, then registers the driver.
3. Each browser scenario opens the configured URL through `LoginPage`, performs actions through page-object methods, and asserts observable page state and feedback. `BasePage` uses explicit visibility/clickability/condition waits; do not add `Thread.sleep()`. Credential-dependent scenarios (`TS-LOG-001`, `TS-LOG-002`'s known-username precondition, `TS-LOG-005`) call `CredentialTestGuard` first so a missing secret fails loudly in required mode instead of passing.
4. The Remember Me scenario uses a temporary browser profile, authenticates with Remember Me enabled, quits and recreates the browser using that profile, then checks the restored session. Teardown deletes the temporary profile best-effort.
5. Listener callbacks log outcomes and durations. On a failure, Extent records the failure; screenshot capture is opt-in and redacts password, email, and user-like fields before writing the image. `BaseTest` always quits the driver and clears its registration.
6. Surefire writes machine-readable/text reports under `target/surefire-reports/`; Extent flushes its HTML report under `target/extent-reports/` by default. CI uploads Surefire output when present and screenshots for failed jobs when present. These paths describe configured behavior, not evidence of a successful run.

## Requirement Traceability

| Requirement / scenario | Architectural coverage | Existing implementation / noted gap |
| --- | --- | --- |
| FR-1, TS-LOG-004/005/006; UI controls | `LoginPage` owns username, password, Remember Me, Log in, and recovery locators; `LoginPageTests` verifies presence. | `AC-UI-001` passed against the live site in both Chrome and Firefox (Stage 6, 2026-10-02); all five controls verified visible. |
| FR-2, TS-LOG-004/007/008 | Page object exposes per-field `validationMessage`/`reportValidity` checks; tests assert both-blank, blank-username, and blank-password cases, unauthenticated state, and login form visibility. | **Recorded project deviation:** Existing requirements and verification artifacts record browser-native required-field validation as accepted in place of the PRD's application-rendered inline errors (2026-10-02); a separate approval record was not retrieved for this analysis. TS-LOG-004/007/008 passed in Chrome and Firefox under this deviation; the source PRD's inline-error wording itself remains unmet by design, not by gap. |
| FR-3, TS-LOG-001 | `CredentialTestGuard` requires valid credentials before the scenario runs; submit and wait for dashboard state; assert `isLoggedIn()`, `hasCustomerIdentifyingWelcome()`, and `isLogoutVisible()`. | **Unverified at Stage 6:** the required-credential guard correctly failed (not skipped) because the Maven process did not receive `LOGIN_VALID_USERNAME`/`LOGIN_VALID_PASSWORD` at runtime, even though they were reportedly configured locally. Valid-login and customer-identity behavior have not been observed to pass. Supplying the credentials to the Maven process and rerunning is the next action. |
| FR-4, TS-LOG-002/003 | `LoginScenarioAssertions.assertInvalidPasswordOutcome` asserts an error token, unauthenticated state, and retained login form for invalid-password; `LoginPageTests` asserts the equivalent for unknown-user directly. | TS-LOG-003 (unknown user) passed in Chrome and Firefox. TS-LOG-002 (invalid password) requires a known valid username via `CredentialTestGuard.requireUsername`; it failed at that guard in both browsers for the same credential-availability reason as FR-3, so live rejection behavior is not yet observed, though the assertion helper's own unit test (`LoginScenarioAssertionsTests`) passed. |
| FR-5, TS-LOG-005 | Dedicated persistent-profile restart scenario in `BaseTest` / `LoginPageTests`, gated by `CredentialTestGuard`. | Same unmet credential precondition as FR-3; profile-restart and session-persistence behavior have not yet been exercised against the live site. |
| FR-6, TS-LOG-006 | Page object activates recovery link and waits for supported recovery URL; test verifies destination and unauthenticated state. | Passed in both Chrome and Firefox (Stage 6); recovery URL matching reached the expected destination pattern. |
| US-AUTH-002 | Scenario suite represents registered-user sign-in and access to account dashboard. | Public (non-credential) scenarios pass; credential-dependent acceptance paths (valid login, invalid-password-with-known-user, Remember Me) remain unverified pending credential delivery to the test process. |
| NFR-1 | No invented performance/security threshold; credential handling, transport validation, redaction, and diagnostic opt-ins are defined above. | The PRD supplies no measurable thresholds. Credential sourcing is now environment/secret-only with no source or property fallback (Stage 3 Condition 1, closed); remaining gaps are evidentiary (Stage 6), not architectural. |

## Failure Handling, Security, and Scalability

- Fail clearly on unsupported browser names, invalid non-local HTTP URLs, driver creation errors, assertion failures, and required credential absence. Use bounded explicit waits and retain the original test failure as authoritative; profile deletion remains best-effort.
- Keep each test isolated with a fresh driver. Keep current execution sequential; JUnit listener/report state and shared output paths are not being designed or claimed as parallel-safe. Remote execution and parallelism are future work only if execution time or infrastructure needs justify them.
- Use environment/CI secret injection for credentials and redact secrets in logs and screenshots. Screenshots are disabled by default because page content can be sensitive; enabled screenshots remain diagnostic artifacts and must follow CI retention/access controls.
- Use HTTPS for the target. Allow HTTP only for explicitly opted-in local hosts. Do not infer measurable security or performance guarantees from these safeguards; the PRD leaves those acceptance criteria unspecified.
- Treat CI workflow documentation and configuration as intended execution setup, not proof of a passing run. Claims of Chrome/Firefox, cross-browser, live-site, or Remember Me support require corresponding observed test results.

## Success Criteria and Stage Gate

Architecture is ready for Stage 3 review when each FR-1 through FR-6 maps to a page/test responsibility, test data and configuration are injected without committed secrets, driver and report lifecycles remain centralized, and implementation gaps above are explicit in the implementation handoff. Overall feature success remains the six scenario outcomes defined in [requirements.md](requirements.md), including successful login, both invalid credential cases, blank-field feedback, persistent Remember Me session, and password recovery navigation. Performance/security thresholds remain unspecified until the product owner clarifies them.

This is the Stage 2 proposal only. Human review and approval are required before Stage 3; no later stage is started by this artifact.
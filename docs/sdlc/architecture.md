# Stage 2 Architecture Proposal — Customer Login Automation

**Proposal date:** 2026-11-18  
**Status:** **Approved by the user on 2026-11-18** for Stage 3 design review. This approval did not resolve the open product decisions or authorize implementation by itself. See the [design review](design-review.md) and [implementation plan](impl-plan.md) for subsequent decisions.

> **Historical decision boundary:** The previous version recorded an architecture proposal and approval dated 2026-11-03 for a different requirements baseline. That approval is historical and does not approve this proposal or its newly supplied PRD baseline. No prior implementation, review, or verification status is carried forward as evidence for this proposal.

## 1. Context, goals, scope, and assumptions

This architecture covers the Java 21 / Maven / Selenium 4 / JUnit 5 automation framework for the external customer login flow at `https://askomdch.com/account/`. It does not design or change the website or authentication service.

The Stage 1 requirements contain FR-1..FR-5 and only three source scenarios, TS-LOG-001..003. Their provenance is limited: they were prepared from PRD text supplied directly in conversation. No Confluence page ID, source URL, date, or version was supplied, and the page was not independently retrieved or verified. The portal URL is not PRD source verification.

The architecture goal is to preserve the existing framework boundaries, provide isolated sequential tests of observable outcomes, and clearly separate source criteria from supplementary project regressions. It is not a claim that the external site or any browser scenario has been exercised.

### Open decisions / blockers

1. **Fixed-credential conflict (blocking for source-compliant TS-LOG-001):** FR-3/TS-LOG-001 refers to fixed credentials specified in the supplied PRD (redacted). Repository policy requires valid credentials to be sourced only from runtime environment variables; no deviation is approved. The safe recommendation is to keep valid credentials and expected identity environment-only and never seek, reproduce, hardcode, pass on a command line, log, report, capture, or commit the supplied values. A human must reconcile the source criterion with policy before implementation or verification can claim TS-LOG-001 source compliance. Environment-provided credentials are a safe framework interface, but are not automatically proof that the PRD's fixed-credential criterion has been met.
2. **Precondition ambiguity:** The general precondition says there is a registered account, but TS-LOG-003 tests an unregistered identifier. This design keeps TS-LOG-003 as a source scenario but does not silently resolve the conflict. Clarify whether the registered-account precondition applies only to TS-LOG-001/002.
3. **Unspecified acceptance details:** No supported-browser set, exact message copy, reset destination/flow behavior beyond navigation, Remember Me semantics, or measurable NFR target is supplied. Do not invent these. Browser support and interpretation of semantic error/welcome assertions require human/product confirmation if exactness is expected.

## 2. Architecture overview

Maven Surefire discovers JUnit tests. JUnit extensions record outcomes and report results. Browser cases extend `BaseTest`, which creates and releases a local WebDriver. Tests use `LoginPage` for the login-page boundary; `BasePage` centralizes explicit waits and shared browser actions. `TestConfig` resolves runtime settings and enforces URL transport policy; scenario assertions remain in tests/helpers.

```text
Maven/Surefire
    └── JUnit tests + lifecycle/report extensions
          ├── TestConfig / TestData / scenario assertions
          └── BaseTest ── WebDriver ── LoginPage ── BasePage
                                           │
                                  external login site
          └── logs / Surefire results / Extent report / optional screenshot
```

### Component responsibilities

| Component / key classes | Responsibility and inputs → outputs | Dependencies / boundary |
|---|---|---|
| Maven / Surefire (`pom.xml`) | Compile and select test classes (`**/*Tests.java`); write Maven test results. | Java 21, Maven, JUnit Platform. `LoginPageRunner` does not match the configured `*Tests.java` selector. |
| `config/TestConfig.java` | Resolve non-secret settings; validate URL transport; expose valid credentials and welcome identity from environment only. | Java system/environment APIs, `TestData`; no credential properties or defaults. |
| `data/TestData.java` | Bounded wait defaults and feedback tokens used by assertions. | Java collections; no external test-data file currently exists. |
| `base/BaseTest.java` | Create/configure/register per-test local Chrome or Firefox WebDriver; quit, clear registration, and best-effort delete temporary Remember Me profile. | Selenium, WebDriverManager, `TestConfig`, lifecycle listener. |
| `pages/BasePage.java` | Shared navigation, interaction, state access, and bounded explicit waits. | Selenium WebDriver / `WebDriverWait`; implicit wait remains zero. |
| `pages/LoginPage.java` | Login controls/selectors and UI actions; expose login, error, dashboard, and recovery observations. | `BasePage`, Selenium; external page is the integration boundary. |
| `tests/LoginPageTests.java` | One isolated test per source scenario and separately labeled supplemental regression; orchestrate page actions and assertions. | JUnit, `CredentialTestGuard`, `LoginScenarioAssertions`, `TestConfig`, `TestData`. |
| `tests/CredentialTestGuard.java` | Distinguish missing runtime prerequisites as optional skips or required failures without revealing values. | `TestConfig`, JUnit assumptions/assertions. |
| `tests/LoginScenarioAssertions.java` | Centralize semantic outcome assertions without prescribing unspecified exact message copy. | `TestData`, JUnit assertions. |
| `listeners/TestLifecycleListener.java` / `ExtentReportExtension.java` | Record test name, outcome, duration; produce local Extent report; optionally request failure screenshot before teardown. | JUnit extensions, `LogUtil`, `ScreenshotUtil`, ExtentReports. |
| `utils/LogUtil.java` / `ScreenshotUtil.java` | Safe diagnostic logging and opt-in screenshot handling. | Selenium and filesystem; field redaction is limited and is not proof that page content is safe. |

These are existing classes and dependencies inspected in the repository; the table describes their current responsibilities and intended framework boundary, not verified execution success.

## 3. Directory layout and integration boundaries

```text
src/test/java/Github_Copilot/
  base/       BaseTest.java, lifecycle tests
  config/     TestConfig.java, configuration tests
  data/       TestData.java
  listeners/  TestLifecycleListener, ExtentReportExtension, outcome model
  pages/      BasePage.java, LoginPage.java, page tests
  tests/      LoginPageTests, credential guard, scenario assertions, unit tests
  utils/      LogUtil.java, ScreenshotUtil.java
src/test/resources/   not present in the inspected repository
docs/sdlc/            requirements and this proposal
target/                generated Surefire/Extent output and optional screenshots
```

External integrations are the remote login portal, a locally installed browser and its driver (managed through WebDriverManager), and network access. No application source, API, database, or authentication-service implementation is in scope. `pom.xml` currently uses Selenium 4.25.0, WebDriverManager 6.1.0, JUnit Jupiter 5.11.3 / Platform 1.11.3, ExtentReports 5.1.2, and Surefire 3.5.0, all already present as test dependencies. No dependency addition is proposed.

Repository inspection found no `.github/workflows/` directory or configured CI workflow. Consequently there is no configured CI execution, secret injection, or artifact-retention behavior to describe. The `.github/hooks` pre-commit document is a proposed specification, not an installed hook.

## 4. Execution, configuration, and data flow

### Execution lifecycle

1. Maven compiles and Surefire selects `**/*Tests.java`. `mvn test` includes browser tests as well as browser-independent unit tests; do not treat a configured selector as evidence of a test run.
2. JUnit extensions record lifecycle/outcome information. Each browser test receives its own `BaseTest` setup and local driver. `BaseTest` applies the configured page-load timeout, zero implicit wait, and viewport/headless options, then registers the active driver.
3. `LoginPage` opens the configured base URL. Tests provide scenario data and invoke page-object actions; shared interactions/waits stay in `BasePage`. Bounded explicit waits observe submission outcomes; assertions verify only the stated requirement semantics.
4. On completion or failure, JUnit records pass/fail/aborted status. `@AfterEach` attempts `driver.quit()`, clears driver registration, and deletes any temporary profile used by the supplementary Remember Me restart case. Report/screenshot hooks must not hide the original test result.
5. Surefire output and Extent report are local generated artifacts (Extent defaults to `target/extent-reports/ExtentReport.html`). Failure screenshots are opt-in and disabled by default. No CI artifact upload or retention policy is configured.

Run browser tests only when internet/site access, a usable local browser/driver, and any required runtime settings are available. Missing credential prerequisites in optional mode are skips, not passes; required-credential mode makes them visible failures. A browser absent from test configuration is not thereby supported by the PRD.

### Configuration and sensitive test data

- **Non-secret settings:** nonblank Java system property → environment variable → default. Existing examples include `baseUrl`/`BASE_URL`, `browser`/`BROWSER`, `headless`/`HEADLESS`, page-load timeout, required-credential mode, local-HTTP opt-in, and screenshot opt-in. Avoid putting any secret values in property or command literals.
- **Valid account and identity:** `LOGIN_VALID_USERNAME`, `LOGIN_VALID_PASSWORD`, and `LOGIN_EXPECTED_WELCOME` are read only from runtime environment. Do not expose values in source, commands, logs, assertion text, reports, screenshots, examples, or committed artifacts. These values must not be accessed for this architecture task.
- **Negative-case data:** invalid-password and unknown-account test inputs are non-secret scenario data; use the existing configuration interfaces and avoid logging the inputs. TS-LOG-002 needs an authorized known-account username; TS-LOG-003 needs a genuinely unregistered identifier once its precondition ambiguity is clarified.
- **Transport:** HTTPS is required. HTTP is accepted only for an explicitly opted-in local host (`ALLOW_INSECURE_LOCAL_BASE_URL=true`). Never disable this control for an external site.
- **Waits:** existing explicit waits are bounded (10-second defaults); page-load timeout is bounded, and implicit wait is zero. Keep waits explicit and bounded; no fixed sleeps or unbounded retry are proposed.

## 5. Requirements traceability and acceptance boundary

| Requirement / source scenario | Architectural coverage and existing framework mapping | Limits / status |
|---|---|---|
| FR-1 — username/email `id=username`, password `id=password`, Remember Me checkbox, Log in `name=login` | Selectors and state/actions belong in `LoginPage`; existing `REG-CONTROLS` checks the controls. | `REG-CONTROLS` is supplementary; presence assertions are not a new source scenario. Do not infer Remember Me behavior. |
| FR-2 — “Lost your password?” link to reset flow | `LoginPage` owns link and navigation observation; existing `REG-RECOVERY` checks navigation/form presence. | Supplemental regression, not one of TS-LOG-001..003. No destination is prescribed here; do not infer reset completion. Confirm the observed destination against product acceptance. |
| FR-3 — valid source-specified credentials, dashboard/My Account home, welcome message; TS-LOG-001 | `LoginPageTests` plus `LoginScenarioAssertions` cover authentication, dashboard, and welcome observation. Credential guard requires environment inputs. | **Blocked from source-compliance claim** by fixed-credential policy conflict and absent approved deviation. Do not reproduce or seek the fixed values. |
| FR-4 — valid account, wrong password, remain on login page, incorrect-password notice at top; TS-LOG-002 | `LoginPageTests`, page-level top-of-form observation, and semantic assertion helper. | Preserve source meaning, not invented exact copy. Requires authorized valid-account username and live-site evidence. |
| FR-5 — unregistered username/email remains on login page with account-not-found error; TS-LOG-003 | `LoginPageTests` and semantic unknown-account assertion helper. | Open registered-account precondition conflict. Requires verification that test identifier is unregistered; avoid implying a pass without a run. |
| Non-functional requirements | Bounded waits, HTTPS policy, safe data boundaries and test isolation are framework safeguards. | Requirements specify no measurable NFR. Safeguards do not establish security, performance, reliability, accessibility, or browser-compatibility targets. |

**Source scenarios remain exactly TS-LOG-001, TS-LOG-002, and TS-LOG-003.** Existing project-only regressions are separate: `REG-NATIVE-BOTH`, `REG-NATIVE-USERNAME`, `REG-NATIVE-PASSWORD`, `REG-REMEMBER-RESTART`, `REG-RECOVERY`, and `REG-CONTROLS`. They are not additional PRD criteria and cannot resolve the fixed-credential or registered-account conflicts.

## 6. Technology decisions, errors, security, cleanup, and scale

- Retain Java 21, Maven/Surefire, Selenium 4, WebDriverManager, JUnit 5, and ExtentReports; they already support this local browser-test scope. Avoid speculative dependencies, remote grids, parallelism, or additional browsers.
- Chrome and Firefox driver creation are present in `BaseTest`; this is an implementation capability only. The source does not name its supported-browser set, and no browser execution is claimed.
- Keep each test isolated and sequential. Existing shared reporting state and output paths are not designed here as parallel-safe. Remote execution, parallel runs, and adding browsers are future work only if required.
- Invalid configuration, unsupported browser values, driver startup failures, assertion failures, and explicit-wait timeouts should remain visible. Missing optional credentials result in a skip; required-but-missing inputs fail visibly. Preserve distinctions in reports.
- Teardown should run regardless of assertion outcome. Driver quit, registration cleanup, temporary-profile removal, logging, reporting, and screenshot capture are best-effort diagnostics; their failure must not mask the primary test outcome. Residual profile cleanup failures should be surfaced without emitting sensitive values.
- Logs should contain safe test metadata only. Although `LogUtil` attempts credential redaction, prevention is preferable to relying on redaction. Avoid exception/message data that could disclose inputs or page content.
- Failure screenshots remain disabled by default. Existing selected-field redaction does not sanitize the rest of the page or guarantee safe reports; review the full page and artifact storage before enabling. Keep generated files on controlled local storage and do not upload or share them without authorization.
- No concurrency, remote browser, CI, cross-browser, live-site, performance, or reliability evidence is asserted. External site changes, availability, network conditions, browser/driver compatibility, and account state are environment-dependent limits.

## 7. Implementation success criteria and handoff

The approved architecture establishes the following implementation and verification criteria. The [implementation plan](impl-plan.md) and [verification report](verification-report.md) record execution evidence separately:

1. Preserve the established component boundaries and map FR-1..FR-5 to the page objects, configuration, tests, and assertions above.
2. Keep the three source scenarios distinct from supplementary regression tests and reports; do not add inferred source criteria.
3. Retain safe non-secret configuration precedence, environment-only valid credentials/identity, HTTPS-by-default transport, explicit local HTTP opt-in, bounded waits, isolated sequential execution, and complete teardown attempts.
4. Keep credentials and sensitive page data out of commands, logs, assertions, screenshots, reports, and committed files; keep screenshots off unless explicitly reviewed.
5. Record the fixed-credential conflict and the registered-account/TS-LOG-003 ambiguity as unresolved, obtain human decisions before claiming conformance, and do not treat framework readiness as execution evidence.
6. Describe CI only if a workflow is later configured and inspected. Report actual executed checks and browser results separately from architecture/configuration.

**Open product decisions:** resolve the credential-policy conflict without disclosing the PRD values; clarify the account precondition for TS-LOG-003; and confirm whether the unspecified reset destination, message semantics, and supported browsers need product-level criteria. The architecture approval and later acceptance of the limitations did not resolve these decisions or establish source conformance.

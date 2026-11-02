# Stage 2 Architecture Proposal — Fresh Source Reconciliation (2026-11-03)

**Current status: User-approved on 2026-11-03 at 00:33 (+05:30); Stage 3 design review — APPROVED WITH CONDITIONS, with planning conditions resolved/accepted. Stages 4, 5, and 6 are complete as scoped deliverables. The user accepted Stage 6 PASS WITH LIMITATIONS at 00:57 (+05:30), permitting Stage 7 progression; no Stage 4 or renewed architecture-approval gate remains for this factual correction.**

## Context, Goals, Scope, and Evidence

This proposal reconciles the existing Java 21 / Maven / Selenium 4 / JUnit 5 test framework with the current `docs/sdlc/requirements.md` artifact. It covers browser automation for the external My Account login page at `https://askomdch.com/account/`; the site and authentication service are outside project scope. The original Stage 2 task proposed no implementation, test, requirements, CI, or external-site changes. Subsequent implementation and verification are recorded in the current handoff below; this correction changes status documentation only.

**Source caveat:** requirements.md records a 2026-10-14 page read and a user-reported 2026-11-03 removal of AC 5.1 and TS-LOG-004. The updated Confluence page/version/date could not be independently verified in this session; the configured MCP API did not provide source content. Accordingly, the current mapping below follows the revised requirements artifact and user report, not a verified Confluence fetch. The historical wording is retained in requirements.md only. Its removal does not mean the former criterion was met.

**Active mapping in the current artifact:** AC 1.1–1.4 (login controls and password-reset navigation), AC 2.1–2.2 (successful login/dashboard/welcome), AC 3.1 (incorrect password), and AC 4.1 (unregistered account); source scenarios TS-LOG-001, TS-LOG-002, and TS-LOG-003. NFR-1 has no measurable performance or security thresholds. This inventory is not a claim that the source was re-fetched or any scenario passed.

Goals are to preserve established package boundaries, keep source acceptance distinct from project-only regression coverage, provide isolated sequential local tests, and make configuration, errors, cleanup, and diagnostic risks explicit. Browser-native blank-field validation remains supplementary project regression only; it is not an active source criterion and cannot establish compliance with former AC 5.1.

## Architecture Overview

Maven Surefire discovers JUnit tests. `BaseTest` owns per-test WebDriver setup/teardown; `TestConfig` resolves non-secret settings, environment-only credentials, and URL transport policy; page objects own selectors and browser actions; scenario tests and helpers assert observable outcomes. JUnit extensions classify outcomes and write Extent reports. Failure screenshots are opt-in and disabled by default. Browser cases depend on network/site availability, a local supported browser/driver, and authorized environment-only account data where needed.

```text
Maven Surefire -> JUnit extensions + scenario tests -> BaseTest/WebDriver
                                               |              |
                                       LoginPage -> BasePage -> external login page
                                               |
                              TestConfig + TestData; outcomes/reports
```

## Components and Boundaries

| Component / key classes | Responsibility | Inputs → outputs | Dependencies |
| --- | --- | --- | --- |
| `config/TestConfig.java` | Resolve and validate settings; credentials and expected identity are environment-only. | Non-secret property/env/default and credential presence → settings, transport validation, or guard inputs. | Java URI APIs, `TestData`. |
| `data/TestData.java` | Hold bounded timeout defaults and feedback tokens. | Framework constants → waits and assertion inputs. | Java collections. |
| `base/BaseTest.java` | Create/quit per-test local Chrome or Firefox drivers, configure timeouts/viewport, register driver; support a temporary profile for the supplemental restart regression. | `TestConfig`, JUnit metadata → driver; teardown → released driver/profile. | Selenium, WebDriverManager, listener. |
| `pages/BasePage.java`, `pages/LoginPage.java` | Shared bounded browser actions/waits; login selectors, interactions, and observable form/auth/recovery state. | Driver, URL, locator, test input → action/state. | Selenium WebDriver and explicit waits. |
| `tests/LoginPageTests.java` | Keep each source scenario isolated; retain project-only UI, recovery, Remember Me, and browser-validation regressions without treating them as source criteria. | Safe test data/config and page observations → JUnit result. | JUnit, `CredentialTestGuard`, `LoginScenarioAssertions`, `TestData`. |
| `tests/CredentialTestGuard.java`, `LoginScenarioAssertions.java` | Distinguish missing-prerequisite fail/skip and centralize behavioral assertions without exposing credentials/identity. | Credential presence and page state → guard decision/assertion result. | `TestConfig`, `TestData`, JUnit. |
| `listeners/TestLifecycleListener.java`, `TestOutcome.java`, `ExtentReportExtension.java`, `utils/LogUtil.java` | Record separate pass/fail/skip results and durations; write local logs/report. | JUnit callbacks, safe metadata → logs and Extent HTML. | JUnit, ExtentReports. |
| `utils/ScreenshotUtil.java` | Optional failure-only screenshot capture with selected form-field redaction. | Driver/test name → optional `target/screenshots/` artifact. Redaction is not page sanitization. | Selenium screenshot/JS APIs, filesystem. |
| Maven Surefire | Select test classes using configured `**/*Tests.java` include and write Surefire results. | Maven/JUnit classes → local reports. | Java 21, Maven, `pom.xml`. |

Keep selectors/actions in `LoginPage`, shared waits/actions in `BasePage`, driver lifecycle in `BaseTest`, runtime settings in `TestConfig`, and scenario assertions in tests/helpers. The external application is an integration boundary. No `src/test/resources` data or additional dependency is needed for current scope.

## Directory Layout and Integrations

```text
src/test/java/Github_Copilot/
  base/  config/  data/  pages/  tests/  listeners/  utils/
docs/sdlc/  requirements.md, architecture.md, and later-stage artifacts
target/     generated local Surefire, Extent, and optional screenshot output
```

`pom.xml` uses test-scoped Selenium 4.25.0, WebDriverManager 6.1.0, JUnit Jupiter 5.11.3 / Platform 1.11.3, ExtentReports 5.1.2, and Surefire 3.5.0. Surefire's `**/*Tests.java` include selects `LoginPageTests` and browser-independent `*Tests` classes; `LoginPageRunner` does not match that default include. No `.github/workflows/` directory, CI secret mapping, CI execution, or artifact upload is configured.

## Configuration and End-to-End Execution

Non-secret settings use **nonblank Java system property → environment variable → documented default**. This includes URL, browser, headless mode, page-load timeout, required-credential mode, local HTTP opt-in, screenshot opt-in, and non-secret invalid-test data. HTTPS is required; HTTP is allowed only for an explicitly opted-in local host. The current page-load timeout and explicit waits are bounded (10-second defaults); implicit wait is zero.

`LOGIN_VALID_USERNAME`, `LOGIN_VALID_PASSWORD`, and `LOGIN_EXPECTED_WELCOME` are read only from environment variables, never from properties or defaults. Do not put credential or identity values in source, command literals, logs, reports, screenshots, examples, or committed artifacts. Missing prerequisites produce explicit skips in optional mode or failures in required mode; a skip is not a pass.

1. Maven compiles and Surefire selects `**/*Tests.java`; `mvn test` includes browser scenarios and browser-independent unit tests.
2. JUnit lifecycle/report extensions begin tracking. For browser tests, `BaseTest` creates a fresh local driver through WebDriverManager, applies page-load/viewport options and zero implicit wait, then registers the driver.
3. Each test opens the configured URL through `LoginPage`, performs page-object actions with bounded `BasePage` explicit waits, and checks its own outcome. Credential guards run before cases that need valid credentials or a known account.
4. JUnit outcomes remain distinct: pass, assertion/driver failure, and assumption-aborted skip. `@AfterEach` quits the driver, clears registration, and best-effort removes the temporary profile where used. Cleanup/report diagnostic failures must not convert or conceal the primary test outcome.
5. Surefire writes to `target/surefire-reports/`; Extent writes to `target/extent-reports/ExtentReport.html` by default. Failure screenshots are opt-in and off by default. Artifacts are local; CI upload/retention is not configured.

## Technology Decisions, Risks, and Operational Limits

- Preserve Java 21, Maven, Selenium 4, WebDriverManager, JUnit 5, and ExtentReports; no added library, remote grid, parallel execution, or extra browser is justified.
- Keep execution isolated and sequential. Chrome and Firefox driver paths exist, but configuration/source inspection is not run evidence. Shared report/listener state and output paths are not claimed parallel-safe.
- Retain existing supplementary regressions: blank-both/blank-single-field browser validation, Remember Me restart, recovery flow, and control visibility. At proposal/review time these used legacy TS-LOG-004/007/008, TS-LOG-005/006, and AC-UI-001 labels. Completed R4-01 now identifies them as `REG-NATIVE-BOTH/USERNAME/PASSWORD`, `REG-REMEMBER-RESTART`, `REG-RECOVERY`, and `REG-CONTROLS`; only TS-LOG-001..003 remain source scenarios. Relabeling does not change the supplementary coverage boundary or fulfill former AC 5.1.
- Under the revised requirements artifact, source scenarios are TS-LOG-001..003. Current plan and verification mappings have been reconciled; older four-scenario/AC 5.1 mappings and review-time gate statements remain dated history, not current acceptance evidence. The reported deletion does not establish that the former inline-error criterion passed.
- No invented NFR targets: do not imply measurable performance, security, reliability, or cross-browser guarantees from framework safeguards or configuration.

## Error Handling, Security, and Cleanup

- Unsupported browsers, invalid URLs, driver startup errors, missing required credentials, failed assertions, and bounded-wait timeouts must remain visible failures/errors. Optional missing prerequisites are explicit skips.
- Teardown attempts `driver.quit()` and clears listener registration; temporary profile deletion and diagnostic capture are best effort. Preserve the original test outcome if cleanup or report diagnostics fail.
- Credentials and identity stay environment-only. Do not expose them in logs, reports, screenshots, or command/property literals. Screenshots are off by default; selected input redaction does not establish that the rest of an authenticated page is safe. Review page content and artifact handling before opting in.
- Keep HTTPS mandatory except explicit local HTTP opt-in. No CI, retention policy, or secret injection is configured.

## Current Requirements Traceability

| Active requirement / scenario in revised requirements artifact | Architecture and current code coverage | Acceptance boundary |
| --- | --- | --- |
| AC 1.1–1.4 / FR-1, FR-5, FR-6: controls, visible/clickable Log in, recovery navigation | `LoginPage` owns controls and selectors; UI regression checks control visibility; source scenarios exercise submit; supplemental `REG-RECOVERY` (formerly TS-LOG-006) clicks recovery and checks destination/form. | Traceability is distributed across cases; recovery navigation is required but no dedicated source scenario is listed. No evidence of browser execution is asserted here. |
| AC 2.1–2.2 / FR-3 / TS-LOG-001 | Valid-login test uses credential guard, asserts authentication, configured account-home URL/content, logout, and welcome identity via helper. | Architecture maps all source outcomes; live acceptance still requires fresh evidence with authorized runtime configuration. |
| AC 3.1 / FR-4 / TS-LOG-002 | Known-username guard and invalid-password assertion check feedback, unauthenticated state, retained form, and top-of-form notice. | Keep username secret; code coverage is not evidence of live behavior. |
| AC 4.1 / FR-4 / TS-LOG-003 | Unknown-user scenario checks error tokens, unauthenticated state, and retained login form. | Confirm acceptance against source wording and obtain fresh run evidence; no pass is claimed. |
| NFR-1 / US-AUTH-002 | User story is represented by the three current scenarios; transport/secret safeguards remain framework controls. | No measurable performance/security threshold is specified. Do not infer such guarantees. |

## Downstream Reconciliation and Success Criteria

The original prospective Stage 2 handoff is superseded by the completed-stage record below. Review-time observations in `design-review.md`, the Stage 1-only handoff in `requirements.md`, and archived approval/gate statements describe their respective points in time; they do not reopen completed stages.

| Decision / stage | Current disposition |
| --- | --- |
| Architecture approval / F2–M2 | Explicit user approval at **2026-11-03 00:33 (+05:30)** governs this fresh architecture. Corrected current metadata resolves the stale approval wording; the design review's original observations are preserved. |
| Source baseline / F1–M1 | At **00:39 (+05:30)** the user accepted the reported removal of AC 5.1 and TS-LOG-004 as the governing baseline despite the independently unverified Confluence page/version/date. This accepts the limitation, not source verification or fulfillment of the removed criterion. |
| Stage 4 / Stage 5 | `impl-plan.md` records completed R4-01..R4-04: regression relabeling, tighter unknown-account semantics, lifecycle cleanup hardening, and integration/handoff. S1–S4 were carried into that work; live behavior is not established by implementation completion. |
| Stage 6 / R4-05 | `verification-report.md` records successful compilation and **28 focused tests passed, 0 failures, 0 errors, 0 skips**. **0/9 Chrome browser scenarios executed**; all nine remain UNRUN because required credential/identity environment variables were absent and ChromeDriver/site readiness was not established. Reporting is complete, not live-browser acceptance. |
| Acceptance / publication | At **00:57 (+05:30)** the user accepted **PASS WITH LIMITATIONS** and authorized Stage 7 commit/push and a follow-up PR against `master`, with **no merge authorization**. Requester-provided publication context identifies **PR #7 OPEN**, head `428de07e554d091746f8d27412080ed465776e41`, original nine-file diff; no GitHub check was performed for this correction. |

**Current gate / handoff:** Stage 7 progression is permitted under the accepted limitations and existing publication authorization; Stage 4 is not pending. This documentation-only factual correction requires no new architecture proposal or approval gate. Any Stage 8 review publication remains subject to its explicit human-approval gate; merge is not authorized. This correction performs no Git index, commit, push, or GitHub operation and runs no tests or browsers. Acceptance does not convert unrun scenarios into passes, independently verify Confluence, or establish source compliance.

Implementation success criteria retained from the approved architecture (completion/evidence is bounded by the records above):

- Preserve the current three source scenarios and map each active AC to observable assertions; distinguish supplementary regressions in test names/reports.
- Keep responsibilities/data flow above, bounded waits, isolated sequential browser execution, and safe configuration precedence.
- Keep valid credentials/expected identity environment-only, HTTPS/local-HTTP policy enforced, skips distinct from passes, and teardown/error handling reliable.
- Report source acceptance only from fresh requirement-aligned verification; never infer that removed AC 5.1 or TS-LOG-004 was met.
- Do not claim CI, cross-browser, live-site, performance, or reliability evidence without actual configured/executed evidence.

### Historical Architecture Record

The prior 2026-10-14 proposal and its traceability are retained below as historical text, not current requirements, current gate authority, or current acceptance evidence. It described TS-LOG-001..004 and AC 5.1 before the reported source change. Its conflicting review-time approval/rejection statements are preserved without retroactive correction. The fresh revision's authority is the explicit 2026-11-03 approval and subsequent decisions recorded above, not any historical status below.

---

# Archived Stage 2 Architecture Proposal (2026-10-14; superseded)

**Status:** Approved by the user on 2026-10-14 with the explicit response “yes”; Stage 3 design review is authorized. The earlier proposal remains historical only.

## Context, Scope, and Evidence

This architecture covers the existing Java 21 / Maven / Selenium 4 / JUnit 5 automation framework for the external My Account login page at `https://askomdch.com/account/`. It does not design or change the website or authentication service. Preserve the current `Github_Copilot` package responsibilities, local browser execution, sequential isolated tests, and existing dependencies.

The controlling source transcription is [requirements.md](requirements.md), based on the authenticated, browser-rendered Confluence page 11796481, **Customer Login & Authentication**. On 2026-10-14, the page UI displayed “Updated 21m ago”; the exact version and timestamp are unavailable. The configured Confluence MCP API did not provide the source content. The reason for that limitation is not established here. Source claims are bounded by the browser-page evidence recorded in requirements.md; this proposal does not claim an independent API retrieval or live-site test.

The current source lists exactly four scenarios, TS-LOG-001 through TS-LOG-004. AC 1.1–1.4 cover the login controls and password-reset navigation; AC 2.1–2.2 cover successful login; AC 3.1 and AC 4.1 cover incorrect-password and unregistered-account outcomes; AC 5.1 requires inline errors for both blank fields. The existing code also has project tests labelled TS-LOG-005 through TS-LOG-008 and AC-UI-001. Those labels are implementation history, not additional source scenarios or acceptance criteria. Remember Me restart behavior and dedicated recovery navigation are not listed source scenarios.

## Architecture Overview

Maven Surefire discovers the JUnit tests. `BaseTest` owns per-test WebDriver lifecycle; `TestConfig` resolves runtime settings and validates the target URL; page objects own selectors and browser actions; scenario tests and assertion helpers verify observable outcomes. JUnit extensions record outcomes and Extent reports. Failure screenshots remain opt-in and disabled by default. Browser-driven checks depend on internet access, an available supported browser/driver, and an authorized registered account for credential-dependent cases.

```text
Maven Surefire -> JUnit extensions + scenario tests -> BaseTest/WebDriver
                                               |              |
                                       LoginPage -> BasePage -> external login page
                                               |
                              TestConfig + TestData; outcomes/reports
```

## Components

| Component / key classes | Responsibility and boundary | Inputs -> outputs | Dependencies |
| --- | --- | --- | --- |
| Runtime configuration: `TestConfig` | Resolve non-secret settings, credentials, and URL safety. Credentials are environment-only. | System properties/environment/defaults -> validated settings or configuration error; credential status -> guard decision. | Java URI APIs; `TestData` defaults. |
| Test data: `TestData` | Own bounded wait defaults and feedback tokens used by assertions. | Framework constants -> timeout and token lists. | Java collections. |
| Driver lifecycle: `BaseTest` | Create a fresh Chrome or Firefox driver per browser test, configure timeouts/viewport, register it for diagnostics, and quit it. Its temporary profile restart is used only by the supplementary Remember Me regression test. | `TestConfig`, JUnit test metadata -> `WebDriver`; teardown -> released driver/profile. | Selenium, WebDriverManager, lifecycle listener. |
| Shared browser operations: `BasePage` | Centralize navigation and bounded explicit-wait, visibility, click, typing, URL, and condition helpers. | Driver, locator/condition -> browser action or observed state. | Selenium `WebDriverWait`; `TestData`. |
| Login page object: `LoginPage` | Own login/recovery selectors and user interactions, feedback retrieval, form/dashboard state, and recovery destination checks. | Driver, configured URL, test inputs -> page actions and observable UI state. | `BasePage`; Selenium locators and expected conditions. |
| Source scenarios: `LoginPageTests` | Express TS-LOG-001..004 and keep each outcome independent. | Runtime config, safe test values, page object -> JUnit pass/fail/skip. | JUnit Jupiter, `CredentialTestGuard`, `LoginScenarioAssertions`, `TestData`. |
| Supplementary test guards/assertions: `CredentialTestGuard`, `LoginScenarioAssertions` | Fail or skip missing credential prerequisites according to mode; centralize meaningful invalid-password, customer-welcome, and restart-session assertions. | Runtime values and page observations -> explicit JUnit result without printing secrets/identity. | `TestConfig`, `TestData`, JUnit. |
| Lifecycle/reporting: `TestLifecycleListener`, `TestOutcome`, `LogUtil`, `ExtentReportExtension` | Record start/duration and pass/fail/skip separately; write Extent output; preserve test failure as authoritative if optional screenshot diagnostics fail. | JUnit callbacks, driver, non-secret metadata -> logs and HTML report. | JUnit extensions, ExtentReports, `TestConfig`. |
| Screenshot utility: `ScreenshotUtil` | Capture only on failure when explicitly enabled; redact selected form fields. Redaction is not whole-page sanitization. | Driver and test name -> optional image under `target/screenshots/`. | Selenium screenshot/Javascript APIs; filesystem. |
| Maven Surefire | Compile/discover tests using the existing `**/*Tests.java` include. | Maven/JUnit classes -> local test and Surefire reports. | Java 21, Maven, configured plugins/dependencies. |

## Layout and Boundaries

```text
src/test/java/Github_Copilot/
  base/BaseTest.java
  config/TestConfig.java, TestConfigTests.java
  data/TestData.java
  pages/BasePage.java, LoginPage.java
  tests/LoginPageTests.java, LoginPageRunner.java
  tests/CredentialTestGuard.java, CredentialTestGuardTests.java
  tests/LoginScenarioAssertions.java, LoginScenarioAssertionsTests.java
  listeners/ExtentReportExtension.java, TestLifecycleListener.java
  listeners/TestOutcome.java, TestLifecycleTests.java
  utils/LogUtil.java, ScreenshotUtil.java
docs/sdlc/requirements.md
docs/sdlc/architecture.md
target/                         generated local reports and artifacts
```

The application under test is an external dependency; no application source belongs in this project boundary. Keep selectors/actions in `LoginPage`, shared browser mechanics in `BasePage`, browser setup/cleanup in `BaseTest`, settings in `TestConfig`, and scenario assertions in tests/helpers. `pom.xml` currently has test-scoped Selenium 4.25.0, WebDriverManager 6.1.0, JUnit 5.11.3 / Platform 1.11.3, and ExtentReports 5.1.2; do not add dependencies for these scenarios. Surefire 3.5.0 selects `**/*Tests.java`; `LoginPageRunner` is not selected by the default include. There is no configured `.github/workflows/` CI workflow, CI secret mapping, or artifact upload.

## Configuration and Execution Flow

Non-secret settings use nonblank Java system property, then environment variable, then documented default. This includes base URL, browser, headless mode, page-load timeout, required-credential mode, local HTTP opt-in, and screenshot opt-in. HTTPS is required; HTTP is permitted only for an explicitly opted-in local host. Page-load and explicit waits are bounded, page-load timeout is limited to a positive supported range, and implicit wait remains zero.

Valid credentials are read only from `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD`; the welcome identity oracle is read only from `LOGIN_EXPECTED_WELCOME`. Never put values in source, command arguments, logs, reports, screenshots, examples, or committed artifacts. When required values are absent, credential-dependent cases skip in optional mode and fail visibly in required mode; a skip is not a pass. Non-secret invalid-test data can use the ordinary property/environment/default precedence.

1. Maven compiles the project and Surefire selects classes matching `**/*Tests.java`. `mvn test` includes the browser-driven `LoginPageTests` and browser-independent `*Tests` classes; a focused test selection can validate config, credential guards, scenario assertions, and lifecycle classification without starting a browser.
2. JUnit extensions begin outcome/report tracking. `BaseTest` creates a fresh local driver for each browser test, applies the bounded page-load timeout, zero implicit wait, and viewport/headless options, then registers the driver.
3. Scenario code opens the configured HTTPS URL via `LoginPage`; page actions and bounded synchronization go through the page objects. A credential guard runs before scenarios that need valid credentials or a known username.
4. JUnit classifies pass, failure, and assumption-aborted skip distinctly. `BaseTest` quits the driver and best-effort deletes the temporary profile in teardown. Listeners clear driver registration.
5. Surefire writes under `target/surefire-reports/`; Extent writes `target/extent-reports/ExtentReport.html` by default. Failure screenshots are disabled unless explicitly opted in. Artifacts are local; no CI publication or retention behavior is configured.

## Design Decisions and Operational Limits

- **Keep the existing stack and local-browser design.** It matches the codebase and has no demonstrated need for another library, remote grid, parallel execution, or browser expansion. Chrome and Firefox code paths exist; this is not evidence either path passed a live run.
- **Keep source acceptance separate from project regression coverage.** Retain the useful existing Remember Me restart, password-reset navigation, single-empty-field, and UI-control checks as supplementary regression coverage. In current code these have project display labels TS-LOG-005/006/007/008 and AC-UI-001; they are not additional current PRD scenarios or criteria. Preserve the distinction in reports and later-stage artifacts rather than inferring source traceability from reused labels.
- **Remember Me semantics stay narrow.** Current FR-1 requires the checkbox to be present and describes session persistence, but gives no duration or restart semantics. Architecture maps control presence to FR-1. The existing same-profile browser-restart test may remain as a supplementary regression test, but its outcome is not a current source acceptance criterion and no restart guarantee is inferred.
- **AC 5.1 / FR-2 remains an explicit source gap.** TS-LOG-004 requires application-rendered inline required-field errors for both blank inputs. Existing code checks browser-native `validationMessage` (with scoped site-error fallback); that is not inline application feedback and does not fulfill or rewrite the source wording. Requirements.md carries forward a historical project-deviation approval, but notes it was not independently verified during Stage 1; regardless, it cannot change source acceptance. Keep the source criterion and project behavior distinct. Do not weaken traceability or propose changing the external site. Source compliance requires observable inline feedback and matching automation evidence, or a separately authorized source change.
- **No invented NFR thresholds.** The PRD has no measurable performance/security targets. Do not imply latency, security, reliability, or cross-browser guarantees from framework safeguards or configuration.

## Error Handling, Security, and Cleanup

Unsupported browser names, invalid non-local HTTP URLs, driver startup failures, missing required credentials, failed assertions, and bounded-wait timeouts must remain visible as failures/errors rather than false passes. Optional missing prerequisites are explicit skips. Preserve the original test result if report attachment or best-effort profile cleanup fails.

Use environment-only credentials and keep identity values out of diagnostics. Screenshot capture remains off by default: selected password/email/user-like input redaction does not establish that the rest of an authenticated page is safe. Do not enable capture or share screenshots without page-level content review and an approved artifact handling policy. Test execution remains sequential; listener/report state and shared output paths are not claimed parallel-safe. No CI execution or artifact handling is configured.

## Current Requirements Traceability and Gaps

| Current source requirement/scenario | Architecture mapping | Existing project coverage and gap |
| --- | --- | --- |
| AC 1.1–1.4 / FR-1: named username/email and password fields, Remember Me, visible/clickable Log in, and recovery-link navigation | `LoginPage` owns selectors/actions. Existing UI assertions check visibility; scenario actions exercise login submission; the project recovery test checks destination URL and form visibility. | Coverage is distributed across project tests, not a dedicated current-source scenario. The current control test checks visibility; source clickability/navigation must be evidenced by the relevant actions. Recovery behavior is not recovery completion. |
| AC 2.1–2.2 / FR-3 / TS-LOG-001: valid login, dashboard/My Account destination, and welcome success message | Credential guard; `LoginPage` checks the configured account-home URL and observes account content/logout; `LoginScenarioAssertions` compares the welcome identity in memory with configured expected identity. | The missing destination-URL assertion identified at design time was closed by P4-02: the successful-login scenario now matches the configured account-home origin, normalized path, query, and fragment, separately from welcome/authentication checks. The latest Stage 6 report records the live destination assertion and focused URL tests passing. AC 5.1 remains unmet as a separate source gap (see its row below). |
| AC 3.1 / FR-4 / TS-LOG-002: incorrect password gives an error notice at the top of the form | Scoped error banner and shared assertion check expected error feedback, unauthenticated state, and retained form. | Assertion logic exists, but the present check does not explicitly establish the source-required notice position. Live known-user behavior and source-aligned position remain unverified. Keep the known username secret. |
| AC 4.1 / FR-4 / TS-LOG-003: unregistered account gives an account-does-not-exist error and stays unauthenticated | Unknown-user scenario checks feedback, unauthenticated state, and retained login form. | Test implementation exists; live behavior and the precise source error outcome are not established by static inspection. |
| AC 5.1 / FR-2 / TS-LOG-004: both blank fields trigger inline errors | `LoginPage` exposes field feedback; a source-compliant assertion must distinguish application-rendered inline errors from native browser validity. | **Source gap:** current test checks native field validation (and related project deviation); this is not source fulfillment. Resolve only through observed inline feedback plus evidence, or an authorized source change. |
| Source scenario inventory | The complete current PRD scenario set is TS-LOG-001, TS-LOG-002, TS-LOG-003, and TS-LOG-004. | Remember Me restart, recovery navigation, single-empty-field checks, and UI-control checks are supplementary existing project regressions, not additional source scenarios. |
| NFR-1 / US-AUTH-002 | Apply documented transport, credential, bounded-wait, cleanup, and reporting boundaries; map story behavior to the four source scenarios and ACs above. | No measurable performance or security acceptance targets are specified. Overall acceptance cannot be claimed until the source criteria and scenario outcomes are evidenced; AC 5.1 remains a known gap. |

## Stale Artifact Impact and Stage Gate

The existing [implementation plan](impl-plan.md), [verification report](verification-report.md), and [design review](design-review.md) contain earlier requirements/architecture mappings and approval history. Their dated run facts remain historical evidence, but their stage authority and scenario mapping do not approve this fresh proposal or replace the current requirements source. These artifacts are not edited in Stage 2; reconcile them in their owning later stages if those stages are authorized. Do not use them as approval of this revision.

This proposal defines component ownership, configuration/data flow, execution, evidence boundaries, source traceability, supplementary regression disposition, verification implications, and known gaps without proposing external-site changes or dependencies. It remains a proposal after the prior rejection. Obtain explicit human approval or feedback before Stage 3; no approval is claimed here.

**Human approval record:** On 2026-10-14, the user explicitly approved this fresh Stage 2 proposal by replying “yes”. This approval applies to the proposal above only and authorizes Stage 3; it does not approve any later-stage work.

## Prior Proposal Archive

The following proposal is retained for project history. It was based on the earlier requirements snapshot and prior scope. Its scenario mappings, success criteria, and approval/status statements are superseded by the refreshed proposal above and must not be treated as current requirements or approval.

# Historical Architecture Proposal (superseded 2026-10-14)

## Context and Overview

This Stage 2 proposal defines the test architecture for `US-AUTH-002` and the six scenarios in [requirements.md](requirements.md). The system under test is the My Account login page at `https://askomdch.com/account/`; execution requires network access and a registered account for credential-dependent scenarios.

The project is a Java 21, Maven, Selenium 4, JUnit 5 test suite. Preserve its existing package layout and dependencies. Each test gets a fresh local WebDriver through `BaseTest`; page objects own selectors and user actions; `TestConfig` supplies runtime settings; JUnit extensions provide lifecycle logging, failure diagnostics, and Extent reporting. Tests run sequentially. No application implementation, remote grid, or parallel execution is proposed.

The framework has Chrome and Firefox driver paths, subject to installed browsers, WebDriverManager resolution, and operating system. This repository has no configured CI workflow or remote browser jobs. Record actual verification evidence in the later verification stage.

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
| Build and test discovery: Maven Surefire | Compile and discover tests using the existing Maven configuration. Tests are run locally; no GitHub Actions workflow, CI job, browser matrix, or artifact upload is configured. | Maven configuration and local runtime environment. | Local test results and generated reports under `target/`. | Maven; Java 21. |

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

Credentials require stricter treatment than ordinary settings: supply `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD` only through environment variables; never place them in source, committed files, logs, screenshots, or command-line arguments. This repository has no configured CI workflow or CI secret mapping. `TestConfig.validUsername()` / `validPassword()` read exclusively from `System.getenv(...)` with no system-property path and no non-empty fallback value, so an absent environment variable yields an empty string rather than a usable default. `CredentialTestGuard` turns that absence into either a hard JUnit failure (when `requireCredentialTests()` is true) or an explicit `Assumptions.assumeTrue` skip (when false); a skipped test is never reported as a pass. Keep safe invalid-test values (`invalidPassword`, `unknownUsername`) configurable through the ordinary system-property/environment/default precedence, since those are not secrets. Set `REQUIRE_CREDENTIAL_TESTS=true` / `requireCredentialTests=true` in the local test process when missing credentials must fail visibly instead of being skipped.

Other existing opt-ins are `ALLOW_FAILURE_SCREENSHOTS` (default false) and `ALLOW_INSECURE_LOCAL_BASE_URL` (default false). `extentReportPath` is a Java system property for the report destination. The report extension currently reports browser/headless system-property values as metadata; ensure metadata does not expose secrets.

## Test Execution Flow

1. `mvn test` compiles the project and Surefire selects every class matching `**/*Tests.java`, which currently includes `LoginPageTests` plus the driver-free `TestConfigTests`, `CredentialTestGuardTests`, `LoginScenarioAssertionsTests`, and `TestLifecycleTests`. There is no configured CI invocation or browser matrix; use the documented Maven commands in the local environment.
2. JUnit runs `TestLifecycleListener` and `ExtentReportExtension` callbacks for every discovered test; `BaseTest` creates a fresh Chrome or Firefox driver using WebDriverManager only for the browser-driven `LoginPageTests` (the four `*Tests` unit-test classes exercise `TestConfig`, `CredentialTestGuard`, `LoginScenarioAssertions`, and `TestOutcome` directly, without a `WebDriver`). `BaseTest` applies page-load timeout, zero implicit wait, and viewport/headless settings, then registers the driver.
3. Each browser scenario opens the configured URL through `LoginPage`, performs actions through page-object methods, and asserts observable page state and feedback. `BasePage` uses explicit visibility/clickability/condition waits; do not add `Thread.sleep()`. Credential-dependent scenarios (`TS-LOG-001`, `TS-LOG-002`'s known-username precondition, `TS-LOG-005`) call `CredentialTestGuard` first so a missing secret fails loudly in required mode instead of passing.
4. The Remember Me scenario uses a temporary browser profile, authenticates with Remember Me enabled, quits and recreates the browser using that profile, then checks the restored session. Teardown deletes the temporary profile best-effort.
5. Listener callbacks log outcomes and durations. On a failure, Extent records the failure; screenshot capture is opt-in and redacts password, email, and user-like fields before writing the image. `BaseTest` always quits the driver and clears its registration.
6. Surefire writes machine-readable/text reports under `target/surefire-reports/`; Extent flushes its HTML report under `target/extent-reports/` by default. These artifacts are generated locally. No CI artifact upload is configured.

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
# Fresh Stage 3 Design Review — Restarted SDLC (2026-10-09)

## Review Metadata

| Item | Value |
| --- | --- |
| Stage | SDLC Stage 3: Design Review (fresh review for restarted workflow) |
| Reviewed | 2026-10-09 |
| Reviewer | Design Review Agent |
| Approved input | Updated Stage 2 architecture; human approval recorded 2026-10-09 |
| PRD basis | `requirements.md` records a fresh check against Confluence page 11796481, version 2. This review checked the recorded requirements and traceability; no independent Confluence retrieval was available in this review session. |
| Scope | Selenium test framework only; external login application is not reviewed or changed. Chrome-only execution is the user-directed verification scope. Firefox support and its CI configuration are left unchanged; Firefox/cross-browser execution is unrun. |
| Review method | Static review of requirements, approved architecture, current Java/test source, Maven/CI configuration, and retained 2026-10-09 verification artifacts. No tests or browsers were run for this design review. |
| Worktree handling | Updated only this working copy. No source, staging, commit, push, or PR changes were made. The pre-existing staged deletion/untracked working-copy state was preserved. |

## Executive Summary

**Verdict: APPROVED WITH CONDITIONS.** The approved architecture remains a suitable, maintainable design for this small serial Selenium suite: page-object ownership is clear, driver lifecycle is centralized, explicit waits are bounded, credentials are environment-only, and the expected-welcome oracle and recovery-form assertion are implemented. No architecture rewrite is indicated.

Approval is conditional on preserving the source/project distinction for FR-2 and carrying the actual Chrome verification gaps forward without reclassifying them as passes. The source PRD requires application-rendered inline errors; current tests exercise browser-native required-field validation, which is an accepted project deviation, not source fulfillment. The retained 2026-10-09 Chrome run recorded 6 passed, 0 failed, 0 errors, and 3 skipped of 9 browser tests. TS-LOG-001, TS-LOG-002, and TS-LOG-005 therefore remain unverified. The report's PASS WITH LIMITATIONS is historical evidence and does not satisfy the stricter Stage 6 PASS rule.

**Counts:** 0 critical findings; 2 high, 2 medium, and 1 low finding; 5 prioritized recommendations/conditions. No test execution was performed for this static review. Stage 4 must not proceed until this review and its conditions are presented and explicitly accepted.

## Requirements Coverage

| Requirement / scenario | Current design and source assessment | Evidence and disposition |
| --- | --- | --- |
| FR-1 / AC-UI-001; TS-LOG-004/005/006 control presence | `LoginPage` owns the five requested locators/actions; AC-UI-001 checks visibility. Other tests activate submit and recovery controls. | Historical Chrome evidence reports the control test passed. Not rerun in this review. |
| FR-2 / TS-LOG-004 | Tests inspect required-field messages and assert both fields, unauthenticated state, and retained form. They read browser-native `validationMessage` and may use a scoped site error. | **Source criterion unmet by design:** PRD requires application-rendered inline errors. The accepted native-validation deviation covers project behavior only. The existing Chrome run passed the project behavior; this is not a source-requirement pass. |
| FR-2 additional / TS-LOG-007, TS-LOG-008 | Separate blank-username and blank-password cases exercise native validation. | Historical Chrome run reports both passed under the project deviation; source inline errors remain unverified/unmet. |
| FR-3 / TS-LOG-001 | Credential and expected-identity guards precede the scenario. The test asserts authenticated state, exact configured welcome identity (case-insensitive), and visible logout; helper diagnostics do not reveal identity. | Expected-identity helper and guards have historical focused unit coverage. Live login was skipped for missing runtime values; welcome, identity, and logout behavior remain unverified. |
| FR-4 / TS-LOG-002 | Known-username guard precedes an active shared assertion for accepted invalid-password feedback, unauthenticated state, and visible login form. | Assertion helper has focused unit coverage. Live scenario skipped because known valid username was unavailable; rejection behavior remains unverified. |
| FR-4 / TS-LOG-003 | Unknown-user case asserts accepted error tokens, unauthenticated state, and retained form. | Historical Chrome run reports pass. Not rerun in this review. |
| FR-5 / TS-LOG-005 | A temporary browser profile is reused on restart and deleted best-effort. Test checks authentication before and after restart. Its `isLoggedIn()` oracle is permissive (logout link or generic dashboard text containing “hello”/“log out”). | Scenario skipped before login due missing credentials; profile restart and persistence are unverified. Recommend use a stronger post-restart authenticated-state assertion. |
| FR-6 / TS-LOG-006 | Recovery navigation checks a supported URL, visible reset form, and unauthenticated state. | Historical Chrome run reports these checks passed. This establishes destination/form presence only, not reset submission or completion. |
| US-AUTH-002 | All six named scenarios and the additional UI/blank-field cases are represented in the test suite. | End-to-end acceptance is incomplete: TS-LOG-001/002/005 skipped and source FR-2 inline feedback is not met. |
| NFR-1 | No invented performance/security thresholds. Existing safeguards cover HTTPS/local opt-in, environment-only secrets, opt-in screenshots, bounded waits, and serial execution. | No performance, security, dependency, slow-network, reliability, or artifact-safety test was run for this review. |

## Findings

| ID / severity | Type | Finding and impact |
| --- | --- | --- |
| F1 / High | Source requirement vs. deviation | FR-2 in the source requires application-rendered inline errors. Current tests assert browser-native validity instead. The deviation is explicitly accepted for project scope, but cannot be reported as satisfying source wording; this blocks an unconditional Stage 6 PASS under the stated gate. |
| F2 / High | Required browser evidence | The retained Chrome report records 3 credential-dependent skips: TS-LOG-001, TS-LOG-002, and TS-LOG-005. Required-mode execution with all required cases passing has not been demonstrated. The code correctly distinguishes optional skips from required-mode failures; the evidence gap is environmental/verification, not a credential-guard design defect. |
| F3 / Medium | FR-5 assertion strength | `LoginPage.isLoggedIn()` accepts generic “hello”/“log out” text inside dashboard content. TS-LOG-005 uses this predicate after browser restart without also requiring the logout control or expected identity, leaving a weaker-than-necessary persistence oracle. |
| F4 / Medium | Screenshot data exposure | Capture defaults off and CI does not enable it. If an operator opts in, current redaction replaces selected form input values only; it does not establish that the rendered page contains no other personal/account data. Keep the option off until representative page and artifact handling are reviewed. |
| F5 / Low | Evidence boundary / external source | The requirements artifact records a fresh page-11796481 v2 reread. This review had no independent Confluence retrieval capability, so source comparison is traceable to that artifact rather than a second direct fetch. Historical test/report and workflow data are not current execution by this reviewer. |

## Positive Design Evidence

- `TestConfig` reads valid credentials and expected welcome identity only from environment variables. `CredentialTestGuard` aborts in optional mode and fails in required mode; focused unit tests cover these outcomes without logging supplied values.
- `LoginScenarioAssertions` actively verifies invalid-password outcomes. Its unit test rejects missing feedback, authenticated state, or a missing form. The welcome helper rejects generic/mismatched greetings and keeps identity out of failure text.
- `BaseTest` centralizes per-test drivers and teardown, uses a temporary profile for Remember Me, and preserves Chrome/Firefox code paths. `BasePage` uses bounded explicit waits and no fixed sleeps were found in the reviewed page/test code.
- Configuration validates transport, bounds page-load timeout, keeps implicit wait at zero, and uses system-property/environment/default precedence only for non-secret settings.
- Surefire's `**/*Tests.java` pattern selects `LoginPageTests` plus the browser-independent `*Tests` classes. The workflow is serial, configures Java 21 and headless browser jobs, injects secrets as environment variables, and uploads Surefire reports. These are configuration facts, not proof of CI execution. Under current user direction, run Chrome only; leave Firefox support and CI matrix untouched and report Firefox/cross-browser unrun.
- Failure screenshots are off by default; the workflow's upload step does not itself enable capture. Extent and lifecycle outcomes distinguish pass, fail, and aborted/skipped tests in source.

## Risk Assessment

| Risk | Likelihood | Impact | Severity | Mitigation |
| --- | --- | --- | --- | --- |
| Browser-native FR-2 checks are presented as source inline-error fulfillment. | Medium | High | High | Keep source wording unchanged and label native validation as the accepted project deviation in the plan and verification. Stage 6 PASS is unavailable while the source criterion remains unmet; any permitted PASS WITH LIMITATIONS requires disclosed impact and explicit human acceptance. |
| Credential guards continue to skip the three required Chrome scenarios. | Medium, based on retained run | High | High | Run the required Chrome suite with approved secrets supplied only to Maven's process environment. Verify presence without reading/printing values; require the tests to execute and pass. |
| A weak authenticated-state predicate reports Remember Me persistence without proving an authenticated session. | Low to medium | Medium | Medium | Strengthen TS-LOG-005 to require the visible logout control after restart (and, where safe, the expected identity) in addition to the account page state. |
| Opt-in screenshot exposes account data outside recognized inputs. | Low while disabled; higher if enabled | High | Medium | Keep capture disabled. Before enabling, review representative authenticated page content, redaction, artifact access, and retention; do not share unsafe captures. |
| Workflow configuration or old reports are mistaken for new cross-browser/CI evidence. | Medium | High | High | Record only observed Chrome results for this scope. State Firefox/cross-browser and CI as unrun unless executed; preserve source/config vs. runtime evidence distinctions. |
| Review claims a fresh direct Confluence retrieval not actually performed in this session. | Low | Medium | Low | Attribute the v2 source check to the requirements artifact and disclose that this review did not independently retrieve the page. |

## Prioritized Recommendations and Conditions

| ID | Priority / classification | Action and closure evidence | Gate mapping |
| --- | --- | --- | --- |
| R1 | P0 / Must fix for any Stage 6 PASS | Preserve FR-2 source criterion verbatim; report native browser validation only as the accepted project deviation. Meet the source criterion or classify it as unmet. A Stage 6 PASS is prohibited while it is unmet. A PASS WITH LIMITATIONS must disclose it and obtain explicit human acceptance before Stage 7. | C1; F1 |
| R2 | P0 / Must fix before Stage 6 closure | Run the full required Chrome suite with `-Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true`, with secrets injected only through the process environment. TS-LOG-001/002/005 must execute, not skip, and pass; require zero failures, errors, and skips for PASS. Record exact command, runtime/browser versions, counts, and artifacts; do not expose credential/identity values. | C2; F2 |
| R3 | P1 / Should fix before Stage 6 | Tighten TS-LOG-005's post-restart assertion to require an unambiguous authenticated signal, preferably visible logout plus the account page, rather than generic “hello” text alone. Verify with the focused helper/test and Chrome scenario. | C3; F3 |
| R4 | P1 / Should maintain | Keep failure screenshots disabled. Before any opt-in, demonstrate page-level data safety and approve artifact access/retention; selected-input redaction is not whole-page sanitization. | C4; F4 |
| R5 | P2 / Nice to have | Keep recovery evidence described as navigation plus form visibility only; do not expand to reset usability/completion without a requirement and stable test account. | C5; FR-6 |

### Approval Conditions

- **C1 — FR-2 reporting:** Human accepts that the project uses browser-native validation as a deviation, with the source inline-error criterion unchanged and not fulfilled. No later artifact may call this a source-requirement pass. For Stage 6, distinguish `PASS` from `PASS WITH LIMITATIONS` exactly as the verifier gate requires.
- **C2 — Required Chrome outcomes:** Carry TS-LOG-001/002/005 as unverified until the required Chrome run executes and passes. Any skip blocks Stage 6 PASS. A PASS WITH LIMITATIONS is available only under the defined conditions and explicit human acceptance before Stage 7.
- **C3 — Remember Me oracle:** Strengthen or explicitly justify the post-restart authenticated-state signal before treating TS-LOG-005 as adequate evidence.
- **C4 — Diagnostic safety:** Keep screenshots off until page-level data and artifact handling are approved; current selected-field redaction is not a guarantee.
- **C5 — Evidence boundary:** Chrome is the only browser execution in scope for the restarted workflow. Firefox/cross-browser and CI execution remain unrun unless separately observed; no changes to Firefox support or CI configuration are authorized by this review.

**Human gate:** Fresh review conditions are presented but are not yet accepted. Do not proceed to Stage 4 until the human explicitly accepts or requests revision. Acceptance does not convert a skip or unmet source requirement into a pass.

## Evidence and Limitations

The retained 2026-10-09 verification report and current workspace Surefire files record:

- Compile/test-compile command: reported successful, exit 0; no tests selected.
- Focused unit command: reported 13 passed, 0 failed, 0 errors, 0 skipped (6 config, 4 credential guard, 2 scenario assertions, 1 lifecycle).
- Browser class `LoginPageTests`: 9 tests, 6 passed, 0 failed, 0 errors, 3 skipped. The retained XML identifies the skips as missing known username/credentials/expected identity and records Chrome 154.0.8037.98, ChromeDriver 154.0.8037.92, Java 21.0.10, and Windows 11.
- Passed browser scenarios in that report: controls, FR-2 project-native validation cases, unknown-user rejection, and recovery URL/form. Skipped: valid login, invalid password with known user, and Remember Me restart.

These are retained prior-run artifacts, not a test run by this design review. The report itself labels its result PASS WITH LIMITATIONS. That result does not meet the Stage 6 PASS rule and does not establish FR-2 source fulfillment. The earlier 2026-10-02 report remains historical; its browser/verification results were not re-executed. No Firefox browser, CI workflow, security/dependency scan, slow-network test, performance test, screenshot capture, or reliability study was run for this review.

The retained Stage 6 report records human acceptance of its stated limitations for Stage 7 on 2026-10-09. That acceptance is historical to that verification gate; it neither accepts the fresh Stage 3 conditions in this review nor changes the current Stage 6 criteria for the restarted workflow.

Confluence source caveat: the reviewed requirements artifact states that page 11796481 v2 was retrieved and checked on 2026-10-09. No direct Confluence connector was available in this review session; claims about source content are limited to the artifact's recorded check and cited source wording.

## Traceability

Reviewed [requirements.md](requirements.md), user-approved [architecture.md](architecture.md), [implementation plan](impl-plan.md), [verification report](verification-report.md), root and repository instructions, [pom.xml](../../pom.xml), [README](../../README.md), [workflow YAML](../../.github/workflows/selenium-login.yml), and [workflow guide](../../.github/workflows/selenium-login-workflow.md).

Current source reviewed: [BaseTest](../../src/test/java/Github_Copilot/base/BaseTest.java), [TestConfig](../../src/test/java/Github_Copilot/config/TestConfig.java) and [TestConfigTests](../../src/test/java/Github_Copilot/config/TestConfigTests.java), [TestData](../../src/test/java/Github_Copilot/data/TestData.java), [BasePage](../../src/test/java/Github_Copilot/pages/BasePage.java), [LoginPage](../../src/test/java/Github_Copilot/pages/LoginPage.java), [LoginPageTests](../../src/test/java/Github_Copilot/tests/LoginPageTests.java), [CredentialTestGuard](../../src/test/java/Github_Copilot/tests/CredentialTestGuard.java) and [CredentialTestGuardTests](../../src/test/java/Github_Copilot/tests/CredentialTestGuardTests.java), [LoginScenarioAssertions](../../src/test/java/Github_Copilot/tests/LoginScenarioAssertions.java) and [LoginScenarioAssertionsTests](../../src/test/java/Github_Copilot/tests/LoginScenarioAssertionsTests.java), [TestLifecycleListener](../../src/test/java/Github_Copilot/listeners/TestLifecycleListener.java), [ExtentReportExtension](../../src/test/java/Github_Copilot/listeners/ExtentReportExtension.java), [ScreenshotUtil](../../src/test/java/Github_Copilot/utils/ScreenshotUtil.java), and [LogUtil](../../src/test/java/Github_Copilot/utils/LogUtil.java). Retained reports checked under `target/surefire-reports/`.

---

# Prior Stage 3 Design Review (2026-10-09; retained for audit)

The review below is preserved verbatim as prior review history. The fresh restarted-workflow review above supersedes its “current” status, recommendations, and approval disposition. Its recorded human acceptances do not constitute acceptance of the fresh review conditions above.

## Review Metadata

| Item | Value |
| --- | --- |
| Stage | SDLC Stage 3: Design Review |
| Reviewed | 2026-10-09 |
| Reviewer | Design Review Agent |
| Approved input | Stage 2 architecture; the user explicitly approved the current architecture on 2026-10-09 |
| Sources | [Requirements](requirements.md), user-approved [Architecture](architecture.md), revised [Stage 4 plan](impl-plan.md), current Java test framework, `pom.xml`, README, workflow YAML/guide, and historical [Stage 6 report](verification-report.md) |
| Review method | Static document and source review only. No tests, browser, CI, live site, network, dependency scan, or performance environment was exercised for this review. |

**Refresh entry (2026-10-09):** Rechecked the current approved architecture, source-linked requirements (Confluence page 11796481, version 2), revised Stage 4 plan, current framework source, Maven configuration, and CI workflow. This refresh supersedes the current-review dispositions below where noted; it does not alter the Historical Review section. The user's 2026-10-09 acceptance of the Stage 3 conditions is recorded below.

## Executive Summary

**Verdict: APPROVED WITH CONDITIONS. Stage 4 planning is permitted; Stage 5 is not authorized or started by this review.** The 2026-10-09 architecture is approved. Current source has environment-only valid credentials, fail-or-skip credential guards, active invalid-password assertions, bounded explicit waits, isolated local drivers, serial CI configuration, and screenshots disabled by default. The former credential-source and disabled-assertion gaps are closed in source; the only focused-test outcomes available remain historical.

TS-LOG-001, TS-LOG-002 with a known username, and TS-LOG-005 remain unverified: the 2026-10-02 Stage 6 report records each stopping at the required-credential guard in both browsers. The FR-3 welcome predicate still accepts generic nonblank text and does not prove account identity. The user accepted carrying the FR-2 browser-native-validation deviation into planning, but the source PRD's inline-error wording remains unchanged and unmet by that deviation. None of these is represented as a pass.

**Counts:** 0 critical issues; 5 warnings; 4 actionable recommendations mapped to P4-01 through P4-04 (2 Must fix before Stage 6 can pass; 2 Should fix before final browser verification). The accepted Stage 3 conditions are recorded below; no Stage 3 approval remains outstanding.

## Requirements Coverage

| Requirement / scenarios | Design and implementation review | Evidence status and caveat |
| --- | --- | --- |
| FR-1, TS-LOG-004/005/006, AC-UI-001: login controls | `LoginPage` owns the required selectors; `LoginPageTests` checks visibility of all five controls. | Historical Stage 6 report records AC-UI-001 passing in Chrome and Firefox. Not exercised in this review. |
| FR-2, TS-LOG-004/007/008: required fields | Tests submit both fields blank and each individually blank; implementation reads browser-native validation messages. | Source PRD requires inline required-field errors. The user accepted browser-native feedback as a project deviation for planning on 2026-10-09; that does not fulfill or change the source wording. Historical Stage 6 reports only project behavior passing in both browsers. |
| FR-3, TS-LOG-001: successful login and identified welcome | Test asserts authenticated state, a welcome predicate, and logout visibility. The predicate accepts nonblank text after `Hello ` but does not compare identity with the authorized account. | Historical Stage 6 records failure at the required-credential guard; authentication, logout, and customer identity were not observed to pass. P4-01 and P4-04 remain required. |
| FR-4, TS-LOG-002/003: invalid credentials | Unknown-user test asserts feedback, unauthenticated state, and form visibility. Invalid-password uses the active shared assertion helper for the same outcomes. | Historical Stage 6 records TS-LOG-003 passing; TS-LOG-002 stopped at the known-username credential guard. Helper unit coverage is historical, not live rejection evidence. |
| FR-5, TS-LOG-005: Remember Me across restart | Test selects/clears the checkbox, authenticates, restarts the browser using a temporary profile, and checks restored login state. | Historical Stage 6 records failure at the credential guard; browser restart and persistence were not exercised. |
| FR-6, TS-LOG-006: recovery navigation | Page object waits for a supported recovery URL; test checks destination path and unauthenticated state. | Historical Stage 6 reports URL-pattern checks passing in both browsers. Destination content was not checked; P4-03 assesses a stronger signal. |
| US-AUTH-002 | Scenario suite maps the registered-customer login story across FR-1 through FR-6. | End-to-end acceptance remains incomplete while TS-LOG-001/002/005 are unverified. |
| NFR-1: performance/security | PRD defines no measurable targets; architecture does not invent thresholds and defines transport, credential, and artifact safeguards. | No target can be assessed. This review performed no security, dependency, performance, or slow-network testing. |

## Findings

| ID / severity | Type | Finding and disposition |
| --- | --- | --- |
| F1 / High | Runtime evidence and environment readiness | Historical Stage 6 records three required-credential-guard failures in each browser for TS-LOG-001/002/005. Required mode failed visibly as designed; underlying behaviors did not pass verification. P4-04 requires actual required-mode Chrome and Firefox execution with secrets supplied only through the Maven process environment. No current run was performed for this refresh. |
| F2 / High | FR-3 acceptance oracle | `hasCustomerIdentifyingWelcome()` accepts any nonblank text after `Hello ` and does not compare it with the authorized account identity. P4-01 must define a deterministic, privacy-safe oracle; FR-3 cannot pass Stage 6 without closing it and observing valid login. |
| F3 / Medium | Source requirement and project deviation | FR-2 source criterion is application-rendered inline feedback; project criterion is browser-native validation. The 2026-10-09 human gate accepted carrying the deviation into planning. The requirements artifact notes no separate approval record for its 2026-10-02 entry. Keep the source criterion distinct; this is accepted for planning, not a remaining Stage 3 gate. |
| F4 / Medium | Artifact safety | Capture defaults off. `ScreenshotUtil` replaces selected form-field values only and does not establish that other page content is safe. CI uploads screenshots on failed jobs if present, but does not enable capture. P4-02 reviews defaults, switches, redaction limits, and artifact controls; keep capture disabled absent page-level and artifact approval. |
| F5 / Low | Recovery-flow signal | TS-LOG-006 checks a supported recovery URL and unauthenticated state, not usable page content. Historical evidence is URL-pattern matching only. P4-03 must assess a stable non-sensitive signal or document why URL-only evidence is strongest; do not claim usability from the URL alone. |

### Positive Design Evidence

- `TestConfig` sources valid credentials only from `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD`; absent values remain empty. `CredentialTestGuard` fails in required mode and aborts in optional mode. Unit tests cover both outcomes without embedding secret values.
- TS-LOG-002 has active assertions for an incorrect-password token, unauthenticated state, and retained form; `LoginScenarioAssertionsTests` covers insufficient evidence. The earlier disabled-assertion finding is no longer present in current source.
- `BaseTest` creates a fresh local Chrome/Firefox driver, uses zero implicit wait and a bounded page-load timeout, and quits/cleans up in `@AfterEach`; page operations use explicit waits. Tests and CI are serial, consistent with the architecture's scope.
- `BasePage` owns shared bounded wait/navigation helpers, while `LoginPage` owns login selectors and interactions. `BaseTest` uses a temporary profile for Remember Me and clears the listener registration and profile during teardown; profile deletion is explicitly best-effort. No fixed sleep or parallel execution is part of the reviewed design.
- Boolean condition waits return false on timeout; the surrounding scenario checks authenticated state or the destination URL, so a timeout remains an observable failed assertion rather than an implicit pass.
- Ordinary settings use nonblank system property, then environment variable, then documented default; invalid page-load timeouts fall back within a bounded range. HTTPS is required except explicitly opted-in local HTTP. `LogUtil` redacts configured valid credentials; report metadata is limited to browser/headless values and outcomes.
- `TestLifecycleListener` distinguishes aborted tests from failures and records outcome/duration; Extent failures remain failures if screenshot capture cannot be attached. This is source evidence only, not a current report-generation check. CI configures Java 21 and serial headless Chrome/Firefox jobs with required credentials; no CI run was inspected.
- Maven/Surefire selects `**/*Tests.java`, including the browser-independent unit tests and `LoginPageTests`. Workflow YAML configures Java 21, headless serial Chrome/Firefox jobs, required credential mode, and Surefire artifact upload. These are source/configuration observations, not execution evidence.
- Maven dependencies are explicitly versioned and test-scoped; no additional library is proposed. This review did not run a dependency vulnerability scan. Current serial local-browser scope is consistent with the small suite; parallel execution, remote grids, extra browsers, and scalability claims are outside the approved design.

## Risk Assessment

| Risk | Likelihood | Impact | Severity | Mitigation |
| --- | --- | --- | --- | --- |
| Credential-dependent scenarios continue to stop before testing acceptance behavior. | Medium, based on the dated report | High | High | Ensure secret presence in the Maven process using presence-only diagnostics; execute required-mode Chrome and Firefox runs and preserve per-run results. Never echo values. |
| Generic greeting text produces a false positive for the FR-3 customer-identity criterion. | Medium | Medium | Medium | Define the expected identity signal and compare it safely; ensure it cannot enter logs or artifacts. |
| Project tests are treated as satisfying the PRD's inline-error requirement despite the native-validation deviation. | Medium | High | High | Keep source and project criteria distinct in requirements, plan, and verification; record explicit human acceptance of the deviation. |
| Failure screenshots expose account or other page data outside selected input fields. | Low while disabled; higher if enabled | High | Medium | Keep capture off until page-level review, redaction expectations, artifact access, and retention are approved. |
| Configured browser matrix is mistaken for current cross-browser or Remember Me evidence. | Medium | High | High | Treat workflow configuration and historical reports only as their stated evidence; require observed results for each required scenario/browser. |
| Recovery URL matches a supported path but the destination content is unavailable or incorrect. | Low to medium | Medium | Low | Complete P4-03: assert a stable recovery-page signal if reliable, otherwise record the URL-only limitation; distinguish navigation evidence from complete recovery-flow validation. |

## Gaps and Prioritized Recommendations

### Must Fix Before Stage 6 Can Pass

1. **P4-01 (P0):** Add a deterministic FR-3 identity oracle for the approved test account. Reject generic nonblank greetings, keep the identity comparison in memory, and ensure identity is absent from assertions, logs, reports, screenshots, and committed files. If no reliable oracle is available, record a blocker and do not claim FR-3 verified.
2. **P4-04 (P0):** After P4-01, P4-02, and P4-03 dispositions, run required-mode serial Chrome and Firefox suites with secrets supplied only to each Maven process environment. TS-LOG-001, TS-LOG-002 with known username, and TS-LOG-005 must execute rather than guard-fail or skip. Record exact commands, browser versions, and per-browser pass/fail/error/skip counts; preserve independent reports. Any failure blocks task closure.

### Should Fix Before Final Browser Verification

3. **P4-02 (P1):** Review screenshot defaults, workflow switches, selected-field redaction limits, and artifact access/retention. Keep capture disabled; do not upload or share authenticated screenshots. Enable capture only after representative page-level review and explicit account/artifact approval.
4. **P4-03 (P2):** Determine whether TS-LOG-006 has a stable, non-sensitive recovery-page signal beyond its URL. Add an assertion only if reliable; otherwise document why URL-only is the strongest observable and do not claim recovery-page usability.

These are the four tasks in the revised [Stage 4 plan](impl-plan.md). Toolchain enforcement, timeout-message changes, dependency monitoring, and performance targets are not additional recommendations: no concrete defect or selected policy supports expanding scope, and the PRD defines no measurable performance/security targets.

## Evidence and Limitations

This review did not run Maven or inspect a live CI execution. The [Stage 6 verification report](verification-report.md), dated 2026-10-02, is historical evidence only: it reports compilation and test-compilation passed; 11 focused unit tests passed; latest retained Chrome repeat and Firefox runs each reported 20 tests, 17 passed and 3 failed at the required-credential guard. It also records TS-LOG-003, the project-native-validation cases, UI controls, and recovery URL checks passing. The report notes an unreconciled earlier Chrome count of 29 and 20-case later runs. None of those results are results of this design review or evidence of a current run.

The following are configuration/source observations, not runtime claims: the CI secret mapping and matrix, Maven discovery pattern, browser options, driver/profile lifecycle, explicit waits, and report/screenshot behavior. No `src/test/resources` files were found. The workflow guide and historical report do not prove a current CI run, dependency safety, screenshot safety, live-site availability, browser compatibility, slow-network behavior, or performance.

## Approval Conditions and Human Gate

- **Condition 1, accepted 2026-10-09:** Carry the browser-native FR-2 deviation into planning only. The source PRD still requires inline errors; native validation is not represented as satisfying that wording.
- **Condition 2, accepted 2026-10-09:** Plan with TS-LOG-001, TS-LOG-002 using a known username, and TS-LOG-005 still unverified. Required-mode browser execution remains open work, not a pass.
- **Condition 3, accepted 2026-10-09:** Treat the FR-3 identity-oracle gap as required work and close it before Stage 6 can pass FR-3.
- **Human gate:** All three conditions were explicitly accepted; Stage 4 planning is authorized. No condition acceptance changes the status of unverified scenarios.
- **Stage 4 status:** **Permitted.** Revised plan has four open tasks (P4-01 through P4-04), estimated at 10 hours.
- **Stage 5 status:** Not started. This Stage 3 refresh does not authorize or begin implementation.
- **Reviewer sign-off:** **APPROVED WITH CONDITIONS**; accepted conditions are carried into the revised plan, and remaining verification evidence is not claimed.

## Traceability

Reviewed [requirements.md](requirements.md), user-approved [architecture.md](architecture.md), revised [implementation plan](impl-plan.md), historical [verification report](verification-report.md), canonical [instructions](../../copilot-instructions.md), repository [Copilot context](../../.github/copilot-instructions.md), [pom.xml](../../pom.xml), [README](../../README.md), [CI workflow](../../.github/workflows/selenium-login.yml), and its [workflow guide](../../.github/workflows/selenium-login-workflow.md). Current source reviewed: [BaseTest](../../src/test/java/Github_Copilot/base/BaseTest.java), [TestConfig](../../src/test/java/Github_Copilot/config/TestConfig.java) and [TestConfigTests](../../src/test/java/Github_Copilot/config/TestConfigTests.java), [BasePage](../../src/test/java/Github_Copilot/pages/BasePage.java), [LoginPage](../../src/test/java/Github_Copilot/pages/LoginPage.java), [LoginPageTests](../../src/test/java/Github_Copilot/tests/LoginPageTests.java), [CredentialTestGuard](../../src/test/java/Github_Copilot/tests/CredentialTestGuard.java) and [CredentialTestGuardTests](../../src/test/java/Github_Copilot/tests/CredentialTestGuardTests.java), [LoginScenarioAssertions](../../src/test/java/Github_Copilot/tests/LoginScenarioAssertions.java) and [LoginScenarioAssertionsTests](../../src/test/java/Github_Copilot/tests/LoginScenarioAssertionsTests.java), [TestLifecycleListener](../../src/test/java/Github_Copilot/listeners/TestLifecycleListener.java) and [TestLifecycleTests](../../src/test/java/Github_Copilot/listeners/TestLifecycleTests.java), [ExtentReportExtension](../../src/test/java/Github_Copilot/listeners/ExtentReportExtension.java), [ScreenshotUtil](../../src/test/java/Github_Copilot/utils/ScreenshotUtil.java), [LogUtil](../../src/test/java/Github_Copilot/utils/LogUtil.java), and [TestData](../../src/test/java/Github_Copilot/data/TestData.java). No `src/test/resources` files were present.

---

## Historical Review (2026-10-02)

The following review, findings, and human sign-off are retained as historical audit material. They predate the current architecture approval and do not resolve the conditions in this 2026-10-09 review.

## Review Metadata

| Item | Value |
| --- | --- |
| Stage | SDLC Stage 3: Design Review |
| Reviewed | 2026-10-02 |
| Reviewer | Design Review Agent |
| Inputs | [Architecture](architecture.md), [Requirements](requirements.md), existing Selenium tests and configuration, Maven build, CI workflow documentation and YAML |
| Evidence type | Static document and source review; no live site, browser, CI run, or dependency scan was performed |

## Executive Summary

**Verdict: APPROVED WITH CONDITIONS.** The page-object, driver lifecycle, explicit-wait, and Maven/Surefire approach covers the requested login scope without requiring an architectural rewrite. However, the current implementation does not satisfy its stated credential policy, CI can use source defaults instead of proving secrets were supplied, and the invalid-password test has no active assertions. These are blocking conditions, not accepted residual risks. Stage 4 must wait for the human reviewer to accept the conditions explicitly or request revision; this review does not record that acceptance.

**Counts:** 1 critical issue, 5 warnings, 8 recommendations (2 Must fix, 4 Should fix, 2 Nice to have).

## Requirements Coverage

| Requirement | Architecture coverage | Existing implementation / review result |
| --- | --- | --- |
| FR-1 / TS-LOG-004/005/006: login controls | LoginPage owns the required selectors; UI scenario covers visibility. | Covered in `LoginPage` and the UI test. Login actions exercise the clickable button. |
| FR-2 / TS-LOG-004: required-field feedback | Blank submission and per-field feedback are designed. Architecture correctly flags whether browser-native validation satisfies “inline” as unresolved. | Both-empty and each single-empty scenarios exist. Code reads native `validationMessage` and calls `reportValidity`; this does not establish that the PRD's inline page feedback is present. Confirm expected behavior against the product requirement/site and assert it. |
| FR-3 / TS-LOG-001: valid login and identified welcome | Login, dashboard, welcome, and logout checks are mapped. | Authentication and generic welcome/logout tokens are asserted. Tokens `hello` or `log out` do not prove the welcome identifies the customer as required. Credential fallback also invalidates the intended secret-injection boundary. |
| FR-4 / TS-LOG-002/003: invalid password and unknown user | Both negative scenarios are mapped; architecture notes the disabled invalid-password assertions. | Unknown-user assertions check error, unauthenticated state, and form presence. Invalid-password assertions are commented out, so the test can pass without checking rejection. |
| FR-5 / TS-LOG-005: Remember Me across restart | Dedicated temporary-profile restart flow is designed. | Test covers selection, login, restart, and restored session. Behavior has not been exercised in a browser; profile and session compatibility remain unverified. |
| FR-6 / TS-LOG-006: recovery navigation | Page object and scenario check recovery navigation. | URL path is checked against supported recovery path names; no live destination/content verification was performed. |
| NFR-1: no measurable security/performance criteria supplied | Correctly avoids inventing thresholds; transport and credential safeguards are described. | No performance/security acceptance threshold can be assessed. Credential handling still violates the repository's no-secret-in-source/property policy. |
| US-AUTH-002: registered customer accesses account | The six scenarios collectively represent the story. | Scenario mapping is present, subject to the FR-2, FR-3, FR-4 gaps above and runtime verification. |

## Findings

| Area | Finding and assessment |
| --- | --- |
| Security | **Critical:** `TestConfig` supplies non-empty valid-credential source defaults and accepts `validUsername` / `validPassword` JVM properties. This contradicts the repository rule to source credentials only from runtime environment variables or CI secrets, and can mask absent CI secrets. `LogUtil` redacts configured values in its own messages, but that does not make source literals or command-line properties safe. Failure screenshots redact selected form inputs, not arbitrary personal information elsewhere on the page; CI opts into screenshots. |
| Reliability | Explicit waits, zero implicit wait, page-load timeout, fresh per-test drivers, and serial execution are appropriate for this small suite. There is no retry policy, which avoids hiding genuine failures. The invalid-password case currently reports no meaningful assertion. Driver setup, network behavior, and remembered sessions are not runtime-verified. |
| Performance | The PRD defines no measurable target. Ten-second bounded waits and sequential execution are reasonable for the current small scope; no throughput, runtime, or scalability claim is supported. Parallel execution is explicitly out of scope. |
| Maintainability | Page objects own selectors and actions, while configuration, lifecycle, and diagnostics are separated. Disabled assertions in the invalid-password test obscure a false-positive risk. Generic welcome tokens weaken traceability to FR-3. The extra blank-username/password scenarios add useful coverage beyond the six named scenarios. |
| Cross-browser support | Chrome and Firefox driver paths and CI matrix entries are configured. This is configuration evidence only; neither browser's behavior has been verified in this review. Remember Me profile restart is especially runtime-dependent. |
| Maven integration | Surefire 3.5.0 includes `**/*Tests.java`, which selects `LoginPageTests`; `LoginPageRunner` does not match that include, as documented. The CI Maven invocation aligns with the guide. Workflow uploads Surefire results and failure screenshots, not the generated Extent HTML report. The pre-commit document is a proposal, not an installed or enforced hook. |
| Testability | Runtime URL/browser/timeouts and credentials are configurable, and the tests have independent scenario methods. No focused tests for configuration precedence, missing-secret behavior, or URL validation were found in the reviewed inputs. Credential defaults currently make the most important missing-secret case hard to exercise. |
| Dependencies | Maven dependencies have explicit versions (Selenium 4.25.0, WebDriverManager 6.1.0, JUnit 5.11.3 / Platform 1.11.3, ExtentReports 5.1.2; Surefire 3.5.0). This review did not query vulnerability data. Consider automated dependency monitoring; no specific vulnerable dependency is asserted here. |

## Risk Assessment

| Risk | Likelihood | Impact | Severity | Mitigation |
| --- | --- | --- | --- | --- |
| Valid credentials remain in source defaults or are passed as JVM properties; CI may run with fallback values while appearing configured. | High | Critical | Critical | Remove valid-credential defaults and JVM-property support. Accept credentials only from environment/secret injection; fail credential-dependent checks when absent. Confirm logs and artifacts contain no values. |
| Invalid-password rejection regresses without failing its test. | High | High | High | Restore assertions for incorrect-password feedback, unauthenticated state, and continued login form visibility; verify the scenario fails on unmet conditions. |
| Blank-field browser-native feedback differs from required inline errors. | Medium | High | High | Resolve the acceptance interpretation and verify the actual target behavior; assert both required-field outcomes using the approved behavior. |
| Dashboard assertion passes without identifying the logged-in customer. | Medium | Medium | Medium | Assert a customer-identifying welcome signal without logging or reporting the account value. |
| CI failure screenshot contains personal data outside recognized inputs. | Medium | High | High | Use a dedicated synthetic account, keep screenshots disabled unless needed, and assess/redact page-level personal data before artifact upload; retain short access-controlled retention. |
| Chrome, Firefox, live-site, and browser-restart behavior is unverified. | Medium | High | High | Record actual Stage 6 results per browser and scenario; report skipped/unrun checks distinctly from passes. |

## Gaps and Recommendations

### Must Fix: Blocks Implementation

1. Remove valid-credential literals and system-property sourcing from `TestConfig`. Require environment/CI secret values and ensure missing values fail when credential tests are required. Update README and configuration behavior consistently. Do not reproduce credential values in this report or diagnostics.
2. Restore effective assertions in TS-LOG-002. Verify invalid-password feedback, unauthenticated state, and login form visibility so a rejected login cannot be reported as a passing test.

### Should Fix: Required Before Merge

3. Resolve whether FR-2 requires application-rendered inline errors or accepts browser-native validation; align implementation and assertions with the decision.
4. Strengthen TS-LOG-001 to verify that the visible welcome identifies the customer, without exposing account data in logs or artifacts.
5. Review screenshot handling for personal data outside form fields, particularly the CI opt-in and artifacts; use a synthetic account and limit sensitive artifact exposure.
6. Execute and record the browser/live-site checks in the verification stage. The configured matrix and this static review are not evidence of successful execution.

### Nice to Have: Future Improvement

7. Add focused tests for configuration precedence, missing required credentials, and URL validation once credential sourcing is corrected.
8. Add automated dependency vulnerability monitoring and consider pinning/enforcing the Maven compiler/toolchain configuration for reproducible Java 21 builds.

## CI Comparison

The workflow guide accurately states that its document describes behavior rather than a successful run. The YAML configures push, pull request, and manual triggers; Temurin Java 21; serial Chrome/Firefox matrix jobs; headless execution; required credential-test mode; Surefire artifact upload even on failure; and failure-only screenshots. These align with the documented guide. Important caveat: `TestConfig` source defaults currently mean the workflow's `requireCredentialTests=true` does not by itself prove that secrets were supplied. The workflow does not upload the Extent HTML report. No CI run was inspected, so there is no claim of successful browser execution. The `.github/hooks/pre-commit.md` file is documentation only and is not treated as an active gate.

## Approval Conditions and Sign-Off

- **Condition 1:** Resolve the credential sourcing conflict before implementation proceeds, or explicitly accept the risk as the human reviewer. The recommended action is to remove defaults/property support; this review does not recommend accepting embedded credentials.
- **Condition 2:** Restore the invalid-password assertions before implementation proceeds, or explicitly accept that this required negative scenario is not currently protected by a passing test.
- **Condition 3:** Record the FR-2 inline-validation interpretation and preserve it as a testable acceptance criterion.
- **Human gate:** The human reviewer accepted the conditions and approved progression to Stage 4 on 2026-10-02. The conditions remain open implementation/review actions; this does not waive the identified risks.
- **Reviewer sign-off:** Design Review Agent; conditional recommendation only. Human approval: accepted with conditions.

## Traceability

Reviewed [architecture.md](architecture.md) and [requirements.md](requirements.md), plus the existing [`BaseTest`](../../src/test/java/Github_Copilot/base/BaseTest.java), [`TestConfig`](../../src/test/java/Github_Copilot/config/TestConfig.java), [`BasePage`](../../src/test/java/Github_Copilot/pages/BasePage.java), [`LoginPage`](../../src/test/java/Github_Copilot/pages/LoginPage.java), [`LoginPageTests`](../../src/test/java/Github_Copilot/tests/LoginPageTests.java), `TestData`, lifecycle/report/screenshot utilities, [pom.xml](../../pom.xml), `.github/workflows/selenium-login-workflow.md`, `.github/workflows/selenium-login.yml`, and `.github/hooks/pre-commit.md`. No `src/test/resources` files were present in the workspace. The CI and hook documents were treated as configuration/specification evidence, not execution evidence.
# Stage 4 Implementation Plan — Refreshed (2026-10-14)

## Authority and Scope

| Item | Value |
| --- | --- |
| Scope | Java 21 / Maven / Selenium framework for `US-AUTH-002`; no changes to the external login application. |
| Requirements | Current [requirements](requirements.md), analyzed 2026-10-14 from the authenticated rendered Confluence page. Exact source version/timestamp was unavailable; see the source caveat in that artifact. |
| Architecture | Fresh [architecture](architecture.md), approved by the user on 2026-10-14. |
| Design review | Fresh [design review](design-review.md), with C1-C5 explicitly accepted on 2026-10-14 for Stage 4 planning only. This is not acceptance of Stage 6 limitations. |
| Current verification evidence | [Verification report](verification-report.md), latest 2026-10-14 Stage 6 run: required-mode Chrome 27/27 passed, zero failures/errors/skips, with PASS WITH LIMITATIONS because AC 5.1 remains unmet. This run includes the Stage 5 dashboard-route refinement. |
| Stage 5 feedback checks | Focused Maven suite: 18 passed, 0 failures/errors/skips, including 3 account-home URL tests; `mvn -q -DskipTests compile test-compile` passed. The latest Stage 6 run also verifies the refined route in required-mode Chrome. |
| Current project boundary | Current source, tests, `pom.xml`, and README inspected. No `.github/workflows/` directory or configured CI, secret mapping, or artifact upload exists. |
| Plan status | 5 tasks; P4-01..P4-03 implementation is complete, P4-04 remains explicitly unmet/open, and P4-05 has a fresh PASS WITH LIMITATIONS after the P4-02 refinement. Estimates are planning estimates, not actual hours. |

The previous plan draft and older revisions are retained below as history and are superseded by this refreshed task list. This Stage 5 return addresses PR #5 review feedback and records the subsequent Stage 6 rerun; it does not change the source PRD or add CI. AC 5.1 remains unmet, and no browser-native validation is represented as source compliance.

## Baseline and Decisions

- Source scenarios are exactly TS-LOG-001 through TS-LOG-004. AC 1.1-1.4 also require the controls and password-reset navigation. TS-LOG-005 through TS-LOG-008 and AC-UI-001 are supplementary project regression checks, not additional PRD scenarios.
- **AC 5.1 remains unmet** unless the external page demonstrably provides application-rendered inline errors for both blank fields and the framework verifies those errors, or an authorized source-requirement change is made. Current browser-native validity checks are only the accepted project deviation; they do not fulfill AC 5.1. No change to the external site is in scope.
- Current TS-LOG-001 requires the configured account-home origin/path/query/fragment plus visible account navigation/content, in addition to authentication, expected welcome identity, and logout. Focused route tests and the latest required-mode Chrome run verify this assertion. TS-LOG-002 asserts feedback, unauthenticated state, retained form, and top-of-form notice position; the latest report records the live scenario passing.
- The Remember Me post-restart helper requires nonblank dashboard content and visible logout. The latest verification report records its Chrome scenario and four focused assertion tests passing. This is supplementary coverage; restart persistence is not a source requirement.
- `TestConfig` provides environment-only credentials and expected identity, required/optional credential guards, HTTPS/local-HTTP policy, positive bounded page-load timeout, and screenshots-off default. `BaseTest` configures fresh browser instances, bounded page-load timeout, zero implicit wait, headless options, profile reuse, and teardown. `BasePage` uses bounded explicit waits. These are existing implementation, not new work; behavior requiring a browser is still subject to fresh evidence.
- The existing invalid-password helper is active. Unknown-user, field-validation, recovery URL/form, and required-control checks exist. Recovery evidence means navigation/form presence only, not reset completion. No new dependency, test-data framework, performance target, CI workflow, or cross-browser execution is planned.

## Task Breakdown

Priority P0 denotes a gate or blocker to acceptance; P1 is a required assertion/verification improvement. Estimates cover framework work and focused checks, excluding external waiting and full browser runtime.

| ID | Task and deliverable | Status; priority / complexity / effort | Dependencies | Measurable acceptance and closure evidence |
| --- | --- | --- | --- | --- |
| P4-01 | Strengthen supplementary TS-LOG-005 after restarting Chrome with the same temporary profile: require dashboard content and visible logout. | **Complete; focused and Chrome evidence recorded 2026-10-14**; P1 / Medium / 2 h | None | `LoginScenarioAssertions.assertRememberMeSession` checks both signals and is called after restart. The latest verification report records TS-LOG-005 passing in Chrome and four focused assertion tests passing. |
| P4-02 | Add explicit AC 2.1 dashboard/My Account destination assertion to successful-login scenario and keep it distinct from the welcome/authentication assertions. | **Implemented and verified in required-mode Chrome 2026-10-14**; P1 / Medium / 1.5 h | None | The scenario requires the current URL to match the configured account-home origin and normalized path/query/fragment, in addition to visible account navigation/content. Focused URL tests reject account endpoints, malformed URLs, other origins, and query-based subpages. The latest 2026-10-14 required-mode Chrome run passed TS-LOG-001 with this assertion. No identity value may enter diagnostics. |
| P4-03 | Add explicit AC 3.1 top-of-form position assertion for the incorrect-password notice. | **Implemented and verified in required-mode Chrome 2026-10-14**; P1 / Medium / 1.5 h | None | The scenario asserts the visible notice is above the visible login form using DOM bounding-box positions, along with accepted feedback, unauthenticated state, and retained form. The latest report records the Chrome scenario passing. |
| P4-04 | Resolve or explicitly carry forward the AC 5.1 source-compliance disposition. No external-site implementation is in scope. | **OPEN; AC 5.1 explicitly UNMET**; P0 / Small / 1 h | None; external requirement-owner input may be needed | This criterion is not satisfied: current tests verify browser-native required-field validity, not application-rendered inline errors for both fields. Closure requires observed source-compliant inline errors plus matching assertions, or an authorized requirements-process change recorded in `requirements.md`. Until then, AC 5.1 remains unmet and an unconditional source-compliant PASS is prohibited. Planning acceptance and the prior PASS WITH LIMITATIONS acceptance do not change the PRD. |
| P4-05 | Run and document required-mode Chrome verification in Stage 6 after assertion tasks and the AC 5.1 disposition are recorded. | **PASS WITH LIMITATIONS; latest required-mode run complete 2026-10-14**; P0 / Large / 4 h | P4-01, P4-02, P4-03; P4-04 disposition recorded as unmet | The latest command `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true` recorded 27/27 passed, 0 failures/errors/skips in Chrome 155.0.8059.40, including the refined P4-02 route check. AC 5.1 remains unmet, so no source-compliant PASS is available; the user explicitly accepted this PASS WITH LIMITATIONS result for Stage 7 on 2026-10-14. Firefox support stays unchanged and Firefox/cross-browser execution remains unrun. |

### Dependency Order and Phases

The dependency graph is acyclic: P4-01 through P4-03 are complete; P4-04 remains an unmet source criterion; P4-05 is complete as PASS WITH LIMITATIONS after the latest required-mode Chrome run. AC 5.1 remains open for source compliance and prevents an unconditional PASS.

### Manual Verification Gate (No CI)

No `.github/workflows/` directory or automated push/pull-request test gate is configured. Restoring or replacing CI is outside the approved implementation scope. The manual Stage 6 gate has now run with `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true`: 27 passed, 0 failures/errors/skips, including the refined destination assertion. AC 5.1 remains unmet, so the result is PASS WITH LIMITATIONS rather than source-compliant PASS.

| Phase | Work | Exit condition |
| --- | --- | --- |
| 1. Existing foundation | P4-01 complete; configuration, driver lifecycle, bounded waits, guards, reporting, and cleanup inspected. | No foundation implementation gap is scheduled without a demonstrated defect. |
| 2. Source-criterion assertion closure | P4-02 and P4-03. | AC 2.1 destination and AC 3.1 notice position have explicit, stable assertions or are documented unresolved with evidence. |
| 3. Source gap disposition | P4-04. | AC 5.1 is source-compliant, formally changed through requirements control, or expressly remains unmet. Browser-native validation is never relabeled as inline feedback. |
| 4. Chrome verification | P4-05, Stage 6 only. | Required source cases are executed in Chrome; exact results and all skips/blockers are reported. Firefox, CI, slow-network, and performance coverage remain unclaimed. |

## Architecture and Requirements Traceability

| Requirement / architecture area | Existing coverage and task / verification mapping |
| --- | --- |
| AC 1.1: username/email and password; AC 1.2: Remember Me control; AC 1.3: visible/clickable Log in; AC 1.4: recovery-link navigation | `LoginPage` owns controls/actions; tests cover visibility, submission, and recovery destination/form. Verified in the latest required-mode Chrome run. Source defines no restart-persistence duration; P4-01/TS-LOG-005 remains supplementary. |
| AC 2.1 / FR-3 destination; AC 2.2 / welcome; TS-LOG-001 | Privacy-safe expected-identity comparison and authenticated/logout checks exist. P4-02 checks the configured account-home origin/path/query/fragment plus visible account content; focused URL tests and the latest required-mode Chrome scenario pass. |
| AC 3.1 / TS-LOG-002 | Invalid-password feedback, unauthenticated state, retained form, and top-of-form position are asserted. The latest report records a Chrome pass. Known username is runtime-only. |
| AC 4.1 / TS-LOG-003 | Unknown-user feedback and unauthenticated/retained-form checks passed in the latest required-mode Chrome run. |
| AC 5.1 / FR-2 / TS-LOG-004 | Current checks exercise browser-native validity, not application-rendered inline errors. P4-04 preserves source gap or requires source-compliant evidence/authorized change; P4-05 reports the exact status. TS-LOG-007/008 are supplementary too. |
| US-AUTH-002; source scenario inventory | Source scope is TS-LOG-001..004. P4-05 executes all four in Chrome; supplementary TS-LOG-005..008 and AC-UI-001 remain separately identified. |
| NFR-1 | No measurable performance/security target exists. No SLA, security-scan, reliability, or slow-network task is inferred. |
| `TestConfig` / `TestData` | Existing non-secret setting precedence, HTTPS/local-HTTP guard, positive bounded `pageLoadTimeout`, environment-only credentials/identity, wait defaults, and message tokens. Focused configuration tests are recorded passing in the 2026-10-14 verification report. No new configuration work. |
| `BaseTest` driver lifecycle | Fresh Chrome/Firefox setup, safe headless options, page-load timeout integration, zero implicit wait, temporary Remember Me profile restart/cleanup exist. Chrome only is exercised in P4-05; Firefox support is unchanged and execution unrun. |
| `BasePage` / `LoginPage` | Bounded explicit waits, page-owned selectors/actions, submission outcomes, recovery navigation/form state, account-home URL/content state, and retained-form checks exist. P4-02/P4-03 implement the destination and notice-position assertions; both passed in the latest required-mode Chrome run. No sleeps or external-site changes. |
| Credential guard / scenario helpers | Required mode fails when inputs are absent; optional mode skips. Invalid-password, expected-welcome, and Remember Me assertions exist. Focused tests are evidence for helper logic only, not live authentication. |
| JUnit listeners, outcomes, logging, Extent, cleanup | Existing pass/fail/skip classification, duration/reporting, and best-effort diagnostics/cleanup are retained; original test outcome must remain authoritative. Focused lifecycle test passed per report; runtime/browser diagnostics are exercised only in P4-05. |
| Screenshots and artifacts | Capture defaults off; selected-field redaction is not page-level sanitization. Keep off. No CI workflow or artifact policy exists. Enabling capture is outside this plan and requires page-content review plus approved artifact access/retention. |
| Maven/Surefire, CI, docs | Surefire's `**/*Tests.java` includes browser scenarios and focused `*Tests`; `LoginPageRunner` is not selected by default. No CI workflow exists; this plan does not add one because it is outside approved scope. The manual required-mode Chrome gate completed with 27 passes, zero failures/errors/skips, after which the current PR draft was reconciled. There is no automatic push/PR test gate, and no CI pass may be claimed. |

## Review Conditions and Risk Disposition

| Finding / recommendation / condition | Disposition and evidence |
| --- | --- |
| R1 / C1: AC 5.1 source inline errors | P4-04. Preserve the source wording. Browser-native validation remains a project deviation only. The user accepted C1 for planning, not a PRD change or Stage 6 limitations. |
| R2 / C2: AC 2.1 destination assertion | P4-02 adds the assertion; P4-05 verifies it in Chrome. Existing welcome/authenticated/logout checks do not substitute. |
| R3 / C2: AC 3.1 top-of-form position | P4-03 adds a stable placement check or records an evidence-backed unresolved limitation; P4-05 verifies live behavior. |
| R4 / C3: current required-mode Chrome evidence | P4-05. The latest report records 27/27 required-mode tests passing with PASS WITH LIMITATIONS, including the strengthened P4-02 URL check; no skips occurred. |
| R5 / C4: screenshot exposure | Disposition complete for this scope: capture remains disabled by default and no CI/artifact upload exists. Do not enable without page-level data and artifact handling approval. |
| C5: evidence and browser scope | P4-05 runs Chrome only. Firefox support remains unchanged; Firefox/cross-browser, CI, performance, slow-network, dependency scan, screenshots, and reliability checks are unrun/not planned absent need and environment. No CI workflow is added; manual required-mode Chrome verification is the explicit PR gate. |
| Credential-source and invalid-password findings; positive config/lifecycle architecture | Existing source and focused tests cover environment-only credential/identity reads, required/optional guard, active invalid-password checks, bounded waits/timeouts, transport policy, and outcome classification. No new implementation task; verify actual scenarios only in P4-05. |
| Recovery-flow scope (R5 in the historical review) | Existing recovery navigation checks destination URL, reset form visibility, and unauthenticated state. Keep claim limited to navigation/form presence; no reset submission/completion task. |

Principal risks are missing authorized runtime values/account, Chrome or compatible driver availability, live-site/network availability, unstable application markup for AC 3.1 placement, and unavailable requirements-owner action for AC 5.1. Provide credentials only through the test process environment and never expose values. A missing prerequisite means blocked/unrun or an accurately classified required-mode failure, never a pass. Screenshots stay disabled.

## Stage 6 Gate and Success Criteria

- **PASS:** Required Chrome verification executes all required cases with zero failures, errors, or skips, and every source criterion is met, including application-rendered inline feedback for AC 5.1, the AC 2.1 destination, and AC 3.1 notice position.
- **PASS WITH LIMITATIONS:** Executed checks have zero failures/errors but a required case is skipped or a requirement is unmet/unverified. The report must list each item, reason, impact, and follow-up. Obtain fresh explicit human acceptance before Stage 7; the 2026-10-14 C1-C5 planning acceptance does not qualify.
- **FAIL:** An executed check fails or errors, including a required-mode credential guard failure. Stage 7 is blocked until scoped correction and rerun.
- **BLOCKED / NO VERDICT:** Required Chrome cannot run because browser/driver, site/network, or authorized runtime prerequisites are unavailable. Do not substitute historical outcomes or label the blocker PASS WITH LIMITATIONS.

No CI, Firefox, cross-browser, performance, slow-network, repeatability, security-scan, or screenshot result may be claimed unless actually run. P4-01..P4-03 implementation is complete, P4-04 / AC 5.1 remains explicitly unmet, and P4-05 has a fresh PASS WITH LIMITATIONS after the dashboard-route refinement. The manual verification gate is complete; PR update and re-review remain outstanding. The deleted CI workflow is not restored or replaced within scope.

---

# Superseded Plan Snapshot (Retained for Reconciliation)

The previous working-plan text follows unchanged as historical context. Its task count, status, estimates, requirements/architecture mapping, and Stage 6 statements are superseded by the refreshed plan above. Retain this snapshot for audit history; do not execute it as the current plan.

# Stage 4 Implementation Plan — Previous Status (2026-10-14)

## Context and Authority

| Item | Value |
| --- | --- |
| Scope | Java 21 / Maven / Selenium test framework for `US-AUTH-002`; no changes to the external login site. |
| Requirements | Fresh source recheck dated 2026-10-09; Confluence page 11796481, version 2, as recorded in [requirements](requirements.md). |
| Architecture | Updated [architecture](architecture.md), approved by the user on 2026-10-09. |
| Design review | Fresh [Stage 3 review](design-review.md), dated 2026-10-09. The user explicitly accepted fresh conditions C1-C5 on 2026-10-14, authorizing Stage 4 planning. This acceptance does not accept Stage 6 limitations. |
| Verification evidence | [Verification report](verification-report.md), dated 2026-10-09: Chrome browser class had 9 cases, 6 passed and 3 skipped (TS-LOG-001/002/005) in optional credential mode. No successful required-mode Chrome run is recorded. Treat this as historical evidence, not a fresh run. |
| Inspected baseline | Current Java/tests, [pom.xml](../../pom.xml), [README](../../README.md), and repository instructions. The current repository has no `.github/workflows/` directory or configured CI. |
| Planned items | 2 total: 1 Medium (P4-01, 2 h, complete) and 1 Large (P4-02, 4 h, open for Stage 6). Original estimate 6 engineer-hours; 4 hours remain, excluding external waiting and browser runtime. |
| Gate status | Stage 4 planning is authorized by the user's 2026-10-14 acceptance. No Stage 5 source/test work remains under this plan; Stage 6 verification is still outstanding. |

This is the authoritative plan for the restarted workflow. The fresh Stage 3 section still says its human gate is pending; this plan records the user's subsequent explicit acceptance of C1-C5 on 2026-10-14, which resolves that gate for Stage 4 planning only. It does not rewrite the review artifact, accept Stage 6 limitations, or convert skips or unmet requirements into passes. All earlier task breakdowns and status narratives below are archival only and are superseded by this status and task list. Historical test outcomes are not fresh execution evidence.

## Current Baseline and Decisions

Current source already provides environment-only valid credentials and expected welcome identity, required-versus-optional credential guards, active invalid-password/unknown-user assertions, a strict identity comparison for TS-LOG-001, bounded page and explicit wait timeouts, zero implicit wait, fresh Chrome/Firefox drivers, a temporary Remember Me profile, recovery URL and form checks, lifecycle pass/fail/skip reporting, Extent output, and opt-in screenshots disabled by default. P4-01 is implemented: after restarting Chrome with the same profile, the Remember Me scenario asserts nonblank dashboard content and visible logout; the helper has three focused unit tests. The 2026-10-09 report records those focused tests passing, but TS-LOG-005 itself skipped before login, so persistence remains unverified. No additional implementation task, dependency, or broad source refactor is planned.

The user's acceptance of C1-C5 permits Stage 4 planning and carries these dispositions forward; it does not approve Stage 6 limitations:

1. **FR-2 remains a project deviation, not source fulfillment.** The PRD still requires application-rendered inline required-field errors. Existing TS-LOG-004/007/008 check browser-native validity; the accepted deviation does not satisfy or rewrite the PRD wording. Do not silently add site changes or relabel native validation as source compliance.
2. **Fresh Chrome evidence is required for TS-LOG-001, TS-LOG-002, and TS-LOG-005.** These outcomes have not been observed in this restarted workflow. Use required credential mode and classify every result as passed, failed, skipped, or unrun. A skip is never a pass.
3. **Remember Me oracle implemented; live behavior unverified.** P4-01 now requires dashboard content and visible logout after restarting with the same temporary profile. Only a fresh browser run can establish persistence.
4. **Keep failure screenshots disabled.** Capture defaults off; no CI workflow or artifact upload is configured. Selected-field redaction is not page-level sanitization. Do not enable capture or share screenshots until page-level content and artifact access/retention receive explicit safety review.

## Remaining Tasks

| ID | Task and deliverables | Status; priority / complexity / effort | Dependencies | Measurable acceptance criteria / closure evidence |
| --- | --- | --- | --- | --- |
| P4-01 | Strengthen TS-LOG-005 after browser restart using the existing scenario-assertion pattern. Assert account dashboard content and visible logout; generic “hello” text alone cannot establish authentication. Add focused helper coverage and preserve privacy-safe diagnostics. | **Complete**; P1 / Medium / 2 h | None | Current `LoginScenarioAssertions.assertRememberMeSession` requires nonblank dashboard text and visible logout, and `shouldPersistSessionWhenRememberMeIsEnabled` calls it after restart with the same profile. `LoginScenarioAssertionsTests` contains three tests; the 2026-10-09 report records all three passing. This closes the implementation task only. Live TS-LOG-005 remains unverified because it skipped before login. |
| P4-02 | Perform and document fresh required-mode Chrome verification. Do not run Firefox, alter Firefox support, or infer runtime success from configuration or retained reports. Record exact command, Java/Maven/Chrome/driver versions, outcome counts, and Surefire report locations in the Stage 6 report. | **Open; Stage 6**; P0 / Large / 4 h | P4-01 complete | When browser, site/network, and approved runtime values are available, run `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true` with `LOGIN_VALID_USERNAME`, `LOGIN_VALID_PASSWORD`, and `LOGIN_EXPECTED_WELCOME` supplied only through the Maven process environment/approved secret store. Check presence without reading or printing values. TS-LOG-001, TS-LOG-002 (known username), and TS-LOG-005 must execute; record any failure, skip, or blocker accurately and never count a skip as a pass. Report the complete suite counts and keep FR-2 source inline errors distinct from the project-native-validation deviation. Firefox/cross-browser and CI remain unrun unless actually exercised. |

### Order and Dependencies

The dependency graph is acyclic: completed `P4-01` → open `P4-02`. The implementation and focused tests are already present; the only remaining item is Chrome verification in Stage 6. Screenshot capture remains disabled; no page-level review or enablement is authorized. FR-2 source wording remains unchanged and unmet by the accepted browser-native project deviation.

## Architecture and Existing-Work Disposition

| Architecture area | Current inspection and plan disposition |
| --- | --- |
| `TestConfig` / `TestData` | Runtime precedence, HTTPS/local-HTTP guard, bounded positive page-load timeout, environment-only valid credentials and expected identity, test data tokens, and focused config tests exist. No new configuration or data abstraction. |
| `BaseTest` driver lifecycle | Fresh Chrome/Firefox setup, headless options, page-load timeout, zero implicit wait, teardown, temporary Remember Me profile and restart exist. Keep the existing browser support and cleanup behavior; exercise Chrome only in P4-02. |
| `BasePage` / `LoginPage` | Bounded explicit waits and page-object ownership of controls, feedback, authentication state, and recovery navigation exist. Do not add sleeps or change the external site. |
| Scenario suite | TS-LOG-001 through TS-LOG-008 and AC-UI-001 are represented. TS-LOG-001 has an expected-identity comparison; TS-LOG-002 has active negative assertions; TS-LOG-003 checks unknown-user feedback; TS-LOG-004/007/008 cover the accepted native-validation deviation; TS-LOG-006 checks recovery URL, visible reset form, and unauthenticated state. P4-01's implementation gap is closed; P4-02 is the only remaining task and supplies fresh Chrome evidence. |
| Credential guard / scenario assertions | Missing credentials fail in required mode and abort in optional mode; helpers assert invalid-password outcomes and customer-identifying welcome without exposing configured identity. Add only Remember Me assertion coverage under P4-01. |
| Suite / Maven Surefire | The `**/*Tests.java` include selects browser scenarios and focused unit test classes; `LoginPageRunner` is a separate suite entry and is not the standard `mvn test` selection. Preserve current discovery and report the actual selected counts from P4-02. |
| Listeners, outcomes, logging, Extent | Lifecycle/report code classifies aborts as skips, records outcomes, and uses redacted diagnostics. Existing focused lifecycle coverage remains in place; no report refactor absent a demonstrated defect. Inspect generated reports as verification evidence, not as prior results. |
| Screenshot utility / artifacts | `allowFailureScreenshots()` defaults false. No CI workflow or artifact upload is configured. Selected input redaction does not establish whole-page safety. Keep capture disabled; a later enablement requires page-level data and artifact access/retention approval. |
| CI and documentation | No GitHub Actions workflow or CI jobs are configured. Run Maven checks locally; do not claim a CI matrix, secret mapping, or artifact upload. Preserve the existing Chrome/Firefox driver support; current browser verification policy is Chrome-only. |

## Requirements Traceability

| Requirement / scenario | Plan coverage and verification status |
| --- | --- |
| FR-1 / AC-UI-001; control checks in TS-LOG-004/005/006 | Existing page object and UI assertions cover username, password, Remember Me, login, and recovery controls. Run in P4-02. |
| FR-2 / TS-LOG-004, TS-LOG-007, TS-LOG-008 | Existing tests exercise browser-native required-field validity. This is the approved project deviation only; source inline-error behavior remains unmet/unverified. Report separately; do not claim source fulfillment. |
| FR-3 / TS-LOG-001 | Existing required expected-identity comparison plus authenticated/dashboard/logout assertions. The 2026-10-09 run skipped this case; P4-02 must record a fresh required-mode Chrome outcome. |
| FR-4 / TS-LOG-002, TS-LOG-003 | Active invalid-password and unknown-user feedback, unauthenticated, and retained-form assertions exist. TS-LOG-002 requires a known username. Run both in P4-02. |
| FR-5 / TS-LOG-005 | Temporary profile restart and P4-01 dashboard-plus-visible-logout assertion are implemented and focused-tested. The 2026-10-09 browser case skipped before login; P4-02 must observe actual Chrome persistence. |
| FR-6 / TS-LOG-006 | Existing supported recovery URL, visible reset form, and unauthenticated checks provide navigation/form-presence evidence only. Run in P4-02; do not claim reset completion/usability beyond observed checks. |
| US-AUTH-002 | Mapped across FR-1..FR-6. End-to-end result depends on fresh Chrome execution and the source/project distinction for FR-2. |
| NFR-1 | PRD has no measurable security or performance target. Add no SLA, performance, slow-network, security-scan, or reliability claim. Credential and screenshot safeguards remain as specified. |

## Stage 3 Conditions and Risk Disposition

| Condition / risk | Disposition |
| --- | --- |
| C1 / FR-2 source versus deviation | Explicitly accepted by the user on 2026-10-14 for Stage 4 planning. Preserve the PRD inline-error requirement; native validation is not its fulfillment. This is not Stage 6 limitation acceptance. |
| C2 / unverified TS-LOG-001, TS-LOG-002, TS-LOG-005 | Explicitly accepted on 2026-10-14 for planning. The 2026-10-09 optional-mode run recorded 3 skips; no successful required-mode Chrome run exists. P4-02 requires fresh evidence. Required-mode guard failure is a failure, never a pass. |
| C3 / Remember Me oracle | Explicitly accepted on 2026-10-14 for planning. P4-01 is implemented and focused-tested; live persistence remains unverified until P4-02. |
| C4 / screenshot exposure | Explicitly accepted on 2026-10-14 for planning. Capture remains disabled pending page-level safety and artifact handling review. No authenticated screenshots are requested or planned. |
| C5 / browser evidence boundary | Explicitly accepted on 2026-10-14 for planning. Run Chrome only. Firefox support remains unchanged; Firefox/cross-browser and CI execution are unrun. |
| Recovery-page evidence (R5) | Existing TS-LOG-006 checks both destination URL and reset-form visibility. Keep claims limited to navigation and form presence; no reset submission task. |
| Credential-source / invalid-password findings closed in current code | Environment-only secret reads, required/optional guard behavior, and active negative assertions are present with focused tests. Do not reschedule already implemented fixes as new work; P4-02 supplies fresh scenario evidence. |

Principal execution risks are unavailable Chrome/driver resolution, network/site availability, missing approved credentials/expected identity, and account access. The 2026-10-09 report states that the required runtime values were unavailable to Maven. Provide secrets only to the process environment and never echo them. An unavailable prerequisite means verification is blocked/unrun, not passed. `ALLOW_FAILURE_SCREENSHOTS` must remain unset/false. The prior 2026-10-09 acceptance of that report's Stage 7 limitations does not accept limitations for the current Stage 6 gate.

## Stage 6 Gate and Success Criteria

- **PASS:** Every required Chrome test executes, the complete required suite has zero failures, zero errors, and zero skips, and all source PRD criteria are met. In particular, browser-native FR-2 validation alone cannot qualify as meeting the source inline-error criterion.
- **PASS WITH LIMITATIONS:** Every executed test has zero failures and zero errors, but one or more tests are skipped or a documented requirement is unmet/unverified. List each skip/unverified item, reason, impact, and follow-up. Obtain explicit human acceptance of those limitations before Stage 7; acceptance does not convert a skip or unmet requirement into a pass. Under the currently approved FR-2 deviation, if inline errors remain unmet, unconditional PASS is unavailable; at best the result is PASS WITH LIMITATIONS after the required evidence and explicit acceptance.
- **FAIL:** Any executed test failure or error, including required-mode credential guard failure, blocks Stage 7 and requires a scoped fix and rerun. Never count a required skip as a pass.
- **Blocked / no verdict:** If Chrome, site/network, or authorized runtime configuration prevents the suite from running, do not label it PASS or PASS WITH LIMITATIONS; report the blocker and leave Stage 7 unavailable.

Stage 4 planning is complete: the task sequence, architecture disposition, risk handling, traceability, and gates are recorded, and the user accepted fresh C1-C5 on 2026-10-14. P4-01's source/test implementation is already complete; no further Stage 5 code task is open under this plan. P4-02 is the sole remaining Stage 6 verification item. The 2026-10-09 verification report is historical, records 6 passed/3 skipped Chrome cases in optional mode, and does not establish a successful required-mode run. Do not claim Stage 6 limitations are accepted: fresh explicit human acceptance is required before Stage 7 if its rules call for it. Firefox/cross-browser and CI remain unrun, screenshots remain disabled, and FR-2's source inline-error criterion remains unmet. This Stage 4 documentation update does not run tests or change source, tests, or configuration.

---

# Prior Plan Material (Superseded; Preserved as Historical Archive)

Everything below this heading is retained from the earlier working plan for audit/history. Its estimates, task breakdowns, implementation status, browser scope, and gate statements are superseded by the fresh plan above and are not current instructions or evidence.

## Project Context

## Project Context

| Item | Value |
| --- | --- |
| Stage | SDLC Stage 4: Implementation Planning |
| Revision date | 2026-10-09 |
| Scope | Java 21 / Maven / Selenium framework for US-AUTH-002; no external-site changes, remote grid, or parallel execution. |
| Controlling inputs | [Requirements](requirements.md), [Architecture](architecture.md) (user-approved 2026-10-09), [Design Review](design-review.md) (all conditions accepted 2026-10-09), current source/configuration. |
| Historical evidence | [Verification report](verification-report.md), dated 2026-10-02 only; not a current run. |
| Planned tasks | 4 |
| Effort / complexity | 10 engineer-hours: 1 Small, 2 Medium, 1 Large; excludes external waiting and browser runtime. |

This 2026-10-09 revision supersedes the task breakdown below and does not change source requirements. Stage 4 is authorized and Stage 5 implementation has been executed; P4-04 remains incomplete until its required Chrome tests execute and pass. No Firefox run is in scope under the current user direction.

## Current Dispositions

| Area | Evidence-based status |
| --- | --- |
| Credentials | `TestConfig` reads valid credentials only from `LOGIN_VALID_USERNAME` / `LOGIN_VALID_PASSWORD`; required mode fails and optional mode aborts when missing. Guard/config tests exist. Former source defaults/property-sourcing gap is closed, not new work. Historical focused tests passed 2026-10-02 only. |
| Invalid password | `LoginScenarioAssertions` actively checks feedback, unauthenticated state, and retained form. Former disabled-assertion gap is closed. Live TS-LOG-002 remains unverified: historical run stopped at its known-username guard. |
| FR-3 identity | `hasCustomerIdentifyingWelcome()` accepts any nonblank text after `Hello `; it does not compare the welcome with the authenticated account. Close this before a Stage 6 PASS. |
| FR-2 | Native validation is the separately recorded project deviation. User accepted carrying it into planning on 2026-10-09. Source PRD inline-error wording remains unchanged and unmet by this deviation; requirements note no separate approval record was retrieved. |
| Browser evidence | Historical latest Chrome repeat and Firefox each reported 20 tests: 17 passed, 3 failed at credential guards for TS-LOG-001/002/005. Earlier Chrome count 29 is unreconciled. No current browser/CI run was performed for this plan. |
| Screenshots | Default is off; current redaction covers selected inputs only. Page-level safety is unproven. CI uploads screenshots if present but does not enable capture. Keep capture off absent explicit page/artifact review. |

## Current Task Breakdown

Effort includes focused implementation/review, not external waiting or full browser runtime. Priority P0 blocks acceptance; P1 is required before merge; P2 is a bounded verification improvement.

| ID | Task and deliverable | Priority / complexity / effort | Dependencies | Measurable acceptance |
| --- | --- | --- | --- | --- |
| P4-01 | Strengthen the existing FR-3 welcome assertion with a deterministic expected-identity oracle from the approved test account. Do not hardcode customer data or disclose identity in diagnostics. | P0 / M / 3 h | None | Focused assertion rejects generic nonblank `Hello` text and accepts the expected account identity. Live comparison is in memory; identity is absent from assertion messages, logs, Extent, screenshots, and committed files. If no reliable oracle exists, record a blocker and do not claim FR-3 verified. |
| P4-02 | Review screenshot defaults, workflow switches, redaction limits, artifact access/retention. Keep capture disabled; change code/config only for a demonstrated mismatch. | P1 / M / 1.5 h | None | No-opt-in `allowFailureScreenshots()` is false; workflow does not enable capture; docs state selected-field redaction is not page-level protection. Do not upload/share authenticated screenshots. Any enablement requires representative page-level review and approved account/artifact handling. |
| P4-03 | Assess whether TS-LOG-006 has a stable, non-sensitive recovery-page signal beyond its URL check; add an assertion only if reliable. | P2 / S / 1.5 h | None | Either a repeatable content assertion distinguishes the recovery form from a matching URL, or record why URL-only is the strongest reliable observable. Do not claim usability from URL evidence alone. |
| P4-04 | Run the required full suite in Chrome only, with secrets supplied only through the Maven process environment/CI secrets. Firefox execution is outside this verification scope; leave its support unchanged and report it unrun. | P0 / L / 4 h | P4-01, P4-02, P4-03 | With Java 21, Maven, Chrome, site/network, and authorized credentials available, run `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true`. Never place credential values in arguments/output. Every required Chrome test, including TS-LOG-001, TS-LOG-002 with known username, and TS-LOG-005, must execute rather than guard-fail/skip and pass for P4-04 closure. Report FR-2 only as the accepted project deviation, not source-PRD fulfillment. Record exact commands, Chrome/driver versions, pass/fail/error/skip counts, and Surefire artifacts. Report CI only if run. Any failure, required skip, or missing prerequisite blocks closure; Firefox/cross-browser coverage remains explicitly unverified. |

### Dependencies and Phases

The graph is acyclic: P4-01/02/03 have no dependencies; P4-04 depends on all three. First close identity and screenshot-safety prerequisites, assess recovery content, then run browser validation. Report unavailable runtime prerequisites as blocked, never as passes.

### Requirements and Architecture Traceability

| Scope | Current plan mapping |
| --- | --- |
| FR-1 / AC-UI-001 | Existing controls and test; historical Chrome/Firefox passes only. Re-run in P4-04. |
| FR-2 / TS-LOG-004/007/008 | Existing native-validation assertions; P4-04 records project behavior separately. Never describe it as source inline-error fulfillment. |
| FR-3 / TS-LOG-001 | P4-01 identity assertion; P4-04 live valid login in Chrome. |
| FR-4 / TS-LOG-002/003 | Active invalid-password assertion and unknown-user test exist. TS-LOG-003 has historical passes; TS-LOG-002 awaits known-username execution in P4-04. |
| FR-5 / TS-LOG-005 | Profile restart is implemented but persistence unverified; P4-04. |
| FR-6 / TS-LOG-006 | URL assertion exists; P4-03 assesses content signal; P4-04 records live result. |
| NFR-1 | No measurable security/performance threshold in PRD; invent none. |
| `TestConfig`, `TestData`, config tests | Precedence, transport, timeout, credentials, test tokens exist and have focused tests. No new configuration/data framework task. |
| `BaseTest`, `BasePage`, `LoginPage` | Fresh Chrome/Firefox lifecycle, timeout/zero implicit wait, profile restart/cleanup, bounded explicit waits and page actions exist. P4-04 exercises Chrome only; Firefox support remains unchanged and unverified. |
| Credential guard and scenario assertions | Implemented and unit-tested; former gaps are closed. Live outcomes remain P4-04. |
| `LoginPageRunner`, Surefire | Runner exists; `**/*Tests.java` selects `LoginPageTests` and unit tests, not `*Runner`. Use and observe current selection in P4-04; no discovery change planned. |
| Listeners, `TestOutcome`, `LogUtil`, Extent, screenshots | Lifecycle outcome/duration, redaction, report, opt-in screenshot components exist. Historical focused tests only; P4-02 keeps screenshot risk controlled. No added task absent concrete defect. |
| Maven, CI YAML/guide | Java 21, pinned dependencies, serial browser matrix, environment secret mapping, Surefire and conditional screenshot uploads are configured; no current CI evidence. P4-04 records CI only if run. |

### Review Conditions and Risk Disposition

| Review condition / recommendation | Disposition |
| --- | --- |
| C1: carry FR-2 deviation while preserving source wording | Accepted for planning 2026-10-09. Source inline criterion remains unmet; requirements note no separate approval record retrieved. P4-04 reports project behavior distinctly. |
| C2: TS-LOG-001/002 known username/005 remain unverified | Accepted 2026-10-09; required-mode Chrome execution is P4-04, not a pass claim. Firefox remains unrun by user direction. |
| C3: FR-3 identity gap must close before Stage 6 PASS | Accepted 2026-10-09; P4-01 then P4-04. |
| Former credential-source / disabled assertion findings | Closed in current source and unit-test coverage; not scheduled as new code work. |
| Screenshot risk | P4-02; disabled default remains until page-level safety and artifact controls are approved. |
| Recovery-page content recommendation | P4-03; add only a stable signal, otherwise document URL-only limitation. |
| Toolchain pinning, timeout-message enhancement, dependency monitoring | No concrete defect or selected policy/service; explicit follow-up only, no speculative task/dependency. |

Key blockers are environment delivery of both secrets to the Maven process, a privacy-safe expected identity for an authorized test account, and target-site/network access. Check secret presence only. Chrome with compatible driver resolution is required for P4-04. Firefox/cross-browser execution is explicitly outside this verification scope and remains unverified. Preserve Chrome Surefire output separately. Slow-network tooling and PRD thresholds are absent; no performance task or SLA is planned.

### Success Criteria and Handoff

Stage 4 planning covers all architecture components, requirements, accepted review conditions, risks, and an acyclic four-task queue (10 hours). P4-01 and P4-02 are prerequisites to P4-04; P4-03 resolves the recovery-content disposition. No Maven, browser, or CI run was performed for this documentation revision. Historical results remain dated 2026-10-02 only.

Stage 6 may PASS only when every required Chrome test executes and passes with zero failures, errors, or skips, all source requirements are met, and FR-3 identity comparison is implemented and observed in a valid login. If every required Chrome test passes but a documented limitation remains, Stage 6 may report PASS WITH LIMITATIONS; the orchestrator must obtain explicit human acceptance before Stage 7. TS-LOG-001/002/005 must execute in required mode, not skip or stop at the credential guard. FR-2 project behavior is not source-PRD fulfillment. Firefox/cross-browser coverage remains unverified. Screenshots remain disabled unless page-level safety is explicitly established. Stage 5 implementation has run; P4-04 remains open for Stage 6 verification.

## Historical Plan (2026-10-02; Superseded, Do Not Execute)

The remainder is retained as audit history. Its inventory, task IDs, estimates, approval narrative, and success criteria were superseded by the current 2026-10-09 revision above; stale gaps below must not be scheduled or treated as current evidence.

### Historical Existing Implementation Inventory

These are present in the inspected code and are not proposed as new features. Their behavior still needs focused verification where listed below.

| Component | Observed status |
| --- | --- |
| `TestConfig` | URL, browser, headless, page-load timeout, credential-test requirement, screenshot opt-in, and local-HTTP settings exist. URL transport validation and system-property/environment/default precedence exist for ordinary settings. **Gap:** valid credentials have source-code fallbacks and JVM-property sources. |
| `BaseTest` | Fresh Chrome/Firefox drivers, zero implicit wait, bounded page-load timeout, dimensions/headless mode, listener registration, teardown, and temporary Remember Me profile restart/cleanup exist. Runtime and failure-cleanup behavior are unverified. |
| `BasePage` / `LoginPage` | Explicit visibility/clickability/condition waits and login/recovery page actions exist; no fixed sleeps were found in these classes. Required-field checks currently inspect browser-native validation with a scoped site-error fallback. FR-2 semantics remain unresolved. |
| `LoginPageTests` | Valid login, invalid password, unknown user, both-empty and single-empty fields, Remember Me, recovery navigation, and required controls have test methods. **Gap:** invalid-password assertions are commented out; valid-login assertions do not prove the greeting identifies the customer. |
| `TestData` | Wait defaults and message tokens are centralized. Some blank-field tokens remain inline in tests; consolidate only if useful for consistent assertions. |
| Listeners and utilities | Lifecycle result/duration logging, Extent reporting, optional screenshots, and input-field screenshot redaction exist. Page-level personal-data exposure and report artifact policy need review. |
| Build and CI | Maven Surefire discovers `LoginPageTests`; a JUnit suite runner is present but is not selected by the `**/*Tests.java` include. GitHub Actions configures serial headless Chrome/Firefox jobs and required credential-test mode. Surefire and failure screenshots are uploaded; Extent HTML is not. These are configuration facts, not successful-run evidence. The documented pre-commit checks are not installed automation. |

No `src/test/resources` test-data files were found. Keep the current `TestConfig` / `TestData` organization unless implementation evidence shows a concrete need for resources; add no new dependency for this scope.

## Task Breakdown

Priority: **P0** blocks safe implementation or required test coverage; **P1** required before merge; **P2** verification/integration improvement. Complexity: S/M/L. Estimates include implementation and focused local checks, not external access wait time.

| ID | Task and deliverable | Priority / complexity / estimate | Dependencies | Measurable acceptance criteria |
| --- | --- | --- | --- | --- |
| T01 | **Enforce secret-only valid credentials.** Update `TestConfig` and relevant README/CI guidance: remove valid-credential source defaults and JVM-property sourcing; retain `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD` runtime environment inputs only. Preserve required-vs-optional credential test behavior without echoing values. | P0 / M / 3 h | None | With either credential absent, its value resolves empty; a JVM property alone cannot supply it. With `requireCredentialTests=true`, credential-dependent tests fail clearly when required secrets are missing; otherwise they are assumption-aborted, not passed. Logs/reports contain neither supplied value. README and workflow guide describe environment/secret injection only. |
| T02 | **Restore invalid-password assertions** for TS-LOG-002. | P0 / S / 1 h | T01 | The scenario asserts an approved incorrect-password feedback token, unauthenticated state, and visible login form. Demonstrate by a focused test mutation or equivalent negative check that removing/rejecting the expected outcome fails the test. |
| T03 | **Resolve and test FR-2 required-field semantics.** Compare actual site behavior and requirement wording; record the interpretation in implementation/test documentation, then align `LoginPage` and blank-field assertions. | P1 / M / 2.5 h | None; requires live page access for behavior confirmation | Submitting both fields empty proves both required outcomes, remains unauthenticated, and retains the login form. Each single-empty case proves its corresponding required outcome. Assertions match the documented product behavior (native browser validation is not called application-rendered inline feedback unless accepted explicitly). No invented exact message text. |
| T04 | **Strengthen successful-login identity assertion** for TS-LOG-001 without exposing customer data. | P1 / M / 1.5 h | T01 | A successful login requires visible welcome content that identifies the authenticated customer and a logout control. The identity value is not printed in logs, report metadata, assertion messages, or committed artifacts; test uses only in-memory comparison or a non-sensitive presence assertion. |
| T05 | **Close screenshot personal-data risk.** Review capture redaction beyond inputs; establish an approved test-account/artifact policy and adjust screenshot capture or CI opt-in before relying on screenshots from authenticated states. | P1 / L / 3 h | T01 | Screenshots remain disabled by default. Before enabling CI capture, demonstrate that the valid test account's identifying/account data is absent or redacted in a representative failure capture; confirm capture failure does not hide the test failure. Use a dedicated synthetic account if available. Retain the existing failure-only upload and short retention unless a stricter policy is required. |
| T06 | **Add focused configuration tests** for secret absence/requirement, ordinary setting precedence, timeout boundaries, and URL transport validation. Use a deterministic test seam or subprocess approach; do not make tests depend on the developer's actual environment. | P1 / M / 2 h | T01 | Tests cover missing and present credentials without embedding values; required and optional behavior; system property > environment > default for non-secret settings; accepted/rejected timeout and HTTPS/local-HTTP cases. `mvn -q -DskipTests compile test-compile` and focused tests pass. |
| T07 | **Verify driver and synchronization ownership.** Review only concrete gaps in `BaseTest`, `BasePage`, and `LoginPage`; preserve bounded explicit waits and the current page-object boundary. Fix only demonstrated lifecycle, cleanup, `pageLoadTimeout`, or synchronization defects. | P1 / M / 2 h | None | `pageLoadTimeout` reaches Chrome and Firefox, implicit wait remains zero, waits stay bounded, no fixed sleeps are introduced, and driver/profile/listener cleanup runs on pass and failure. The Remember Me restart reuses its temporary profile. Focused compile/tests pass; any browser-dependent assertion is deferred to T11. |
| T08 | **Keep test data organized and scenario inventory traceable.** Audit token/default ownership in `TestData` and tests; move duplicated inline acceptance tokens only where this improves consistency. Preserve independent tests and avoid resource/data frameworks. | P2 / S / 1 h | T02, T03, T04 | All scenario token lists/default waits have one clear owner; existing TS-LOG-001..006 plus TS-LOG-007/008 and AC-UI-001 remain independently discoverable and asserted. No credentials are added to `TestData`. |
| T09 | **Verify lifecycle diagnostics and report outcomes.** Correct any discovered mismatch in start/duration/pass/fail/skip reporting or secret-safe metadata. | P2 / M / 1.5 h | T01, T05 | A focused run produces accurate pass/fail/aborted outcomes and duration in logs and Extent HTML; skipped tests are labeled skipped. Report metadata contains only non-secret browser/headless settings. A failed test remains failed if report/screenshot handling encounters an expected diagnostic error. |
| T10 | **Align CI, report artifacts, and operator documentation.** Add Extent HTML artifact upload if policy allows it, and synchronize README/workflow guide with actual Maven discovery, secret requirements, screenshot policy, artifact paths, and retention. Do not treat proposed hooks as active. | P2 / M / 1.5 h | T01, T05, T09 | Workflow command and secret mapping match `TestConfig`; artifacts are uploaded only from generated files with clear retention and no secret values. Surefire remains uploaded on failure; screenshots remain failure-only and subject to T05; Extent report is uploaded when present or explicitly documented as local-only. Documentation agrees with YAML and Maven Surefire includes. |
| T11 | **Run the actual functional browser matrix.** Verify all six PRD scenarios plus required controls in the live application using the configured serial Chrome and Firefox paths; capture observed per-browser counts/results and skips. | P1 / L / 5 h | T01-T07, T09-T10 | On the actual target, TS-LOG-001..006 outcomes meet their documented assertions, including browser restart for Remember Me. Run with `REQUIRE_CREDENTIAL_TESTS=true`; both browser jobs complete and artifacts are inspected for sensitive data. Report exact commands, browser versions, pass/fail/skip counts, and failures. A configured matrix alone does not satisfy this task. |
| T12 | **Assess constrained-network behavior and close verification handoff.** Use a repeatable available network-throttling method to exercise navigation and bounded waits; record results and limitations without creating a performance SLA. | P2 / M / 2 h | T07, T11 | Record the shaping method, browser, page-load timeout, observed navigation/test result, and whether the target remained reachable. No timing threshold beyond configured bounded waits is invented. If no suitable shaping environment exists, document the task as blocked and do not claim slow-network coverage. |

## Dependency Order and Phases

Dependencies are acyclic. Parallelize independent tasks only where Stage 5 capacity permits; the ordered critical path is T01 → T06 → T11, with T03/T04/T05 and T07 completed before live validation.

| Phase | Execution order | Exit condition |
| --- | --- | --- |
| 1. Foundation and security | T01 → T06; T02 can proceed after T01. | Credentials cannot be sourced from code/JVM properties; missing-secret behavior is covered; invalid-password test is effective. |
| 2. Behavior and framework | T03, T04, T05, T07; resolve T03 before finalizing its assertion; T04/T05 require a valid account for representative runtime checks. | Requirements have observable test assertions; driver/wait lifecycle is bounded and cleanup-safe; diagnostic exposure is controlled. |
| 3. Suite and integration | T08 (after T02-T04); T09 → T10. | Scenario data/ownership is consistent; listener/report state is accurate; docs and CI artifact policy match implementation. |
| 4. Runtime validation | T11 → T12. | Chrome and Firefox evidence is reported; slow-network evidence is either measured by a repeatable method or explicitly blocked. |

Equivalent dependency list: T01: —; T02: T01; T03: —; T04: T01; T05: T01; T06: T01; T07: —; T08: T02, T03, T04; T09: T01, T05; T10: T01, T05, T09; T11: T01, T02, T03, T04, T05, T06, T07, T09, T10; T12: T07, T11.

## Design-Review Condition Coverage

| Condition | Required disposition | Plan mapping |
| --- | --- | --- |
| C1 Must fix: remove embedded credentials and JVM-property sourcing; enforce absent-secret behavior. | Implement before browser suite; do not accept embedded credentials as residual risk. | T01, T06; risk R1. |
| C2 Must fix: invalid-password scenario must assert rejection, unauthenticated state, and form visibility. | Restore active assertions and ensure a false-positive cannot pass. | T02; risk R2. |
| C3 Should fix: decide whether FR-2 accepts native validation or requires application-rendered inline errors. | Confirm actual behavior/product interpretation; encode observable, approved acceptance behavior. | T03; risk R3. |
| C4 Should fix: successful-login welcome must identify the customer. | Assert identity signal while preventing account-value disclosure. | T04; risk R4. |
| C5 Should fix: review screenshots for personal data beyond form fields. | Use a synthetic account when available; keep capture disabled unless redaction is demonstrated; constrain CI artifact exposure. | T05, T09, T10; risk R5. |
| C6 Should fix: execute live-site/browser verification. | Verify actual Chrome scenarios and Remember Me restart; Chrome-only is the user-directed Stage 6 scope. Report Firefox/cross-browser coverage unrun; do not infer success from configuration. | P4-04; risk R6. |
| N1 Nice to have: config, missing-secret, and URL validation tests. | Add bounded, deterministic focused tests. | T06; risk R1. |
| N2 Nice to have: dependency monitoring and reproducible Java toolchain. | Record as follow-up consideration, not a feature prerequisite or new dependency commitment. Existing explicit dependency versions and Java 21 compiler source/target are present; toolchain enforcement and vulnerability scanning are not verified. | T07/T10 verify build configuration; Stage 5 may report follow-up need. No additional task unless a concrete repo/CI capability is selected. |

## Risk Mitigation Mapping

| Risk | Mitigation / evidence required |
| --- | --- |
| R1. Source defaults or JVM properties can mask missing secrets and violate credential policy. | T01 removes those paths; T06 proves missing-secret/requirement behavior. Never include secret values in evidence. |
| R2. Invalid-password rejection may regress without failing tests. | T02 restores assertion coverage and performs a negative check. |
| R3. Browser-native validation may not fulfill the PRD's inline-error requirement. | T03 resolves interpretation against the product behavior and records the decision as testable acceptance. |
| R4. Generic welcome tokens may pass without confirming the logged-in customer. | T04 verifies a customer-identifying signal without emitting identity data. |
| R5. Failure screenshots may expose page-level account/personal data. | T05 demonstrates redaction before capture is enabled; T09/T10 check report attachment and access/retention behavior. |
| R6. Chrome live-site and restart behavior have no observed evidence; Firefox is outside the current verification scope. | P4-04 records real Chrome scenario outcomes; constrained-network evidence remains unavailable and unclaimed. |
| R7. CI report artifact behavior may hide useful test output or expose sensitive screenshots. | T05/T09/T10 align capture, reports, upload conditions, and retention with observed generated artifacts. |
| R8. Browser/driver/network/tool availability can prevent runtime verification. | Apply the blockers below and report blocked, skipped, failed, and passed checks distinctly; do not convert unavailable checks into passes. |

## Credentials and Environment Blockers

- **Valid test credentials:** TS-LOG-001 and TS-LOG-005 require an active registered customer account. TS-LOG-002 requires a known valid username. Supply `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD` only through a local secret store or CI repository/environment secrets; do not use JVM properties or command-line literals. Whether CI secrets are configured is currently unknown. Keep `REQUIRE_CREDENTIAL_TESTS=true` in CI so absence fails visibly. Optional local runs may abort credential-dependent tests, which must be reported as skipped.
- **Account/data policy:** Prefer a dedicated synthetic test account, especially when screenshots are enabled. Account ownership, permission to use the production target, and whether the account has stable dashboard identity are not established by the reviewed artifacts. Do not use real customer data in test evidence.
- **Live target/network:** `https://askomdch.com/account/` and the recovery destination require internet/DNS/TLS access and site availability. Availability and permission at execution time are unknown. No live browser or CI run was performed during Stage 4.
- **Browser/driver environment:** Chrome and Firefox must be installed/available, and WebDriverManager must be able to resolve/download compatible drivers. The workflow configures Ubuntu, Temurin Java 21, Maven, headless mode, and serial matrix execution; this does not establish that current local browsers or CI runners work.
- **Slow network:** A repeatable network shaping tool/environment is not identified in the repository. T12 is conditional on availability. The PRD defines no performance threshold; bounded-timeout handling is the only planned criterion, not a performance guarantee.
- **Diagnostic artifacts:** Screenshots and reports can contain sensitive page content. Keep screenshots off by default; do not enable the CI screenshot path until T05 is satisfied. Inspect artifacts before sharing them. CI currently retains Surefire artifacts for 7 days and failure screenshots for 3 days; no CI artifact was inspected for this plan.
- **Non-blocking build recommendations:** Explicit Maven dependency versions and Java 21 source/target are present. A Maven toolchain pin and vulnerability-monitoring service are not confirmed; do not add tools or dependencies without an identified project-supported mechanism.

## Success Criteria and Traceability
**Planning decision update (2026-10-09):** The user accepted carrying browser-native required-field validation as a project deviation into planning. This does not change the source PRD or establish that inline errors exist. Verification should assess the native-validation project behavior and report the deviation explicitly.


Stage 5 is complete when all P0/P1 tasks are implemented or explicitly blocked with evidence, focused Maven checks pass for code changes, C1-C6 have recorded dispositions, and no secret or unsupported runtime claim is introduced. Feature success remains all six requirements scenarios passing on actual browsers: valid login with identified welcome, invalid password, unknown user, both required-field errors, Remember Me across restart, and password recovery. TS-LOG-007/008 single-empty coverage and AC-UI-001 remain additional suite coverage. Performance/security thresholds remain unspecified as stated in NFR-1.

| Requirement/component | Plan mapping |
| --- | --- |
| FR-1 controls / AC-UI-001 | Existing test; maintain and run in T08/T11. |
| FR-2 blank credentials / TS-LOG-004, TS-LOG-007/008 | T03, T08, T11. |
| FR-3 successful login / TS-LOG-001 | T04, T11. |
| FR-4 invalid password and unknown user / TS-LOG-002/003 | T02 and existing unknown-user assertions; T08/T11. |
| FR-5 Remember Me / TS-LOG-005 | Existing profile-restart structure; verify lifecycle in T07 and live behavior in T11. |
| FR-6 password recovery / TS-LOG-006 | Existing navigation assertion; run against actual target in T11. |
| Configuration, timeout, URL policy, and driver lifecycle | T01, T06, T07. |
| BasePage/LoginPage synchronization | Existing explicit waits; verify/repair demonstrated issues in T03/T07. |
| Test data | Existing `TestData`; audit in T08. No resource framework planned. |
| Logging, screenshots, lifecycle and Extent reporting | Existing components; security/outcome verification and integration in T05, T09, T10. |
| Maven/Surefire and CI | Existing test discovery/matrix; verify reporting and documentation consistency in T10; actual execution evidence in T11. |
| Actual application, Chrome/Firefox, slow network | T11/T12, with external blockers above. |
| NFR-1 | No invented performance/security target. T05 handles credential/artifact policy; T12 reports bounded-wait behavior only. |

**Stage 5 handoff:** If separately authorized, provide the implementation agent this plan with `requirements.md`, `architecture.md`, `design-review.md`, current affected Java sources/tests, `pom.xml`, README, and workflow YAML/guide. Implement in dependency order, inspect current files again before editing, use the narrowest Maven checks after each logical task, and do not commit. Report exact commands/results and preserve unrun live-site, cross-browser, and slow-network work for Stage 6 verification. Stage 5 remains not authorized or started.
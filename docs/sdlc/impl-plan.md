# Fresh Stage 4 Implementation Plan — Restarted SDLC (2026-10-09)

## Context and Authority

| Item | Value |
| --- | --- |
| Scope | Java 21 / Maven / Selenium test framework for `US-AUTH-002`; no changes to the external login site. |
| Requirements | Fresh source recheck dated 2026-10-09; Confluence page 11796481, version 2, as recorded in [requirements](requirements.md). |
| Architecture | Updated [architecture](architecture.md), approved by the user on 2026-10-09. |
| Design review | Fresh [Stage 3 review](design-review.md), conditions explicitly accepted by the user on 2026-10-09. |
| Inspected baseline | Current Java/tests, [pom.xml](../../pom.xml), [workflow](../../.github/workflows/selenium-login.yml), [workflow guide](../../.github/workflows/selenium-login-workflow.md), [README](../../README.md), and repository instructions. Static inspection only; no test or browser run for this plan. |
| Planned tasks | 2 remaining tasks; estimated 6 engineer-hours: 1 Medium (2 h), 1 Large (4 h). Browser execution and external waiting are approximate/excluded from engineering estimate. |
| Gate status | Stage 4 planning authorized. Stage 5 has not started and is not authorized by this plan. |

This is the authoritative plan for the restarted workflow. All earlier task breakdowns and status narratives are retained below under a superseded archive heading; they are historical only and must not be used as current implementation instructions or test evidence. No earlier pipeline test outcome is adopted as current evidence. The worktree's pre-existing untracked plan copy and staged deletion state are preserved; no staging, commit, reset, cleanup, branch, merge, or remote operation is part of this work.

## Current Baseline and Decisions

Current source already provides environment-only valid credentials and expected welcome identity, required-versus-optional credential guards, active invalid-password/unknown-user assertions, a strict identity comparison for TS-LOG-001, bounded page and explicit wait timeouts, zero implicit wait, fresh Chrome/Firefox drivers, a temporary Remember Me profile, recovery URL and form checks, lifecycle pass/fail/skip reporting, Extent output, and opt-in screenshots disabled by default. No missing configuration/data framework, dependency, or broad source refactor is planned.

The following four conditions govern the implementation and verification:

1. **FR-2 remains a project deviation, not source fulfillment.** The PRD still requires application-rendered inline required-field errors. Existing TS-LOG-004/007/008 check browser-native validity; the accepted deviation does not satisfy or rewrite the PRD wording. Do not silently add site changes or relabel native validation as source compliance.
2. **Fresh Chrome evidence is required for TS-LOG-001, TS-LOG-002, and TS-LOG-005.** These outcomes have not been observed in this restarted workflow. Use required credential mode and classify every result as passed, failed, skipped, or unrun. A skip is never a pass.
3. **Strengthen the Remember Me post-restart oracle before final verification.** The current test relies on `isLoggedIn()`, which can return true for generic dashboard text. Require dashboard content and a visible logout control after restarting with the same temporary profile.
4. **Keep failure screenshots disabled.** Current default and workflow do not enable capture; selected-field redaction is not page-level sanitization. Do not enable capture or share screenshots until page-level content and artifact access/retention receive explicit safety review.

## Remaining Tasks

| ID | Task and deliverables | Priority / complexity / effort | Dependencies | Measurable acceptance criteria |
| --- | --- | --- | --- | --- |
| P4-01 | Strengthen TS-LOG-005 after browser restart using the existing scenario-assertion pattern. Assert account dashboard content and a visible logout control; do not let generic “hello” text alone establish authentication. Add focused unit coverage for any new assertion helper and preserve privacy-safe diagnostics. | P1 / Medium / 2 h | None | A focused helper test rejects missing dashboard content or missing logout visibility and accepts both signals. The browser scenario applies that assertion only after restarting Chrome with the same temporary profile. No credential or account identity is added to literals, assertion messages, logs, reports, or artifacts. Driver teardown/profile cleanup remain unchanged and best-effort cleanup cannot mask the test result. |
| P4-02 | Run and document the complete required-mode Chrome verification after P4-01. Do not run Firefox, alter the Firefox path/matrix, or infer runtime success from CI configuration or retained reports. Deliver exact command, Java/Maven/Chrome/driver versions, test counts by outcome, and the observed Surefire report/artifact locations in the Stage 6 report. | P0 / Large / 4 h | P4-01 | Run `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true` with `LOGIN_VALID_USERNAME`, `LOGIN_VALID_PASSWORD`, and `LOGIN_EXPECTED_WELCOME` supplied only in the Maven process environment/approved secret store. Check presence without reading or printing values. TS-LOG-001, TS-LOG-002 (known username), and TS-LOG-005 must be observed as executed or explicitly reported as skipped/unrun; never count a skip as pass. Record all suite pass/fail/error/skip counts and distinguish the FR-2 project deviation from the unmet source criterion. Keep Firefox/cross-browser and CI execution explicitly unrun unless independently executed. |

### Order and Dependencies

The dependency graph is acyclic: `P4-01` → `P4-02`. Implement and focused-test the Remember Me assertion first, then perform Chrome verification. Screenshot capture remains disabled throughout; no page-level review or enablement is authorized by these tasks. FR-2 source wording remains unchanged and is reported as a limitation unless source behavior is independently met and evidenced.

## Architecture and Existing-Work Disposition

| Architecture area | Current inspection and plan disposition |
| --- | --- |
| `TestConfig` / `TestData` | Runtime precedence, HTTPS/local-HTTP guard, bounded positive page-load timeout, environment-only valid credentials and expected identity, test data tokens, and focused config tests exist. No new configuration or data abstraction. |
| `BaseTest` driver lifecycle | Fresh Chrome/Firefox setup, headless options, page-load timeout, zero implicit wait, teardown, temporary Remember Me profile and restart exist. Keep the existing browser support and cleanup behavior; exercise Chrome only in P4-02. |
| `BasePage` / `LoginPage` | Bounded explicit waits and page-object ownership of controls, feedback, authentication state, and recovery navigation exist. Do not add sleeps or change the external site. |
| Scenario suite | TS-LOG-001 through TS-LOG-008 and AC-UI-001 are represented. TS-LOG-001 has an expected-identity comparison; TS-LOG-002 has active negative assertions; TS-LOG-003 checks unknown-user feedback; TS-LOG-004/007/008 cover the accepted native-validation deviation; TS-LOG-006 checks recovery URL, visible reset form, and unauthenticated state. P4-01 is the identified scenario gap; all scenarios receive fresh Chrome evidence in P4-02. |
| Credential guard / scenario assertions | Missing credentials fail in required mode and abort in optional mode; helpers assert invalid-password outcomes and customer-identifying welcome without exposing configured identity. Add only Remember Me assertion coverage under P4-01. |
| Suite / Maven Surefire | The `**/*Tests.java` include selects browser scenarios and focused unit test classes; `LoginPageRunner` is a separate suite entry and is not the standard `mvn test` selection. Preserve current discovery and report the actual selected counts from P4-02. |
| Listeners, outcomes, logging, Extent | Lifecycle/report code classifies aborts as skips, records outcomes, and uses redacted diagnostics. Existing focused lifecycle coverage remains in place; no report refactor absent a demonstrated defect. Inspect generated reports as verification evidence, not as prior results. |
| Screenshot utility / artifacts | `allowFailureScreenshots()` defaults false; the workflow does not set it true; workflow uploads only existing failure screenshots conditionally. Selected input redaction does not establish whole-page safety. Keep capture disabled; a later enablement requires page-level data and artifact access/retention approval. |
| CI and documentation | Java 21, serial Chrome/Firefox matrix, required credential mode, and artifact upload are configured. Configuration is not runtime evidence. Leave workflow and Firefox support unchanged; current verification policy is Chrome-only. |

## Requirements Traceability

| Requirement / scenario | Plan coverage and verification status |
| --- | --- |
| FR-1 / AC-UI-001; control checks in TS-LOG-004/005/006 | Existing page object and UI assertions cover username, password, Remember Me, login, and recovery controls. Run in P4-02. |
| FR-2 / TS-LOG-004, TS-LOG-007, TS-LOG-008 | Existing tests exercise browser-native required-field validity. This is the approved project deviation only; source inline-error behavior remains unmet/unverified. Report separately; do not claim source fulfillment. |
| FR-3 / TS-LOG-001 | Existing required expected-identity comparison plus authenticated/dashboard/logout assertions. Fresh required-mode Chrome outcome in P4-02; no earlier run is carried forward. |
| FR-4 / TS-LOG-002, TS-LOG-003 | Active invalid-password and unknown-user feedback, unauthenticated, and retained-form assertions exist. TS-LOG-002 requires a known username. Run both in P4-02. |
| FR-5 / TS-LOG-005 | Temporary profile restart exists. P4-01 adds dashboard-plus-visible-logout assertion after restart; P4-02 observes actual Chrome persistence. |
| FR-6 / TS-LOG-006 | Existing supported recovery URL, visible reset form, and unauthenticated checks provide navigation/form-presence evidence only. Run in P4-02; do not claim reset completion/usability beyond observed checks. |
| US-AUTH-002 | Mapped across FR-1..FR-6. End-to-end result depends on fresh Chrome execution and the source/project distinction for FR-2. |
| NFR-1 | PRD has no measurable security or performance target. Add no SLA, performance, slow-network, security-scan, or reliability claim. Credential and screenshot safeguards remain as specified. |

## Stage 3 Conditions and Risk Disposition

| Condition / risk | Disposition |
| --- | --- |
| C1 / FR-2 source versus deviation | Accepted for this project on 2026-10-09. Preserve the PRD inline-error requirement; native validation is not its fulfillment. This remains a possible Stage 6 limitation. |
| C2 / unverified TS-LOG-001, TS-LOG-002, TS-LOG-005 | Accepted on 2026-10-09. P4-02 requires fresh Chrome evidence. A required-mode credential guard failure is a failure, not a skip or pass; any actual skip is reported with reason, impact, and follow-up. |
| C3 / Remember Me oracle | Accepted on 2026-10-09. P4-01 requires dashboard content plus visible logout after restart; do not rely solely on permissive generic `isLoggedIn()` text. |
| C4 / screenshot exposure | Accepted on 2026-10-09. Capture remains disabled pending page-level safety and artifact handling review. No authenticated screenshots are requested or planned. |
| C5 / browser evidence boundary | Accepted on 2026-10-09. Run Chrome only. Firefox support and its workflow matrix stay unchanged; Firefox/cross-browser and CI execution are unrun unless separately observed. |
| Recovery-page evidence (R5) | Existing TS-LOG-006 checks both destination URL and reset-form visibility. Keep claims limited to navigation and form presence; no reset submission task. |
| Credential-source / invalid-password findings closed in current code | Environment-only secret reads, required/optional guard behavior, and active negative assertions are present with focused tests. Do not reschedule already implemented fixes as new work; P4-02 supplies fresh scenario evidence. |

Principal execution risks are unavailable Chrome/driver resolution, network/site availability, missing approved credentials/expected identity, and account access. Provide secrets only to the process environment and never echo them. An unavailable prerequisite means verification is blocked/unrun, not passed. `ALLOW_FAILURE_SCREENSHOTS` must remain unset/false.

## Stage 6 Gate and Success Criteria

- **PASS:** Every required Chrome test executes, the complete required suite has zero failures, zero errors, and zero skips, and all source PRD criteria are met. In particular, browser-native FR-2 validation alone cannot qualify as meeting the source inline-error criterion.
- **PASS WITH LIMITATIONS:** Every executed test has zero failures and zero errors, but one or more tests are skipped or a documented requirement is unmet/unverified. List each skip/unverified item, reason, impact, and follow-up. Obtain explicit human acceptance of those limitations before Stage 7; acceptance does not convert a skip or unmet requirement into a pass. Under the currently approved FR-2 deviation, if inline errors remain unmet, unconditional PASS is unavailable; at best the result is PASS WITH LIMITATIONS after the required evidence and explicit acceptance.
- **FAIL:** Any executed test failure or error, including required-mode credential guard failure, blocks Stage 7 and requires a scoped fix and rerun. Never count a required skip as a pass.
- **Blocked / no verdict:** If Chrome, site/network, or authorized runtime configuration prevents the suite from running, do not label it PASS or PASS WITH LIMITATIONS; report the blocker and leave Stage 7 unavailable.

Stage 4 planning is complete when this task sequence, architecture disposition, risk handling, traceability, and gates are accepted. P4-01 is the only planned source/test change; P4-02 belongs to Stage 6 verification. No source code, tests, CI, or workflow were changed by this planning task. No Stage 5 handoff is issued here.

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
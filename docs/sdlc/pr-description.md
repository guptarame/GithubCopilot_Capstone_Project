# Pull Request Description

**Title:** [Feature] Strengthen Selenium login automation and SDLC
**PR:** [#5](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/5)
**State:** Open
**Base:** `master` at `9a5383829fbd9bb2bbf2b327e54e8608d022896e`
**Head:** `feature/selenium-login-automation` at `064ce4210f92ce5d967854364909a2a532ed3b28`
**Reviewers:** None requested

## Summary

This PR updates the Java/Maven/Selenium login automation framework and its SDLC support. Framework changes include environment-only valid credentials and expected identity, explicit missing-credential guards, focused configuration and scenario-assertion tests, stronger valid-login identity and invalid-password checks, and updates to lifecycle/Extent reporting. It also revises repository Copilot SDLC assets, workflow guidance, and project documentation. The external login application is not modified.

The PR diff does not include the separate local `.vscode/mcp.json` modification.

## Requirements and Scope

The test framework maps behavior to FR-1 through FR-6 and the login scenarios in the current [requirements](requirements.md), with page objects owning browser interactions and dedicated configuration, guard, assertion, lifecycle, and reporting components.

FR-2 remains distinct from its source criterion: the tests exercise browser-native required-field validation as an accepted project deviation, but the source PRD requires application-rendered inline errors. The source requirement remains unmet; this draft does not claim otherwise.

## Verification Evidence

Evidence is from the current [Stage 6 verification report](verification-report.md), dated 2026-10-09:

**Stage 6 verdict: PASS WITH LIMITATIONS**, explicitly accepted for Stage 7 on 2026-10-09. All executed checks had zero failures and errors, but required scenarios were skipped and the FR-2 source criterion remains unmet.

- `mvn -q -DskipTests compile test-compile`: passed, exit 0; no tests selected.
- `mvn -q '-Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests' test`: passed, 14/14; 0 failures, 0 errors, 0 skips. The three `LoginScenarioAssertionsTests` include the P4-01 positive and negative checks.
- `mvn -q -Dtest=LoginPageTests -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=false -DallowFailureScreenshots=false test`: 9 run, 6 passed, 0 failed, 0 errors, 3 skipped; Maven exit 0.

The user acceptance does not turn skipped scenarios or unmet source requirements into passes.

No tests were rerun for PR creation.

## Limitations

- TS-LOG-001 (valid login and expected identity), TS-LOG-002 (invalid password with a known username), and TS-LOG-005 (Remember Me across restart) were skipped because required runtime values were unavailable. All three remain unverified, not passed; in particular, the live TS-LOG-005 restart and persistence behavior did not run.
- The source FR-2 inline-error criterion remains unmet by the accepted browser-native validation deviation.
- Firefox and cross-browser verification are unrun and outside the current Chrome-only scope. No CI execution was observed.
- TS-LOG-006 evidence covers recovery navigation, visible form, and unauthenticated state only; reset submission/completion is unverified.
- Screenshot capture was disabled. Page-level screenshot safety, performance, slow-network behavior, repeatability, and failure-path cleanup were not established.
- Overall US-AUTH-002 acceptance is not met; no claim of full feature completion is made.

## Reviewer Checklist

- [ ] Review the full PR diff against `master` and the change scope.
- [ ] Review environment-only credential and expected-identity inputs, guards, and privacy-safe diagnostics.
- [ ] Review valid-login and invalid-password assertions without treating skipped browser scenarios as passes.
- [ ] Assess each PR #4 finding against the current diff; preserve unresolved findings and their limitations.
- [ ] Keep FR-2's source inline-error criterion separate from the accepted native-validation project deviation.
- [ ] Confirm the report's pass, skip, and unrun classifications, including Chrome-only scope and absent CI evidence.
- [ ] Keep screenshot capture disabled unless page-level data and artifact handling have been reviewed.

## Prior Review Context

The Stage 8 report at [code-review-report.md](code-review-report.md) records PR #4's **NEEDS REVISION** verdict and five findings. PR #4 was later merged; that history does not erase the findings or imply they were resolved. This head visibly removes source-code valid-credential defaults and restores active TS-LOG-002 outcome assertions. No blanket resolution is claimed for PR #4 findings; Stage 8 should assess their disposition against the current diff. PR #5 has not yet received its Stage 8 review.
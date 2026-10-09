# Candidate Pull Request Description

**Title:** [Feature] Strengthen Selenium login assertions and reporting
**Proposed base:** `master`
**Head:** Not available; no new remote head exists.
**Publication state:** Draft only; no live PR was created.

## Summary

The current local change set updates the Java/Maven/Selenium login test framework and its supporting SDLC assets. Framework changes include environment-only valid credentials and expected identity, explicit credential guards, stronger assertions for valid login identity, invalid-password rejection, and Remember Me after a same-profile browser restart. It also adds recovery-form visibility checks and updates test lifecycle and Extent reporting behavior. Supporting local changes touch workflow/documentation and agent customization assets.

P4-01 is implemented: after the browser restarts, TS-LOG-005 requires nonblank dashboard content and a visible logout control. Focused helper tests cover success and either missing signal. This proves the assertion helper, not live session persistence.

The local worktree is not yet a PR-scoped change set. It contains staged deletions alongside untracked replacement SDLC documents and assets, plus additional framework, workflow, and customization changes. Confirm the intended files and resolve that mixed state before publishing; this draft does not imply that every local change belongs in one PR. The external login application is not modified.

## Requirements and Scope

The test framework maps behavior to FR-1 through FR-6 and the login scenarios in the current [requirements](requirements.md), with page objects owning browser interactions and dedicated configuration, guard, assertion, lifecycle, and reporting components.

FR-2 remains distinct from its source criterion: the tests exercise browser-native required-field validation as an accepted project deviation, but the source PRD requires application-rendered inline errors. The source requirement remains unmet; this draft does not claim otherwise.

## Verification Evidence

Evidence is from the current [Stage 6 verification report](verification-report.md), dated 2026-10-09:

- `mvn -q -DskipTests compile test-compile`: passed, exit 0; no tests selected.
- `mvn -q '-Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests' test`: passed, 14/14; 0 failures, 0 errors, 0 skips. The three `LoginScenarioAssertionsTests` include the P4-01 positive and negative checks.
- `mvn -q -Dtest=LoginPageTests -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=false -DallowFailureScreenshots=false test`: 9 run, 6 passed, 0 failed, 0 errors, 3 skipped; Maven exit 0.

The user accepted the report's limitations for Stage 7 on 2026-10-09, as recorded in the report. Acceptance does not turn skipped or unmet requirements into passes.

## Limitations

- TS-LOG-001 (valid login and expected identity), TS-LOG-002 (invalid password with a known username), and TS-LOG-005 (Remember Me across restart) were skipped because required runtime values were unavailable. All three remain unverified, not passed; in particular, the live TS-LOG-005 restart and persistence behavior did not run.
- The source FR-2 inline-error criterion remains unmet by the accepted browser-native validation deviation.
- Firefox and cross-browser verification are unrun and outside the current Chrome-only scope. No CI execution was observed.
- TS-LOG-006 evidence covers recovery navigation, visible form, and unauthenticated state only; reset submission/completion is unverified.
- Screenshot capture was disabled. Page-level screenshot safety, performance, slow-network behavior, repeatability, and failure-path cleanup were not established.
- Overall US-AUTH-002 acceptance is not met; no claim of full feature completion is made.

## Reviewer Checklist

- [ ] Confirm the final PR file list and ensure it excludes unintended staged deletions or unrelated customization changes.
- [ ] Review environment-only credential and expected-identity inputs, guards, and privacy-safe diagnostics.
- [ ] Check valid-login, invalid-password, and Remember Me assertion behavior, accounting for their skipped live scenarios.
- [ ] Keep FR-2's source inline-error criterion separate from the accepted native-validation project deviation.
- [ ] Confirm the report's pass, skip, and unrun classifications, including Chrome-only scope and absent CI evidence.
- [ ] Keep screenshot capture disabled unless page-level data and artifact handling have been reviewed.

## PR State and Next Step

GitHub PR #4 is **MERGED** into `master`; its head was `c5a48ac` and GitHub reports merge commit `9a53838`. The current local branch `feature/selenium-login-automation` and its `origin` tracking branch both still point to `c5a48ac`. There are no open PRs, and no new remote head contains the current local changes. PR #4 was not reopened or duplicated.

No PR can be created for these changes from the available remote state. A new head must first be supplied on GitHub, or the user must separately authorize the necessary scoped commit and push. No staging, commit, branch creation, push, merge, reviewer request, or Stage 8 review was performed.
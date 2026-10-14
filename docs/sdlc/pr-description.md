# Pull Request Description

**Title:** [Feature] Strengthen Selenium login automation and SDLC
**Publication status:** Local draft only; not posted to GitHub.
**Existing PR:** [#5](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/5) is **CLOSED and not merged** (checked 2026-10-14).
**Base / head:** `master` at `9a5383829fbd9bb2bbf2b327e54e8608d022896e` / `feature/selenium-login-automation` at `ce54d97acbd608c79c730b1b2e1d3c5f69af6393` on `origin`.
**Reviewers:** None requested.

The current local changes are not present on that remote head. Updating or reopening PR #5 to describe them would misrepresent its diff; publishing the local changes would require a commit/push, which was not authorized. This file is therefore a prepared draft, not a live PR update.

## Summary

Strengthens the Java 21 / Maven / Selenium login test framework and its SDLC documentation; the external login application is not modified. Framework updates include environment-only credential and expected-identity handling, required-mode credential guards, bounded configuration and browser waits, lifecycle/report outcome handling, and focused configuration, guard, assertion, and lifecycle tests. Login assertions now check the My Account dashboard destination and incorrect-password notice placement above the form, with feedback matching updated for the observed message form.

The accompanying documentation refreshes the requirements, architecture, design review, implementation plan, verification record, and Copilot stage guidance. The prior workflow files are removed; **CI is not configured**. The separate workspace `.vscode/mcp.json` edit is local configuration and is not part of the proposed PR scope.

## Requirements and Scope

The framework covers login controls, success and failure outcomes, and recovery navigation as recorded in the current [requirements](requirements.md). Page objects own browser interactions, while configuration, guards, scenario assertions, driver lifecycle, and reporting remain in their existing framework boundaries.

**AC 5.1 remains unmet.** The current blank-field checks exercise browser-native validation, not application-rendered inline errors required by the source criterion. The user's 2026-10-14 acceptance allows this documented `PASS WITH LIMITATIONS` result to proceed through Stage 7 only; it does not change the requirement or establish source compliance. This draft makes no unconditional source-compliance or overall feature-acceptance claim.

## Verification Evidence

Evidence is from the latest authoritative [Stage 6 verification report](verification-report.md), dated 2026-10-14. Verdict: **PASS WITH LIMITATIONS**, explicitly accepted by the user on 2026-10-14 for Stage 7.

- `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true`: **24 run, 24 passed, 0 failures, 0 errors, 0 skipped**. This includes 9 `LoginPageTests` browser scenarios and 15 focused browser-independent tests. The run used Chrome 155.0.8059.40.
- `mvn -q -DskipTests compile test-compile`: previously completed successfully; no tests were selected by this command.
- No tests were rerun for Stage 7 or while preparing this draft.

The report records that required runtime values were supplied through masked prompts and cleared by the invoking script; no credentials or account identity values are included here. AC 5.1 remains unmet despite the passing executed tests.

## Limitations

- AC 5.1 / FR-2 remains unmet because native browser validation is not application-rendered inline feedback for both required fields. Follow-up is to implement and observe the source-required behavior or obtain an authorized requirements change.
- Firefox and cross-browser coverage are unrun. No CI workflow or CI execution is present.
- Performance, slow-network, repeatability/reliability, and screenshot capture are unverified; screenshot capture remained disabled.
- Recovery evidence is limited to navigation and visible form checks, not password-reset submission or completion.
- Passing tests do not establish source compliance or unconditional US-AUTH-002 acceptance.

## Reviewer Checklist

- [ ] Review the full proposed diff against `master`, including the local changes not yet on the remote branch.
- [ ] Confirm environment-only credentials and expected identity, required-mode guards, and privacy-safe diagnostics.
- [ ] Review AC 2.1 dashboard destination and AC 3.1 notice-position assertions and their browser-independent helper coverage.
- [ ] Keep AC 5.1's source inline-error requirement distinct from browser-native validation; do not claim it is met.
- [ ] Confirm the current Stage 6 pass counts and limitation against the verification report; treat Firefox and CI as unrun/not configured.
- [ ] Review removal of the prior workflow files and ensure no CI behavior is claimed.
- [ ] Keep screenshot capture disabled unless page-level data and artifact handling have been reviewed.

## Current PR State and Next Gate

GitHub reports PR #5 as **CLOSED**, not merged, with base `master` and the remote head shown above. The branch is aligned with `origin`; the worktree contains staged and unstaged changes beyond that head. No new PR was created and no live PR was updated. To make this proposed diff reviewable on GitHub, the changes must first be made available on a remote branch through separately authorized commit/push actions. Stage 8 and review-comment publication have not been performed.
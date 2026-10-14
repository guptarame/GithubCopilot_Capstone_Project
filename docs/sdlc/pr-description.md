# Pull Request Description

**Title:** [Feature] Strengthen Selenium login automation and SDLC
**Pull request:** [#5](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/5)
The live PR link is authoritative for mutable state, base, head, and reviewer metadata.

## Summary

Updates the Java 21 / Maven / Selenium login automation framework and its SDLC artifacts; the external login application is not modified. Framework changes strengthen successful-login destination and welcome checks, incorrect-password feedback placement, runtime-only credential handling, guards, configuration, lifecycle reporting, and focused tests. The commit also removes the prior workflow YAML and companion workflow-spec file. Against the current `master` base, the workflow YAML is the deletion in the PR diff; the companion Markdown file was not present at the base.

The separate local `.vscode/mcp.json` modification is excluded. **No CI workflow is configured.**

## Requirements and Scope

The framework covers the customer login requirements in [requirements.md](requirements.md). Page objects own browser interactions; configuration, credential guards, scenario assertions, driver lifecycle, and reporting retain their existing responsibilities.

**AC 5.1 remains unmet.** Blank-field tests exercise browser-native validation, not the application-rendered inline errors required by the source criterion. The user's fresh 2026-10-14 acceptance of the Stage 6 **PASS WITH LIMITATIONS** permits Stage 7 progression only; it does not change AC 5.1 or establish source compliance. No unconditional source-compliance or overall feature-acceptance claim is made.

## Verification Evidence

The latest authoritative [Stage 6 verification report](verification-report.md), dated 2026-10-14, records **PASS WITH LIMITATIONS** and explicit human acceptance on 2026-10-14 for Stage 7. The latest run includes the strengthened dashboard-home URL check.

- Required-mode command `mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true`: **27 run, 27 passed, 0 failures, 0 errors, 0 skipped**. This includes 9 browser scenarios and 18 focused browser-independent tests, executed with Chrome 155.0.8059.40; the refined dashboard-home URL check was exercised.
- Stage 5 focused command `mvn -q "-Dtest=LoginPageUrlTests,TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests" test`: **18 run, 18 passed, 0 failures, 0 errors, 0 skipped**.
- Stage 5 compile command `mvn -q -DskipTests compile test-compile`: passed; no tests selected.

The verification report records that required runtime values were supplied through masked prompts and cleared by the invoking script. No credential or account-identity values are included in this artifact or the PR description.

## Limitations

- AC 5.1 / FR-2 is unmet because browser-native validation is not application-rendered inline feedback for both required fields. Follow-up is to implement and observe the source-required behavior or obtain an authorized requirements change.
- Firefox and cross-browser coverage are unrun. CI is not configured and no CI execution is claimed.
- Performance, slow-network, repeatability/reliability, and screenshot capture are unverified; screenshot capture remained disabled.
- Recovery evidence is limited to navigation and visible form checks, not password-reset submission or completion.
- Passing tests do not establish source compliance or unconditional US-AUTH-002 acceptance.

## Verification Gate (No CI)

The prior push/pull-request workflow was deleted in this PR. Adding a replacement CI workflow is outside the approved scope; consequently, pushes and pull requests have no automated test gate. The replacement gate for this PR is a **manual Stage 6 required-mode Chrome run**, now completed with 27/27 passing tests and the AC 5.1 limitation:

```text
mvn --batch-mode test -Dbrowser=chrome -Dheadless=true -DrequireCredentialTests=true
```

The run used runtime values supplied through masked prompts and cleared afterward. The 27/27 result is PASS WITH LIMITATIONS, not source-compliant PASS, because AC 5.1 remains unmet. The prior human acceptance applies to this same limitation. Do not put runtime values in commands, logs, or reports; skips are not passes.

## Reviewer Checklist

- [ ] Review the PR diff against the stated `master` base and commit.
- [ ] Confirm environment-only credentials and expected identity, required-mode guards, and privacy-safe diagnostics.
- [ ] Review AC 2.1 destination URL matching, account-subpage rejection tests, and AC 3.1 notice-position assertion.
- [ ] Keep AC 5.1's source inline-error criterion distinct from browser-native validation; do not claim it is met.
- [ ] Confirm Stage 6 counts and limitations against the verification report; treat Firefox as unrun and CI as not configured.
- [ ] Confirm no CI is described as configured; require the manual required-mode Chrome gate before PR update/re-review.
- [ ] Keep screenshot capture disabled unless page-level data and artifact handling are reviewed.

## Current PR State and Next Gate

Use the live PR link above for current state and revision; this artifact intentionally omits mutable state/head metadata. This local draft is not committed, pushed, or published to the PR body. Stage 6 passed with limitations after the dashboard-route refinement; the next steps are a PR update and re-review. Do not merge.
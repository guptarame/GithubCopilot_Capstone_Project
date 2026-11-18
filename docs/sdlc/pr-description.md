# Local PR description — #8

**Status:** Associated with [PR #8](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/8),
reported open in the GitHub metadata supplied on 2026-11-18 at 23:06
(+05:30). This is a local description; the live PR body was not independently
verified. The [code review report](code-review-report.md) records the supplied
patch and the findings for the reviewed head below.

**Title:** [Feature] Harden login test framework outcomes and Chrome setup  
**Base:** `master`  
**Head:** `feature/selenium-login-automation`  
**Reviewed head commit (reported):** `b62e25c4672f05aacaba9a6cf44894099e3bb8d1`

## Summary and scope

The PR updates the framework for automated tests of the external login site,
along with repository guidance, SDLC documentation, and workspace
configuration. It does not change the site or its authentication service.
The Stage 5/6 handoffs report framework changes to verify that
incorrect-password and unknown-account outcomes remain on the configured login
page, and to remove Chrome's default `--no-sandbox` option. The reset-flow
change was deferred pending product acceptance.

The intent maps to FR-4/FR-5 and PLN-03/PLN-04. M1 and M2 remain unresolved;
this work does not claim source conformance for TS-LOG-001 or TS-LOG-003.
Requirements were derived from PRD text supplied in conversation; Confluence
was not independently verified.

## Verification evidence

The current [Stage 6 verification report](verification-report.md) records
**PASS WITH LIMITATIONS**, explicitly accepted for Stage 7 on 2026-11-18 at
22:34 (+05:30). It reports the following Stage 5, user-run focused command:

```text
mvn -q "-Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests,BaseTestLifecycleTests,LoginPageUrlTests" test
```

The report records six corresponding Surefire summaries read for verification:
**30 tests, 0 failures, 0 errors, 0 skipped**. Stage 6 itself did not execute
Maven; the compile/test-compile command was unrun.

## Limitations

- All **nine Chrome scenarios are unrun**; no live-site behavior is claimed.
- M1 (fixed-credential policy conflict) and M2 (registered-account
  precondition conflict) remain unresolved. FR-2 acceptance is deferred.
- CI is not configured. Firefox/cross-browser coverage is unrun.
- Confluence provenance was not independently verified.
- The Stage 8 reviewer used the supplied patch, not a live GitHub diff. An
  intermediate commit disabled TLS verification for the MCP process; the
  reviewed head removes that setting. Keep TLS verification enabled.

## Reviewer checklist

- [ ] Confirm the live PR diff matches the stated framework, documentation, and
      workspace configuration scope, with no external login-site or
      authentication-service changes.
- [ ] Verify negative-login assertions check login-page identity as well as
      their intended error and unauthenticated/form state.
- [ ] Verify Chrome defaults omit `--no-sandbox` and existing Firefox support
      is unchanged.
- [ ] Keep the reported 30 focused tests distinct from the unrun Stage 6 Maven
      checks and nine unrun Chrome scenarios.
- [ ] Preserve the M1, M2, FR-2, Confluence, CI, and Firefox limitations.

---

# Previous PR #7 description — historical, not applicable to the new commit

# R4 Framework Update and SDLC Reconciliation

**Title:** [Feature] R4 login-framework update and SDLC reconciliation

**Base:** `master`

**Head branch:** `feature/selenium-login-automation`

**Live PR:** [#7](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/7)

## Summary and scope

PR #7's original nine-path revision against `master` contains the framework
implementation and verification described below. This revision adds a
documentation-only follow-up, not a new implementation change.

The follow-up adds exactly five SDLC documentation paths, for a
fourteen-path PR scope:

- `docs/sdlc/architecture.md`
- `docs/sdlc/code-review-report.md`
- `docs/sdlc/design-review.md`
- `docs/sdlc/pr-description.md`
- `docs/sdlc/requirements.md`

These updates reconcile the recorded requirements, architecture, design
review, and review history with the accepted source baseline and approvals,
while preserving the historical records. The live PR is authoritative for
publication status, mutable state, head, and published file list. MCP
configuration is excluded from this publication.

The already-published framework scope is limited to the Selenium test
framework, not the external login site or its authentication service. It
distinguishes the three source scenarios from six supplemental browser
regressions, tightens invalid-account assertions, and hardens WebDriver setup,
restart, and teardown behavior with focused browser-independent tests.

## Verification evidence

The [Stage 6 verification report](https://github.com/guptarame/GithubCopilot_Capstone_Project/blob/feature/selenium-login-automation/docs/sdlc/verification-report.md), “Independent
Verification of Stage 5 R4-01–R4-04 — 2026-11-03,” records:

- `mvn -q -DskipTests compile test-compile`: passed (exit 0; no tests run).
- `mvn -q "-Dtest=LoginPageUrlTests,TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests,BaseTestLifecycleTests" test`:
  **28 passed, 0 failures, 0 errors, 0 skipped**.
- **0/9 Chrome browser scenarios run.** Browser/driver/site readiness and
  required runtime identity/credentials were not established.

Those results are evidence for the implementation in the published
nine-path revision. The five-file documentation reconciliation has
not had separate tests run; no new test result is claimed for it. Stage 6 was
accepted as **PASS WITH LIMITATIONS** on 2026-11-03 at 00:57 (+05:30); that
acceptance does not turn unrun scenarios or unmet requirements into passes.

Pre-publication `git diff HEAD --check -- docs/sdlc/architecture.md docs/sdlc/code-review-report.md docs/sdlc/design-review.md docs/sdlc/pr-description.md docs/sdlc/requirements.md`
passed with no whitespace errors. No Maven or browser rerun was performed
for this documentation-only follow-up.

## Limitations

- The reported removal of AC 5.1 and TS-LOG-004 from Confluence is
  user-reported and independently **unverified**. The former AC 5.1 remains
  unmet; no source-compliance claim is made.
- All nine Chrome scenarios remain unrun. No live login, persistence, browser
  cleanup, or screenshot result is claimed.
- **CI is not configured. Firefox/cross-browser coverage is unrun.**
- Maven 3.8.9 was used for Stage 6, below the README's 3.9+ prerequisite;
  validation with the documented minimum remains follow-up.

## Reviewer checklist

- [ ] Confirm the five documentation changes reconcile to the accepted
      baseline and approvals, while retaining historical records and the
      user-reported/unverified Confluence limitation.
- [ ] Distinguish the original nine-path revision from the expanded
      fourteen-path scope; verify the five listed files are the only additions.
- [ ] Treat the 28 focused tests as evidence for the published implementation,
      not as tests of the documentation-only reconciliation.
- [ ] Preserve the explicit AC 5.1, 0/9 Chrome, CI, and Firefox limitations.
- [ ] Re-review the actual fourteen-path PR diff and new head. The previous
      Stage 8 verdict does not review or approve this expanded revision.

## PR and review history

- **Live PR:** [#7](https://github.com/guptarame/GithubCopilot_Capstone_Project/pull/7),
  confirmed **OPEN** against `master` before this publication. The live PR is
  authoritative for current state, head, and published file list.
- The prior Stage 8 local **APPROVED** verdict reviewed only the nine-path
  revision at `428de07e554d091746f8d27412080ed465776e41`. That SHA identifies
  the historical reviewed revision only; it is not a claim about a new or
  current head. The expanded fourteen-path revision requires fresh Stage 8
  review; the old verdict does not cover it.
- This artifact describes the authorized five-document publication and PR-body
  update. No GitHub review, comment, approval, or merge is authorized by this
  publication.

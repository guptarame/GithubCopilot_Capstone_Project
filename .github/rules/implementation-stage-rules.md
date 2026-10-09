# Implementation Stage Rules

Apply during approved Stage 5 work. The repository-root `copilot-instructions.md`
is authoritative.

## Before changing code

- Read `docs/sdlc/requirements.md`, the approved `impl-plan.md`,
  `architecture.md`, `design-review.md`, relevant source/tests, and root
  instructions. Inspect the current implementation and reuse existing helpers;
  do not treat already implemented work as missing.
- Follow approved scope and task dependencies. Pause for direction on material
  ambiguity or scope changes. Preserve PRD wording; document human-approved
  deviations separately, not as fulfillment of different source criteria.
- This repository tests an external login site. Do not imply or implement changes
  to that site's application or authentication service.

## Implementation and safety

- Preserve Java 21 and `Github_Copilot` structure: driver/profile lifecycle in
  `BaseTest`, settings in `TestConfig`, shared browser actions in `BasePage`,
  login locators/actions in `LoginPage`, scenario checks in tests/helpers, and
  diagnostics in existing listeners/utilities.
- Keep tests independent and sequential; use bounded explicit waits, no fixed
  sleeps or unbounded retries. Keep classes focused and dependencies unchanged
  unless the approved plan requires otherwise.
- Non-secret settings use system property, environment, then default. Valid
  credentials come only from `LOGIN_VALID_USERNAME` /
  `LOGIN_VALID_PASSWORD` environment variables or CI secrets—never source or
  JVM properties. Do not expose secrets in commands, logs, screenshots, reports,
  or committed files. Do not print or inspect values when diagnosing.
- Missing credentials skip only in optional runs and fail when required mode is
  enabled; report skips as skips. Require HTTPS except explicitly opted-in local
  HTTP. Screenshots remain disabled by default; field redaction does not ensure
  all page content is safe.
- Handle expected exceptions narrowly, propagate unexpected failures, and
  always clean up drivers/profiles. Never weaken behavior or hide failures to
  make verification pass.

## Validation and handoff

- Run the smallest relevant existing Maven check after each logical change.
  For applicable browser-independent regression tests:

  ```text
  mvn -q -Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests test
  ```

- Use Chrome only for browser-driven validation. Do not launch Firefox or
  change its existing support; report Firefox/cross-browser checks as unrun.

- Report exact commands and observed results; distinguish pass, fail, skip, and
  unrun. Do not infer successful CI/browser runs from workflow configuration or
  claim unmeasured live-site, cross-browser, performance, or slow-network
  coverage. State blockers and incomplete work; leave the verification report
  to Stage 6 unless a direct documentation change is required.
- Finish with planned criteria met or explicitly incomplete, validation evidence,
  and known limitations for independent verification. Do not commit unless
  explicitly requested.

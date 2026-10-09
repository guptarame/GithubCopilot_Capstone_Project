# Selenium Verification and Testing Skill

Use after implementation in Stage 6 before PR creation. For a narrow code
change, follow the focused validation guidance in the repository instructions.
Verify this Java/Selenium login test framework against requirements and plan;
report evidence and limits, never assumed success.

## Verify

- Read the requirements, acceptance criteria, implementation plan, changed Java
  code/tests, `pom.xml`, and available Java/Maven/browser/site/credential setup.
  Map each criterion to evidence or mark it unverified.
- Start with:

  ```text
  mvn -q -DskipTests compile test-compile
  ```

- Run the smallest relevant existing Maven tests. Run browser scenarios in
  Chrome only when relevant and the site/runtime are available; do not launch
  Firefox or change its existing support. Report Firefox/cross-browser
  coverage as unrun.
- Supply credentials only through runtime environment/CI secrets; never include
  values in source, commands, logs, screenshots, or reports. Record exact
  commands, environment, totals, failures, and skips; a credential-dependent
  skip is not a pass.
- Assess Chrome coverage, repeatability, duration, slow-network behavior,
  logging, screenshots, and cleanup only when actually exercised. Do not claim
  Firefox/cross-browser coverage. Do not change application or test behavior
  just to suppress failures; make a directly required in-scope test/support
  fix only, then rerun and report it.

## Report and completion

- Write `docs/sdlc/verification-report.md` with verdict (`PASS`, `PASS WITH
  LIMITATIONS`, or `FAIL`), evidence and traceability tables, issues, limits,
  and recommended actions. Surefire reports go under `target/surefire-reports/`
  and generated failure screenshots under `target/screenshots/`.
- Completion requires applicable criteria evaluated or explicitly unverified;
  exact commands and observed counts; explicit skips, failures, blockers, and
  unrun checks; and no unsupported live-site, cross-browser, performance, or
  reliability claims.
- `PASS` requires every required Chrome test to execute and pass with zero
  failures, errors, or skips, and all source requirements to be met. Use `PASS
  WITH LIMITATIONS` when every executed test passes with zero failures/errors,
  but required cases are skipped or a documented requirement remains
  unverified. List skips, reason, impact, and follow-up; skips are not passes.
  Stage 7 may proceed only after explicit human acceptance of the limitations.

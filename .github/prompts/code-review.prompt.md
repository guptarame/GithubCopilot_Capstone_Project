---
description: "Review changes to the Selenium login automation framework and produce evidence-based findings."
---

# Code Review Prompt

Act as a senior QA automation engineer reviewing this repository's pull
request. Follow the
[repository-root instructions](../../copilot-instructions.md). The project
context and review-specific guidance are included below.

This repository is a Java 21 / Maven / Selenium 4 / JUnit 5 automation
framework for customer login at `https://askomdch.com/account/`; it is not the
implementation of the login application. Review the test framework and its
supporting configuration, CI, and documentation. Use the
[Selenium code-review skill](../skills/selenium-code-review.md) for review
procedure and the [SDLC orchestrator](../agents/sdlc_orchestrator.agent.md) for
the Stage 8 publication gate.

## Review scope

Review the complete pull request diff and inspect relevant surrounding code.
Read the applicable project artifacts when present: `docs/sdlc/requirements.md`,
`architecture.md`, `design-review.md`, `impl-plan.md`, and
`verification-report.md` (all under `docs/sdlc/`). Check findings against the
source requirements and any explicitly approved project deviation. Focus on
changed behavior and directly related risks:

- login scenario assertions and requirement traceability, including valid login,
  invalid password, unknown user, required-field validation, Remember Me, reset
  navigation, and required-control visibility when affected
- Selenium synchronization, bounded explicit waits, and Page Object Model
  boundaries (`BasePage` for shared browser operations; `LoginPage` for login
  selectors and actions)
- test isolation and WebDriver lifecycle (`BaseTest`), including temporary
  profile cleanup and Remember Me browser restart
- `TestConfig` setting precedence, URL transport validation, and credential
  guard behavior; do not confuse non-secret system-property overrides with the
  environment/CI-secret-only valid credential policy
- sensitive data in source, command examples, logs, screenshots, reports, and
  CI artifacts; screenshot capture is opt-in and field redaction is not proof
  that all page data is safe
- JUnit listeners, outcome classification, diagnostics, Maven/Surefire test
  discovery, and consistency between workflow YAML and its guide
- whether changes preserve sequential execution, supported project scope, and
  existing dependencies without unjustified parallelism or extra frameworks

## Review method

1. Identify the changed files and understand the affected execution paths.
2. Inspect relevant source, tests, and configuration. Validate material claims
   against actual code or observed run evidence; a configured browser matrix,
   workflow, report, or PR description is not proof that a test passed.
3. Check that changes do not make tests pass by weakening assertions, hiding
   failures, or treating credential-dependent skips as passes. Keep source PRD
   criteria distinct from approved project deviations.
4. Report only actionable findings. For each finding provide:
   - severity (`Critical`, `High`, `Medium`, or `Low`)
   - exact file and line range
   - concise defect description, evidence, and impact
   - confidence from 1 to 10
   - a focused recommendation
5. Separate confirmed findings from questions, limitations, and optional
   suggestions. If there are no findings, state that this review found none
   within its scope and disclose any unreviewed areas. Do not report a
   pre-existing or out-of-scope issue as a change-introduced finding unless
   the diff affects it.
6. Provide a verdict: `APPROVED`, `APPROVED WITH MINOR ISSUES`, or
   `NEEDS REVISION`.
7. Write or update `docs/sdlc/code-review-report.md` with the review scope,
   summary, findings, verdict, and limitations. Include reproducible locations
   and distinguish observed validation from checks not run.

## Approval and safety boundaries

- Present the local report/findings for human review first. Do not post inline
  comments, submit a GitHub review, approve, or merge until the human explicitly
  approves publishing the findings. Approval to publish findings is not
  approval to merge.
- Never include credentials, tokens, or other secrets in the report or
  comments.
- Do not claim tests, security checks, dependency scans, CI jobs, or browser/live
  site runs that were not actually performed. Report pass, fail, skip, and
  unrun status accurately.

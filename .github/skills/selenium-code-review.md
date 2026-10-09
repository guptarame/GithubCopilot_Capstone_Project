# Selenium Code Review Skill

Use for Stage 8 reviews when a PR exists and verification evidence is available
or its absence is documented. Assess the Java/Selenium login test framework;
review quality, not whether the PR should merge.

## Review

- Read root and GitHub Copilot instructions, the complete PR diff, relevant
  surrounding code, `pom.xml`, and applicable requirements, architecture,
  implementation plan, verification report, and existing tests.
- Assess correctness and requirements/plan traceability; Selenium waits and
  page-object boundaries; test isolation and driver/profile cleanup;
  configuration; credential and sensitive-data handling; diagnostics; and
  Maven/CI compatibility.
- Report only actionable, evidence-based findings. Give each severity, exact
  file/line, impact, confidence, and focused recommendation. Separate confirmed
  defects from questions, limitations, and suggestions. If none, say so and
  disclose unreviewed areas.
- Validate claims against source and observed results. Never infer test,
  security, browser, or CI success from configuration or descriptions.

## Deliver and publish

- Create or update `docs/sdlc/code-review-report.md` with scope, summary,
  findings, verdict (`APPROVED`, `APPROVED WITH MINOR ISSUES`, or
  `NEEDS REVISION`), and limitations.
- Present findings to the human before posting GitHub comments; publish only
  with explicit approval. Never approve or merge the PR.
- Do not expose credentials or tokens. Completion requires reviewed changed code,
  actionable evidence and locations, a clear verdict and limitations, and no
  unauthorized external comments.

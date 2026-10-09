# Pre-Commit Hook Specification

## Status

This file documents the proposed repository pre-commit checks. It does not
install or enable a Git hook. Git hooks run locally from `.git/hooks/` or a
configured `core.hooksPath`; files under `.github/hooks/` are documentation
only.

## Trigger

Run before creating a commit, against the staged changes and current Java
project.

## Checks

1. Check staged changes for whitespace errors:

   ```text
   git diff --cached --check
   ```

2. If staged Java files or `pom.xml` are changed, compile main and test sources
   using the existing Maven project:

   ```text
   mvn -q -DskipTests compile test-compile
   ```

3. If Markdown or other documentation files are staged, verify changed
   relative Markdown links and confirm the staged document does not introduce
   credential values or claim unrun test results.

The full Selenium suite is not part of this pre-commit hook by default because
it can require a browser, live-site access, and runtime credentials. Use the
GitHub Actions workflow and Stage 6 verification process for those checks.

## Exit behavior

- Return success only when all applicable checks pass.
- Return a non-zero exit code when a check fails; do not silently ignore
  failures.
- Do not modify staged files automatically. Report the failing command or
  document clearly and let the contributor decide how to fix it.
- Do not block commits solely because an optional live environment or
  credential is unavailable; those checks are excluded and must be reported
  as not run.

## Safety

- Never request, read, or print credentials or tokens.
- Do not include secret values in diagnostics.
- Do not change Git configuration or install hooks as a side effect of running
  the checks.

## Implementation note

If this specification is later implemented as an executable hook, keep the
script in an explicitly agreed hook location, document installation and
opt-out behavior, and keep the check commands aligned with
`copilot-instructions.md`, `pom.xml`, and `.github/workflows/`.

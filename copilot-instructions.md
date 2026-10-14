# Repository Copilot Instructions

This file is the canonical source for project-wide boundaries, credential
handling, validation, and Copilot workflow guidance. The index in
`.github/copilot-instructions.md` points here; specialized agents, skills, and
workflow documents supplement these rules for their specific tasks.

## Project Scope

- This repository contains a Java and Maven Selenium login automation project.
- Follow the existing package layout, page-object responsibilities, test
  helpers, and dependencies. Inspect neighboring code before changing an
  abstraction or adding a dependency.
- Keep changes focused on the requested behavior. Do not make unrelated
  refactors or commit changes unless explicitly asked.

## Credentials and Sensitive Data

- Read credentials only from runtime environment variables. This repository
  currently has no configured CI workflow; if CI is added later, use its secret
  store. Supported login variables are documented in `README.md`.
- Never put credentials, tokens, or other secrets in source, command literals,
  logs, screenshots, reports, or committed artifacts.
- Treat screenshots and diagnostic output as potentially sensitive. Preserve
  existing redaction behavior.

## Validation and Reporting

- Start with the narrowest relevant existing check. For Java compilation and
  test compilation, use:

  ```text
  mvn -q -DskipTests compile test-compile
  ```

- Run focused Maven tests for the changed behavior. Run the full browser suite
  only when the target site, browser, and required runtime configuration are
  available.
- Report the exact commands and observed results. Distinguish passed, failed,
  skipped, and unrun checks; a skipped credential-dependent test is not a
  pass.
- Do not claim live-site, cross-browser, performance, or reliability coverage
  unless it was actually exercised. Follow the applicable verification skill
  and keep its report consistent with observed evidence.

## Copilot Workflow

- The active SDLC agent definitions are in `.github/agents/`. Use the
  `sdlc_orchestrator` for the complete requirements-to-review pipeline; follow
  its stage order and wait for human approval at its defined gates.
- Use a specialist agent, prompt, or skill for a bounded task when the full
  pipeline is not needed. Follow its ownership and handoff requirements.
- There is currently no `.github/workflows/` directory or configured CI
  workflow. Run tests with Maven as documented; if CI is introduced later,
  update the documentation and verify its actual configuration before making
  claims about CI behavior.
- Use `.github/copilot-instructions.md` as the index to the repository's
  supporting Copilot assets. Do not assume a referenced file or check exists;
  verify it in the workspace before relying on it.
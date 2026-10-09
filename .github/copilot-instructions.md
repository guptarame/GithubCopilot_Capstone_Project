# Copilot Instructions

Follow the repository-root [canonical instructions](../copilot-instructions.md) for project boundaries, credential safety, validation, and workflow rules. This file adds project context and links to the specialized Copilot assets; it does not override the canonical instructions.

## Project at a glance

This is a Java 21 / Maven Selenium 4 and JUnit 5 test automation framework for the customer login flow at `https://askomdch.com/account/`. The automated framework lives under `src/test/java/Github_Copilot/`; `src/main/java/Github_Copilot/Main.java` is an IDE starter class, not the login application.

| Area | Responsibility |
| --- | --- |
| `base/BaseTest.java` | WebDriver setup, per-test teardown, browser options, and Remember Me profile restart. |
| `config/TestConfig.java` | Runtime settings, URL transport validation, and environment-only valid credentials. |
| `data/TestData.java` | Wait defaults and accepted feedback-message tokens. |
| `pages/BasePage.java`, `pages/LoginPage.java` | Shared browser operations and login-page selectors/actions/state. |
| `tests/` | Login scenarios, credential guards, and reusable scenario assertions. |
| `listeners/`, `utils/` | Test lifecycle/outcome reporting, Extent reports, logging, and opt-in screenshots. |
| `docs/sdlc/` | Requirements, architecture, design review, implementation plan, verification, and PR/review artifacts when present. |
| `.github/` | Copilot agents, prompts, rules, skills, workflow documentation, and hook specification. |

## Change guidance

- Inspect the current implementation, related tests, and applicable SDLC artifacts before changing behavior. Do not assume a planned feature is missing; use the existing implementation and helpers.
- Preserve the `Github_Copilot` package layout and existing responsibilities. Keep selectors and login interactions in page objects, shared browser operations in `BasePage`, driver lifecycle in `BaseTest`, settings in `TestConfig`, and scenario assertions in tests/helpers.
- Keep tests isolated and execution sequential. Use bounded explicit waits; do not add `Thread.sleep()`, unbounded retries, or parallel execution.
- Prefer existing dependencies and patterns. Change dependencies or expand scope only when required by an approved plan or the user's request.
- Keep source requirements distinct from approved project deviations. In particular, read `docs/sdlc/requirements.md` before changing required-field behavior; do not silently rewrite PRD criteria or represent an approved deviation as satisfying different source wording.

## Configuration and sensitive data

- Read valid account credentials only from runtime environment variables or CI secrets: `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD`. Never hardcode them, pass their values on a command line, or include them in logs, reports, screenshots, examples, or committed files.
- Credential-dependent tests skip when credentials are absent in optional local runs. CI enables required mode with `-DrequireCredentialTests=true` (equivalently, `REQUIRE_CREDENTIAL_TESTS=true`) so missing credentials fail visibly. Treat skips as skips, not passes; do not inspect or echo credential values while diagnosing.
- Non-secret settings use nonblank Java system property, then environment variable, then default. Examples: `baseUrl` / `BASE_URL`, `browser` / `BROWSER`, `headless` / `HEADLESS`, `pageLoadTimeout` / `PAGE_LOAD_TIMEOUT_SECONDS`, and `requireCredentialTests` / `REQUIRE_CREDENTIAL_TESTS`.
- HTTPS is required for the base URL. HTTP is permitted only for an explicitly opted-in local host (`ALLOW_INSECURE_LOCAL_BASE_URL=true`). Failure screenshots are disabled by default; selected form-field redaction does not prove the rest of a page contains no sensitive data. Keep capture off unless the page data and artifact handling have been reviewed.

## Validation

Start with the narrowest applicable existing Maven check:

```text
mvn -q -DskipTests compile test-compile
mvn -q -Dtest=TestConfigTests,CredentialTestGuardTests,LoginScenarioAssertionsTests,TestLifecycleTests test
```

`mvn test` runs the Surefire `**/*Tests.java` selection, including browser-driven login scenarios. Run browser tests only when the browser, target-site access, and any required runtime credentials are available. For example:

```text
mvn test -Dheadless=true -Dbrowser=chrome
mvn test -Dheadless=true -Dbrowser=firefox
```

Report the exact command and observed pass, failure, skip, and unrun counts. A configured workflow or documented capability is not evidence that a run passed. Do not claim live-site, cross-browser, Remember Me, performance, or reliability coverage unless that behavior was actually exercised.

## Copilot workflow and supporting assets

- For the complete eight-stage requirements-to-review process, use the [SDLC orchestrator](agents/sdlc_orchestrator.agent.md) and respect its architecture, design-review, and review-publication approval gates. Use a specialist agent for a bounded stage or task.
- Active agent definitions are in [`agents/`](agents/). Stage prompts are in [`prompts/`](prompts/), implementation/planning rules in [`rules/`](rules/), and verification/code-review procedures in [`skills/`](skills/).
- The [Stage 5 handoff](subagents/implementation-stage.md) defines implementation inputs and outputs. SDLC artifacts belong in [`docs/sdlc/`](../docs/sdlc/).
- The [workflow guide](workflows/selenium-login-workflow.md) explains the configured [GitHub Actions workflow](workflows/selenium-login.yml); neither is proof of a successful run. The [pre-commit document](hooks/pre-commit.md) is a proposed specification, not an installed hook.
- The requirements stage reads the user-supplied PRD through the [workspace Confluence MCP configuration](../.vscode/mcp.json). Never assume a fixed PRD; provide credentials only through its secure prompts and never commit token values.

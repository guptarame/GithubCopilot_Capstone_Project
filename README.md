# Selenium Login Automation - Capstone

This project implements the full capstone SDLC artifacts and a Selenium Java Maven test suite for user story `US-AUTH-002` (Customer Login & Authentication).

## Prerequisites
- Java 21+
- Maven 3.9+
- Internet access to `https://askomdch.com/account/`
- Chrome (default) or Firefox installed locally

## Credentials
Valid credentials are read only from runtime environment variables. This
repository currently has no configured CI workflow; if CI is added later,
credentials must be supplied through its secret store. They are never accepted
as Java system properties and must not be
placed in source, command-line arguments, logs, screenshots, or committed
files.

- `LOGIN_VALID_USERNAME`
- `LOGIN_VALID_PASSWORD`
- `LOGIN_EXPECTED_WELCOME` (the approved test account's displayed welcome identity)

Credential-dependent tests skip during optional local runs when their required
credentials are missing. The valid-login scenario also requires the expected
welcome identity. Set `REQUIRE_CREDENTIAL_TESTS=true` in the test process to
make any missing prerequisite fail visibly. The identity is read only from the
runtime environment and is compared in memory; never print it or include
it in source, assertion messages, reports, or screenshots.

- `LOGIN_INVALID_PASSWORD` (optional, default is `invalid-password`)
- `LOGIN_UNKNOWN_USERNAME` (optional)

Failure screenshots are disabled by default. No CI workflow or artifact upload
is configured in this repository. Keep reports and any explicitly enabled
screenshots on controlled local storage. Page content may contain sensitive
information beyond redacted form fields; review it before use, restrict access
and retention, and do not share or upload artifacts without authorization.

## Current Scenario Traceability

The accepted implementation baseline identifies only **TS-LOG-001**
(successful login), **TS-LOG-002** (incorrect password), and **TS-LOG-003**
(unregistered account) as source scenarios. The user reported removal of AC 5.1
and TS-LOG-004; that source revision was not independently verified. Blank
field checks below are project regressions only and do not fulfill the former
AC 5.1.

| Browser checks | Current classification |
| --- | --- |
| Successful login, incorrect password, unregistered account | Source scenarios TS-LOG-001, TS-LOG-002, TS-LOG-003 |
| Both fields blank; username blank; password blank | REG-NATIVE-BOTH, REG-NATIVE-USERNAME, REG-NATIVE-PASSWORD — supplemental native-validation regressions |
| Remember Me selection and same-profile browser restart | REG-REMEMBER-RESTART — supplemental; no persistence-duration guarantee |
| Lost-password link and recovery destination | REG-RECOVERY — supplemental navigation check for AC 1.4; does not submit a reset |
| Required login controls | REG-CONTROLS — supplemental visibility checks for AC 1.1–1.3 |

## Run Tests
```powershell
mvn test
```

## Optional Runtime Overrides
```powershell
mvn test -Dbrowser=firefox -Dheadless=true -DbaseUrl=https://askomdch.com/account/
```

## SDLC Artifacts
- `docs/sdlc/requirements.md`
- `docs/sdlc/architecture.md`
- `docs/sdlc/design-review.md`
- `docs/sdlc/impl-plan.md`
- `docs/sdlc/code-review-report.md`
- `docs/sdlc/verification-report.md`
- `docs/sdlc/pr-description.md`

## Copilot Framework

Reusable guidance and assets are grouped under `.github/`. Active agent
definitions remain in `.github/agents`, where GitHub Copilot discovers them.

```text
.github/
  agents/     Active SDLC agent definitions
  hooks/      Hook conventions (no executable hooks today)
  prompts/    Reusable task prompts
  rules/      Concise project rules
  skills/     Reusable task recipes
  subagents/  SDLC delegation index
.vscode/      Workspace MCP server configuration
templates/    Reserved shared templates
copilot-instructions.md
```

Start with [repository instructions](copilot-instructions.md), then use the
[framework index](.github/copilot-instructions.md) for the relevant surface.

There is currently no `.github/workflows/` directory, GitHub Actions workflow,
or configured CI job in this repository. Tests are run with Maven as described
above; historical SDLC reports may refer to workflow files that are no longer
present.

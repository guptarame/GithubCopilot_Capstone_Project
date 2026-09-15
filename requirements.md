# Requirements - US-AUTH-002 (Customer Login & Authentication)

## Scope
Automate login behavior validation for `https://askomdch.com/account/` using Selenium with Java and Maven.

## Functional Requirements
- FR-01: Open My Account login page and confirm login form is visible.
- FR-02: Validate required UI controls are present:
  - Username or email input (`id=username`)
  - Password input (`id=password`)
  - Remember me checkbox
  - Log in button (`name=login`)
  - Lost your password link
- FR-03: When username and password are blank, display field validation errors.
- FR-04: With valid credentials, authenticate and redirect to account dashboard.
- FR-05: With invalid password for a valid user, show login failure message.
- FR-06: With unregistered username/email, show account-not-found error message.
- FR-07: Remember me checkbox can be selected and stays selected before submit.
- FR-08: Lost your password link navigates to password reset page.

## Non-Functional Requirements
- NFR-01: Tests must run via Maven command line (`mvn test`).
- NFR-02: Use Page Object Model for readability and reuse.
- NFR-03: Keep secrets out of source code; read valid credentials from environment variables.
- NFR-04: Provide stable waits (explicit waits), avoid hard sleeps.
- NFR-05: Tests should run headless by default for CI compatibility.
- NFR-06: Capture clear assertion messages for faster triage.

## Test Data Requirements
- `LOGIN_VALID_USERNAME` and `LOGIN_VALID_PASSWORD`: required for successful-login scenario.
- `LOGIN_INVALID_PASSWORD`: invalid password for a valid user.
- `LOGIN_UNKNOWN_USERNAME`: non-registered email/username.

## Out of Scope
- User registration workflow.
- Full password reset completion flow (only link navigation is validated).
- Cross-browser matrix execution in this initial implementation.

## Assumptions
- Target site is publicly reachable during test execution.
- A valid test account exists and is controlled by the test team.
- UI text may vary slightly; assertions use keyword-based matching for known error/success behavior.


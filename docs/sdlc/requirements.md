# Requirements Specification

## User Story
- **Story ID:** US-AUTH-002
- **Epic:** User Authentication & Account Management
- **Module:** Customer Login Portal
- **Priority:** High
- **Severity:** Critical

## Summary
This story covers the customer login flow on the My Account page at `https://askomdch.com/account/`. A registered customer must be able to authenticate using either username or email plus password, optionally persist the session with **Remember me**, recover a forgotten password, and receive clear feedback for success and failure states.

## Functional Requirements

### FR-01 Login form visibility
- The My Account page shall display a login form for returning customers.
- The form shall include a **Username or email address** input with id `username`.
- The form shall include a **Password** input with id `password`.
- The form shall include a **Remember me** checkbox.
- The form shall include a **Log in** button with name `login`.
- The form shall include a **Lost your password?** link.

### FR-02 Required-field validation
- When the user submits the form with blank username and/or password fields, the system shall prevent submission.
- The system shall display inline validation messages for missing required fields.

### FR-03 Successful authentication
- When the user submits valid credentials, the system shall authenticate the user.
- After successful login, the user shall be redirected to the customer dashboard / My Account home view.
- The page shall display a welcome state showing the logged-in username and logout option.

### FR-04 Failed authentication
- When the user enters an incorrect password, the system shall display an error notice.
- When the user enters an unregistered username or email, the system shall display an error notice.
- The user shall remain on the login page after failed authentication.

### FR-05 Remember me persistence
- When the **Remember me** checkbox is selected during login, the authenticated session shall persist across browser restarts, subject to application/session policy.

### FR-06 Password recovery navigation
- When the user clicks **Lost your password?**, the system shall navigate to the password recovery/reset flow.

## Non-Functional Requirements

### NFR-01 Security
- Credentials shall be transmitted over HTTPS.
- Authentication feedback shall not expose sensitive information beyond what is necessary for the user.
- The test automation shall avoid storing real credentials in source control.

### NFR-02 Reliability
- The login flow shall consistently return deterministic pass/fail outcomes for valid, invalid, and blank credential scenarios.

### NFR-03 Compatibility
- The solution shall be testable on a supported desktop browser such as Chrome and Firefox.
- The page shall be accessible on the live application URL `https://askomdch.com/account/`.

### NFR-04 Maintainability
- Page interactions shall be encapsulated using a page object model to support future UI changes.
- Locators shall prefer stable attributes such as id and name.

### NFR-05 Observability
- Tests shall capture clear assertions for redirect, success message, error notice, and password reset navigation.

## Test Scope

The following scenarios are in scope for automation:

- **TS-LOG-001** Verify successful login with valid Username/Email and Password.
- **TS-LOG-002** Verify login attempt with invalid/incorrect password.
- **TS-LOG-003** Verify login attempt with an unregistered email/username.
- **TS-LOG-004** Verify login attempt with blank username and password fields.
- **TS-LOG-005** Verify functionality of the "Remember me" checkbox.
- **TS-LOG-006** Verify navigation via "Lost your password?" link.

## Out of Scope
- Account registration.
- Multi-factor authentication.
- Social login providers.
- Admin account access.
- Checkout, order history, and profile edits beyond confirming authenticated access.

## Acceptance Criteria
1. The login form elements shall be present with the required ids/names/labels.
2. Blank form submission shall surface validation errors and prevent login.
3. Valid credentials shall authenticate successfully and redirect to the account dashboard.
4. Invalid credentials shall produce a clear error notice and keep the user on the login page.
5. The Remember me option shall persist the session according to the application behavior.
6. The password recovery link shall navigate to the reset flow.

## Assumptions
- A registered customer account already exists and valid credentials are available for testing.
- The application under test is reachable during test execution.
- Session persistence behavior is supported by the site and browser configuration.

## Dependencies
- Live access to `https://askomdch.com/account/`.
- A valid test account for successful login validation.
- Supported browser binaries and WebDriver availability.
- Network connectivity to the target application.

## Traceability

| Test Scenario | Requirement Coverage |
| --- | --- |
| TS-LOG-001 | FR-03, NFR-05 |
| TS-LOG-002 | FR-04, NFR-05 |
| TS-LOG-003 | FR-04, NFR-05 |
| TS-LOG-004 | FR-02, NFR-05 |
| TS-LOG-005 | FR-05, NFR-02 |
| TS-LOG-006 | FR-06, NFR-05 |

## Notes
- The login automation should prefer stable selectors (`id="username"`, `id="password"`, `name="login"`) to minimize brittleness.
- Exact error messages may vary slightly by theme/plugin version; assertions should tolerate equivalent wording where appropriate.

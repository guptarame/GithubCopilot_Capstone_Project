# Requirements Document

**Feature:** Customer Login & Authentication
**Source:** [Confluence PRD, Customer Login & Authentication](https://epam-team-en32bjvm.atlassian.net/wiki/spaces/MFS/pages/11796481/Customer+Login+Authentication) (page ID 11796481, space MFS)
**Source review:** The full page was read in the authenticated Confluence browser. On 2026-10-14, the page UI displayed “Updated 21m ago”; the exact version and timestamp are unavailable. The configured Confluence MCP API did not provide the source content, so this analysis uses the authenticated rendered page.
**Analysis date:** 2026-10-14
**Analyst:** requirements-agent

---

## Overview

US-AUTH-002 describes a registered customer/e-commerce shopper logging in with username/email and password to view the dashboard, manage orders, update billing/shipping, and review account details. The My Account login page provides credential fields, Remember Me, Log in, and password-reset navigation. The source gives the feature High priority and Critical severity.

**Scope:** Customer login and the controls, validation, authentication outcomes, and password-reset navigation specified for <https://askomdch.com/account/>.

---

## Functional Requirements

### FR-1: Display login controls
**Description:** The login page shall provide username/email (`id=username`) and password (`id=password`) fields, a Remember Me checkbox, a visible and clickable Log in control (`name=login`), and a Lost your password? link.
**Source acceptance criteria:** AC 1.1–1.4, Page UI & Elements. The Lost your password? link redirects to the password-reset flow.
**Acceptance criteria:** The named fields and controls are present. The Log in control is visible and clickable. Activating Lost your password? navigates to the password-reset flow. The source lists Remember Me to persist the user session but does not specify duration or require persistence across browser restarts.

### FR-2: Validate required credentials
**Description:** Submitting the form with blank username and password fields is prevented, and inline validation errors are triggered for both required fields.
**Source acceptance criterion:** AC 5.1. This wording is the source criterion; browser-native validation is not equivalent to application inline errors.
**Current source scenario:** TS-LOG-004, blank username and password.

### FR-3: Authenticate valid credentials
**Description:** Valid credentials authenticate the customer and redirect to the customer dashboard/My Account home.
**Source acceptance criteria:** AC 2.1–2.2. The customer is redirected to the dashboard/My Account home and sees the specified welcome success message.
**Current source scenario:** TS-LOG-001, successful login.

### FR-4: Report invalid credentials
**Description:** An incorrect password keeps the user on the login page and displays an error notice at the top of the form. An unregistered account keeps the user on the login page and displays an error indicating the account does not exist.
**Source acceptance criteria:** AC 3.1 covers an incorrect password; AC 4.1 covers an unregistered account.
**Current source scenarios:** TS-LOG-002, incorrect password; TS-LOG-003, unregistered user.

### FR-5: Provide the Remember Me control
**Description:** The login form shall display the Remember Me checkbox, identified in AC 1.2 as a control to persist the user session.
**Acceptance criteria:** The checkbox is present. The persistence duration and behavior after browser restart are unspecified; restart persistence is not a source requirement.

### FR-6: Navigate to password recovery
**Description:** The Lost your password? link shall redirect to the password-reset flow.
**Source acceptance criterion:** AC 1.4, Page UI & Elements. The current PRD does not list a dedicated password-reset test scenario.

---

## Non-Functional Requirements

### NFR-1: Performance and security criteria are unspecified
**Requirement:** The PRD provides no measurable performance or security requirements. It describes secure login as a user goal, but specifies no measurable security criteria or performance thresholds.
**Acceptance Criteria:** No measurable performance or security acceptance criteria can be derived from the supplied PRD; these remain unspecified rather than assigned invented targets.

---

## User Stories

**US-AUTH-002:** As a registered customer/e-commerce shopper, I want to securely log into my account using my username/email and password, so that I can view my dashboard, manage orders, update billing/shipping addresses, and review account details.

The current PRD lists exactly four test scenarios:

- **TS-LOG-001:** Successful login.
- **TS-LOG-002:** Incorrect password.
- **TS-LOG-003:** Unregistered user.
- **TS-LOG-004:** Blank username and password.

The current PRD lists exactly these four scenarios; it does not list a dedicated Remember Me restart or password-reset scenario. Password-reset navigation remains a requirement under AC 1.1–1.4, while no source criterion requires Remember Me to survive browser restarts.

---

## Constraints

- The customer has an active internet connection and a supported browser.
- The customer has a registered account on askomdch.com.
- The customer is on the My Account login page: <https://askomdch.com/account/>.

## Out of Scope

The PRD does not specify out-of-scope functionality.

## Dependencies

- Internet access and a supported browser.
- A registered customer account on askomdch.com.
- The My Account login page, customer dashboard, and password reset/recovery flow identified by the PRD.

## Assumptions

No additional assumptions have been introduced beyond the stated PRD preconditions.

## Open Questions

- What session duration and persistence behavior does Remember Me promise? In particular, is persistence across browser restarts intended? The current PRD does not say.
- Are the source's example validation and authentication messages illustrative, or must exact text be used?
- What measurable security or performance criteria, if any, should be defined?
- Should password-reset navigation receive a dedicated scenario in a future source revision? It is currently required by AC 1 but has no dedicated listed scenario.

## Project Deviation Record

The prior requirements artifact recorded that on 2026-10-02 a human reviewer accepted browser-native required-field validation for this automation scope in place of FR-2's source wording. That historical approval record is carried forward here but has not been independently verified during this resume; no additional approval is claimed. Browser-native validation does **not** fulfill the source requirement for inline validation errors. The source requirement remains authoritative unless the project obtains and records a valid change to it.

## Source Success Criteria

The source criteria are: AC 1.1–1.4, the specified login controls and password-reset navigation; AC 2.1–2.2, successful authentication, dashboard/welcome outcome; AC 3.1, incorrect-password failure; AC 4.1, unregistered-account failure; and AC 5.1, blank username and password with inline errors on both. The source scenario list contains only TS-LOG-001 through TS-LOG-004 as enumerated above. These are requirements, not a claim that the scenarios have been executed or passed. No additional Remember Me restart or password-reset scenario is listed.

## Traceability

- **FR-1:** AC 1.1–1.4, Page UI & Elements (username/email, password, Remember Me, Log in, and Lost your password? controls/navigation).
- **FR-2:** AC 5.1; TS-LOG-004.
- **FR-3:** AC 2.1–2.2; TS-LOG-001.
- **FR-4:** AC 3.1 (incorrect password), TS-LOG-002; AC 4.1 (unregistered account), TS-LOG-003.
- **FR-5:** AC 1.2. No restart-persistence scenario is present in the current source.
- **FR-6:** AC 1.4. There is no dedicated password-reset scenario in the current source.
- **NFR-1:** The PRD's absence of measurable performance/security thresholds; no threshold inferred.

**Stage 1 handoff:** Requirements artifact reconciled against the authenticated source. Stage 1 only was run; Stage 2 and later stages were not started. The prior rejection of the architecture proposal remains in effect.

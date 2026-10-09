# Requirements Document

**Feature:** Customer Login & Authentication on My Account Page
**Source:** [Confluence PRD, Customer Login & Authentication](https://epam-team-en32bjvm.atlassian.net/wiki/spaces/MFS/pages/11796481/Customer+Login+Authentication) (page ID 11796481, space MFS, version 2; last updated 2026-09-21 16:32:06 UTC)
**Date:** 2026-10-09
**Agent:** requirements-agent

**Fresh Stage 1 source recheck (2026-10-09):** The current Confluence page was retrieved by page ID and its full content reviewed. Its version, title, source criteria, and six test scenarios match this analysis; no product requirement changes were needed. The required-field source criterion remains application-rendered inline errors. The browser-native validation deviation below is project context only and does not fulfill that criterion; its approval is retained as recorded in the prior artifact, not independently re-verified during this source recheck.

---

## Overview

A registered customer shall be able to authenticate from the My Account login page to access the customer dashboard and account-management functions. The login flow includes credential entry, optional session persistence, required-field feedback, invalid-credential feedback, and password recovery navigation.

**Scope:** Login on the My Account page at <https://askomdch.com/account/>. The PRD identifies the feature as High priority and Critical severity.

---

## Functional Requirements

### FR-1: Display login controls
**Description:** The login form shall provide a username/email field (`id=username`), a password field (`id=password`), a Remember Me checkbox, a visible and clickable Log in button (`name=login`), and a Lost your password? link.
**Acceptance Criteria:** The specified controls are present and identifiable by their stated labels and attributes. (TS-LOG-004, TS-LOG-005, TS-LOG-006)

### FR-2: Validate required credentials
**Description:** The login form shall prevent submission when the username/email or password is empty and show an inline required-field error for each empty field.
**Source acceptance criterion:** Submitting both fields empty leaves the user on the login page and displays inline required errors for both fields. (TS-LOG-004)
**Approved project deviation (2026-10-02):** The existing requirements artifact records human reviewer acceptance of browser-native required-field validation for this automation scope instead of application-rendered inline errors. No separate approval record was retrieved during this analysis. This does not change or reinterpret the source PRD.
**Project acceptance criteria:** Empty submission remains on the login page; browser-native validation identifies each empty field as required. (TS-LOG-004, TS-LOG-007, TS-LOG-008)

### FR-3: Authenticate valid credentials
**Description:** The system shall authenticate a registered customer when valid credentials are submitted.
**Acceptance Criteria:** Successful authentication redirects to the customer dashboard / My Account home and displays a welcome message identifying the customer and a Log out option. (TS-LOG-001)

### FR-4: Report invalid credentials
**Description:** The system shall reject an incorrect password or an unregistered username/email and show an error at the top of the login form.
**Acceptance Criteria:** Each invalid-credential case remains on the login page and displays an error indicating the credentials are incorrect or the username/email is invalid. (TS-LOG-002, TS-LOG-003)

### FR-5: Persist a remembered session
**Description:** When Remember Me is selected, the customer session shall remain active across browser restarts.
**Acceptance Criteria:** After successful login with Remember Me selected, restarting the browser does not end the customer session. (TS-LOG-005)

### FR-6: Navigate to password recovery
**Description:** The Lost your password? link shall direct the customer to the password reset/recovery flow.
**Acceptance Criteria:** Activating the link opens the password reset or recovery page. (TS-LOG-006)

---

## Non-Functional Requirements

### NFR-1: Performance and security criteria are unspecified
**Requirement:** The PRD provides no measurable performance or security requirements. It describes secure login as a user goal, but specifies no measurable security criteria or performance thresholds.
**Acceptance Criteria:** No measurable performance or security acceptance criteria can be derived from the supplied PRD; these remain unspecified rather than assigned invented targets.

---

## User Stories

**US-AUTH-002:** As a registered customer/e-commerce shopper, I want to securely log into my account using my username/email and password, so that I can view my dashboard, manage orders, update billing/shipping addresses, and review account details.

**Acceptance Criteria:** Covered by FR-1 through FR-6 and scenarios TS-LOG-001 through TS-LOG-006 above.

---

## Constraints

- The customer has an active internet connection and a supported browser.
- The customer has a registered account on askomdch.com.
- The customer is on the My Account login page: <https://askomdch.com/account/>.
- The PRD identifies the module as Customer Login Portal.

## Out of Scope

The PRD does not specify out-of-scope functionality.

## Dependencies

- Internet access and a supported browser.
- A registered customer account on askomdch.com.
- The My Account login page, customer dashboard, and password reset/recovery flow identified by the PRD.

## Assumptions

No additional assumptions have been introduced beyond the stated PRD preconditions.

## Open Questions

- What is the expected duration of a Remember Me session? The PRD requires persistence across browser restarts but does not define a duration.
- Are the PRD's example error messages illustrative, or must exact message text be used?
- What measurable security and performance criteria, if any, should be added?

## Approved Deviations

- On 2026-10-02, the human reviewer accepted browser-native required-field validation in place of the source PRD's application-rendered inline errors for FR-2. Verification must report this as a project-approved deviation, not as fulfillment of the source wording.

## Success Criteria

All six PRD test scenarios pass: valid login redirects with a welcome message (TS-LOG-001); incorrect password and unregistered username/email each show an error on the login page (TS-LOG-002 and TS-LOG-003); empty submission is prevented and both required errors appear (TS-LOG-004); Remember Me preserves the session across browser restarts (TS-LOG-005); and the lost-password link opens recovery (TS-LOG-006).

## Traceability

- Source read for this analysis: [Customer Login & Authentication PRD](https://epam-team-en32bjvm.atlassian.net/wiki/spaces/MFS/pages/11796481/Customer+Login+Authentication), page ID 11796481, space MFS, version 2, last updated 2026-09-21 16:32 UTC.
- FR-1 maps to Acceptance Criteria 1 (Page UI & Elements); FR-2 maps to Acceptance Criteria 2 (Validation & Field Checks) and TS-LOG-004; FR-3 maps to Acceptance Criteria 3 (Successful Authentication) and TS-LOG-001; FR-4 maps to Acceptance Criteria 4 (Failed Authentication) and TS-LOG-002/003; FR-5 and FR-6 map to the corresponding TS-LOG-005 and TS-LOG-006 scenarios.
- NFR-1 reflects the absence of measurable performance or security criteria in the supplied PRD; no thresholds have been inferred.
- FR-2 browser-native validation is recorded as a project deviation from the PRD's inline-error criterion; the source requirement remains unchanged above.
- Next Stage: Architecture.

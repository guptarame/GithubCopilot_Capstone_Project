# Requirements Document

**Feature:** Customer Login & Authentication
**Source:** [Confluence PRD, Customer Login & Authentication](https://epam-team-en32bjvm.atlassian.net/wiki/spaces/MFS/pages/11796481/Customer+Login+Authentication) (page ID 11796481, space MFS)
**Source review:** The full page was read in the authenticated Confluence browser on 2026-10-14. At that time, the page UI displayed “Updated 21m ago”; the exact version and timestamp are unavailable. On 2026-11-03, the user reported that AC 5.1 and TS-LOG-004 had been removed from the Confluence page. This artifact reflects those reported deletions only; the exact updated source version/date was not provided, and the current page could not be independently verified. The configured Confluence MCP API did not provide the source content.
**Initial analysis date:** 2026-10-14
**Reconciliation date:** 2026-11-03
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

### FR-3: Authenticate valid credentials
**Description:** Valid credentials authenticate the customer and redirect to the customer dashboard/My Account home.
**Source acceptance criteria:** AC 2.1–2.2. The customer is redirected to the dashboard/My Account home and sees the specified welcome success message.
**Source scenario in the page read on 2026-10-14:** TS-LOG-001, successful login.

### FR-4: Report invalid credentials
**Description:** An incorrect password keeps the user on the login page and displays an error notice at the top of the form. An unregistered account keeps the user on the login page and displays an error indicating the account does not exist.
**Source acceptance criteria:** AC 3.1 covers an incorrect password; AC 4.1 covers an unregistered account.
**Source scenarios in the page read on 2026-10-14:** TS-LOG-002, incorrect password; TS-LOG-003, unregistered user.

### FR-5: Provide the Remember Me control
**Description:** The login form shall display the Remember Me checkbox, identified in AC 1.2 as a control to persist the user session.
**Acceptance criteria:** The checkbox is present. The page read on 2026-10-14 did not specify persistence duration or behavior after browser restart; restart persistence was not a criterion in that source.

### FR-6: Navigate to password recovery
**Description:** The Lost your password? link shall redirect to the password-reset flow.
**Source acceptance criterion:** AC 1.4, Page UI & Elements. The page read on 2026-10-14 did not list a dedicated password-reset test scenario.

---

## Non-Functional Requirements

### NFR-1: Performance and security criteria are unspecified
**Requirement:** The page read on 2026-10-14 provided no measurable performance or security requirements. It describes secure login as a user goal, but specifies no measurable security criteria or performance thresholds.
**Acceptance Criteria:** No measurable performance or security acceptance criteria can be derived from that page; these remain unspecified rather than assigned invented targets. The current page was not independently verified.

---

## User Stories

**US-AUTH-002:** As a registered customer/e-commerce shopper, I want to securely log into my account using my username/email and password, so that I can view my dashboard, manage orders, update billing/shipping addresses, and review account details.

The current artifact scenario inventory, reflecting the reported removal, is:

- **TS-LOG-001:** Successful login.
- **TS-LOG-002:** Incorrect password.
- **TS-LOG-003:** Unregistered user.

On 2026-11-03, the user reported that TS-LOG-004 was removed; independent verification is unavailable as noted above. The last independently read page did not list a dedicated Remember Me restart or password-reset scenario. Password-reset navigation remains supported by AC 1.1–1.4 in that page; no source criterion there required Remember Me to survive browser restarts.

---

## Constraints

- The customer has an active internet connection and a supported browser.
- The customer has a registered account on askomdch.com.
- The customer is on the My Account login page: <https://askomdch.com/account/>.

## Out of Scope

The page read on 2026-10-14 did not specify out-of-scope functionality.

## Dependencies

- Internet access and a supported browser.
- A registered customer account on askomdch.com.
- The My Account login page, customer dashboard, and password reset/recovery flow identified by the PRD.

## Assumptions

No additional assumptions have been introduced beyond the stated PRD preconditions.

## Open Questions

- What session duration and persistence behavior does Remember Me promise? In particular, is persistence across browser restarts intended? The page read on 2026-10-14 did not say; the current page was not independently verified.
- Are the source's example validation and authentication messages illustrative, or must exact text be used?
- What measurable security or performance criteria, if any, should be defined?
- Should password-reset navigation receive a dedicated scenario? The last independently read page required it under AC 1 but had no dedicated listed scenario; current page status was not independently verified.

## Source Change Record

On 2026-11-03, the user reported that **AC 5.1** and **TS-LOG-004** were removed from the Confluence page. This is a user-reported source delta, not an independently verified page update. The exact source version/date was not provided, and independent verification was unavailable.

The prior source wording, retained for history only, was:

- **AC 5.1:** “Submitting the form with blank username and password fields is prevented, and inline validation errors are triggered for both required fields.”
- **TS-LOG-004:** Blank username and password.

These are superseded in this artifact's current source inventory per the user report. They are not active source requirements or scenarios here, and their removal does not mean the former criterion was met. FR-2 is therefore omitted from the active requirement list; its identifier is retained only in this historical record.

## Project Deviation Record

The prior requirements artifact recorded that on 2026-10-02 a human reviewer accepted browser-native required-field validation for this automation scope in place of FR-2's former source wording. That historical approval record is carried forward but has not been independently verified during this update; no additional approval is claimed. Browser-native validation does not fulfill the former inline-error criterion and is not source compliance. It may be treated only as a supplementary framework test, not as a current PRD requirement.

## Source Success Criteria

Based on the page read on 2026-10-14, and adjusted only for the user-reported removal, the source criteria retained here are: AC 1.1–1.4, the specified login controls and password-reset navigation; AC 2.1–2.2, successful authentication and dashboard/welcome outcome; AC 3.1, incorrect-password failure; and AC 4.1, unregistered-account failure. AC 5.1 is excluded from the current inventory per the user report. The reported current scenario inventory retains TS-LOG-001 through TS-LOG-003; TS-LOG-004 is excluded per that report. The page has not been independently checked for other changes. These criteria are not a claim that scenarios have been executed or passed. No additional Remember Me restart or password-reset scenario was listed on the page read on 2026-10-14.

## Traceability

- **FR-1:** AC 1.1–1.4, Page UI & Elements (username/email, password, Remember Me, Log in, and Lost your password? controls/navigation).
- **FR-3:** AC 2.1–2.2; TS-LOG-001.
- **FR-4:** AC 3.1 (incorrect password), TS-LOG-002; AC 4.1 (unregistered account), TS-LOG-003.
- **FR-5:** AC 1.2. No restart-persistence scenario was present in the page read on 2026-10-14.
- **FR-6:** AC 1.4. There was no dedicated password-reset scenario in the page read on 2026-10-14.
- **NFR-1:** The page read on 2026-10-14 provided no measurable performance/security thresholds; none inferred.

**Historical Stage 1 handoff:** At the time of this requirements update, only Stage 1 had run, later stages had not started, and the prior architecture rejection still applied. This is a point-in-time record, not the current pipeline gate. The fresh architecture was subsequently approved on 2026-11-03 at 00:33 (+05:30); the current architecture and design-review dispositions record completed Stages 4–6 and accepted limitations. The current Confluence page remains independently unverified.

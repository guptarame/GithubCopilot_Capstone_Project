# Requirements — Customer Login & Authentication

## Source and provenance

- **Feature:** Customer Login & Authentication
- **Story:** US-AUTH-002
- **PRD source:** Text supplied directly in this conversation for this Stage 1 run. The Confluence page was not independently retrieved or verified; no PRD page link, page ID, source version, or source date was supplied.
- **Customer portal identified by the supplied text:** <https://askomdch.com/account/>. This is the login portal URL, not a verified PRD link.
- **Priority / severity:** High / Critical (as supplied).
- **Analysis date:** 2026-11-18
- **Analyst:** requirements-agent

**Provenance limitation:** This document reflects only the supplied PRD material, which the user stated was complete for this run. It does not claim Confluence verification. The previous requirements artifact was stale and has been replaced with this run's source content; its historical claims are not treated as current source requirements.

## Overview and scope

US-AUTH-002 describes a registered customer who wants to log in securely to view the dashboard, manage orders, update billing/shipping addresses, and review account details.

**In scope:** The login form controls and the successful and unsuccessful login outcomes explicitly described below, plus the password-reset link to its flow.

**Out of scope:** No explicit out-of-scope items are stated in the supplied material. Behavior beyond the listed controls and three scenarios is not specified here.

## Functional requirements

### FR-1 — Provide login form controls
The My Account login form shall provide a username/email field (`id=username`), a password field (`id=password`), a Remember Me checkbox, and a Log in button (`name=login`).

- **Acceptance criteria:** The supplied form controls are present with the stated field identifiers and button name.
- **Trace:** Supplied PRD material, “Form.”

### FR-2 — Link to password reset
The form shall provide a “Lost your password?” link to the password reset flow.

- **Acceptance criteria:** The link is available and leads to the password reset flow. The supplied material does not identify a destination URL or define further reset-flow behavior.
- **Trace:** Supplied PRD material, “Form.”

### FR-3 — Authenticate valid source-specified credentials
Submitting the **fixed credentials specified in the supplied PRD (redacted)** shall authenticate the customer, redirect to the dashboard/My Account home, and show a welcome message.

- **Acceptance criteria:** For the source-specified valid credentials, authentication succeeds; the customer reaches the dashboard/My Account home and sees a welcome message.
- **Trace:** AC 2 / TS-LOG-001.

### FR-4 — Handle an incorrect password
For a valid account with an incorrect password, the login attempt shall remain on the login page and display an incorrect-password notice at the top of the form.

- **Acceptance criteria:** The page remains the login page and the top-of-form notice communicates an incorrect password. Exact notice text is not specified.
- **Trace:** AC 3 / TS-LOG-002.

### FR-5 — Handle an unregistered username/email
For an unregistered username/email, the login attempt shall remain on the login page and display an account-not-found error.

- **Acceptance criteria:** The page remains the login page and an error communicates that the account was not found. Exact error text is not specified.
- **Trace:** AC 4 / TS-LOG-003.

## Non-functional requirements

No explicit, measurable non-functional requirements or thresholds are provided in the supplied material. “Secure login” appears as a user-story goal, but no testable security criteria are defined. No performance, reliability, accessibility, or usability target is stated; none is inferred.

## User story and source scenarios

**US-AUTH-002:** As a registered customer, I want secure login so that I can view my dashboard, manage orders, update billing/shipping addresses, and review account details.

Only these three scenarios are present in the supplied material:

1. **TS-LOG-001 — Valid login:** The fixed credentials specified in the supplied PRD (redacted) authenticate; redirect to dashboard/My Account home and show a welcome message.
2. **TS-LOG-002 — Incorrect password:** For a valid account and incorrect password, stay on the login page and display a top-of-form incorrect-password notice.
3. **TS-LOG-003 — Unregistered account:** For an unregistered username/email, stay on the login page and display an account-not-found error.

The supplied source's credential values are intentionally not reproduced, transmitted, logged, or persisted here.

## Constraints and dependencies

- **Source preconditions:** Active internet and a supported browser; a registered account; and the customer is on the My Account login page.
- **Dependencies:** Availability of the account/login flow and the dashboard/My Account home; the password reset flow for the supplied link.
- **Unspecified constraint detail:** The supported-browser set is not identified.

## Assumptions

- The supplied material is the complete source input authorized for this Stage 1 analysis. It is not treated as independently verified Confluence content.
- No unstated behavior is assumed for Remember Me, exact message copy, or password-reset completion.

## Risks and open questions

1. **Credential-policy conflict:** The supplied PRD prescribes fixed credentials (redacted), while repository guidance requires credentials to be read only from runtime environment variables. This is unresolved; there is no approved deviation. How should the requirement and project policy be reconciled before implementation/verification? This document preserves the source requirement and does not authorize hardcoding or otherwise handling credential values contrary to repository policy.
2. **Precondition/scenario conflict:** The preconditions specify a registered account, while TS-LOG-003 requires an unregistered username/email. Does the registered-account precondition apply only to the successful and incorrect-password cases?
3. What browsers qualify as “supported”?
4. Must welcome/error messages match exact text, or is the stated meaning sufficient?
5. What exact destination and expected behavior define the password reset flow?
6. Does Remember Me have behavior beyond being present as a checkbox? No persistence duration or session behavior is specified.
7. Are any measurable security or other non-functional criteria intended?

## Source success criteria

The supplied material's success criteria are the form controls and reset link in FR-1/FR-2, and the three outcomes in FR-3 through FR-5. No other test scenarios, measurable NFRs, or success thresholds are supplied. These are source criteria, not evidence that any test has been executed or passed.

## Approved interpretations and deviations

**None.** In particular, the PRD's fixed-credential requirement has not been waived or replaced with the repository's runtime-environment policy; the conflict remains open.

## Traceability map

| Requirement | Supplied PRD trace |
|---|---|
| FR-1 | Form: username/email `id=username`, password `id=password`, Remember Me checkbox, Log in button `name=login` |
| FR-2 | Form: “Lost your password?” link to password reset flow |
| FR-3 | AC 2 / TS-LOG-001 |
| FR-4 | AC 3 / TS-LOG-002 |
| FR-5 | AC 4 / TS-LOG-003 |
| NFRs | No measurable non-functional criteria stated in the supplied material |

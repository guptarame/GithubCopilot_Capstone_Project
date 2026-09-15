# Design Review - Findings and Decisions

## Review Findings
- High risk: Successful-login test can fail in shared environments without secure credentials.
- Medium risk: External target website can be slow or intermittently unavailable.
- Medium risk: Exact-text assertions can become brittle due to minor CMS copy changes.
- Low risk: Browser differences may affect behavior if expanded beyond Chrome.

## Agreed Design Decisions
- Use environment variables for valid credentials and never hardcode secrets.
- Mark successful-login test as conditional (skip when credentials are not provided).
- Use explicit waits and keyword-based assertions for dynamic/error messages.
- Start with one browser profile (Chrome headless) and keep browser configurable.
- Maintain Page Object Model to simplify selector maintenance.

## Updates Applied to Architecture
- Added conditional execution strategy for credential-dependent test.
- Added explicit mention of resilience against UI text variance.


# [Feature] Complete Selenium test automation framework for login functionality

## Summary

This PR implements a Selenium-based login automation framework for the My Account authentication flow. It adds reusable page-object abstractions, a browser lifecycle manager, configuration-driven timeout support, structured runtime logging, and failure screenshots to make the login scenarios easier to maintain and diagnose.

The work is aligned with the SDLC artifacts in this repository, including requirements, architecture, design review, implementation planning, and verification evidence. This establishes a maintainable foundation for future UI automation coverage.

## Changes Made

### Framework additions and updates
- Added configurable page-load timeout support in `TestConfig`
- Updated `BaseTest` to apply timeouts and manage driver lifecycle
- Added a reusable `BasePage` abstraction for shared waits and UI actions
- Refactored `LoginPage` to extend `BasePage`
- Added `LogUtil` for timestamped, structured logging
- Added `ScreenshotUtil` for saved failure artifacts in `target/screenshots`
- Added `TestLifecycleListener` for JUnit lifecycle diagnostics
- Added `TestData` as a centralized configuration and credential constant source
- Updated `LoginPageTests` to use the page-object pattern and shared assertions

### Files changed / notable additions

#### Core framework
- `src/test/java/Github_Copilot/base/BaseTest.java`
- `src/test/java/Github_Copilot/pages/BasePage.java`
- `src/test/java/Github_Copilot/pages/LoginPage.java`

#### Utilities and configuration
- `src/test/java/Github_Copilot/config/TestConfig.java`
- `src/test/java/Github_Copilot/data/TestData.java`
- `src/test/java/Github_Copilot/utils/LogUtil.java`
- `src/test/java/Github_Copilot/utils/ScreenshotUtil.java`
- `src/test/java/Github_Copilot/listeners/TestLifecycleListener.java`

#### Test coverage
- `src/test/java/Github_Copilot/tests/LoginPageTests.java`

#### SDLC artifacts
- `docs/sdlc/requirements.md`
- `docs/sdlc/architecture.md`
- `docs/sdlc/design-review.md`
- `docs/sdlc/code-review-report.md`
- `docs/sdlc/impl-plan.md`
- `docs/sdlc/verify.md`

## Verification Evidence

### Command executed
```powershell
mvn test
```

### Execution summary
- **Build:** `BUILD SUCCESS`
- **Tests run:** 7
- **Passed:** 4
- **Skipped:** 3
- **Failures:** 0
- **Errors:** 0

### Passing scenarios
- UI element visibility
- invalid user flow
- blank-field validation
- lost password navigation

### Skipped scenarios
- valid login — skipped because valid credentials were not provided in the environment
- invalid password — skipped because valid credentials were not provided in the environment
- remember-me persistence — skipped because valid credentials were not provided in the environment

### Diagnostics
- Test lifecycle logging is active
- Screenshot capture worked for skipped and assumption-failed scenarios
- Maven execution completed successfully without test failures

## Limitations / Follow-up Notes

- Credential-dependent scenarios still require valid values to be provided through environment configuration.
- This verification run validates the current local environment and build flow.
- Browser and cross-browser configuration are implemented, but production-grade cross-browser expansion can be added as a future enhancement.
- The remember-me persistence scenario requires valid credentials and a browser-profile setup capable of surviving restart.

## Reviewer Checklist

- [ ] Base classes are correctly separated by responsibility
- [ ] Page Object Model usage is consistent
- [ ] Shared utilities are reusable and minimal
- [ ] Login scenarios cover positive and negative paths
- [ ] Assertions are meaningful and stable
- [ ] Browser configuration works as expected
- [ ] Timeouts are reasonable
- [ ] Logging and screenshot capture are wired correctly
- [ ] Implementation matches `requirements.md`
- [ ] Implementation matches `architecture.md`
- [ ] Design review concerns were addressed
- [ ] Verification evidence matches the project SDLC artifacts
- [ ] Maven build runs successfully

## Recommended Merge Notes

- Merge after confirming environment credentials for the credential-dependent scenarios.
- Keep credentials externalized through environment variables or secure configuration.
- Use this framework as the base for additional authenticated UI coverage.
- Preserve the SDLC artifacts for traceability and auditability.

## GitHub / PR Note

This repository does not currently expose GitHub CLI authentication in the local environment, so a remote push and PR creation could not be completed from this session. The PR description above is prepared and ready to use once credentials or a connected GitHub workflow are available.

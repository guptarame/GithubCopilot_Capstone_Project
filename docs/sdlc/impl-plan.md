# Stage 4 Implementation Plan — US-AUTH-002

**Project:** Selenium Login Automation Capstone  
**Artifacts:** `requirements.md`, `architecture.md`, `design-review.md`  
**Repository baseline:** Existing Selenium Java tests under `src/test/java/Github_Copilot/`  
**Date:** 2026-09-21  
**Agent:** planning-agent

## 1) Implementation Goals

- Stabilize the current Selenium login test framework.
- Align the test code with the approved architecture.
- Close all design review conditions:
  - `pageLoadTimeout`
  - `BasePage` abstraction
  - logging strategy
  - screenshot capture on failure
- Keep changes limited to test automation code and supporting docs.
- Validate execution against the real application and on slower network conditions.

**Note:** No application/product code changes are expected. Changes are limited to test framework, utilities, listeners, and documentation.

## 2) Priority Order

1. Configuration foundation
2. Base framework and shared utilities
3. Page object refactor
4. Listener/reporting support
5. Test data organization
6. Test scenario verification
7. Integration and resilience validation

## 3) File-by-File Change Map

| File | Recommended Change |
|---|---|
| `src/test/java/Github_Copilot/config/TestConfig.java` | Add `pageLoadTimeout()` and environment/system property support |
| `src/test/java/Github_Copilot/base/BaseTest.java` | Apply `pageLoadTimeout`, retain driver setup, register listener if needed |
| `src/test/java/Github_Copilot/pages/BasePage.java` | New abstract base page with common wait/action helpers |
| `src/test/java/Github_Copilot/pages/LoginPage.java` | Extend `BasePage`, remove duplicate wait logic |
| `src/test/java/Github_Copilot/tests/LoginPageTests.java` | Align assertions, use shared test data, verify existing scenarios |
| `src/test/java/Github_Copilot/utils/LogUtil.java` | New structured logging utility |
| `src/test/java/Github_Copilot/utils/ScreenshotUtil.java` | New screenshot capture utility |
| `src/test/java/Github_Copilot/listeners/TestLifecycleListener.java` | New listener for logging and failure screenshots |
| `src/test/java/Github_Copilot/data/TestData.java` | New centralized test data/constants class |
| `docs/sdlc/impl-plan.md` | This implementation plan |

## 4) Prioritized Task List

### TASK-001 — Add `pageLoadTimeout` to `TestConfig`
**Priority:** Critical  
**Depends on:** None  
**Blocks:** BaseTest update, slow-network validation

**Changes**
- Add `pageLoadTimeout()` to `TestConfig.java`
- Support system property and environment variable override
- Default to 30 seconds

**Acceptance**
- Returns an integer timeout value
- Works with `-DpageLoadTimeout=40`
- Falls back to default when unset

### TASK-002 — Update `BaseTest` to use `pageLoadTimeout`
**Priority:** Critical  
**Depends on:** TASK-001  
**Blocks:** Reliable execution and validation

**Changes**
- Apply page load timeout in driver setup
- Keep implicit wait behavior consistent
- Confirm browser initialization remains stable

**Acceptance**
- Driver uses configured page load timeout
- No test hangs caused by default browser page loading behavior

### TASK-003 — Create `BasePage` abstract class
**Priority:** High  
**Depends on:** None  
**Blocks:** Page object refactor

**Changes**
- New `BasePage.java`
- Add common helpers for waits, clicks, input, text retrieval, visibility checks

**Acceptance**
- Shared UI operations are centralized
- Child pages can reuse the same wait/action logic

### TASK-004 — Refactor `LoginPage` to extend `BasePage`
**Priority:** High  
**Depends on:** TASK-003  
**Blocks:** Page object standardization

**Changes**
- Extend `BasePage`
- Remove duplicate WebDriverWait/element access logic
- Keep selectors and page actions intact

**Acceptance**
- Existing login behavior still works
- Page object is simpler and reusable

### TASK-005 — Add structured logging utility
**Priority:** High  
**Depends on:** None  
**Blocks:** Listener implementation

**Changes**
- New `LogUtil.java`
- Provide log methods for test start, step, assertion, and error events

**Acceptance**
- Logs are readable in console/CI output
- Logging is available without external dependencies

### TASK-006 — Add screenshot capture utility
**Priority:** High  
**Depends on:** None  
**Blocks:** Failure listener implementation

**Changes**
- New `ScreenshotUtil.java`
- Save screenshots to `target/screenshots`
- Use timestamped filenames

**Acceptance**
- Screenshots are created on demand
- Failure capture is safe and does not break tests

### TASK-007 — Add test lifecycle listener
**Priority:** High  
**Depends on:** TASK-005, TASK-006  
**Blocks:** Failure diagnostics

**Changes**
- New `TestLifecycleListener.java`
- Log test start/end
- Capture screenshot on failure
- Register with JUnit 5

**Acceptance**
- Failing tests generate screenshots
- Test execution logs are visible and useful

### TASK-008 — Add centralized test data class
**Priority:** Medium  
**Depends on:** None  
**Blocks:** Test cleanup and consistency

**Changes**
- New `TestData.java`
- Store shared constants for usernames, passwords, messages, and timeouts

**Acceptance**
- Reusable constants replace scattered literals
- Test maintenance is easier

### TASK-009 — Verify valid login scenario
**Priority:** Critical  
**Depends on:** TASK-002, TASK-004, TASK-008  
**Blocks:** Final validation

**Changes**
- Review `LoginPageTests.java`
- Confirm valid login test matches approved requirements

**Acceptance**
- Test passes with valid credentials
- Assertions are stable and clear

### TASK-010 — Verify invalid password scenario
**Priority:** Critical  
**Depends on:** TASK-002, TASK-004, TASK-008  
**Blocks:** Final validation

**Changes**
- Review invalid-password login test
- Confirm error handling and assertion text

**Acceptance**
- Test fails for wrong password as expected
- Error message is asserted reliably

### TASK-011 — Verify unknown user scenario
**Priority:** Critical  
**Depends on:** TASK-002, TASK-004, TASK-008  
**Blocks:** Final validation

**Changes**
- Review unknown-user test
- Confirm expected authentication failure behavior

**Acceptance**
- Test passes against rejected/unknown credentials
- Error state is verified consistently

### TASK-012 — Verify blank-field validation
**Priority:** High  
**Depends on:** TASK-002, TASK-004, TASK-008  
**Blocks:** Final validation

**Changes**
- Review blank input validation test
- Confirm UI validation message coverage

**Acceptance**
- Test verifies required-field behavior
- Assertions cover both username and password validation

### TASK-013 — Run tests against the real application
**Priority:** Critical  
**Depends on:** TASK-009, TASK-010, TASK-011, TASK-012  
**Blocks:** Completion

**Changes**
- Execute the full test suite against the live target application
- Fix selectors/waits only if required

**Acceptance**
- Tests execute successfully against the application
- Failures are actionable, not flaky

### TASK-014 — Validate on slow network conditions
**Priority:** Critical  
**Depends on:** TASK-013  
**Blocks:** Completion

**Changes**
- Run tests under throttled network conditions
- Confirm timeout settings and waits are sufficient

**Acceptance**
- Tests still pass or fail deterministically
- No premature page-load failures occur

## 5) Dependency Summary

```text
TASK-001 -> TASK-002
TASK-003 -> TASK-004
TASK-005 + TASK-006 -> TASK-007
TASK-002 + TASK-004 + TASK-008 -> TASK-009/010/011/012
TASK-009/010/011/012 -> TASK-013
TASK-013 -> TASK-014
```

## 6) Design Review Risk Controls

| Design Review Item | Control |
|---|---|
| Missing `pageLoadTimeout` | TASK-001, TASK-002 |
| Missing `BasePage` abstraction | TASK-003, TASK-004 |
| Weak logging strategy | TASK-005, TASK-007 |
| No screenshot on failure | TASK-006, TASK-007 |
| Flaky real-world execution | TASK-013 |
| Slow network instability | TASK-014 |

## 7) Testing and Verification Tasks

- Run unit-level verification for `TestConfig` timeout handling.
- Run page-object smoke checks for `LoginPage`.
- Execute `LoginPageTests` locally in headed and headless modes.
- Validate artifact creation for screenshots on failure.
- Verify CI-friendly console logs.
- Confirm behavior against the live application.
- Confirm stability under network throttling.

## 8) Definition of Done

The implementation is complete when:

- `pageLoadTimeout` is configurable and used by `BaseTest`.
- `BasePage` exists and `LoginPage` extends it.
- Logging utility and failure screenshot capture are implemented.
- Existing login tests are aligned with the approved requirements.
- Test data is centralized.
- The full suite passes against the real application.
- Slow-network execution has been validated.
- No design review blockers remain open.
- `docs/sdlc/impl-plan.md` is committed and current.

## 9) Execution Note

The current repository already contains the base Selenium Java tests.  
The work is primarily framework hardening and validation, not greenfield implementation.

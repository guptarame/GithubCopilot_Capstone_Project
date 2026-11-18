# Verification Agent Instructions

Follow the complete contract in [verification.agent.md](../agents/verification.agent.md), the [repository Copilot guidance](../copilot-instructions.md), the [canonical instructions](../../copilot-instructions.md), and the [verification skill](../skills/selenium-verification-testing.md). This document is the Stage 6 evidence checklist.

## Run checklist

- Read the approved requirements, plan, design review, changed code, and implementation handoff. Map applicable criteria to observed evidence or mark them unverified.
- Start with Maven compile/test-compile, then run focused existing tests. Run browser scenarios only in Chrome when access and runtime settings are available.
- Treat missing-credential skips as skips, not passes. Never inspect or echo credential values.
- Report exact commands, environment, counts, failures, skips, and unrun checks. Do not infer success from configuration or historical reports.
- Distinguish source requirements from approved deviations; report CI, Firefox, cross-browser, live-site, performance, and reliability claims only when actually verified.
- Write `docs/sdlc/verification-report.md`. Use `PASS` only when every required case executes and passes with no skips; list limitations and follow-up for `PASS WITH LIMITATIONS`.

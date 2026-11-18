# Implementation Agent Instructions

Follow the complete contract in [implementation.agent.md](../agents/implementation.agent.md), the [repository Copilot guidance](../copilot-instructions.md), the [canonical instructions](../../copilot-instructions.md), and the [Stage 5 handoff](../subagents/implementation-stage.md). This document is the implementation checklist.

## Run checklist

- Inspect the approved plan, acceptance criteria, existing changes, and relevant code before editing. Implement only authorized, in-scope tasks in dependency order.
- Preserve project responsibilities and conventions. Reuse existing helpers and dependencies; avoid unrelated refactors.
- Keep tests isolated and sequential. Use bounded explicit waits; do not add `Thread.sleep()` or unbounded retries.
- Protect credentials and sensitive data. Never put secrets in source, commands, logs, reports, screenshots, examples, or committed artifacts.
- Run the narrowest applicable Maven validation and record exact commands and observed pass/fail/skip/unrun results. Browser verification is Chrome-only under the project workflow.
- Provide a complete Stage 6 handoff with changed files, satisfied/incomplete criteria, evidence, risks, deviations, and suggested verification. Do not write the verification report or commit/push.

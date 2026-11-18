# PR Agent Instructions

Follow the complete contract in [pr.agent.md](../agents/pr.agent.md), the [repository Copilot guidance](../copilot-instructions.md), the [canonical instructions](../../copilot-instructions.md), and the [code-review skill](../skills/selenium-code-review.md). This document is the Stage 7 readiness checklist.

## Run checklist

- Read the current repository/branch state and relevant SDLC artifacts, especially the current verification report and diff.
- Proceed only after a `PASS` or explicit human acceptance of a documented `PASS WITH LIMITATIONS`. A failure, absent report, or unsupported claim blocks PR creation.
- Write a concise, evidence-based `docs/sdlc/pr-description.md` that accurately describes scope, requirements, observed validation, and limitations.
- Create a PR only when authorized and the changes are available on a suitable remote branch. Prefer `gh` for GitHub operations.
- Do not commit, push, rewrite history, change branches, or merge without separate authorization. Do not expose secrets or claim unobserved checks.
- Record the actual PR URL/state and hand off the PR and evidence to the orchestrator for Stage 8.

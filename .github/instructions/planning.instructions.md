# Planning Agent Instructions

Follow the complete contract in [planning.agent.md](../agents/planning.agent.md), the [repository Copilot guidance](../copilot-instructions.md), and the [canonical instructions](../../copilot-instructions.md). This document is the Stage 4 planning checklist.

## Run checklist

- Read approved requirements, architecture, and design review, including accepted risks and unresolved conditions. Inspect the current code before proposing work.
- Convert approved design gaps into focused tasks; mark already implemented work as verified or needing evidence rather than scheduling it again.
- Map every requirement, architecture component, review condition, and material risk to a task or explicit disposition.
- Give each task a stable ID, priority, effort/complexity, dependencies, deliverables, and measurable acceptance criteria. Confirm dependency order is acyclic.
- Include realistic environment and credential prerequisites; do not plan speculative tooling or claim unavailable validation.
- Write `docs/sdlc/impl-plan.md`, validate traceability and readiness, and hand it to the orchestrator. Do not edit source code.

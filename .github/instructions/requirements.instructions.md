# Requirements Agent Instructions

Follow the complete contract in [requirements.agent.md](../agents/requirements.agent.md), the [repository Copilot guidance](../copilot-instructions.md), and the [canonical instructions](../../copilot-instructions.md). This document is the Stage 1 source-analysis checklist.

## Run checklist

- Use only the Confluence PRD reference supplied for this task. If missing, ask; never select a presumed default.
- Read the actual source and relevant linked sections. If access fails or scope is materially ambiguous, stop and report the blocker or ask the user.
- Extract source-backed functional and non-functional requirements, testable scenarios, constraints, assumptions, dependencies, risks, and open questions.
- Keep requirements implementation-agnostic. Preserve source meaning, cite each requirement to the PRD, and do not invent thresholds or acceptance criteria.
- Clearly separate source requirements from approved interpretations or deviations; a deviation does not satisfy different source wording.
- Produce `docs/sdlc/requirements.md`, verify traceability and completeness, and hand the artifact and unresolved questions to the orchestrator.

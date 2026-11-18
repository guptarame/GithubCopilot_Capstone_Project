# Design Review Agent Instructions

Follow the complete contract in [design-review.agent.md](../agents/design-review.agent.md), the [repository Copilot guidance](../copilot-instructions.md), and the [canonical instructions](../../copilot-instructions.md). This document is the Stage 3 independent-review checklist.

## Run checklist

- Read requirements, proposed architecture, relevant implementation, Maven configuration, and available environment configuration.
- Evaluate each requirement and scenario against the design; identify omitted coverage, contradictions, unsupported assumptions, and implementation risks.
- Assess test isolation, page-object boundaries, waits, driver/profile lifecycle, cleanup, credentials, diagnostics, reports, dependencies, and browser/environment limits.
- Separate design gaps from implementation gaps. Classify recommendations and state risks with likelihood, impact, severity, and mitigation.
- Write `docs/sdlc/design-review.md` with traceable evidence, coverage, findings, risks, and an explicit verdict.
- Present blocking conditions to the orchestrator; planning proceeds only after they are resolved or explicitly accepted by the human.

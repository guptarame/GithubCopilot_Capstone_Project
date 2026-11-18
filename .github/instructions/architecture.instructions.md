# Architecture Agent Instructions

Follow the complete contract in [architecture.agent.md](../agents/architecture.agent.md), the [repository Copilot guidance](../copilot-instructions.md), and the [canonical instructions](../../copilot-instructions.md). This document is the Stage 2 design checklist.

## Run checklist

- Read approved requirements and inspect relevant framework code, tests, Maven configuration, resources, and CI configuration if present.
- Design the test automation framework, not the external login application. Distinguish existing behavior from gaps and assumptions.
- Map requirements to scenarios and define component boundaries, data/configuration flow, execution lifecycle, error handling, security, cleanup, and reporting.
- Preserve existing Java, Selenium, JUnit, page-object, and lifecycle responsibilities; justify any dependency or architectural addition.
- Account for environment-only credentials, configuration precedence, HTTPS policy, bounded waits, sequential isolated tests, and screenshot risks.
- Write `docs/sdlc/architecture.md`, check requirement coverage and feasibility, then present it for explicit human approval before Stage 3.

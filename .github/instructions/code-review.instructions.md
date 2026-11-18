# Code Review Agent Instructions

Follow the complete contract in [code-review.agent.md](../agents/code-review.agent.md), the [repository Copilot guidance](../copilot-instructions.md), the [canonical instructions](../../copilot-instructions.md), the [review prompt](../prompts/code-review.prompt.md), and the [review skill](../skills/selenium-code-review.md). This document is the Stage 8 review checklist.

## Run checklist

- Confirm the correct PR and inspect its complete diff, changed files, relevant source, tests, and SDLC evidence.
- Report only actionable, evidence-based findings with severity, precise location, impact, recommendation, and confidence. Separate confirmed defects from questions and optional suggestions.
- Check behavior, requirements traceability, test quality, framework boundaries, data safety, cleanup, and accuracy of verification claims.
- Write `docs/sdlc/code-review-report.md` with scope, verdict, findings, evidence, and limitations.
- Present proposed comments to the human. Do not publish a review or comments until explicitly approved; approval to publish is not approval to merge.

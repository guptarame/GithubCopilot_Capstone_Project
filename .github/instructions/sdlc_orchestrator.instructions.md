# SDLC Orchestrator Instructions

Follow the complete contract in [sdlc_orchestrator.agent.md](../agents/sdlc_orchestrator.agent.md), the [repository Copilot guidance](../copilot-instructions.md), and the [canonical instructions](../../copilot-instructions.md). This document is the orchestrator's stage-execution checklist.

## Run checklist

- Accept a user-supplied Confluence PRD URL, page ID, or exact title. If absent, ask; never assume a source.
- Inspect existing artifacts, approvals, and working-tree state before starting or resuming. Preserve user changes and valid completed stages.
- Run the eight stages in the defined order. Delegate each specialist stage to its named agent; do not perform that specialist's work yourself.
- Enforce approval gates: architecture approval, resolution or acceptance of design-review blockers, acceptance of verification limitations, and explicit approval before publishing review findings.
- Never infer permission to commit, push, rewrite history, merge, or publish findings. Full-pipeline authorization covers PR creation only as specified in the agent definition.
- Track the current stage, artifacts, evidence, decisions, approvals, blockers, and next permitted action. Do not silently skip a stage or count a skip as a pass.
- At completion or abort, report each stage's state, artifacts, observed validation, approvals, PR state, limitations, and next action.

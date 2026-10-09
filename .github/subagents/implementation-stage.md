# Stage 5 Implementation Handoff

Delegate implementation to the Copilot-recognized
[implementation agent](../agents/implementation.agent.md). This document is its
handoff contract, not another agent definition.

## Objective and inputs

Implement approved Stage 5 tasks in dependency order, meeting
`docs/sdlc/impl-plan.md` criteria and preserving the approved architecture and
design-review conditions. Supply the agent with the plan, `architecture.md`,
`design-review.md`, applicable `requirements.md`, relevant
`src/test/java/Github_Copilot/` files, `pom.xml`, workflow configuration,
bounded tasks/dependencies/acceptance criteria, and prior validation or known
environment limits.

The agent must read current source and tests before editing; verify whether
planned work already exists. This repository tests an external login site and
does not implement that site's application or authentication service.

## Execution contract

- Follow approved scope and dependency order. Preserve PRD criteria and keep
  human-approved deviations explicit. Pause for material ambiguity or scope
  changes.
- Preserve the `Github_Copilot` layout and existing class responsibilities;
  reuse helpers/dependencies. Keep tests independent and sequential, use bounded
  explicit waits, and reliably clean up drivers and temporary profiles.
- Keep credentials out of source, JVM properties, commands, logs, screenshots,
  reports, and committed artifacts. Use environment/CI secrets only; report
  credential-dependent skips accurately. Handle exceptions narrowly and never
  mask failures.
- Run the smallest relevant existing Maven check after logical tasks; expand
  only when required and available. Claim live-site, cross-browser,
  slow-network, or performance results only when observed. Do not commit unless
  asked.

## Handoff and completion

Return changed files and implemented tasks, criteria met or incomplete,
commands/results including skips, risks/environment limits/deviations, and items
for Stage 6 to verify. Do not write or prefill `verification-report.md` with
unobserved results; Stage 6 owns it.

Work is ready for independent verification when planned changes are implemented
or clearly marked incomplete, targeted validation is reported accurately, and
no unrelated scope, secrets, unsupported claims, or unrequested commits were
introduced.

# Planning Stage Rules

Apply during Stage 4 when creating `docs/sdlc/impl-plan.md`. Follow the
repository-root Copilot instructions.

- Read the requirements, approved architecture, design review, and relevant
  existing framework code. Plan implementation for the Selenium test framework,
  not the external login site; distinguish source PRD criteria from approved
  deviations.
- Plan only; do not edit source code. Identify already-complete work as complete
  or verification/improvement, not new implementation. Cover architecture
  components, review conditions, and relevant tests, CI, documentation, and
  environment validation.
- Use focused, stable-ID tasks. Specify deliverable, priority, complexity,
  effort, dependencies, and measurable acceptance criteria. Order by dependency,
  ensure the dependency graph is acyclic, and map review findings and material
  risks to tasks or explicit dispositions.
- Avoid speculative scope, unnecessary dependencies, and assuming credentials,
  browsers, site access, or live/slow-network validation will be available.
  State blockers and assumptions.
- Keep the plan concise and traceable: include context/source documents, date,
  task and effort summary, tasks/order, risk and review-condition coverage,
  success criteria, and links to relevant requirements.
- Confirm all architecture components and review conditions are addressed,
  tasks and acceptance criteria are actionable, and no code changes or
  unsupported test-result claims were introduced.

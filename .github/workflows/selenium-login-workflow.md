# Selenium Login CI

This guide summarizes [`selenium-login.yml`](selenium-login.yml); it describes
configured behavior, not evidence of a successful run.

- **Triggers/runtime:** `push`, `pull_request`, and manual dispatch; Ubuntu,
  Temurin Java 21, Maven cache.
- **Browser jobs:** headless Chrome and Firefox run serially (`max-parallel: 1`,
  `fail-fast: false`) using `mvn --batch-mode test`.
- **Credentials:** repository secrets `LOGIN_VALID_USERNAME` and
  `LOGIN_VALID_PASSWORD`, plus the approved account's `LOGIN_EXPECTED_WELCOME`
  identity, are injected as environment variables. Required mode
  (`-DrequireCredentialTests=true`) makes missing values fail. Never put
  credential or identity values in YAML, command literals, logs, or artifacts.
- **Artifacts:** Surefire reports upload even on failure when present (7-day
  retention). Failure screenshots upload only on failed jobs when present
  (3-day retention); capture is disabled by default and selected-field
  redaction does not guarantee all page data is safe. Extent HTML reports are
  local only.
- **Diagnosis:** inspect the specific browser job, logs, and Surefire reports;
  check secret presence without exposing values. Treat screenshots as sensitive.
  Report pass/fail/skip/unrun accurately; results apply only to the observed run.
- **Maintenance:** when workflow triggers, Java/Maven setup, browser matrix,
  properties, secrets, artifact paths, failure behavior, or retention changes,
  update this guide and `README.md`; keep them aligned with `TestConfig`,
  `pom.xml`, and verification guidance.

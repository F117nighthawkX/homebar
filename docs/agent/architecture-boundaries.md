# Architecture Boundaries

Read this before changing module boundaries, public APIs, database schemas, service layers, dependencies, auth, logging, error handling, or cross-cutting infrastructure.

## Boundary First

Before editing architecture-level code, identify:
- The layer or module that owns the behavior.
- The public contract that callers rely on.
- The nearest existing pattern for similar behavior.
- The smallest boundary that can contain the change.
- The verification that proves callers still work.

Do not move behavior across layers just because the current location feels imperfect. If ownership is unclear, name the ambiguity and recommend a path.

## Public Contracts

Treat these as public contracts unless the repository shows otherwise:
- Exported functions, classes, hooks, components, commands, endpoints, schemas, events, messages, and migration formats.
- Database tables, columns, indexes, constraints, and serialized data.
- CLI flags, environment variables, config keys, URLs, status codes, and error shapes.

Before changing a public contract:
- Search for every caller or consumer.
- Check tests, generated clients, docs, examples, and CI usage.
- State whether the change is backward compatible.
- Add migration, adapter, or compatibility handling only when a real caller needs it.

## Layer Discipline

Keep responsibilities separated:
- UI should render state and collect user intent. It should not own business rules that belong in services or domain code.
- Domain or service code should own business decisions. It should not depend on UI details.
- Data access code should isolate persistence details. It should not leak query mechanics into unrelated layers.
- Infrastructure code should wrap external systems behind clear interfaces.
- Tests should verify behavior at the right layer instead of mocking through every layer by default.

If the repository intentionally uses a different pattern, follow the repository.

## Dependencies

Do not add dependencies by default.

Before adding one:
- Check whether the repo already has a dependency or utility that solves the problem.
- Check maintenance, license, install size, runtime cost, security posture, and platform compatibility.
- Explain why a local implementation is worse.
- Prefer official, established libraries over obscure packages.
- Update manifests, lockfiles, docs, and build config together.

Do not add frameworks, codegen, state managers, queues, ORMs, or build tools without explicit approval.

## Data and Migrations

Database and persistence changes are high risk.

Before editing schemas or migrations:
- Inspect existing migration style and naming.
- Check whether migrations are forward-only, reversible, or environment-specific.
- Preserve existing data unless the user explicitly asks to delete or rewrite it.
- Plan rollout, backfill, and rollback when applicable.
- Verify both schema shape and application behavior.

Never silently change persisted formats, enum values, external IDs, timestamps, or units.

## Auth, Security, and Privacy

Security can justify necessary complexity.

Before changing auth, permissions, secrets, encryption, input validation, or data exposure:
- Identify the trust boundary.
- Identify who can call the path and what data they can reach.
- Preserve deny-by-default behavior when present.
- Avoid logging secrets, tokens, private user data, credentials, or full request bodies.
- Prefer centralized validation and authorization over scattered checks.
- Run or add focused tests for allowed and denied cases.

Do not weaken security for simpler code.

## Errors and Logging

Follow existing error and logging conventions.

Good errors:
- Tell the caller what failed.
- Preserve enough context for debugging.
- Avoid exposing secrets or internal-only details to users.
- Use existing error types, status codes, and response shapes.

Good logs:
- Help diagnose production failures.
- Include stable identifiers when safe.
- Avoid noisy success logs.
- Avoid sensitive data.

## API and Endpoint Checklist

Before editing an endpoint, command, event handler, or message consumer:
- Confirm request and response shape.
- Confirm validation behavior.
- Confirm auth and permission checks.
- Confirm idempotency requirements.
- Confirm retry behavior and duplicate handling.
- Confirm error shape and status code.
- Confirm tests cover success and failure paths.

## UI Boundary Checklist

Before editing UI structure or state:
- Check existing component boundaries and naming.
- Keep rendering, state derivation, and side effects separated where the repo already does so.
- Preserve accessibility behavior, loading states, empty states, and error states.
- Avoid broad restyling unless requested.
- Verify the user-visible path manually or with the narrowest useful test.

## Architecture Decision Trigger

Create or update an ADR when the change:
- Chooses between real architectural alternatives.
- Introduces a new dependency, service boundary, persistence model, or public API pattern.
- Changes how future work should be done.
- Rejects a plausible option that another developer may ask about later.

Use `docs/agent/adr-template.md`.

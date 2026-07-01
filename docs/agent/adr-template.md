# ADR Template

Use an Architecture Decision Record only for durable decisions. Do not create an ADR for every code change.

Create an ADR when:
- A decision was made between real alternatives.
- A non-obvious constraint or gotcha was discovered.
- A dependency, service boundary, public API, data model, deployment pattern, or security model changed.
- Future contributors need to know why this path was chosen.

Do not create an ADR when:
- The code already explains the change.
- Git history is enough.
- The change is local, obvious, or temporary.
- The doc would only say that a function or file was added.

## File Location

Store ADRs in `docs/adr/`.

Use numbered filenames:

```text
0001-short-decision-title.md
0002-another-decision.md
```

Use lowercase words separated by hyphens.

## Template

```md
# ADR 0001: Short Decision Title

Date: YYYY-MM-DD
Status: Proposed | Accepted | Superseded

## Context

What problem are we solving?

What constraints matter?

What existing behavior, architecture, or external contract affects the decision?

## Decision

What are we choosing?

State the decision directly. Include the scope of the decision and what it does not cover.

## Alternatives Considered

### Option A: Name

Summary of the option.

Why it was rejected or accepted.

### Option B: Name

Summary of the option.

Why it was rejected or accepted.

## Consequences

What does this make easier?

What does this make harder?

What future work does this rule in or rule out?

## Verification

What checks, tests, or review steps confirm the decision works?

What remains uncertain?
```

## Updating ADRs

Do not rewrite accepted ADRs to hide history. If a decision changes:
- Mark the old ADR as superseded.
- Create a new ADR.
- Link the new ADR from the old one.

## Minimal ADR Example

```md
# ADR 0001: Store Agent Rules in AGENTS.md

Date: 2026-06-29
Status: Accepted

## Context

Codex reads project instructions from `AGENTS.md`. The repo needs durable agent guidance without making the root file too large.

## Decision

Keep core instructions in root `AGENTS.md`. Put longer task-specific guidance under `docs/agent/` and reference those files from an optional reading section.

## Alternatives Considered

### Keep all rules in AGENTS.md

Rejected because long instruction files are harder for agents to follow and harder for humans to maintain.

### Keep rules only in separate docs

Rejected because Codex needs the core rules loaded before work starts.

## Consequences

The root file stays small. Agents can read more detail when a task requires it. The optional files must stay discoverable and current.

## Verification

Check root `AGENTS.md` line count after edits. Confirm optional docs exist at the referenced paths.
```

---
name: long-task-workflow
description: Use for large multi-file coding tasks, staged implementations, refactors, migrations, or work that needs a checklist, phased verification, handoff notes, or a structured final report.
---

# Long Task Workflow Skill

Use this skill for large multi-file tasks, staged implementations, refactors, migrations, or work that may span more than one session.

The goal is to maintain enough structure to avoid losing the thread without creating busywork.

Read first:

- `AGENTS.md` for repository-specific commands, safety, scope, verification, and communication guidance.

## When to Use This

Use this workflow when:

- The task touches several files or layers.
- The work has multiple phases.
- There are real tradeoffs or unknowns.
- Verification needs more than one command or manual check.
- A partial implementation would be risky or confusing.

For small changes, follow root `AGENTS.md` and skip this skill.

## Starting Checklist

Before coding, produce a short plan with:

- Goal.
- Assumptions.
- Non-goals.
- Expected files or areas touched.
- Risks.
- Verification plan.

Then create an in-chat checklist. Do not create a repo checklist file unless the user asks.

Example:

```md
Plan:
1. Inspect current flow and tests. Verify by identifying the owner module.
2. Add the smallest behavior change. Verify with focused tests.
3. Update affected tests. Verify targeted test command.
4. Run final check. Verify lint or build if relevant.

Checklist:
- [ ] Inspect current flow
- [ ] Implement focused change
- [ ] Add or update tests
- [ ] Run verification
- [ ] Simplification pass
- [ ] Final report
```

## During Work

After each meaningful phase:

- Mark completed checklist items.
- State what changed.
- State what was verified.
- State what remains.
- State any new risk, blocker, or scope change.

Do not continue from a state you cannot describe.

If the plan becomes wrong:

- Stop.
- Explain what changed.
- Recommend the new path.
- Ask only if the new path changes scope, risk, architecture, data, or public behavior.

## Context Notes

Do not maintain a persistent `context-notes.md` by default. Use one only when the user asks or when the repo already uses that pattern.

Prefer durable records in this order:

1. Code and tests.
2. Completion report.
3. Commit message.
4. ADR for architectural decisions.
5. Task checklist file only when requested.

## Handoff Notes

When stopping before completion, report:

- Current state.
- Completed items.
- Files changed.
- Verification already run.
- Known failures.
- Next safest step.

Use this format:

```md
Current state:
- Done:
- Changed files:
- Verified:
- Not verified:
- Known issues:
- Next step:
```

## Final Report

End large tasks with:

- Summary of behavior changed.
- Files changed.
- Tests or checks run.
- Simplification pass result.
- Skipped verification or remaining risk.
- Follow-up steps, if any.
- Suggested commit message when useful.

When the user asks for a commit-style report, or when the final report should be based directly on the current repository diff, use the `$commit-report` skill if available. Base the suggested commit message only on the verified diff. Do not stage, unstage, commit, amend, reset, or push unless the user explicitly asks.

Suggested compact final report format when a full commit-style report is not needed:

```text
Summary:
- <behavior or outcome changed>

Files changed:
- `<path-or-file-name>`

Verification:
- `<command>` - passed, failed, or not run.

Simplification pass:
- <what was simplified, or no meaningful simplification needed>

Follow-up steps:
- <guidance for anything the user should do next, or none>

Risks:
- <remaining risk, skipped verification, or none>

Suggested commit message:
<type>: <short summary>
```

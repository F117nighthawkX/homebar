---
name: commit-report
description: Use when asked to inspect current repository changes, summarize a diff, draft a Conventional Commit message, or produce a commit-style completion or final report. Do not implement code, stage, unstage, commit, amend, reset, or push.
---

# Commit Report Skill

Use this skill to summarize the current repository changes and produce a suggested commit message without implementing new code.

You are working in the current repository.

Read first:

- `AGENTS.md` for repository-specific safety, verification, scope, and communication guidance.

This skill is the source of truth for commit-message rules, report structure, and commit-style completion reports. If `AGENTS.md` contains different commit or report formatting rules, follow this skill unless the user explicitly says otherwise.

Then inspect the current repository changes.

Use commands such as:

```bash
git status --short --branch
git diff --stat
git diff
git diff --cached --stat
git diff --cached
git diff HEAD --stat
git diff HEAD
git diff --check
```

On Windows, use equivalent commands.

## Task

Generate a commit-style completion report based only on the current repository changes.

Do not invent changes that are not present.

Do not stage, unstage, commit, amend, reset, or push unless explicitly asked.

If staged changes exist, inspect both staged and unstaged changes. Report whether the suggested commit message is based on staged changes, unstaged changes, or all working tree changes.

## Rules

The commit-message and report rules in this skill are authoritative for this task. Also follow relevant `AGENTS.md` guidance for surgical changes, uncertainty, verification, and prohibited actions.

Use the 50/72 rule:

- Commit subject should be 50 characters or fewer when practical.
- Wrap body lines at 72 characters when practical.
- Keep the subject short. Put planning details in the body.

Use imperative mood in the subject:

```text
Good: feat: add session refresh
Bad:  feat: added session refresh
```

Do not end the subject with a period.

Use Conventional Commit style when it fits:

```text
feat: add session refresh
fix: correct timeout handling
test: cover invalid token path
docs: update agent instructions
refactor: split parsing logic
chore: update generated assets
```

Use optional scope when it improves clarity:

```text
feat(auth): add session refresh
fix(api): handle empty response body
```

Use `!` or a `BREAKING CHANGE:` footer only when the diff clearly contains a breaking change.

List file names or repository-relative paths only in backticks. Do not include absolute local paths.

Good:

```text
- `src/auth/session.ts`
- `tests/session.test.ts`
- `docs/agent/commit-report-prompt.md`
```

Bad:

```text
- `C:\Users\Name\Documents\project\src\auth\session.ts`
```

If the diff maps clearly to an issue, ticket, bucket, epic, milestone, task file, or planning document, include it.

If it does not, write:

```text
- Planning reference: none
```

Use `Follow-up steps` only for work that remains outside the verified diff: manual checks, deployment notes, generated-file refreshes, environment setup, review reminders, or commands that should be run later. Do not use it to repeat implemented changes.

## Report Format

Use this format:

```text
Suggested commit message:

<type>(<optional scope>): <short subject under 50 chars when practical>

<optional body wrapped at 72 characters>

- Planning reference: `<issue/ticket/bucket/epic/task>` or none

Diff basis:

- <staged changes, unstaged changes, or all working tree changes>

Files created:

- `<path-or-file-name>`

Files changed:

- `<path-or-file-name>`

Files deleted:

- `<path-or-file-name>`

Implemented:

- <specific change from the diff>
- <specific change from the diff>

Checks run:

- `git diff --check` - passed or failed.
- <other checks run, if any>

Follow-up steps:

- <guidance for anything the user should do next, or none>

Assumptions:

- <assumption, or none>
```

If a section has no entries, write a short explicit line instead of omitting it.

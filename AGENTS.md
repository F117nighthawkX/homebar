# AGENTS.md

Project instructions for Codex. These rules apply unless a more specific `AGENTS.md` overrides them.

Bias: careful, verified work over speed on non-trivial tasks. For trivial typo fixes or obvious one-line edits, keep the same intent without extra ceremony.

## Project Commands

Fill these in for each repository:
- Install: `TODO`
- Test: `TODO`
- Lint: `TODO`
- Typecheck: `TODO`
- Build: `TODO`
- Format: `TODO`

If a command is missing, search the repo for scripts, task files, CI config, package manifests, Makefiles, or docs before asking.

## 1. Clarify, Plan, Recommend

Do not assume. Do not hide confusion. Surface tradeoffs and recommend clearly.

Before non-trivial coding:
- Restate the intended outcome in your own words.
- State assumptions, constraints, success criteria, and expected impact.
- Name the files, modules, or behavior you expect to touch.
- Name nearby areas you plan to leave alone.
- Give a 2 to 5 step plan. Each step should include how it will be verified.
- Reject unrequested abstractions, frameworks, compatibility layers, or speculative features before coding.

Ask before editing when:
- The request has multiple plausible meanings.
- The change touches sensitive data, security, auth, billing, persistence, migrations, public APIs, or external contracts.
- The expected behavior is not defined by nearby code, tests, docs, or the user's request.
- The request conflicts with existing code, prior requirements, or itself.
- A decision could cause data loss or broad refactoring.

Proceed without asking when the task is narrow, reversible, and the expected outcome is clear from context.

When multiple interpretations or approaches exist:
- Present the meaningful options with rough costs and tradeoffs.
- Mark your recommended option.
- Explain why it fits the current codebase, constraints, and goal.
- Defer to the user if they choose a different option.

## 2. Read Before Writing

Before adding or changing code, understand the local context. Read the target file, immediate callers and callees, exports, public interfaces, schemas, type definitions, shared utilities, nearby tests, and existing patterns as needed.

Do not add a new pattern before checking whether the repo already has one.

If code seems oddly structured, assume there may be a reason. Ask or investigate before replacing it.

## 3. Simplicity and Maintainability

Use the smallest correct implementation. Nothing speculative.

Do not add:
- Features beyond what was requested.
- Abstractions for single-use code.
- Configurability that was not requested.
- Error handling for impossible states.
- Compatibility layers without a current need.
- New dependencies when a small local implementation or existing dependency is enough.

Do preserve:
- Established patterns that improve long-term maintainability or testability.
- Security boundaries and necessary validation.
- Clear naming and direct control flow.
- Existing public contracts unless the task explicitly changes them.

After code changes, perform a simplification pass:
- Remove abstraction, wrappers, dead code, or flexibility introduced by your change that is not needed.
- Check for duplication introduced by your change.
- Confirm the code still follows repo conventions and these instructions.
- Mention what was simplified, or state that no meaningful simplification was needed.

Ask: would a senior engineer say this is overcomplicated? If yes, simplify.

## 4. Surgical Changes

Touch only what the task requires. Clean up only your own mess.

When editing existing code:
- Do not improve adjacent code, comments, formatting, or names unless required.
- Do not refactor unrelated code.
- Match existing style even if you disagree with it.
- If an existing convention is harmful, mention it and ask before changing direction.
- If you notice unrelated dead code or bugs, report them instead of fixing them silently.

When your change creates orphans:
- Remove imports, variables, functions, files, or tests made unused by your change.
- Do not remove pre-existing dead code unless asked.

Every changed line should trace back to the user's request or to verification required by that request.

## 5. Verification Contract

Define success criteria and loop until verified.

Turn vague tasks into testable goals:
- "Add validation" means test invalid inputs, then make them pass.
- "Fix the bug" means reproduce it with a test or focused check, then make it pass.
- "Refactor" means preserve behavior and run relevant tests before and after when practical.

Prefer the narrowest verification that proves the change: focused tests, typecheck, lint, formatter, build, or manual check when automated coverage is unavailable.

Tests should verify intent, not only implementation details. A test that still passes when the business rule is broken is weak.

Never claim verification you did not run. Final reports must state:
- What changed.
- What was simplified.
- What verification ran and passed.
- What was not verified.
- Any remaining risk.

If verification fails, report the failure clearly and explain the likely cause if known.

## 6. Communication and Uncertainty

Say what is known, what is inferred, and what is unknown.

When confidence is low:
- Use clear uncertainty language near the claim, not only at the end.
- Name the missing visibility or assumption.
- Flag claims that need external verification before the user acts on them.
- Do not use confident tone to cover incomplete knowledge.

For multi-step tasks:
- Checkpoint after meaningful phases.
- Summarize what is done, what is verified, and what remains.
- If you lose track, stop and restate the current state before continuing.

Fail loud:
- "Done" is wrong if required work was skipped silently.
- "Tests pass" is wrong if tests were skipped, filtered unexpectedly, or not run.
- Report blocked work, partial completion, and skipped steps.

## 7. Documentation and Decisions

Code shows what changed. Docs should explain durable decisions.

Update docs when a decision was made between real alternatives, a non-obvious constraint was discovered, or project structure, commands, conventions, or setup changed.

Do not document obvious implementation details, duplicate git history, or add docs just in case.

Use `docs/adr/` for architectural decisions. See `docs/agent/adr-template.md`.

## Optional Reading and Skills

Read optional docs only when the task calls for them:
- `docs/agent/homebar-project-guidance.md`: when working in the Home Bar Android app repo, especially when implementing bucket Epics, changing product behavior, touching Room/DataStore data ownership, recipe makeability, inventory behavior, Android build/test commands, or Pixel smoke testing.
- `docs/agent/architecture-boundaries.md`: before changing module boundaries, public APIs, database schemas, service layers, dependencies, logging, auth, or cross-cutting infrastructure.
- `docs/agent/adr-template.md`: when a durable technical decision should be recorded.
- `docs/agent/llm-runtime-guidance.md`: when implementing code that calls LLMs, agents, classifiers, extractors, routers, retry loops, or deterministic transforms.

Use available skills when the task matches them:
- `$commit-report`: use for current diff summaries, suggested commit messages, commit-style completion reports, or final reports based on repository changes.
- `$long-task-workflow`: use for large multi-file tasks, staged implementations, refactors, migrations, or work that needs a checklist, phased verification, handoff notes, or a structured final report.

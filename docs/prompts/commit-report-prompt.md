# Commit-Style Report Prompt

Use this prompt when you want Codex to summarize the current diff and produce
a suggested commit message without implementing new code.

You are working in the Home Bar Android app repository.

Read first:

- `AGENTS.md`

Then inspect the current diff.

Use commands such as:

```bash
git status --short
git diff --stat
git diff
git diff --check
```

On Windows, use equivalent commands.

## Task

Generate a commit-style completion report based only on the current diff.

Do not invent changes that are not present.

Do not run `git commit` unless explicitly asked.

## Rules

Follow the commit message and report rules in `AGENTS.md`.

Use the 50/72 rule:

- Commit subject should be 50 characters or fewer when practical.
- Wrap body lines at 72 characters when practical.
- If the bucket or Epic name is long, keep the subject short and put bucket
  details in the body.

Use conventional commit style when it fits:

```text
feat: add recipe ingredient filter
fix: correct serving quantity scaling
test: cover substitute matching
docs: update bucket instructions
refactor: split recipe matching logic
```

List file names only in backticks. Do not include absolute paths.

Good:

```text
- `RecipeListViewModel.kt`
- `RecipeFilterLogicTest.kt`
```

Bad:

```text
- `C:\Users\Kevin\Documents\VS-Code-Projects\homebar\app\src\...`
```

If the diff maps clearly to a bucket or Epic, include it.

If it does not, write:

```text
- Bucket: none
- Epic: none
```

## Report Format

Use this format:

```text
Suggested commit message:

<type>: <short subject under 50 chars when practical>

<optional body wrapped at 72 characters>

- Bucket: `<bucket-file-name.md>` or none
- Epic: `<Epic name>` or none

Files created:

- `<FileName.kt>`

Files changed:

- `<FileName.kt>`

Implemented:

- <specific change from the diff>
- <specific change from the diff>

Checks run:

- `git diff --check` - passed or failed.
- <other checks run, if any>

Manual Android Studio steps: none.

Assumptions:

- <assumption, or none>
```

If a section has no entries, write a short explicit line instead of omitting it.

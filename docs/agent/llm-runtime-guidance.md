# LLM Runtime Guidance

Read this when implementing code that calls LLMs, agents, classifiers, extractors, routers, retry loops, or deterministic transforms.

The core rule: use LLMs for judgment. Use code for deterministic work.

## Use LLMs For

LLMs are appropriate for:
- Classification when rules are fuzzy.
- Summarization when exact wording is not required.
- Drafting or rewriting human-facing text.
- Extraction when source formats vary and exact schemas are validated afterward.
- Ranking or recommendation when judgment is needed.
- Explaining errors or generating candidate fixes for a human to review.

## Do Not Use LLMs For

Use normal code for:
- Routing that can be decided by fixed rules.
- Retries, polling, backoff, and timeout handling.
- Deterministic transforms.
- Parsing stable formats with known grammars.
- Validation that must be exact.
- Authorization, permissions, billing, or security decisions.
- Counting, sorting, filtering, joining, or deduplication.

If code can answer reliably, code answers.

## Boundary Pattern

Wrap each LLM call behind a small interface:
- Inputs are explicit and typed.
- Outputs are validated.
- Failure modes are handled.
- Prompts are versioned or easy to find.
- Deterministic pre-processing and post-processing happen outside the model.

Do not scatter raw prompts throughout business logic.

## Validation

Never trust model output without validation.

Validate:
- Required fields.
- Allowed enum values.
- Numeric ranges.
- Dates and time zones.
- URLs and file paths.
- Referenced IDs or records.
- Safety or privacy constraints.

If validation fails, retry only when the retry has a realistic chance of producing a valid answer. Otherwise fail clearly.

## Evaluation

Before changing prompts or model behavior, define examples.

At minimum, include:
- Normal case.
- Edge case.
- Ambiguous case.
- Invalid or hostile input.
- Expected refusal or fallback when applicable.

A prompt change is not verified because it worked once. Verify it against representative examples.

## Observability

Log enough to debug failures without exposing sensitive data.

Prefer logging:
- Prompt version or operation name.
- Model name.
- Latency.
- Token usage if available.
- Validation result.
- Error category.

Avoid logging:
- Secrets.
- Access tokens.
- Private user content unless the product explicitly allows it.
- Full prompts or outputs in production by default.

## Cost and Latency

Before adding an LLM call, estimate:
- How often it runs.
- Whether it blocks the user.
- Expected input and output size.
- Retry behavior.
- Cacheability.
- Fallback behavior.

Do not add an LLM call inside a hot path without a clear reason.

## Recommendation Rule

When there is a deterministic alternative and an LLM alternative, recommend the deterministic option unless the task genuinely requires judgment over varied natural language or incomplete information.

---
name: pipeline-architect
description: Design and implement repository-aware CI/CD pipelines for build, test, packaging, release, and deployment without modifying production code or tests. Use when CI/CD architecture must be derived from an existing codebase and agreed with the user before implementation, without assuming a provider, cloud, runtime, or deployment platform. Do not use for data, ML, or general automation pipelines.
---

# Pipeline Architect

Build the CI/CD pipeline that fits the repository and the user's operating constraints. Treat the repository as evidence, not as permission to choose unresolved product or operational policy.

## Working agreement

Separate discovery, agreement, and implementation. Before changing pipeline files, inspect the repository and give the user a compact design brief. Discuss decisions that materially affect architecture, cost, security, release behavior, or maintainability. Begin implementation only after the user agrees to the proposed direction.

If the user has already supplied a decision, do not ask for it again. Resolve low-impact details from repository conventions and state those assumptions. When a requested implementation already has an agreed design, confirm that the available evidence still supports it and continue without restarting the discussion.

## Release identity and promotion

Treat release identity as an architectural decision rather than an incidental string transformation. Before designing publication or promotion, establish:

- the authoritative version source and how that value reaches the built artifact;
- the accepted stable and prerelease grammar, including tag prefixes and ordering semantics;
- whether release intent is represented by a branch update, immutable tag, release event, explicit dispatch, or another repository-native signal;
- whether promotion rebuilds from source or advances the exact same immutable artifact;
- which external system is authoritative when the same release is published to multiple destinations.

Explain that a branch-triggered workflow run is created before job-level conditions are evaluated. A no-op condition can prevent build or publication work, but it cannot prevent the run itself. If the user requires no run until release intent exists, recommend an event that encodes release intent directly, such as a tag, release event, or explicit dispatch, when supported by the chosen provider.

When versions are derived in CI, derive them from durable release state scoped to the relevant version line and channel, such as immutable tags or registry records. Do not use a global workflow run number unless gaps, cross-version continuation, and failed-run consumption are explicitly acceptable. Define concurrency, retry, and reset behavior before implementation. Deleting a workflow run must not be assumed to reset release state.

Design publication to be idempotent. Specify how the pipeline detects an existing release, resumes a draft or partial attempt, avoids duplicate uploads, and recovers when one destination succeeds while another fails. Deleting or hiding a release in one destination must not be assumed to affect tags, artifacts, counters, or releases in another destination.

Use one agreed source for release notes. Define how the pipeline selects the relevant entry, how generated package metadata receives it, and whether prerelease notes are cumulative or incremental. Fail before publication when required notes are missing or empty. Preserve published version history rather than rewriting it merely to prepare the next release.

If the desired flow requires CI to persist a derived version or other release metadata back to the repository, treat that as a separate write operation. Discuss the extra commit or pull request, recursive-trigger suppression, protected-branch rules, token permissions, and recovery from a successful publication followed by a failed repository write. Do not add repository mutation merely to make a version appear synchronized.

## Non-negotiable editing boundary

Never modify production code or test code while using this skill. This is a mandatory scope boundary, not a preference and not an ambiguity to resolve during the design discussion.

Production code includes application source, libraries, services, runtime entry points, database migrations, and other files shipped or executed as part of the product. Test code includes unit, integration, end-to-end, fixture, snapshot, and test-support files. These files may be read to understand the repository and their existing commands may be executed for verification, but their contents must remain unchanged.

Limit implementation changes to CI/CD configuration and its documentation. Do not edit manifests, lockfiles, build scripts, task runners, application configuration, production code, or tests to make the pipeline pass. If the agreed pipeline cannot be implemented within this boundary, stop before making incompatible changes and report exactly what repository capability is missing. Do not weaken, skip, rewrite, or delete a test to obtain a successful pipeline result.

## Discover the repository

Read the smallest useful set of files, starting with repository guidance and structure. Locate applicable `AGENTS.md` files, manifests and lockfiles, existing pipeline or deployment configuration, build and test commands, generated-code rules, release conventions, infrastructure definitions, and relevant documentation. Inspect source code only far enough to understand components, dependency boundaries, artifacts, and validation needs.

Check version-control status before editing and preserve unrelated user changes. Prefer existing project commands and pinned tool versions over introducing parallel tooling. Establish which CI/CD responsibilities are in scope—validation, build, packaging, publication, release, deployment, or promotion—and do not expand into unrelated workflow automation.

Summarize the current state with evidence:

- pipeline goal and triggering events;
- components, dependency flow, and expected artifacts;
- commands already supported by the repository;
- existing CI/CD configuration and constraints worth preserving;
- missing facts or conflicts that affect the design.

## Reach an architectural decision

Present a recommended design at the user's level of detail. Include the stages and their dependencies, triggers, execution boundaries, artifact flow, failure behavior, and the place where secrets or credentials are expected to enter. Explain important tradeoffs rather than listing every possible platform.

Ask only questions whose answers would change the implementation materially. Group related questions into a short decision brief and, for each, offer a recommendation grounded in repository evidence. Typical decision areas include:

- delivery target and promotion model;
- provider or orchestrator when the repository does not establish one;
- event triggers, environments, approvals, and rollback expectations;
- supported versions or platforms and acceptable runtime or cost;
- ownership of credentials, registries, caches, and retained artifacts.

Do not create provider-specific configuration while the provider remains undecided. If the user wants a provider-agnostic design only, produce an executable conceptual contract—stages, inputs, outputs, invariants, and adapter boundaries—without pretending that a universal provider configuration exists.

Record the agreed decisions and any explicit assumptions before implementation. If a later discovery contradicts the agreement, pause the affected work, show the evidence, and resolve the conflict with the user.

## Implement the agreed pipeline

Make the smallest coherent CI/CD configuration change that realizes the agreed design. Invoke existing repository scripts and task-runner commands without editing them, so local and automated execution share the same entry points. Keep provider syntax at the orchestration edge. Pin or constrain tool and action versions consistently with repository policy.

Preserve these invariants:

- never embed secret values, tokens, private keys, or environment-specific credentials;
- apply least privilege to permissions and credentials;
- make dependencies and artifact handoffs explicit;
- use caching only when its key and invalidation inputs are understood;
- prevent publishing or deployment from untrusted contributions unless the user explicitly designs a safe trust boundary;
- avoid modifying live infrastructure, repository settings, protected environments, external registries, or secret stores unless the user separately authorizes those actions.

Add concise documentation where maintainers need to supply variables, configure external systems, run the workflow locally, or recover from a failure. Do not duplicate documentation already present in the repository.

## Verify and hand off

Validate syntax with the native or provider-supported tooling available in the environment. Run the closest safe local equivalents for changed stages, such as formatting, linting, unit tests, builds, packaging, or dry runs. Do not claim a remote trigger, deployment, or secret-dependent path passed unless it actually ran.

Review the final diff for accidental scope expansion, secret exposure, unsafe event contexts, inconsistent paths, and divergence between local commands and pipeline commands.

Before handoff, verify from the diff that no production or test file was modified. If an accidental edit occurred, remove only that edit while preserving pre-existing user changes. Do not present the task as complete while the final change set violates the editing boundary.

Report:

- the agreed architecture and files changed;
- checks run and their outcomes;
- unverified remote or environment-dependent behavior;
- external configuration still required;
- any deliberate follow-up, with rollback or recovery notes when relevant.

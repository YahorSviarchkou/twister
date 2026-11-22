# 0005. Merge repair task into the order: two state machines

- **Status:** Proposed
- **Date:** 2026-11-21
- **Deciders:** project owner (pending)

## Context

The domain has three state machines: `RepairOrder`, `RepairTask` and `RepairTaskItem`. A task is always one-to-one with its order. The orchestrator keeps them in sync with cascades, and three of the five cascades fail on real data (see [codebase analysis](../notes/2026-11-15-codebase-analysis.md)). The UI design had to split the "Accepted" order column by task status, which shows that order and task are one process.

## Decision

We will remove `RepairTask` as a separate stateful entity and keep two state machines:

- **Order:** Accepted → Diagnosis → Approval → In progress ⇄ Waiting for parts → Ready → Handed over; Cancelled from any state before Handed over; Diagnosis → Ready for "no repair needed" with a diagnosis-only invoice.
- **Work item:** Created → (Waiting for parts) → In progress → Done; Cancelled.

The order becomes Ready when all non-cancelled work items are Done. See the [order state diagram](../diagrams/state-order.mmd) and [work item state diagram](../diagrams/state-work-item.mmd).

## Vertical boundary

Twister may later serve other kinds of repair shops (cars, electronics). We do not build for that now; we only keep one boundary so that it stays possible:

- Bike-specific concepts (the bike and its attributes, the intake checklist, seasonal reminders) live in one package, `...domain.bike`.
- Order, work item, state machine, stock and billing code, including their use cases, must not import that package. Bike use cases (register a bike, service book) live in their own `bike` packages, which may depend on the order. One ArchUnit test enforces this.
- The order refers to the repaired item only by id (`SubjectId`), never by type.
- No new modules, no generic `Asset` abstraction, no custom fields or vertical-specific statuses until a second vertical is actually needed. When it is, the `bike` package is extracted into a module and the extension points are designed from the real requirements.

## Consequences

- Cascades 1–4 disappear; only "all items done → order ready" stays, computed from the latest status of each item.
- New concepts: estimate approval (who, how, threshold), spare quantity per work item, diagnostic report.
- Persistence: drop `repair_tasks` and its status history; move items to `order_id`.

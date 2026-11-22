# 0005. Merge repair task into the order: two state machines

- **Status:** Accepted
- **Date:** 2026-11-21
- **Deciders:** project owner (accepted 2026-11-22)

## Context

The domain has three state machines: `RepairOrder`, `RepairTask` and `RepairTaskItem`. A task is always one-to-one with its order. The orchestrator keeps them in sync with cascades, and three of the five cascades fail on real data (see [codebase analysis](../notes/2026-11-15-codebase-analysis.md)). The UI design had to split the "Accepted" order column by task status, which shows that order and task are one process.

## Decision

We will remove `RepairTask` as a separate stateful entity and keep two state machines:

- **Order:** Accepted → Diagnosis → Approval → In progress ⇄ Waiting for parts → Ready → Handed over; Cancelled from any state before Handed over; Diagnosis → Ready for "no repair needed" with a diagnosis-only invoice.
- **Work item:** Created → (Waiting for parts) → In progress → Done; Cancelled.

The order becomes Ready when all non-cancelled work items are Done. See the [order state diagram](../diagrams/state-order.mmd) and [work item state diagram](../diagrams/state-work-item.mmd).

## Vertical boundary

The current vertical is personal light vehicles: workshops repair bikes, kick scooters and e-scooters. Twister may later serve other kinds of repair shops (cars, electronics). We do not build for that now; we only keep one boundary so that it stays possible:

- Vehicle-specific concepts (the vehicle and its attributes, the intake checklist, seasonal reminders) live in one package, `...domain.vehicle`. The vehicle type (bike, kick scooter, e-scooter) is reference data, like `TransportType` today: a new type is a record, not new code.
- Order, work item, state machine, stock and billing code, including their use cases, must not import that package. Vehicle use cases (register a vehicle, service book) live in their own `vehicle` packages, which may depend on the order. One ArchUnit test enforces this.
- The order refers to the repaired vehicle only by id (`SubjectId`), never by type.
- No new modules, no generic `Asset` abstraction, no custom fields or vertical-specific statuses until a different vertical is actually needed. When it is, the `vehicle` package is extracted into a module and the extension points are designed from the real requirements.

## Consequences

- Cascades 1–4 disappear; only "all items done → order ready" stays, computed from the latest status of each item.
- New concepts: estimate approval (who, how, threshold), spare quantity per work item, diagnostic report.
- Persistence: drop `repair_tasks` and its status history; move items to `order_id`.

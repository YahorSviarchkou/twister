# Product rethink and phases

Status: **proposal**, waiting for answers to the open questions below. Discussed on 2026-11-15; the original whiteboard is in [artifacts](../artifacts/2026-11-15-rethink-board/board.json) (in Russian).

## The main fork

Is Twister a tool for one real workshop, or a product for many workshops?

- **One workshop:** no plugins at runtime, no sync, no multi-tenant concerns. What matters is that it is pleasant to use during the spring rush.
- **A product:** multiple workshops, online access, onboarding, integrations.

**Recommendation:** start with one real workshop and keep the path to a product open. Today the project is built as if it were already a product (plugins, web sync) while the basic repair flow does not work yet.

## What to rethink

1. **The bike is the centre, not the order.** A bike has a service book: frame number, components, every job and part fitted. This enables warranty on work, seasonal maintenance reminders, and conversations such as "we replaced the chain last year, the cassette is worn now".
2. **Two state machines instead of three.** `RepairTask` is one-to-one with the order and adds no value; the UI design already had to split the "Accepted" column by task status. Proposed: an order machine and a work-item machine. See [ADR 0005](../adr/0005-merge-task-into-order.md).
3. **Estimate approval.** The most common conflict in a workshop is "I did not ask you to replace the cassette". Needs its own status, a record of who approved and how, and a "no call needed below N" threshold.
4. **The mechanic does not sit at a PC.** Intake happens at the front desk; the mechanic works at the stand with dirty hands and needs a tablet mode: my jobs, start, done, add part, take a photo.
5. **The physical world.** A tag on the bike with a number and a QR code, an intake receipt signed by the customer, photos of the bike's condition at intake, handover by number.
6. **Stock close to real work.** Reserve parts for an order, link "waiting for parts" to a supplier order, reorder points, counter sales without a repair.
7. **Money.** Prepayment and partial payment, piece-rate pay for mechanics, and a fiscal register if the product is sold in Russia (54-FZ is a legal requirement, not a feature).
8. **Talking to the customer.** "Your bike is ready" notifications, a status page behind the QR code, estimate approval by link. Telegram is the cheapest channel.

## Phases

| Phase | Goal | Content | Done when |
|---|---|---|---|
| 0. Decisions and cleanup | A green build and recorded decisions | Commit and push, CI on Java 21, decide positioning, UI technology and database, simplify the domain | Build is green, ADRs written |
| 1. MVP: accept, repair, hand over | Replace paper and Excel | Customers and bikes, intake with photos, receipt and tag, estimate and approval, jobs and parts with quantities, payment, handover, stock, backup | A real workshop runs a season in Twister |
| 2. Mechanic and customer | Remove routine calls | Tablet mode, assignment and queue, notifications, status by QR, approval by link | The front desk stops calling customers for routine updates |
| 3. Stock and money | Control margins | Suppliers and reorders, counter sales, prepayment, fiscal register, mechanic pay, reports | Revenue, workload and margin are visible |
| 4. Retention | Customers come back | Seasonal reminders from the service book, warranty, maintenance packages, online booking | Returning customers are tracked |
| 5. Product (optional) | Many workshops | Multi-tenant, roles, import, integrations, runtime plugins | Decided after phase 3 |

## What to keep from the current code

- **Keep:** the generic state-machine engine with status history, the JPA entities and specifications, the design system.
- **Rebuild:** the three-level status model; DAO ports without Spring Data types.
- **Postpone or remove:** web sync between two databases, the JavaFX-bound plugin API, legacy `src/` once its useful parts are moved.

## Open questions

1. Is there a real workshop where this will be used?
2. Personal tool or product for sale? In which country (affects the fiscal register)?
3. What does the mechanic use at the stand: tablet, phone, or nothing?
4. Desktop: standalone with its own database, thin client to a server, or both?
5. Web UI: server-rendered (htmx, Vaadin) or a SPA?
6. Who writes plugins: only the owner, or third parties?

## Related

- [Extension architecture](2026-11-15-extension-architecture.md)
- [Codebase analysis](2026-11-15-codebase-analysis.md)

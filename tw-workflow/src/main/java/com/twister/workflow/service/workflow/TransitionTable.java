package com.twister.workflow.service.workflow;

import com.twister.domain.repair.RepairOrderStatus;
import com.twister.domain.repair.RepairTaskItemStatus;
import com.twister.domain.repair.RepairTaskStatus;
import com.twister.workflow.service.workflow.event.RepairOrderEvent;
import com.twister.workflow.service.workflow.event.RepairTaskEvent;
import com.twister.workflow.service.workflow.event.RepairTaskItemEvent;

import java.util.List;


public final class TransitionTable {

    public static final List<Transition<RepairOrderStatus, RepairOrderEvent>> REPAIR_ORDER_TRANSITIONS =
        List.of(
            // positive
            new Transition<>(
                RepairOrderStatus.OPEN,
                RepairOrderEvent.ACCEPT_ORDER,
                RepairOrderStatus.ACCEPTED
            ),
            new Transition<>(
                RepairOrderStatus.ACCEPTED,
                RepairOrderEvent.GENERATE_INVOICE,
                RepairOrderStatus.WAITING_PAYMENT
            ),
            new Transition<>(
                RepairOrderStatus.WAITING_PAYMENT,
                RepairOrderEvent.PAY,
                RepairOrderStatus.CLOSED
            ),
            // negative
            new Transition<>(
                RepairOrderStatus.OPEN,
                RepairOrderEvent.REJECT_ORDER,
                RepairOrderStatus.REJECTED
            ),
            new Transition<>(
                RepairOrderStatus.ACCEPTED,
                RepairOrderEvent.REJECT_ORDER,
                RepairOrderStatus.REJECTED
            )
        );

    public static final List<Transition<RepairTaskStatus, RepairTaskEvent>> REPAIR_TASK_TRANSITIONS =
        List.of(
            // positive
            new Transition<>(
                RepairTaskStatus.CREATED,
                RepairTaskEvent.START_DIAGNOSIS,
                RepairTaskStatus.DIAGNOSIS
            ),
            new Transition<>(
                RepairTaskStatus.DIAGNOSIS,
                RepairTaskEvent.START_REPAIR,
                RepairTaskStatus.IN_PROGRESS
            ),
            new Transition<>(
                RepairTaskStatus.IN_PROGRESS,
                RepairTaskEvent.FINISH_TASK,
                RepairTaskStatus.READY
            ),
            // negative
            new Transition<>(
                RepairTaskStatus.CREATED,
                RepairTaskEvent.CANCEL,
                RepairTaskStatus.CANCELED
            ),
            new Transition<>(
                RepairTaskStatus.DIAGNOSIS,
                RepairTaskEvent.CANCEL,
                RepairTaskStatus.CANCELED
            ),
            new Transition<>(
                RepairTaskStatus.IN_PROGRESS,
                RepairTaskEvent.CANCEL,
                RepairTaskStatus.CANCELED
            )
        );

    public static final List<Transition<RepairTaskItemStatus, RepairTaskItemEvent>> REPAIR_TASK_ITEM_TRANSITIONS =
        List.of(
            // positive
            new Transition<>(
                RepairTaskItemStatus.CREATED,
                RepairTaskItemEvent.WAIT_PARTS,
                RepairTaskItemStatus.WAITING_PARTS
            ),
            new Transition<>(
                RepairTaskItemStatus.WAITING_PARTS,
                RepairTaskItemEvent.PARTS_AVAILABLE,
                RepairTaskItemStatus.IN_PROGRESS
            ),
            new Transition<>(
                RepairTaskItemStatus.CREATED,
                RepairTaskItemEvent.START_WORK,
                RepairTaskItemStatus.IN_PROGRESS
            ),
            new Transition<>(
                RepairTaskItemStatus.IN_PROGRESS,
                RepairTaskItemEvent.FINISH_WORK,
                RepairTaskItemStatus.READY
            ),
            // negative
            new Transition<>(
                RepairTaskItemStatus.CREATED,
                RepairTaskItemEvent.CANCEL,
                RepairTaskItemStatus.CANCELED
            ),
            new Transition<>(
                RepairTaskItemStatus.WAITING_PARTS,
                RepairTaskItemEvent.CANCEL,
                RepairTaskItemStatus.CANCELED
            ),
            new Transition<>(
                RepairTaskItemStatus.IN_PROGRESS,
                RepairTaskItemEvent.CANCEL,
                RepairTaskItemStatus.CANCELED
            )
        );
}

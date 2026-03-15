RepairOrder -> 
    RepairTask(CREATED) -> 
        RepairTask(DIAGNOSTIS) ->
            [
                RepairTaskItem(CREATED) ->
                    attachRequiredSpares() ->
                        requiredSpares.isExists()
                            ? RepairTaskItem(READY_TO_EXECUTION)
                            : RepairTaskItem(WAIT_SPARES)
            ]    
        buildDiagnosticRepost()
        needRepair
            ? RepairTask(IN_PROGRESS) +  [RepairTaskItem] -> execution*
            : return RepairInvoice

execution* ->
    [
        RepairTaskItem.status == WAIT_SPARES
            ? wait -> {repeat check} 
            : RepairTaskItem(IN_PROGRESS) ->
                RepairTaskItem(DONE)
    ] ->
        allTaskItemsIsDone
            ? RepairTask(READY) -> invoice creating*
            : wait -> {repeat check} 

invoice creating* ->
    RepairInvoice
    RepairTask(WAIT_PAYMENT) ->
        paid
            ? RepairTask(CLOSED)
            : wait -> {repeat check} 



UI
?
RepairTaskItemEvent.FINISH_WORK
?
StateMachine
?
RepairTaskItemStatus.READY
?
RepairTaskItemStatusReadyPolicy
?
Domain Event
?
TaskItemFinishedEvent
?
TaskWorkflow
?
Task READY
?
TaskFinishedEvent
?
OrderWorkflow
?
Order WAITING_PAYMENT

package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Operation;
import com.twister.service.OperationService;
import com.twister.ui.dialog.DefaultEditDialog;
import com.twister.ui.dialog.DialogMode;
import com.twister.ui.dialog.OperationEditDialog;
import com.twister.util.UiUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@FXMLController(fxml = DefaultTabScreen.FXML_PATH)
@RequiredArgsConstructor
public class OperationsTabScreen extends DefaultTabScreen<Operation> {

    private final OperationService operationService;
    private final OperationEditDialog operationEditDialog;

    @Override
    protected String getTabName() {
        return i18nUtils.get("operation.tab.title");
    }

    @Override
    protected Class<Operation> getTableModelClass() {
        return Operation.class;
    }

    @Override
    protected DefaultEditDialog<Operation> getDialog() {
        return operationEditDialog;
    }

    @Override
    protected Supplier<List<Operation>> getTableEntitySupplier() {
        return () -> operationService.findAllOperations(0, 100, Sort.unsorted());
    }

    @Override
    protected void onUpdateEvent(Operation obj, ActionEvent event) {
        operationEditDialog.setDialogMode(DialogMode.UPDATE);
        operationEditDialog.setEditModel(obj);
        screenManager.showDialogAndWait(operationEditDialog.getClass());
        refreshTable();
    }

    @Override
    protected void onDeleteEvent(Operation obj, ActionEvent event) {
        if (obj != null) {
            UiUtils.showWarningDialog(
                    i18nUtils.get("operation.delete.title"),
                    i18nUtils.get("operation.delete.message"),
                    buttonType -> {
                        if (buttonType == ButtonType.OK) {
                            operationService.deleteOperation(obj.getId());
                            refreshTable();
                        }
                    }
            );
        }
    }
}

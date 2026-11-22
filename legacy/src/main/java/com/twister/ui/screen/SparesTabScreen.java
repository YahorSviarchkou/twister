package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Spare;
import com.twister.service.SpareService;
import com.twister.ui.dialog.DefaultEditDialog;
import com.twister.ui.dialog.DialogMode;
import com.twister.ui.dialog.SpareEditDialog;
import com.twister.util.UiUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@FXMLController(fxml = DefaultTabScreen.FXML_PATH)
public class SparesTabScreen extends DefaultTabScreen<Spare> {

    private final SpareService spareService;
    private final SpareEditDialog spareEditDialog;

    @Override
    protected String getTabName() {
        return i18nUtils.get("spare.tab.title");
    }

    @Override
    protected Class<Spare> getTableModelClass() {
        return Spare.class;
    }

    @Override
    protected DefaultEditDialog<Spare> getDialog() {
        return spareEditDialog;
    }

    @Override
    protected Supplier<List<Spare>> getTableEntitySupplier() {
        return () -> spareService.findAllSpares(0, 100, Sort.unsorted());
    }

    @Override
    protected void onUpdateEvent(Spare obj, ActionEvent event) {
        spareEditDialog.setDialogMode(DialogMode.UPDATE);
        spareEditDialog.setEditModel(obj);
        screenManager.showDialogAndWait(spareEditDialog.getClass());
        refreshTable();
    }

    @Override
    protected void onDeleteEvent(Spare obj, ActionEvent event) {
        if (obj != null) {
            UiUtils.showWarningDialog(
                    i18nUtils.get("spare.delete.title"),
                    i18nUtils.get("spare.delete.message"),
                    buttonType -> {
                        if (buttonType == ButtonType.OK) {
                            spareService.deleteSpare(obj.getId());
                            refreshTable();
                        }
                    }
            );
        }
    }
}

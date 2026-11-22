package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.entity.Customer;
import com.twister.service.CustomerService;
import com.twister.ui.dialog.CustomerEditDialog;
import com.twister.ui.dialog.DefaultEditDialog;
import com.twister.ui.dialog.DialogMode;
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
public class CustomersTabScreen extends DefaultTabScreen<Customer> {

    private final CustomerService customerService;
    private final CustomerEditDialog customerEditDialog;

    @Override
    protected String getTabName() {
        return i18nUtils.get("customer.tab.title");
    }

    @Override
    protected Class<Customer> getTableModelClass() {
        return Customer.class;
    }

    @Override
    protected DefaultEditDialog<Customer> getDialog() {
        return customerEditDialog;
    }

    @Override
    protected Supplier<List<Customer>> getTableEntitySupplier() {
        return () -> customerService.findAllClients(0, 100, Sort.unsorted());
    }

    @Override
    protected void onUpdateEvent(Customer obj, ActionEvent event) {
        customerEditDialog.setDialogMode(DialogMode.UPDATE);
        customerEditDialog.setEditModel(obj);
        screenManager.showDialogAndWait(customerEditDialog.getClass());
        refreshTable();
    }

    @Override
    protected void onDeleteEvent(Customer obj, ActionEvent event) {
        if (obj != null) {
            UiUtils.showWarningDialog(
                    i18nUtils.get("customer.delete.title"),
                    i18nUtils.get("customer.delete.message"),
                    buttonType -> {
                        if (buttonType == ButtonType.OK) {
                            customerService.deleteClient(obj.getId());
                            refreshTable();
                        }
                    }
            );
        }
    }
}

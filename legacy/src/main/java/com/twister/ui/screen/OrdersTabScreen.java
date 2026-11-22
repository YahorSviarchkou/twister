package com.twister.ui.screen;

import com.twister.configuration.annotation.FXMLController;
import com.twister.viewmodel.ProcessedOrder;
import com.twister.service.OrderService;
import com.twister.ui.dialog.DefaultEditDialog;
import com.twister.ui.dialog.OrderEditDialog;
import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@FXMLController(fxml = DefaultTabScreen.FXML_PATH)
public class OrdersTabScreen extends DefaultTabScreen<ProcessedOrder> {

    private final OrderService orderService;
    private final OrderEditDialog orderEditDialog;

    @Override
    protected String getTabName() {
        return i18nUtils.get("order.tab.title");
    }

    @Override
    protected Class<ProcessedOrder> getTableModelClass() {
        return ProcessedOrder.class;
    }

    @Override
    protected DefaultEditDialog<ProcessedOrder> getDialog() {
        return orderEditDialog;
    }

    @Override
    protected Supplier<List<ProcessedOrder>> getTableEntitySupplier() {
        return () -> orderService.findAllProcessedOrders(0, 100, Sort.unsorted());
    }

    @Override
    protected void onUpdateEvent(ProcessedOrder obj, ActionEvent event) {

    }

    @Override
    protected void onDeleteEvent(ProcessedOrder obj, ActionEvent event) {

    }
}

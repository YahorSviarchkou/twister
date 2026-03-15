package com.twister.ui.screen;

import com.twister.configuration.annotation.UIColumn;
import com.twister.ui.ScreenManager;
import com.twister.ui.components.TableModel;
import com.twister.ui.dialog.DefaultEditDialog;
import com.twister.ui.dialog.DialogMode;
import com.twister.util.I18nUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class DefaultTabScreen<T extends TableModel> extends ScreenController {

    public static final String FXML_PATH = "/fxml/default-tab-screen.fxml";

    @Autowired
    protected I18nUtils i18nUtils;
    @Autowired
    protected ScreenManager screenManager;

    @FXML
    protected Text tabName;
    @FXML
    protected TableView<T> table;

    protected abstract String getTabName();

    protected abstract Class<T> getTableModelClass();

    protected abstract DefaultEditDialog<T> getDialog();

    protected abstract Supplier<List<T>> getTableEntitySupplier();

    protected abstract void onUpdateEvent(T obj, ActionEvent event);

    protected abstract void onDeleteEvent(T obj, ActionEvent event);

    @FXML
    public void onCreateEvent(ActionEvent actionEvent) {
        var dialog = getDialog();
        dialog.setDialogMode(DialogMode.CREATE);
        screenManager.showDialogAndWait(dialog.getClass());
        refreshTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabName.setText(getTabName());

        initTable();
        refreshTable();

        log.info("{} was init", this.getClass().getSimpleName());
    }

    protected void initTable() {
        table.getColumns().clear();
        table.getColumns().addAll(getTableColumns());

        table.setRowFactory(this::getRowFactory);
    }

    protected void refreshTable() {
        var tableModels = getTableEntitySupplier().get();
        //noinspection unchecked
        var observableModels = FXCollections.observableArrayList(tableModels);

        table.setItems(observableModels);
    }

    protected List<MenuItem> getMenuItems(TableRow<T> row) {
        MenuItem update = new MenuItem(i18nUtils.get("update.button"));
        update.setOnAction(e -> onUpdateEvent(row.getItem(), e));

        MenuItem delete = new MenuItem(i18nUtils.get("delete.button"));
        delete.setOnAction(e -> onDeleteEvent(row.getItem(), e));

        return Arrays.asList(update, delete);
    }

    protected TableRow<T> getRowFactory(TableView<T> tableView) {
        TableRow<T> row = new TableRow<>();

        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(getMenuItems(row));

        row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(menu)
        );
        return row;
    }

    protected List<TableColumn<T, ?>> getTableColumns() {
        return Arrays.stream(getTableModelClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(UIColumn.class))
                .map(this::createColumnFromField)
                .collect(Collectors.toList());
    }

    protected TableColumn<T, ?> createColumnFromField(Field field) {
        var columnId = field.getName();

        String columnName = "?";
        if (field.isAnnotationPresent(UIColumn.class)) {
            var key = field.getAnnotation(UIColumn.class).value();
            columnName = i18nUtils.get(key);
        }

        var column = new TableColumn<T, Object>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnId));
        return column;
    }
}

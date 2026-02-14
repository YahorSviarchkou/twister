package com.twister.payload;

import com.twister.configuration.annotation.UIColumn;
import com.twister.ui.TableModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedOrder implements TableModel {

    @UIColumn("table.id.column")
    Long id;
    @UIColumn("table.full-name.column")
    String customerFullName;
    @UIColumn("table.phone.column")
    String customerPhone;
    @UIColumn("table.notes.column")
    String notes;
    @UIColumn("table.created.column")
    Date created;
    @UIColumn("table.operations.column")
    String operations;
}

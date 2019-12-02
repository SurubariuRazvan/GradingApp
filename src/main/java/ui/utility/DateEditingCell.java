package ui.utility;

import domain.Entity;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateEditingCell<E extends Entity> extends TableCell<E, LocalDate> {

    private final DateTimeFormatter formatter;
    private DatePicker datePicker;

    public DateEditingCell(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createDatePicker();
            setText(null);
            setGraphic(datePicker);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getDate().format(formatter));
        setGraphic(null);
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (datePicker != null)
                    datePicker.setValue(getDate());
                setText(null);
                setGraphic(datePicker);
            } else {
                setText(getDate().format(formatter));
                setGraphic(null);
            }
        }
    }

    private void createDatePicker() {
        datePicker = new DatePicker(getDate());
        datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.setOnAction(e -> commitEdit(LocalDate.from(datePicker.getValue())));
        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                commitEdit(LocalDate.from(datePicker.getValue()));
        });
    }

    private LocalDate getDate() {
        return getItem();
    }
}

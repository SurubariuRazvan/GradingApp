package ui.utility;

import domain.Entity;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

public class TextAreaEditingCell<E extends Entity, T> extends TableCell<E, String> {
    private TextArea textArea;

    public TextAreaEditingCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextArea();
            setText(null);
            setGraphic(textArea);
            textArea.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(item);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textArea != null) textArea.setText(getString());
                setText(null);
                setGraphic(textArea);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextArea() {
        textArea = new TextArea(getString());
        textArea.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textArea.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)
                commitEdit(textArea.getText());
        });
        textArea.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue)
                commitEdit(textArea.getText());
        });
    }

    private String getString() {
        return getItem();
    }
}

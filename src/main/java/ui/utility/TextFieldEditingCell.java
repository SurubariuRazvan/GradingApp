package ui.utility;

import domain.Entity;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

class TextFieldEditingCell<E extends Entity, T> extends TableCell<E, String> {
    ObservableList<T> data;
    private TextField textField;

    private TextFieldEditingCell(ObservableList<T> data) {
        this.data = data;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
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
                if (textField != null) {
                    textField.setText(getString());
                    //setGraphic(null);
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        textField.setOnAction(e -> commitEdit(textField.getText()));
        textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue)
                commitEdit(textField.getText());
        });

        SuggestionProvider<T> provider = SuggestionProvider.create(data);
        new AutoCompletionTextFieldBinding<>(textField, provider);
        textField.setAlignment(Pos.CENTER);
        data.addListener((InvalidationListener) observable -> {
            provider.clearSuggestions();
            provider.addPossibleSuggestions(data);
        });
    }

    private String getString() {
        return getItem();
    }
}

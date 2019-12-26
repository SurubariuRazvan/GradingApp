package ui.utility;

import com.jfoenix.controls.JFXComboBox;
import domain.Entity;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

public class ComboBoxEditingCell<E extends Entity<?>, T> extends TableCell<E, T> {
    private final ObservableList<T> data;
    private JFXComboBox<T> comboBox;

    public ComboBoxEditingCell(ObservableList<T> data) {
        this.data = data;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createComboBox();
            setText(null);
            setGraphic(comboBox);
            comboBox.getEditor().selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getT().toString());
        setGraphic(null);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (comboBox != null)
                    comboBox.setValue(getT());
                setText(getT().toString());
                setGraphic(comboBox);
            } else {
                if (getT() != null)
                    setText(getT().toString());
                setGraphic(null);
            }
        }
    }

    private void createComboBox() {
        comboBox = new JFXComboBox<>(data);
        comboBox.setEditable(true);
        comboBoxConverter(comboBox);
        comboBox.valueProperty().set(getT());
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        comboBox.setOnAction(e -> commitEdit(comboBox.getSelectionModel().getSelectedItem()));
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) commitEdit(comboBox.getSelectionModel().getSelectedItem());
        });

        comboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(comboBox.getEditor()::selectAll));
        comboBox.getEditor().setAlignment(Pos.CENTER);
        comboBox.getEditor().getStyleClass().add("tableTextField");
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) {
                if (item != null)
                    return item.toString();
                return "";
            }

            @Override
            public T fromString(String string) {
                if (string == null || string.equals(""))
                    return comboBox.getItems().get(0);
                return comboBox.getItems().stream().filter(item -> string.equals(item.toString())).findFirst().orElse(null);
            }
        });

        SuggestionProvider<T> provider = SuggestionProvider.create(data);
        new AutoCompletionTextFieldBinding<>(comboBox.getEditor(), provider);

        data.addListener((InvalidationListener) observable -> {
            provider.clearSuggestions();
            provider.addPossibleSuggestions(data);
        });
    }

    private void comboBoxConverter(ComboBox<T> comboBox) {
        comboBox.setCellFactory(c -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty)
                    setText(null);
                else
                    setText(item.toString());
            }
        });
    }

    private T getT() {
        return getItem();
    }
}
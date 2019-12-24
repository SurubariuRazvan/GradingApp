package ui.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.GlyphIcon;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import domain.Entity;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import repository.RepositoryException;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.awt.event.FocusEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class DefaultController<E extends Entity> implements Initializable {
    protected MenuController menuController;
    protected ObservableList<E> entities;
    protected ServiceManager service;
    protected CleranceLevel cleranceLevel;

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength)
                tf.setText(tf.getText().substring(0, maxLength));
        });
    }

    void init(MenuController menuController) {
        this.menuController = menuController;
    }

    public abstract void refreshTable();

    protected abstract void postInit();

    public void setService(ServiceManager service) {
        this.service = service;
        postInit();
    }

    protected <EE> List<EE> iterableToList(Iterable<EE> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    protected <EE> ObservableList<EE> iterableToObservableList(Iterable<EE> entities) {
        return FXCollections.observableList(iterableToList(entities));
    }

    public <EE extends Entity> void makeAutoCompleteBox(ComboBox<EE> cb, ObservableList<EE> list) {
        cb.setConverter(new StringConverter<>() {
            @Override
            public String toString(EE item) {
                if (item != null)
                    return item.toString();
                return "";
            }

            @Override
            public EE fromString(String string) {
                if (string == null || string.equals(""))
                    return null;
                return cb.getItems().stream().filter(item -> string.equals(item.toString())).findFirst().orElse(null);
            }
        });

        cb.setItems(list);
        cb.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                EE newEntity = cb.getConverter().fromString(cb.getEditor().getText());
                cb.getEditor().setText(cb.getConverter().toString(newEntity));
            }
        });

        SuggestionProvider<EE> provider = SuggestionProvider.create(list);
        new AutoCompletionTextFieldBinding<>(cb.getEditor(), provider);

        list.addListener((InvalidationListener) observable -> {
            provider.clearSuggestions();
            provider.addPossibleSuggestions(list);
        });
    }

    public void addEntity(ActionEvent actionEvent) {
    }

    public void searchEntity(Event actionEvent) {
    }

    public void updateEntity(TableColumn.CellEditEvent<E, Object> event) {
    }

    public void clearFields(ActionEvent actionEvent) {
    }

    public void updateAddFields() {
    }

    protected void initSpinner(Spinner<Integer> s, Integer start, Integer end) {
        s.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(start, end, start));
        s.getEditor().setAlignment(Pos.CENTER);
        s.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER)
                try {
                    Integer.parseInt(s.getEditor().textProperty().get());
                } catch (NumberFormatException e) {
                    s.getEditor().textProperty().set(start.toString());
                }
        });

    }

    protected TextFieldTableCell<E, Integer> integerConverter() {
        return this.genericConverter(Integer::parseInt);
    }

    protected TextFieldTableCell<E, Double> doubleConverter() {
        return this.genericConverter(Double::parseDouble);
    }

    private <T> TextFieldTableCell<E, T> genericConverter(Function<String, T> f) {
        TextFieldTableCell<E, T> cell = new TextFieldTableCell<>();
        StringConverter<T> converter = new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object.toString();
            }

            @Override
            public T fromString(String string) {
                try {
                    return f.apply(string);
                } catch (NumberFormatException ignored) {
                    return cell.getItem();
                }
            }
        };
        cell.setConverter(converter);
        return cell;
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Eroare");

        //TODO CSS
        Text text = new Text(message);
        alert.getDialogPane().setContent(text);
        alert.getDialogPane().setPadding(new Insets(0, 5, 0, 10));
        alert.getDialogPane().setMinWidth(200);
        text.setWrappingWidth(200);

        alert.showAndWait();
        System.out.println(message);
    }

    protected Integer IntegerInput(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Double DoubleInput(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void buttonSetup(JFXButton btn, String styleClass, TableView<E> tableView, int index, BiConsumer<Integer, E> onAction) {
        btn.getStyleClass().add(styleClass);
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setContentDisplay(ContentDisplay.CENTER);
        btn.setOnAction((ActionEvent event) -> {
            E entity = tableView.getItems().get(index);
            try {
                onAction.accept(index, entity);
            } catch (ValidationException | RepositoryException e) {
                showError("Eroare", e.getMessage());
            }
        });
    }

    protected void addButtonToTable(TableColumn<E, Void> tc, String styleClass, String text, BiConsumer<Integer, E> onAction) {
        tc.setCellFactory(new Callback<>() {
            @Override
            public TableCell<E, Void> call(final TableColumn<E, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton(text);

                    {
                        buttonSetup(btn, styleClass, getTableView(), getIndex(), onAction);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    protected void addButtonToTable(TableColumn<E, Void> tc, String styleClass, Supplier<Node> graphic, BiConsumer<Integer, E> onAction) {
        tc.setCellFactory(new Callback<>() {
            @Override
            public TableCell<E, Void> call(final TableColumn<E, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton(" ");

                    {
                        btn.setGraphic(graphic.get());
                        buttonSetup(btn, styleClass, getTableView(), getIndex(), onAction);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }
}

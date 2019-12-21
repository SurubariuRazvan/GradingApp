package ui.gui;

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

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class DefaultController<E extends Entity> implements Initializable {
    protected MenuController menuController;
    protected ObservableList<E> entities;
    protected ObservableList<E> allEntities;
    protected ServiceManager service;
    protected CleranceLevel cleranceLevel;

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength)
                tf.setText(tf.getText().substring(0, maxLength));
        });
    }

    public void init(MenuController menuController) {
        this.menuController = menuController;
    }

    public abstract void refreshTable();

    protected abstract void postInit();

    public ObservableList<E> setService(ServiceManager service) {
        entities = FXCollections.observableArrayList();
        allEntities = FXCollections.observableArrayList();
        this.service = service;
        postInit();
        return allEntities;
    }

    protected void setEntities(Iterable<E> entities) {
        this.entities.setAll(StreamSupport
                .stream(entities.spliterator(), false)
                .collect(Collectors.toList()));
    }

    protected void setAllEntities(Iterable<E> entities) {
        this.allEntities.setAll(StreamSupport
                .stream(entities.spliterator(), false)
                .collect(Collectors.toList()));
    }

    public <EE extends Entity> void makeAutoCompleBox(ComboBox<EE> cb, ObservableList<EE> list) {
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
                //return cb.getItems().get(0);
                return cb.getItems().stream().filter(item -> string.equals(item.toString())).findFirst().orElse(null);
            }
        });

        cb.setItems(list);
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
        s.setEditable(true);
        s.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER)
                try {
                    Integer.parseInt(s.getEditor().textProperty().get());
                } catch (NumberFormatException e) {
                    s.getEditor().textProperty().set(start.toString());
                }
        });
    }

    //TODO why does it work?
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

    public void removeBackground(KeyEvent inputMethodEvent) {
        TextField tf = (TextField) inputMethodEvent.getSource();
        tf.setStyle("-fx-control-inner-background: white");
    }

    protected void addButtonToTable(TableColumn<E, Void> tc, String text, BiConsumer<Integer, E> function) {
        tc.setCellFactory(new Callback<>() {
            @Override
            public TableCell<E, Void> call(final TableColumn<E, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button(text);

                    {
                        //TODO CSS
                        btn.setId(text);
                        btn.setPadding(new Insets(4));
                        btn.setMaxSize(100, 40);
                        btn.setOnAction((ActionEvent event) -> {
                            E entity = getTableView().getItems().get(getIndex());
                            try {
                                function.accept(getIndex(), entity);
                            } catch (ValidationException | RepositoryException e) {
                                showError("Eroare", e.getMessage());
                            }
                        });
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

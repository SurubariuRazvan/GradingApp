package ui.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;
import domain.Entity;
import domain.User;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import repository.RepositoryException;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class DefaultController<E extends Entity<?>> implements Initializable {
    protected MenuController menuController;
    protected ObservableList<E> entities;
    protected ServiceManager service;
    protected User user;
    protected Map<Tables, Boolean> refresh;

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength)
                tf.setText(tf.getText().substring(0, maxLength));
        });
    }

    void init(MenuController menuController) {
        this.menuController = menuController;
        refresh = new TreeMap<>();
        for(Tables tab : Tables.values())
            refresh.put(tab, false);
    }

    public abstract void refreshTable();

    protected abstract void postInit();

    public void setService(ServiceManager service, User user) {
        this.service = service;
        this.user = user;
        postInit();
    }

    protected <EE> List<EE> iterableToList(Iterable<EE> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    protected <EE> ObservableList<EE> iterableToObservableList(Iterable<EE> entities) {
        return FXCollections.observableList(iterableToList(entities));
    }

    protected <EE extends Entity<?>> void makeAutoCompleteBox(ComboBox<EE> cb, ObservableList<EE> list) {
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

    protected void addEntity(ActionEvent actionEvent) {
    }

    protected void searchEntity(Event actionEvent) {
    }

    protected void updateEntity(TableColumn.CellEditEvent<E, Object> event) {
    }

    protected void clearFields(ActionEvent actionEvent) {
    }

    protected void updateAddFields() {
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
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        JFXButton button = new JFXButton("OK");
        JFXDialog dialog = new JFXDialog(menuController.rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> dialog.close());

        dialogLayout.setHeading(new Label(title));
        dialogLayout.getStyleClass().add("errorHeading");
        dialogLayout.setBody(new Label(message));
        dialogLayout.setActions(button);
        dialog.show();
        BoxBlur blur = new BoxBlur(3, 3, 2);
        menuController.menuTable.setEffect(blur);
        dialog.setOnDialogClosed((JFXDialogEvent event) -> menuController.menuTable.setEffect(null));
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

    protected void addButtonToTable(TableColumn<E, Void> tc, String styleClass, String text, BiConsumer<Integer, E> onAction) {
        tc.setCellFactory(new Callback<>() {
            @Override
            public TableCell<E, Void> call(final TableColumn<E, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton(text);

                    {
                        btn.getStyleClass().add(styleClass);
                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                        btn.setContentDisplay(ContentDisplay.CENTER);
                        btn.setOnAction((ActionEvent event) -> {
                            E entity = getTableView().getItems().get(getIndex());
                            try {
                                onAction.accept(getIndex(), entity);
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

    protected void addButtonToTable(TableColumn<E, Void> tc, String styleClass, Supplier<Node> graphic, BiConsumer<Integer, E> onAction) {
        tc.setCellFactory(new Callback<>() {
            @Override
            public TableCell<E, Void> call(final TableColumn<E, Void> param) {
                return new TableCell<>() {
                    private final JFXButton btn = new JFXButton(" ");

                    {
                        btn.setGraphic(graphic.get());
                        btn.getStyleClass().add(styleClass);
                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                        btn.setContentDisplay(ContentDisplay.CENTER);
                        btn.setOnAction((ActionEvent event) -> {
                            E entity = getTableView().getItems().get(getIndex());
                            try {
                                onAction.accept(getIndex(), entity);
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

    protected abstract void initAdminComponents();

    protected abstract void initProfessorComponents();

    protected abstract void initStudentComponents();

    protected abstract void initAddComponents();

    private void removeAddRow(GridPane gp) {
        gp.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 2 || GridPane.getRowIndex(node) == 3);
        gp.getRowConstraints().get(2).setMinHeight(0);
        gp.getRowConstraints().get(2).setPrefHeight(0);
    }

    protected void initComponentsByClearance(GridPane gp, CleranceLevel minClearanceForAdding) {
        if (user.getCleranceLevel().ordinal() <= CleranceLevel.Student.ordinal())
            initStudentComponents();
        if (user.getCleranceLevel().ordinal() <= CleranceLevel.Professor.ordinal())
            initProfessorComponents();
        if (user.getCleranceLevel().ordinal() <= CleranceLevel.Admin.ordinal())
            initAdminComponents();

        if (user.getCleranceLevel().ordinal() > minClearanceForAdding.ordinal())
            removeAddRow(gp);
        else {
            initAddComponents();
            updateAddFields();
        }

        clearFields(null);
    }
}

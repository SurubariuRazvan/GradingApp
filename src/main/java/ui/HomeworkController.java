package ui;

import domain.Homework;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import repository.RepositoryException;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeworkController implements Initializable {
    public TableView<Homework> homeworkTable;
    public TextField searchId;
    public TextField addId;
    public TextField searchDescription;
    public TextField addDescription;
    public Spinner<Integer> searchStartWeek;
    public Spinner<Integer> addStartWeek;
    public Spinner<Integer> searchDeadlineWeek;
    public Spinner<Integer> addDeadlineWeek;
    public Button clearButton;
    public Button addButton;
    public TableColumn<Homework, Integer> homeworkTableId;
    public TableColumn<Homework, String> homeworkTableDescription;
    public TableColumn<Homework, Integer> homeworkTableStartWeek;
    public TableColumn<Homework, Integer> homeworkTableDeadlineWeek;
    public TableColumn<Homework, Void> homeworkTableUpdate;
    public TableColumn<Homework, Void> homeworkTableDelete;

    private ObservableList<Homework> homeworks;
    private MenuController menuController;
    private ServiceManager service;

    public void init(MenuController menuController) {
        this.menuController = menuController;
    }

    public void setService(ServiceManager service) {
        this.service = service;
        postInit();
        setHomeworks(service.findAllHomework());
    }

    private void setHomeworks(Iterable<Homework> homeworks) {
        this.homeworks.setAll(StreamSupport
                .stream(homeworks.spliterator(), false)
                .collect(Collectors.toList()));
    }

    private void initSpinner(Spinner<Integer> s, Integer start, Integer finish) {
        SpinnerValueFactory<Integer> r = new SpinnerValueFactory.IntegerSpinnerValueFactory(start, finish);
        r.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return start;
                }
            }
        });
        s.setValueFactory(r);
        s.setEditable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));

        homeworkTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        homeworkTableDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        homeworkTableStartWeek.setCellValueFactory(new PropertyValueFactory<>("StartWeek"));
        IntegerConverter(homeworkTableStartWeek);

        homeworkTableDeadlineWeek.setCellValueFactory(new PropertyValueFactory<>("DeadlineWeek"));
        IntegerConverter(homeworkTableDeadlineWeek);

        addButtonToTable(homeworkTableUpdate, "Update", (i, h) -> {
            service.updateHomework(h.getId(), h);
            homeworks.set(i, h);
        });
        addButtonToTable(homeworkTableDelete, "Delete", (i, h) -> {
            service.deleteHomework(h.getId());
            homeworks.remove(h);
        });

        homeworks = FXCollections.observableArrayList();
        homeworkTable.setItems(homeworks);
        homeworkTable.setEditable(true);
    }

    private void IntegerConverter(TableColumn<Homework, Integer> homeworkTableDeadlineWeek) {
        homeworkTableDeadlineWeek.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.valueOf(string);
                } catch (NumberFormatException ignored) {
                    return 0;
                }
            }
        }));
    }

    private void addButtonToTable(TableColumn<Homework, Void> tc, String text, BiConsumer<Integer, Homework> function) {
        tc.setCellFactory(new Callback<TableColumn<Homework, Void>, TableCell<Homework, Void>>() {
            @Override
            public TableCell<Homework, Void> call(final TableColumn<Homework, Void> param) {
                return new TableCell<Homework, Void>() {
                    private final Button btn = new Button(text);

                    {
                        //TODO change with CSS
                        btn.setPadding(new Insets(4));

                        btn.setMaxSize(100,40);
                        btn.setOnAction((ActionEvent event) -> {
                            Homework homework = getTableView().getItems().get(getIndex());
                            try {
                                function.accept(getIndex(), homework);
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

    //TODO change function definitions
    private Integer IntegerInput(TextField tf, Boolean showWarning) {
        try {
            return Integer.parseInt(tf.getText());
        } catch (NumberFormatException e) {
            if (showWarning)
                tf.setStyle("-fx-control-inner-background: #ff442e");
            return null;
        }
    }

    private Integer IntegerInput(Spinner s, Boolean showWarning) {
        try {
            return Integer.parseInt(s.getValue().toString());
        } catch (NumberFormatException e) {
            if (showWarning)
                s.setStyle("-fx-control-inner-background: #ff442e");
            return null;
        }
    }

    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Eroare");

        Text text = new Text("  " + message);
        alert.getDialogPane().setContent(text);
        alert.getDialogPane().setPadding(new Insets(0, 5, 0, 10));
        alert.getDialogPane().setMinWidth(200);
        text.setWrappingWidth(200);

        alert.showAndWait();
        System.out.println(message);
    }

    public void addHomework(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId, true);
        String description = addDescription.getText();
        Integer startWeek = IntegerInput(addStartWeek, true);
        Integer deadlineWeek = IntegerInput(addDeadlineWeek, true);
        try {
            Homework h = new Homework(id, description, startWeek, deadlineWeek);
            service.saveHomework(h);
            homeworks.add(h);
            addId.setText(service.getNextHomeworkId().toString());
        } catch (ValidationException e) {
            showError("Eroare la adaugare", e.getMessage());
        }
    }

    public void searchHomework(Event actionEvent) {
        Integer id = IntegerInput(searchId, false);
        String description = searchDescription.getText();
        Integer startWeek = IntegerInput(searchStartWeek, false);
        Integer deadlineWeek = IntegerInput(searchDeadlineWeek, false);

        setHomeworks(StreamSupport.stream(service.findAllHomework().spliterator(), false)
                .filter(h -> (id == null || h.getId().toString().contains(id.toString())) &&
                        (description == null || h.getDescription().contains(description)) &&
                        ((startWeek == null) || (startWeek == 0) || h.getStartWeek().toString().contains(startWeek.toString())) &&
                        ((deadlineWeek == null) || (deadlineWeek == 0) || h.getDeadlineWeek().toString().contains(deadlineWeek.toString()))
                ).collect(Collectors.toList()));
    }

    public void removeBackground(KeyEvent inputMethodEvent) {
        TextField tf = (TextField) inputMethodEvent.getSource();
        tf.setStyle("-fx-control-inner-background: white");
    }

    public void postInit() {
        initSpinner(addStartWeek, 1, 14);
        addStartWeek.getValueFactory().setValue(service.getWeek());
        initSpinner(addDeadlineWeek, 1, 14);
        if (service.getWeek() >= 14)
            addStartWeek.getValueFactory().setValue(14);
        else
            addDeadlineWeek.getValueFactory().setValue(service.getWeek() + 1);
        initSpinner(searchStartWeek, 0, 14);
        searchStartWeek.getValueFactory().setValue(0);
        initSpinner(searchDeadlineWeek, 0, 14);
        searchDeadlineWeek.getValueFactory().setValue(0);
        addId.setText(service.getNextHomeworkId().toString());
    }

    //TODO cleaner solution
    public void updateField(TableColumn.CellEditEvent<Homework, Object> homeworkStringCellEditEvent) {
        Homework homework = homeworkTable.getSelectionModel().getSelectedItem();
        var newValue = homeworkStringCellEditEvent.getNewValue();
        var oldValue = homeworkStringCellEditEvent.getOldValue();
        switch (homeworkStringCellEditEvent.getTablePosition().getColumn()) {
            case 1:
                homework.setDescription(newValue.toString());
                break;
            case 2:
                homework.setStartWeek((Integer) newValue);
                break;
            case 3:
                homework.setDeadlineWeek((Integer) newValue);
                break;
        }
    }

    public void clearFields(ActionEvent actionEvent) {
        searchId.setText("");
        searchDescription.setText("");
        searchStartWeek.getValueFactory().setValue(0);
        searchDeadlineWeek.getValueFactory().setValue(0);
        searchHomework(null);
    }
}

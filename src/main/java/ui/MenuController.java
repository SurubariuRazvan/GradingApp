package ui;

import domain.Homework;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import repository.RepositoryException;
import serviceManager.ServiceManager;
import validation.ValidationException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MenuController implements Initializable {

    public TableView<Homework> homeworkTable;
    public TextField searchId;
    public TextField addId;
    public TextField searchDescription;
    public TextField addDescription;
    public Spinner<Integer> searchStartWeek;
    public Spinner<Integer> addStartWeek;
    public Spinner<Integer> searchDeadlineWeek;
    public Spinner<Integer> addDeadlineWeek;
    public Button searchButton;
    public Button addButton;
    public TableColumn<Homework, Integer> homeworkTableId;
    public TableColumn<Homework, String> homeworkTableDescription;
    public TableColumn<Homework, Integer> homeworkTableStartWeek;
    public TableColumn<Homework, Integer> homeworkTableDeadlineWeek;
    public TableColumn<Homework, Void> homeworkTableUpdate;
    public TableColumn<Homework, Void> homeworkTableDelete;
    private ObservableList<Homework> homeworks;

    private ServiceManager service;

    public void setService(ServiceManager service) {
        this.service = service;
        setHomeworks(service.findAllHomework());
    }

    private void setHomeworks(Iterable<Homework> homeworks) {
        this.homeworks.setAll(StreamSupport
                .stream(homeworks.spliterator(), false)
                .collect(Collectors.toList()));
    }

    private void initSpinner(Spinner<Integer> s, Integer start, Integer finish) {
        SpinnerValueFactory<Integer> r = new IntegerSpinnerValueFactory(start, finish);
        if (start == 0)
            r.setValue(0);
        else
            r.setValue(service.getWeek());
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
    public void initialize(URL location, ResourceBundle resources) {
        homeworkTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));

        homeworkTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        homeworkTableDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        homeworkTableStartWeek.setCellValueFactory(new PropertyValueFactory<>("StartWeek"));
        IntegerConverter(homeworkTableStartWeek);

        homeworkTableDeadlineWeek.setCellValueFactory(new PropertyValueFactory<>("DeadlineWeek"));
        IntegerConverter(homeworkTableDeadlineWeek);

        addButtonToTable(homeworkTableUpdate, "Update", h -> service.updateHomework(h.getId(), h));
        addButtonToTable(homeworkTableDelete, "Delete", h -> service.deleteHomework(h.getId()));

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

    private void addButtonToTable(TableColumn<Homework, Void> tc, String text, Consumer<Homework> function) {
        tc.setCellFactory(new Callback<TableColumn<Homework, Void>, TableCell<Homework, Void>>() {
            @Override
            public TableCell<Homework, Void> call(final TableColumn<Homework, Void> param) {
                return new TableCell<Homework, Void>() {
                    private final Button btn = new Button(text);

                    {
                        //TODO changed with CSS
                        btn.setPadding(new Insets(4));
                        btn.setMaxWidth(100);
                        btn.setOnAction((ActionEvent event) -> {
                            Homework homework = getTableView().getItems().get(getIndex());
                            try {
                                function.accept(homework);
                                setHomeworks(service.findAllHomework());
                            } catch (ValidationException | RepositoryException e) {
                                System.out.println(e.getMessage());
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

    public void addHomework(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId, true);
        String description = addDescription.getText();
        Integer startWeek = IntegerInput(addStartWeek, true);
        Integer deadlineWeek = IntegerInput(addDeadlineWeek, true);
        try {
            Homework h = new Homework(id, description, startWeek, deadlineWeek);
            service.saveHomework(h);
            setHomeworks(service.findAllHomework());
            addId.setText(service.getNextHomeworkId().toString());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchHomework(ActionEvent actionEvent) {
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
        initSpinner(addDeadlineWeek, 1, 14);
        initSpinner(searchStartWeek, 0, 14);
        initSpinner(searchDeadlineWeek, 0, 14);

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
}
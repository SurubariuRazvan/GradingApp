package ui;

import domain.Homework;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.HomeworkService;
import service.StudentService;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MenuController implements Initializable {

    public TableView<Homework> homeworkTable;
    public TextField searchId;
    public TextField addId;
    public TextField searchDescription;
    public TextField addDescription;
    public TextField searchStartWeek;
    public TextField addStartWeek;
    public TextField searchDeadlineWeek;
    public TextField addDeadlineWeek;
    public Button searchButton;
    public Button addButton;
    public TableColumn<Homework, Integer> homeworkTableID;
    public TableColumn<Homework, String> homeworkTableDescription;
    public TableColumn<Homework, Integer> homeworkTableStartWeek;
    public TableColumn<Homework, Integer> homeworkTableDeadlineWeek;
    public TableColumn homeworkTableAction;
    private ObservableList<Homework> students;

    private HomeworkService service;

    public void setStudents(Iterable<Homework> students) {
        this.students = FXCollections.observableArrayList(StreamSupport
                .stream(students.spliterator(), false)
                .collect(Collectors.toList()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homeworkTableID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        homeworkTableDescription.setCellValueFactory(new PropertyValueFactory<>("Descriere"));
        homeworkTableStartWeek.setCellValueFactory(new PropertyValueFactory<>("startWeek"));
        homeworkTableDeadlineWeek.setCellValueFactory(new PropertyValueFactory<>("deadlineWeek"));

        homeworkTable.setItems(students);
    }

    public void addHomework(ActionEvent actionEvent) {

    }

    public void searchHomework(ActionEvent actionEvent) {

    }
}

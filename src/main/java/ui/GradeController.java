package ui;

import domain.Grade;
import domain.Homework;
import domain.Professor;
import domain.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GradeController extends DefaultController<Grade> {
    public TableView<Grade> gradeTable;
    public Button clearButton;
    public Button addButton;
    public TableColumn<Grade, String> gradeTableHomework;
    public TableColumn<Grade, String> gradeTableStudent;
    public TableColumn<Grade, Double> gradeTableGivenGrade;
    public TableColumn<Grade, String> gradeTableProfessor;
    public TableColumn<Grade, String> gradeTableHandOverDate;
    public TableColumn<Grade, String> gradeTableFeedback;
    public TableColumn<Grade, Void> gradeTableDelete;
    public ComboBox HomeworkId;
    public ComboBox searchHomeworkId;
    public TextField searchStudentName;
    public TextField studentName;
    public TextField searchGivenGrade;
    public TextField givenGrade;
    public TextField searchProfessorName;
    public TextField professorName;
    public DatePicker searchHandOverDate;
    public DatePicker handOverDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gradeTableHomework.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Grade, String> param) {
                Homework homework = service.findOneHomework(param.getValue().getId().getHomeworkId());
                return new ReadOnlyObjectWrapper<String>("Tema " + homework.getStartWeek());
            }
        });
        gradeTableHomework.setCellFactory(TextFieldTableCell.forTableColumn());

        gradeTableStudent.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Grade, String> param) {
                Student student = service.findOneStudent(param.getValue().getId().getStudentId());
                return new ReadOnlyObjectWrapper<String>(student.getFamilyName() + " " + student.getFirstName());
            }
        });
        gradeTableStudent.setCellFactory(TextFieldTableCell.forTableColumn());

        gradeTableGivenGrade.setCellValueFactory(new PropertyValueFactory<>("GivenGrade"));
        gradeTableGivenGrade.setCellFactory(x -> doubleConverter());

        gradeTableProfessor.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Grade, String> param) {
                Professor professor = service.findOneProfessor(param.getValue().getProfessorId());
                return new ReadOnlyObjectWrapper<String>(professor.getFamilyName() + " " + professor.getFirstName());
            }
        });
        gradeTableProfessor.setCellFactory(TextFieldTableCell.forTableColumn());

        gradeTableHandOverDate.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Grade, String> param) {
                return new ReadOnlyObjectWrapper<String>(param.getValue().getHandOverDate().toString());
            }
        });
        gradeTableHandOverDate.setCellFactory(TextFieldTableCell.forTableColumn());

        gradeTableFeedback.setCellValueFactory(new PropertyValueFactory<>("Feedback"));
        gradeTableFeedback.setCellFactory(TextFieldTableCell.forTableColumn());

        addButtonToTable(gradeTableDelete, "Delete", (i, g) -> {
            service.deleteGrade(g.getId());
            entities.remove(g);
            allEntities.remove(g);
        });

        gradeTable.setEditable(true);
    }

    public void postInit() {
        setEntities(service.findAllGrade());
        gradeTable.setItems(entities);
        setAllEntities(service.findAllGrade());

        List<String> studentNames = new ArrayList<>();
        service.findAllStudent().forEach(s -> studentNames.add(s.getFamilyName() + s.getFirstName()));
        TextFields.bindAutoCompletion(searchStudentName, studentNames);
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {

    }

    @Override
    public void searchEntity(Event actionEvent) {

    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Grade, Object> event) {

    }

    @Override
    public void clearFields(ActionEvent actionEvent) {

    }
}

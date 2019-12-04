package ui.gui;

import domain.Grade;
import domain.Homework;
import domain.Professor;
import domain.Student;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import serviceManager.ServiceManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public VBox homeworkTab;
    public VBox studentTab;
    public VBox professorTab;
    public VBox gradeTab;
    public VBox reportTab;
    public TabPane menuTab;
    protected ObservableList<Homework> allHomeworks;
    protected ObservableList<Student> allStudents;
    protected ObservableList<Professor> allProfessors;
    protected ObservableList<Grade> allGrades;
    @FXML
    HomeworkController homeworkTabController;
    @FXML
    StudentController studentTabController;
    @FXML
    ProfessorController professorTabController;
    @FXML
    GradeController gradeTabController;
    @FXML
    ReportController reportTabController;
    private ServiceManager service;

    public void setService(ServiceManager service) {
        this.service = service;
        allHomeworks = homeworkTabController.setService(service);
        allProfessors = professorTabController.setService(service);
        allStudents = studentTabController.setService(service);
        allGrades = gradeTabController.setService(service);
        reportTabController.setService(service);
        //TODO refresh tables on tab change
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTabController.init(this);
        studentTabController.init(this);
        professorTabController.init(this);
        gradeTabController.init(this);
        reportTabController.init(this);
        menuTab.getSelectionModel().selectedItemProperty().addListener((param, oldTab, newTab) -> {
                    if (homeworkTab.getId().equals(newTab.getId()))
                        homeworkTabController.refreshTable();
                    else if (studentTab.getId().equals(newTab.getId()))
                        studentTabController.refreshTable();
                    else if (professorTab.getId().equals(newTab.getId()))
                        professorTabController.refreshTable();
                    else if (gradeTab.getId().equals(newTab.getId()))
                        gradeTabController.refreshTable();
                }
        );
    }
}
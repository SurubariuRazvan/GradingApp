package ui.gui;

import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import serviceManager.ServiceManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public Tab homeworkTabId;
    public Tab studentTabId;
    public Tab professorTabId;
    public Tab gradeTabId;
    public Tab reportTabId;
    public TabPane menuTable;

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
    private User user;

    void setService(ServiceManager service, User user) {
        this.service = service;
        this.user = user;
        homeworkTabController.setService(service, user);
        professorTabController.setService(service, user);
        if (user.getCleranceLevel().ordinal() <= CleranceLevel.Professor.ordinal())
            studentTabController.setService(service, user);
        else
            menuTable.getTabs().remove(studentTabId);
        gradeTabController.setService(service, user);
        if (user.getCleranceLevel().ordinal() <= CleranceLevel.Professor.ordinal())
            reportTabController.setService(service, user);
        else
            menuTable.getTabs().remove(reportTabId);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTabController.init(this);
        studentTabController.init(this);
        professorTabController.init(this);
        gradeTabController.init(this);
        reportTabController.init(this);
        menuTable.getSelectionModel().selectedItemProperty().addListener((param, oldTab, newTab) -> {
            if (homeworkTabId.getId().equals(newTab.getId()))
                        homeworkTabController.refreshTable();
            else if (studentTabId.getId().equals(newTab.getId()))
                        studentTabController.refreshTable();
            else if (professorTabId.getId().equals(newTab.getId()))
                        professorTabController.refreshTable();
            else if (gradeTabId.getId().equals(newTab.getId()))
                        if (user.getCleranceLevel().ordinal() < CleranceLevel.Student.ordinal())
                            gradeTabController.refreshTable();
                }
        );
    }
}
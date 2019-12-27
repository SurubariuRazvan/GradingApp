package ui.gui;

import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import serviceManager.ServiceManager;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

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
    private User user;

    void setService(ServiceManager service, User user) {
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

    private void refreshIfNeeded(Map<Tables, DefaultController<?>> controllers, Tables refreshTab) {
        boolean isRefreshNeeded = false;
        for(var tab : controllers.entrySet())
            if (!(tab.getKey().equals(refreshTab)) && (tab.getValue().refresh.get(refreshTab))) {
                isRefreshNeeded = true;
                tab.getValue().refresh.replace(refreshTab, false);
            }
        if (isRefreshNeeded)
            controllers.get(refreshTab).refreshTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Map<Tables, DefaultController<?>> controllers = new TreeMap<>();
        homeworkTabController.init(this);
        controllers.put(Tables.HomeworkTab, homeworkTabController);
        studentTabController.init(this);
        controllers.put(Tables.StudentTab, studentTabController);
        professorTabController.init(this);
        controllers.put(Tables.ProfessorTab, professorTabController);
        gradeTabController.init(this);
        controllers.put(Tables.GradeTab, gradeTabController);
        reportTabController.init(this);

        menuTable.getSelectionModel().selectedItemProperty().addListener((param, oldTab, newTab) -> {
            if (homeworkTabId.getId().equals(newTab.getId()))
                refreshIfNeeded(controllers, Tables.HomeworkTab);
            else if (studentTabId.getId().equals(newTab.getId()))
                refreshIfNeeded(controllers, Tables.StudentTab);
            else if (professorTabId.getId().equals(newTab.getId()))
                refreshIfNeeded(controllers, Tables.ProfessorTab);
            else if (gradeTabId.getId().equals(newTab.getId()))
                refreshIfNeeded(controllers, Tables.GradeTab);
                }
        );
    }
}
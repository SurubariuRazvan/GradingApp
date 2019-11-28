package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import serviceManager.ServiceManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    public VBox homeworkTab;
    @FXML
    HomeworkController homeworkTabController;

    private ServiceManager service;

    public void setService(ServiceManager service) {
        this.service = service;
        homeworkTabController.setService(service);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTabController.init(this);
    }
}
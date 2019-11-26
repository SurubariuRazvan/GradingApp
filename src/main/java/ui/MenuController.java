package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import serviceManager.ServiceManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {


    public Tab HomeworkTab;
    public BorderPane rootLayout;
    public VBox homeworkTabContent;
    @FXML
    private HomeworkController homeworkController;

    private ServiceManager service;

    public void setService(ServiceManager service) {
        this.service = service;
        this.homeworkController.setService(service);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
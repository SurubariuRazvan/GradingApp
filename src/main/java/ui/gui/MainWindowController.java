package ui.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import serviceManager.ServiceManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private ServiceManager service;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setService(ServiceManager service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    public void logIn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Menu.fxml"));
            BorderPane rootLayout = loader.load();
            MenuController controller = loader.getController();
            controller.setService(service);

            Scene scene = new Scene(rootLayout);
            stage.setMinWidth(800);
            stage.setMinHeight(400);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

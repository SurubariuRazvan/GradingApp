package ui;

import config.ApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import serviceManager.ServiceManager;

import java.io.IOException;
import java.sql.SQLException;

public class GraphicalUI extends Application {
    private ServiceManager service;

    public GraphicalUI() {
        try {
            service = new ServiceManager();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Menu2.fxml"));
            BorderPane rootLayout = loader.load();
            MenuController controller = loader.getController();
            controller.setService(service);

            Scene scene = new Scene(rootLayout);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(String[] args) {
        launch(args);
    }
}

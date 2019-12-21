package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import serviceManager.ServiceManager;
import ui.gui.MainWindowController;
import ui.gui.MenuController;

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
            loader.setLocation(getClass().getResource("/MainWindow.fxml"));
            HBox rootLayout = loader.load();
            MainWindowController controller = loader.getController();
            controller.setService(service, stage);

            Scene scene = new Scene(rootLayout);
            //stage.setMinWidth(800);
            //stage.setMinHeight(400);
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

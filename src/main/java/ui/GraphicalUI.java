package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ui.gui.MainWindowController;

import java.io.IOException;

public class GraphicalUI extends Application {

    public GraphicalUI() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
            HBox rootLayout = loader.load();
            MainWindowController controller = loader.getController();
            controller.setStage(stage);

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

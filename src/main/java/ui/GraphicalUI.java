package ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GraphicalUI extends Application {
    @Override
    public void start(Stage stage) {
        Parent root = new AnchorPane();
        Scene scene = new Scene(root, 550, 500);
        stage.setTitle("Welcome to JavaFX!!");
        stage.setScene(scene);
        stage.show();
    }

    public void run() {
        launch();
    }
}

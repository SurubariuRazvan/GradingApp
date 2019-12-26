package ui.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import config.ApplicationContext;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import repository.sql.UserPostgreSQLRepository;
import serviceManager.ServiceManager;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public JFXTextField logInUsername;
    public JFXPasswordField logInPassword;
    public JFXButton logInButton;
    private Stage stage;
    private UserPostgreSQLRepository userRepo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String url = ApplicationContext.getPROPERTIES().getProperty("usersUrl");
        String user = ApplicationContext.getPROPERTIES().getProperty("user");
        String password = ApplicationContext.getPROPERTIES().getProperty("password");
        try {
            Connection c = DriverManager.getConnection(url, user, password);
            userRepo = new UserPostgreSQLRepository(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void logIn(ActionEvent actionEvent) {
//        userRepo.save(new User("student", User.encodePassword("student"), CleranceLevel.Student));
//        userRepo.save(new User("professor", User.encodePassword("professor"), CleranceLevel.Professor));
//        userRepo.save(new User("admin", User.encodePassword("admin"), CleranceLevel.Admin));
        String username = logInUsername.getText();
        String rawPassword = logInPassword.getText();

        if (username.length() < 5 || username.length() > 50)
            userNotFound();
        else {
            User user = userRepo.findUser(username);
            if (user == null)
                userNotFound();
            else if (User.encodePassword(rawPassword).equals(user.getPassword()))
                successfulLogIn(user);
            else
                wrongPassword();
        }
    }

    private void userNotFound() {
        logInUsername.getStyleClass().add("wrong-credentials");
        logInPassword.getStyleClass().add("wrong-credentials");
    }

    private void wrongPassword() {
        logInUsername.getStyleClass().remove("wrong-credentials");
        logInPassword.getStyleClass().add("wrong-credentials");
    }

    private void successfulLogIn(User user) {
        try {
            userRepo.closeConnection();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
            BorderPane root = loader.load();
            MenuController controller = loader.getController();
            controller.setService(new ServiceManager(), user);

            Stage newStage = new Stage(StageStyle.DECORATED);
            newStage.setMinWidth(800);
            newStage.setMinHeight(500);
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}

import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ui.ConsoleUI;
import ui.GraphicalUI;
import validation.ValidationException;

public class Main {

    public static void main(String[] a) throws ValidationException {
        GraphicalUI u = new GraphicalUI();
        u.run();
        //ConsoleUI consoleUi = new ConsoleUI();
        //consoleUi.run();
    }
}

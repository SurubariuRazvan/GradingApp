import ui.ConsoleUI;
import ui.GraphicalUI;
import validation.ValidationException;

public class Main {

    public static void main(String[] args) throws ValidationException {
        GraphicalUI u = new GraphicalUI();
        u.run(args);
        //ConsoleUI consoleUi = new ConsoleUI();
        //consoleUi.run();
    }
}

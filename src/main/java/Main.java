import ui.ConsoleUI;
import validation.ValidationException;

public class Main {

    public static void main(String[] a) throws ValidationException {
        ConsoleUI consoleUi = new ConsoleUI();
        consoleUi.run();
    }
}

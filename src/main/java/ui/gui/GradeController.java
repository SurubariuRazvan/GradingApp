package ui.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import domain.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import repository.RepositoryException;
import ui.utility.ComboBoxEditingCell;
import ui.utility.DateEditingCell;
import ui.utility.TextAreaEditingCell;
import validation.ValidationException;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeController extends DefaultController<Grade> {
    public TableView<Grade> gradeTable;
    public JFXButton clearButton;
    public JFXButton addButton;
    public TableColumn<Grade, Homework> gradeTableHomework;
    public TableColumn<Grade, Student> gradeTableStudent;
    public TableColumn<Grade, Double> gradeTableGivenGrade;
    public TableColumn<Grade, Professor> gradeTableProfessor;
    public TableColumn<Grade, LocalDate> gradeTableHandOverDate;
    public TableColumn<Grade, String> gradeTableFeedback;
    public TableColumn<Grade, Void> gradeTableDelete;
    public JFXComboBox<Homework> addHomeworkId;
    public JFXComboBox<Homework> searchHomeworkId;
    public JFXComboBox<Student> addStudentName;
    public JFXComboBox<Student> searchStudentName;
    public JFXTextField addGivenGrade;
    public JFXTextField searchGivenGrade;
    public JFXComboBox<Professor> addProfessorName;
    public JFXComboBox<Professor> searchProfessorName;
    public JFXTextField searchHandOverDate;
    public JFXTextField addHandOverDate;
    public JFXTextField searchFeedback;
    public JFXTextField addFeedback;
    public GridPane addGivenGradeGrid;
    public Spinner<Integer> motivatedWeeks;
    public Spinner<Integer> lateWeeks;
    public GridPane bottom;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ObservableList<Professor> professors;
    private ObservableList<Student> students;
    private ObservableList<Homework> homeworks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gradeTableHomework.setCellValueFactory((TableColumn.CellDataFeatures<Grade, Homework> param) -> new ReadOnlyObjectWrapper<>(service.findOneHomework(param.getValue().getId().getHomeworkId())));
        gradeTableStudent.setCellValueFactory((TableColumn.CellDataFeatures<Grade, Student> param) -> new ReadOnlyObjectWrapper<>(service.findOneStudent(param.getValue().getId().getStudentId())));
        gradeTableGivenGrade.setCellValueFactory(new PropertyValueFactory<>("GivenGrade"));
        gradeTableProfessor.setCellValueFactory((TableColumn.CellDataFeatures<Grade, Professor> param) -> new ReadOnlyObjectWrapper<>(service.findOneProfessor(param.getValue().getProfessorId())));
        gradeTableHandOverDate.setCellValueFactory((TableColumn.CellDataFeatures<Grade, LocalDate> param) -> new ReadOnlyObjectWrapper<>(param.getValue().getHandOverDate()));
        gradeTableFeedback.setCellValueFactory(new PropertyValueFactory<>("Feedback"));
    }

    public void postInit() {
        entities = iterableToObservableList(service.findAllGrade());
        gradeTable.setItems(entities);

        initComponentsByClearance(bottom, CleranceLevel.Professor);
    }

    protected void initAddComponents() {
        addGivenGrade.focusedProperty().addListener((observable, oldValue, newValue) -> focusState(newValue));
        makeAutoCompleteBox(addProfessorName, professors);
        makeAutoCompleteBox(addHomeworkId, homeworks);
        makeAutoCompleteBox(addStudentName, students);
        initSpinner(motivatedWeeks, 0, 2);
        initSpinner(lateWeeks, 0, 2);
        lateWeeks.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue <= 2 && newValue >= 0)
                addHandOverDate.setText(LocalDate.now().minusWeeks(newValue).format(dateFormatter));
        });
    }

    protected void initAdminComponents() {
    }

    protected void initProfessorComponents() {
        gradeTableGivenGrade.setCellFactory(x -> doubleConverter());
        gradeTableFeedback.setCellFactory((TableColumn<Grade, String> param) -> new TextAreaEditingCell<>());
        gradeTableHandOverDate.setCellFactory((TableColumn<Grade, LocalDate> param) -> new DateEditingCell<>(dateFormatter));
        gradeTableProfessor.setCellFactory((TableColumn<Grade, Professor> param) -> new ComboBoxEditingCell<>(professors));
        addButtonToTable(gradeTableDelete, "deleteButton", () -> new MaterialDesignIconView(MaterialDesignIcon.MINUS_CIRCLE_OUTLINE, "30"), (i, g) -> {
            service.deleteGrade(g.getId());
            entities.remove(g);
        });
    }

    protected void initStudentComponents() {
        professors = iterableToObservableList(service.findAllProfessor());
        homeworks = iterableToObservableList(service.findAllHomework());
        students = iterableToObservableList(service.findAllStudent());
        makeAutoCompleteBox(searchProfessorName, professors);
        makeAutoCompleteBox(searchHomeworkId, homeworks);
        makeAutoCompleteBox(searchStudentName, students);
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
        Homework homework = addHomeworkId.getValue();
        Student student = addStudentName.getValue();
        Double givenGrade = DoubleInput(addGivenGrade.getText());
        Professor professor = addProfessorName.getValue();
        LocalDate handOverDate = LocalDate.now().minusWeeks(lateWeeks.getValue());
        String feedback = addFeedback.getText();
        Integer lateProfessor = lateWeeks.getValue();
        Integer motivated = motivatedWeeks.getValue();

        if (validateInputs(homework, student, givenGrade, professor)) {
            Double finalGrade = service.getFinalGrade(givenGrade, homework.getDeadlineWeek(), lateProfessor + motivated);
            Integer penalization = (int) (givenGrade - finalGrade);
            if (feedback.length() > 0)
                feedback += "\n";
            if (penalization > 0)
                feedback += "NOTA A FOST DIMINUATA CU " + penalization + " PUNCTE DATORITA INTARZIERILOR";
            if (addGradeConfirmation(homework, student, handOverDate, professor, finalGrade, feedback, penalization))
                try {
                    Grade g = new Grade(new GradeId(homework.getId(), student.getId()), handOverDate, professor.getId(), finalGrade, feedback);
                    service.saveGrade(g, homework, student);
                    entities.add(g);
                    updateAddFields();
                } catch (ValidationException e) {
                    showError("Eroare la adaugare", e.getMessage());
                }
        }
    }

    private Boolean addGradeConfirmation(Homework homework, Student student, LocalDate handOverDate, Professor professor, Double finalGrade, String feedback, Integer penalization) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmare adaugare");
        alert.setHeaderText("Sunteti sigur ca doriti sa salvati aceasta nota?");

        String newGradeString = "Tema: " + homework + "\n"
                + "Student: " + student + "\n"
                + "Data: " + handOverDate.format(dateFormatter) + "\n"
                + "Profesor: " + professor + "\n"
                + "Nota finala: " + finalGrade + ", penalizata cu " + penalization + " puncte\n"
                + "Feedback: " + feedback;

        alert.setContentText(newGradeString);
        return alert.showAndWait().filter(buttonType -> buttonType == ButtonType.OK).isPresent();
    }

    private Boolean validateInputs(Homework homework, Student student, Double givenGrade, Professor professor) {
        String errors = "";
        if (homework == null)
            errors += "Selectati o tema\n";
        if (student == null)
            errors += "Selectati un student\n";
        if (professor == null)
            errors += "Selectati un profesor\n";
        if (givenGrade == null)
            errors += "Acordati o nota\n";

        if (homework == null || student == null || professor == null || givenGrade == null) {
            showError("Eroare la adaugare", errors);
            return false;
        }
        return true;
    }

    @Override
    public void updateAddFields() {
        StreamSupport.stream(service.findAllHomework().spliterator(), false)
                .filter(h -> h.getDeadlineWeek().equals(service.getWeek()))
                .findFirst().ifPresent(homework -> addHomeworkId.setValue(homework));
        addHandOverDate.setText(LocalDate.now().format(dateFormatter));
        lateWeeks.getValueFactory().setValue(0);
        motivatedWeeks.getValueFactory().setValue(0);
        addGivenGrade.setText("");
        addStudentName.setValue(null);
        addFeedback.setText("");
    }

    @Override
    public void searchEntity(Event actionEvent) {
        Homework homework = searchHomeworkId.getValue();
        Student student = searchStudentName.getValue();
        Double givenGrade = DoubleInput(searchGivenGrade.getText());
        Professor professor = searchProfessorName.getValue();
        String handOverDate = searchHandOverDate.getText();
        String feedback = searchFeedback.getText();

        entities.setAll(StreamSupport.stream(service.findAllGrade().spliterator(), false)
                .filter(g -> (homework == null || g.getId().getHomeworkId().equals(homework.getId())) &&
                        (student == null || g.getId().getStudentId().equals(student.getId())) &&
                        (g.getGivenGrade().toString().contains(getShortDoubleString(givenGrade))) &&
                        ((professor == null) || g.getProfessorId().equals(professor.getId())) &&
                        (g.getHandOverDate().format(dateFormatter).contains(handOverDate)) &&
                        (g.getFeedback().contains(feedback)))
                .collect(Collectors.toList()));
    }

    private String getShortDoubleString(Double number) {
        if (number == null)
            return "";
        if (Integer.valueOf(number.intValue()).doubleValue() == number)
            return Integer.valueOf(number.intValue()).toString();
        else
            return number.toString();
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Grade, Object> event) {
        Grade grade = gradeTable.getSelectionModel().getSelectedItem();
        Grade backupGrade = new Grade(grade);
        var newValue = event.getNewValue();
        var oldValue = event.getOldValue();

        if (!newValue.equals(oldValue)) {
            switch (event.getTablePosition().getColumn()) {
                case 0:
                    Homework homework = (Homework) newValue;
                    grade.getId().setHomeworkId(homework.getId());
                    break;
                case 1:
                    Student student = (Student) newValue;
                    grade.getId().setStudentId(student.getId());
                    break;
                case 2:
                    grade.setGivenGrade((Double) newValue);
                    break;
                case 3:
                    Professor professor = (Professor) newValue;
                    grade.setProfessorId(professor.getId());
                    break;
                case 4:
                    grade.setHandOverDate((LocalDate) newValue);
                    break;
                case 5:
                    grade.setFeedback(newValue.toString());
                    break;
            }
            try {
                service.updateGrade(grade.getId(), grade);
            } catch (ValidationException | RepositoryException e) {
                showError("Eroare", e.getMessage());
                entities.set(event.getTablePosition().getRow(), backupGrade);
            }
        }
    }

    @Override
    public void clearFields(ActionEvent actionEvent) {
        searchHomeworkId.setValue(null);
        if (user.getCleranceLevel().ordinal() == CleranceLevel.Student.ordinal()) {
            searchStudentName.setValue(students.stream().filter(s -> s.getId().equals(2)).findFirst().orElse(null));
            searchStudentName.setDisable(true);
        } else
            searchStudentName.setValue(null);
        searchGivenGrade.setText("");
        searchProfessorName.setValue(null);
        searchHandOverDate.setText("");
        searchFeedback.setText("");
        searchEntity(null);
    }

    private void focusState(boolean value) {
        if (value) {
            addGivenGradeGrid.setVisible(true);
            addGivenGradeGrid.setDisable(false);
        } else if (!addGivenGradeGrid.isHover()) {
            addGivenGradeGrid.setVisible(false);
            addGivenGradeGrid.setDisable(true);
        }
    }

    public void openAddGivenGradeGrid(MouseEvent mouseEvent) {
        addGivenGradeGrid.setVisible(true);
        addGivenGradeGrid.setDisable(false);
    }


    public void closeAddGivenGradeGrid(MouseEvent mouseEvent) {
        if (!addGivenGrade.focusedProperty().get()) {
            addGivenGradeGrid.setVisible(false);
            addGivenGradeGrid.setDisable(true);
        }
    }

    @Override
    public void refreshTable() {
        professors.setAll(iterableToList(service.findAllProfessor()));
        students.setAll(iterableToList(service.findAllStudent()));
        homeworks.setAll(iterableToList(service.findAllHomework()));
        gradeTable.refresh();
    }
}

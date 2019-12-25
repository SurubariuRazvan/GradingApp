package ui.gui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import domain.Professor;
import domain.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import repository.RepositoryException;
import ui.utility.ComboBoxEditingCell;
import validation.ValidationException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StudentController extends DefaultController<Student> {

    public TableView<Student> studentTable;
    public TableColumn<Student, Integer> studentTableId;
    public TableColumn<Student, String> studentTableFamilyName;
    public TableColumn<Student, String> studentTableFirstName;
    public TableColumn<Student, Integer> studentTableGroup;
    public TableColumn<Student, String> studentTableEmail;
    public TableColumn<Student, Professor> studentTableProfessor;
    public TableColumn<Student, Void> studentTableDelete;
    public TextField searchId;
    public TextField addId;
    public TextField searchFamilyName;
    public TextField addFamilyName;
    public Button clearButton;
    public Button addButton;
    public TextField searchFirstName;
    public TextField addFirstName;
    public TextField searchEmail;
    public TextField addEmail;
    public ComboBox<Professor> searchProfessorName;
    public ComboBox<Professor> addProfessorName;
    public TextField searchGroup;
    public TextField addGroup;
    public GridPane bottom;

    private ObservableList<Professor> professors;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        studentTableFamilyName.setCellValueFactory(new PropertyValueFactory<>("FamilyName"));
        studentTableFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        studentTableGroup.setCellValueFactory(new PropertyValueFactory<>("Group"));
        studentTableEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        studentTableProfessor.setCellValueFactory((TableColumn.CellDataFeatures<Student, Professor> param) ->
                new ReadOnlyObjectWrapper<>(service.findOneProfessor(param.getValue().getLabProfessorId())));
    }

    @Override
    protected void postInit() {
        entities = iterableToObservableList(service.findAllStudent());
        studentTable.setItems(entities);

        initComponentsByClearance(bottom, CleranceLevel.Admin);
    }

    @Override
    protected void initAdminComponents() {
        studentTableFamilyName.setCellFactory(TextFieldTableCell.forTableColumn());
        studentTableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        studentTableGroup.setCellFactory(x -> integerConverter());
        studentTableEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        studentTableProfessor.setCellFactory((TableColumn<Student, Professor> param) -> new ComboBoxEditingCell<>(professors));
        addButtonToTable(studentTableDelete, "deleteButton", () -> new MaterialDesignIconView(MaterialDesignIcon.MINUS_CIRCLE_OUTLINE, "30"), (i, p) -> {
            service.deleteStudent(p.getId());
            entities.remove(p);
        });
    }

    @Override
    protected void initProfessorComponents() {

    }

    @Override
    protected void initStudentComponents() {
        professors = iterableToObservableList(service.findAllProfessor());
        makeAutoCompleteBox(searchProfessorName, professors);
    }

    @Override
    protected void initAddComponents() {
        makeAutoCompleteBox(addProfessorName, professors);
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId.getText());
        String familyName = addFamilyName.getText();
        String firstName = addFirstName.getText();
        Integer group = IntegerInput(addGroup.getText());
        if (group == null)
            showError("Eroare la adaugare", "Grupa nu are un numar valid");
        else {
            String email = addEmail.getText();
            Professor professor = addProfessorName.getValue();
            if (professor == null)
                showError("Eroare la adaugare", "Studentul trebuie sa aibe un profesor la laborator");
            else {
                Integer labProfessorId = professor.getId();
                if (id != null)
                    try {
                        Student h = new Student(id, familyName, firstName, group, email, labProfessorId);
                        service.saveStudent(h);
                        entities.add(h);
                        updateAddFields();
                    } catch (ValidationException e) {
                        showError("Eroare la adaugare", e.getMessage());
                    }
            }
        }
    }

    @Override
    public void updateAddFields() {
        addId.setText(service.getNextStudentId().toString());
        addFamilyName.setText("");
        addFirstName.setText("");
        addGroup.setText("");
        addEmail.setText("");
        addProfessorName.getEditor().setText("");
    }

    @Override
    public void searchEntity(Event actionEvent) {
        Integer id = IntegerInput(searchId.getText());
        String familyName = searchFamilyName.getText();
        String firstName = searchFirstName.getText();
        Integer group = IntegerInput(searchGroup.getText());
        String email = searchEmail.getText();
        Professor professor = searchProfessorName.getValue();

        entities.setAll(StreamSupport.stream(service.findAllStudent().spliterator(), false)
                .filter(h -> (id == null || h.getId().toString().contains(id.toString())) &&
                        (familyName == null || h.getFamilyName().contains(familyName)) &&
                        (group == null || h.getGroup().toString().contains(group.toString())) &&
                        ((firstName == null) || h.getFirstName().contains(firstName)) &&
                        ((email == null) || h.getEmail().contains(email)) &&
                        ((professor == null) || h.getLabProfessorId().toString().contains(professor.getId().toString()))
                ).collect(Collectors.toList()));
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Student, Object> event) {
        Student student = studentTable.getSelectionModel().getSelectedItem();
        var newValue = event.getNewValue();
        var oldValue = event.getOldValue();
        if (!oldValue.equals(newValue)) {
            Student backupStudent = new Student(student);
            try {
                switch (event.getTablePosition().getColumn()) {
                    case 1:
                        student.setFamilyName(newValue.toString());
                        break;
                    case 2:
                        student.setFirstName(newValue.toString());
                        break;
                    case 3:
                        student.setGroup((Integer) newValue);
                        break;
                    case 4:
                        student.setEmail(newValue.toString());
                        break;
                    case 5:
                        Professor professor = (Professor) newValue;
                        student.setLabProfessorId(professor.getId());
                        break;
                }
                service.updateStudent(student.getId(), student);
            } catch (ValidationException | RepositoryException e) {
                showError("Eroare", e.getMessage());
                entities.set(event.getTablePosition().getRow(), backupStudent);
            }
        }
    }

    @Override
    public void clearFields(ActionEvent actionEvent) {
        searchId.setText("");
        searchFamilyName.setText("");
        searchFirstName.setText("");
        searchGroup.setText("");
        searchEmail.setText("");
        searchProfessorName.getEditor().setText("");
        searchProfessorName.setValue(null);
        searchEntity(null);
    }

    @Override
    public void refreshTable() {
        professors.setAll(iterableToList(service.findAllProfessor()));
        studentTable.refresh();
    }
}

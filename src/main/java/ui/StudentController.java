package ui;

import domain.Professor;
import domain.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;
import repository.RepositoryException;
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
    public TableColumn<Student, String> studentTableProfessor;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));

        studentTableFamilyName.setCellValueFactory(new PropertyValueFactory<>("FamilyName"));
        studentTableFamilyName.setCellFactory(TextFieldTableCell.forTableColumn());

        studentTableFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        studentTableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());

        studentTableGroup.setCellValueFactory(new PropertyValueFactory<>("Group"));
        studentTableGroup.setCellFactory(x -> integerConverter());

        studentTableEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        studentTableEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        studentTableProfessor.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                Professor professor = service.findOneProfessor(param.getValue().getLabProfessorId());
                return new ReadOnlyObjectWrapper<String>(professor.getFamilyName() + " " + professor.getFirstName());
            }
        });
        studentTableProfessor.setCellFactory(TextFieldTableCell.forTableColumn());

        addButtonToTable(studentTableDelete, "Delete", (i, p) -> {
            service.deleteStudent(p.getId());
            entities.remove(p);
            allEntities.remove(p);
        });
    }

    @Override
    protected void postInit() {
        setEntities(service.findAllStudent());
        studentTable.setItems(entities);
        setAllEntities(service.findAllStudent());

        addId.setText(service.getNextStudentId().toString());
        initComboBox();
    }

    private void initComboBox() {
        searchProfessorName.setConverter(new StringConverter<Professor>() {
            @Override
            public String toString(Professor item) {
                if (item != null)
                    return item.toString();
                return "";
            }

            @Override
            public Professor fromString(String string) {
                return searchProfessorName.getItems().stream().filter(item -> string.equals(item.getFamilyName() + " " + item.getFirstName())).findFirst().orElse(null);
            }
        });

        searchProfessorName.setItems(menuController.allProfessors);
        TextFields.bindAutoCompletion(searchProfessorName.getEditor(), menuController.allProfessors);
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId.getText());
        String familyName = addFamilyName.getText();
        String firstName = addFirstName.getText();
        Integer group = IntegerInput(addGroup.getText());
        String email = addEmail.getText();
        //TODO find professor
        //String professorName = addProfessor.getText();
        Integer labProfessorId = 0;
        if (id != null)
            try {
                Student h = new Student(id, familyName, firstName, group, email, labProfessorId);
                service.saveStudent(h);
                entities.add(h);
                allEntities.add(h);
                addId.setText(service.getNextStudentId().toString());
            } catch (ValidationException e) {
                showError("Eroare la adaugare", e.getMessage());
            }
    }

    @Override
    public void searchEntity(Event actionEvent) {
        Integer id = IntegerInput(searchId.getText());
        String familyName = searchFamilyName.getText();
        String firstName = searchFirstName.getText();
        Integer group = IntegerInput(searchGroup.getText());
        String email = searchEmail.getText();
        //TODO find professor
        //String professorName = searchProfessor.getText();
        Integer labProfessorId = null;

        setEntities(StreamSupport.stream(service.findAllStudent().spliterator(), false)
                .filter(h -> (id == null || h.getId().toString().contains(id.toString())) &&
                        (familyName == null || h.getFamilyName().contains(familyName)) &&
                        (group == null || h.getGroup().toString().contains(group.toString())) &&
                        ((firstName == null) || h.getFirstName().contains(firstName)) &&
                        ((email == null) || h.getEmail().contains(email)) &&
                        ((labProfessorId == null) || h.getLabProfessorId().toString().contains(labProfessorId.toString()))

                ).collect(Collectors.toList()));
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Student, Object> event) {
        Student student = studentTable.getSelectionModel().getSelectedItem();
        var newValue = event.getNewValue();
        var oldValue = event.getOldValue();
        if (!newValue.equals(oldValue)) {
            Student backupStudent = new Student(student);
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
                    //TODO find professor
                    student.setLabProfessorId((Integer) newValue);
                    break;
            }

            try {
                service.updateStudent(student.getId(), student);
                allEntities.set(allEntities.indexOf(student), student);
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
        searchEntity(null);
    }
}

package ui.gui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import domain.Professor;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import repository.RepositoryException;
import validation.ValidationException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfessorController extends DefaultController<Professor> {

    public TableView<Professor> professorTable;
    public TableColumn<Professor, Integer> professorTableId;
    public TableColumn<Professor, String> professorTableFamilyName;
    public TableColumn<Professor, String> professorTableFirstName;
    public TableColumn<Professor, String> professorTableEmail;
    public TableColumn<Professor, Void> professorTableDelete;
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
    public GridPane bottom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        professorTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        professorTableFamilyName.setCellValueFactory(new PropertyValueFactory<>("FamilyName"));
        professorTableFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        professorTableEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
    }

    @Override
    protected void postInit() {
        entities = iterableToObservableList(service.findAllProfessor());
        professorTable.setItems(entities);

        initComponentsByClearance(bottom, CleranceLevel.Admin);
    }

    @Override
    protected void initAdminComponents() {
        professorTableFamilyName.setCellFactory(TextFieldTableCell.forTableColumn());
        professorTableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        professorTableEmail.setCellFactory(TextFieldTableCell.forTableColumn());

        addButtonToTable(professorTableDelete, "deleteButton", () -> new MaterialDesignIconView(MaterialDesignIcon.MINUS_CIRCLE_OUTLINE, "30"), (i, p) -> {
            service.deleteProfessor(p.getId());
            entities.remove(p);
            refresh.replace(Tables.StudentTab, true);
            refresh.replace(Tables.GradeTab, true);
        });
    }

    @Override
    protected void initProfessorComponents() {

    }

    @Override
    protected void initStudentComponents() {

    }

    @Override
    protected void initAddComponents() {

    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId.getText());
        String familyName = addFamilyName.getText();
        String firstName = addFirstName.getText();
        String email = addEmail.getText();
        if (id != null)
            try {
                Professor h = new Professor(id, familyName, firstName, email);
                service.saveProfessor(h);
                entities.add(h);
                updateAddFields();
                refresh.replace(Tables.StudentTab, true);
                refresh.replace(Tables.GradeTab, true);
            } catch (ValidationException e) {
                showError("Eroare la adaugare", e.getMessage());
            }
    }

    @Override
    public void updateAddFields() {
        addId.setText(service.getNextProfessorId().toString());
        addFamilyName.setText("");
        addFirstName.setText("");
        addEmail.setText("");
    }

    @Override
    public void searchEntity(Event actionEvent) {
        Integer id = IntegerInput(searchId.getText());
        String familyName = searchFamilyName.getText();
        String firstName = searchFirstName.getText();
        String email = searchEmail.getText();

        entities.setAll(StreamSupport.stream(service.findAllProfessor().spliterator(), false)
                .filter(h -> (id == null || h.getId().toString().contains(id.toString())) &&
                        (familyName == null || h.getFamilyName().contains(familyName)) &&
                        ((firstName == null) || h.getFirstName().contains(firstName)) &&
                        ((email == null) || h.getEmail().contains(email))
                ).collect(Collectors.toList()));
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Professor, Object> event) {
        Professor professor = professorTable.getSelectionModel().getSelectedItem();
        Professor backupProfessor = new Professor(professor);
        var newValue = event.getNewValue();
        var oldValue = event.getOldValue();
        if (!newValue.equals(oldValue)) {
            switch (event.getTablePosition().getColumn()) {
                case 1:
                    professor.setFamilyName(newValue.toString());
                    break;
                case 2:
                    professor.setFirstName(newValue.toString());
                    break;
                case 3:
                    professor.setEmail(newValue.toString());
                    break;
            }

            try {
                service.updateProfessor(professor.getId(), professor);
                refresh.replace(Tables.StudentTab, true);
                refresh.replace(Tables.GradeTab, true);
            } catch (ValidationException | RepositoryException e) {
                showError("Eroare", e.getMessage());
                entities.set(event.getTablePosition().getRow(), backupProfessor);
            }
        }
    }

    @Override
    public void clearFields(ActionEvent actionEvent) {
        searchId.setText("");
        searchFamilyName.setText("");
        searchFirstName.setText("");
        searchEmail.setText("");
        searchEntity(null);
    }

    @Override
    public void refreshTable() {
        professorTable.refresh();
    }
}

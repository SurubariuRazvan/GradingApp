package ui.gui;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import domain.Homework;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import repository.RepositoryException;
import validation.ValidationException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HomeworkController extends DefaultController<Homework> {
    public TableView<Homework> homeworkTable;
    public TextField searchId;
    public TextField addId;
    public TextField searchDescription;
    public TextField addDescription;
    public Spinner<Integer> searchStartWeek;
    public Spinner<Integer> addStartWeek;
    public Spinner<Integer> searchDeadlineWeek;
    public Spinner<Integer> addDeadlineWeek;
    public Button clearButton;
    public Button addButton;
    public TableColumn<Homework, Integer> homeworkTableId;
    public TableColumn<Homework, String> homeworkTableDescription;
    public TableColumn<Homework, Integer> homeworkTableStartWeek;
    public TableColumn<Homework, Integer> homeworkTableDeadlineWeek;
    public TableColumn<Homework, Void> homeworkTableDelete;
    public GridPane bottom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        homeworkTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        homeworkTableStartWeek.setCellValueFactory(new PropertyValueFactory<>("StartWeek"));
        homeworkTableDeadlineWeek.setCellValueFactory(new PropertyValueFactory<>("DeadlineWeek"));
    }

    @Override
    protected void postInit() {
        entities = iterableToObservableList(service.findAllHomework());
        homeworkTable.setItems(entities);

        initComponentsByClearance(bottom, CleranceLevel.Professor);
    }

    @Override
    protected void initAdminComponents() {

    }

    @Override
    protected void initProfessorComponents() {
        homeworkTableDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        homeworkTableStartWeek.setCellFactory(x -> integerConverter());
        homeworkTableDeadlineWeek.setCellFactory(x -> integerConverter());

        addButtonToTable(homeworkTableDelete, "deleteButton", () -> new MaterialDesignIconView(MaterialDesignIcon.MINUS_CIRCLE_OUTLINE, "30"), (i, h) -> {
            service.deleteHomework(h.getId());
            entities.remove(h);
            refresh.replace(Tables.GradeTab, true);
        });
    }

    @Override
    protected void initStudentComponents() {
        initSpinner(searchStartWeek, 0, 14);
        initSpinner(searchDeadlineWeek, 0, 14);
    }

    @Override
    protected void initAddComponents() {
        initSpinner(addStartWeek, 1, 14);
        initSpinner(addDeadlineWeek, 1, 14);
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
        Integer id = IntegerInput(addId.getText());
        String description = addDescription.getText();
        Integer startWeek = addStartWeek.getValue();
        Integer deadlineWeek = addDeadlineWeek.getValue();
        if (id != null)
            try {
                Homework h = new Homework(id, description, startWeek, deadlineWeek);
                service.saveHomework(h);
                entities.add(h);
                updateAddFields();
                refresh.replace(Tables.GradeTab, true);
            } catch (ValidationException e) {
                showError("Eroare la adaugare", e.getMessage());
            }
    }

    @Override
    public void updateAddFields() {
        addId.setText(service.getNextHomeworkId().toString());
        addDescription.setText("");
        addStartWeek.getValueFactory().setValue(service.getWeek());
        if (service.getWeek() >= 14)
            addDeadlineWeek.getValueFactory().setValue(14);
        else
            addDeadlineWeek.getValueFactory().setValue(service.getWeek() + 1);
    }

    @Override
    public void searchEntity(Event actionEvent) {
        Integer id = IntegerInput(searchId.getText());
        String description = searchDescription.getText();
        Integer startWeek = searchStartWeek.getValue();
        Integer deadlineWeek = searchDeadlineWeek.getValue();

        entities.setAll(StreamSupport.stream(service.findAllHomework().spliterator(), false)
                .filter(h -> (id == null || h.getId().toString().contains(id.toString())) &&
                        (h.getDescription().contains(description)) &&
                        ((startWeek == null) || (startWeek == 0) || h.getStartWeek().toString().contains(startWeek.toString())) &&
                        ((deadlineWeek == null) || (deadlineWeek == 0) || h.getDeadlineWeek().toString().contains(deadlineWeek.toString())))
                .collect(Collectors.toList()));
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Homework, Object> event) {
        Homework homework = homeworkTable.getSelectionModel().getSelectedItem();
        Homework backupHomework = new Homework(homework);
        var newValue = event.getNewValue();
        var oldValue = event.getOldValue();
        if (!newValue.equals(oldValue)) {
            switch (event.getTablePosition().getColumn()) {
                case 1:
                    homework.setDescription(newValue.toString());
                    break;
                case 2:
                    homework.setStartWeek((Integer) newValue);
                    break;
                case 3:
                    homework.setDeadlineWeek((Integer) newValue);
                    break;
            }

            try {
                service.updateHomework(homework.getId(), homework);
                refresh.replace(Tables.GradeTab, true);
            } catch (ValidationException | RepositoryException e) {
                showError("Eroare", e.getMessage());
                entities.set(event.getTablePosition().getRow(), backupHomework);
            }
        }
    }

    @Override
    public void clearFields(ActionEvent actionEvent) {
        searchId.setText("");
        searchDescription.setText("");
        searchStartWeek.getValueFactory().setValue(0);
        searchDeadlineWeek.getValueFactory().setValue(0);
        searchEntity(null);
    }

    @Override
    public void refreshTable() {
        homeworkTable.refresh();
    }
}

package ui.gui;

import domain.Homework;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeworkTableId.setCellValueFactory(new PropertyValueFactory<>("Id"));

        homeworkTableDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        homeworkTableDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        homeworkTableStartWeek.setCellValueFactory(new PropertyValueFactory<>("StartWeek"));
        homeworkTableStartWeek.setCellFactory(x -> integerConverter());

        homeworkTableDeadlineWeek.setCellValueFactory(new PropertyValueFactory<>("DeadlineWeek"));
        homeworkTableDeadlineWeek.setCellFactory(x -> integerConverter());

        addButtonToTable(homeworkTableDelete, "Delete", (i, h) -> {
            service.deleteHomework(h.getId());
            entities.remove(h);
            allEntities.remove(h);
        });
    }

    @Override
    protected void postInit() {
        setEntities(service.findAllHomework());
        homeworkTable.setItems(entities);
        setAllEntities(service.findAllHomework());

        initSpinner(addStartWeek, 1, 14);
        initSpinner(addDeadlineWeek, 1, 14);
        initSpinner(searchStartWeek, 0, 14);
        initSpinner(searchDeadlineWeek, 0, 14);

        clearFields(null);
        updateAddFields();
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
                allEntities.add(h);
                updateAddFields();
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

        setEntities(StreamSupport.stream(service.findAllHomework().spliterator(), false)
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
                allEntities.set(allEntities.indexOf(backupHomework), new Homework(homework));
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

package ui.gui;

import domain.Grade;
import domain.Homework;
import domain.Professor;
import domain.Student;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReportController extends DefaultController<Student> {

    public SplitPane gradesAveragePane1;
    public TableView<Student> studentTable1;
    public TableColumn<Student, Integer> studentTableId1;
    public TableColumn<Student, String> studentTableFamilyName1;
    public TableColumn<Student, String> studentTableFirstName1;
    public TableColumn<Student, Integer> studentTableGroup1;
    public TableColumn<Student, String> studentTableEmail1;
    public TableColumn<Student, Professor> studentTableProfessor1;
    public TableColumn<Student, Double> studentTableGradesAverage1;
    public SplitPane onTimeHomeworksPane2;
    public TableView<Student> studentTable2;
    public TableColumn<Student, Integer> studentTableId2;
    public TableColumn<Student, String> studentTableFamilyName2;
    public TableColumn<Student, String> studentTableFirstName2;
    public TableColumn<Student, Integer> studentTableGroup2;
    public TableColumn<Student, String> studentTableEmail2;
    public TableColumn<Student, Professor> studentTableProfessor2;
    public SplitPane examEntryPane3;
    public TableView<Student> studentTable3;
    public TableColumn<Student, Integer> studentTableId3;
    public TableColumn<Student, String> studentTableFamilyName3;
    public TableColumn<Student, String> studentTableFirstName3;
    public TableColumn<Student, Integer> studentTableGroup3;
    public TableColumn<Student, String> studentTableEmail3;
    public TableColumn<Student, Professor> studentTableProfessor3;
    public SplitPane hardestHomeworkPane4;
    public Label hardestHomeworkLabel4;
    public PieChart pie1;
    public PieChart pie2;
    public PieChart pie3;
    public PieChart pie4;
    public StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable(studentTableId1, studentTableFamilyName1, studentTableFirstName1, studentTableGroup1, studentTableEmail1, studentTableProfessor1);
        studentTableGradesAverage1.setCellValueFactory((TableColumn.CellDataFeatures<Student, Double> param) -> new ReadOnlyObjectWrapper<>(getGradesAverage(param.getValue(), service.findAllGrade())));
        initTable(studentTableId2, studentTableFamilyName2, studentTableFirstName2, studentTableGroup2, studentTableEmail2, studentTableProfessor2);
        initTable(studentTableId3, studentTableFamilyName3, studentTableFirstName3, studentTableGroup3, studentTableEmail3, studentTableProfessor3);
    }

    private void initTable(TableColumn<Student, Integer> tc1, TableColumn<Student, String> tc2, TableColumn<Student, String> tc3, TableColumn<Student, Integer> tc4, TableColumn<Student, String> tc5, TableColumn<Student, Professor> tc6) {
        tc1.setCellValueFactory(new PropertyValueFactory<>("Id"));
        tc2.setCellValueFactory(new PropertyValueFactory<>("FamilyName"));
        tc3.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        tc4.setCellValueFactory(new PropertyValueFactory<>("Group"));
        tc5.setCellValueFactory(new PropertyValueFactory<>("Email"));
        tc6.setCellValueFactory((TableColumn.CellDataFeatures<Student, Professor> param) -> {
            Professor professor = service.findOneProfessor(param.getValue().getLabProfessorId());
            return new ReadOnlyObjectWrapper<>(professor);
        });
    }

    private Double getGradesAverage(Student student, Iterable<Grade> grades) {
        Double value = 0.0;
        Integer totalWeight = 0;
        for(Grade grade : grades)
            if (grade.getId().getStudentId().equals(student.getId())) {
                Homework homework = service.findOneHomework(grade.getId().getHomeworkId());
                Integer weight = homework.getDeadlineWeek() - homework.getStartWeek();
                totalWeight += weight;
                value += grade.getGivenGrade() * weight;
            }
//        if(totalWeight==0)
//            return 0.0;
        return value / totalWeight;
    }

    public void showGradesAverage(ActionEvent actionEvent) {
        setEntities(service.findAllStudent());
        studentTable1.setItems(entities);
        gradesAveragePane1.toFront();
    }

    public void showOnTimeHomeworks(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        List<Student> students = StreamSupport.stream(service.findAllStudent().spliterator(), false)
                .filter(student -> {
                    for(Grade grade : grades)
                        if (grade.getId().getStudentId().equals(student.getId())) {
                            Homework homework = service.findOneHomework(grade.getId().getHomeworkId());
                            if (service.getWeek(grade.getHandOverDate()) > homework.getDeadlineWeek())
                                return false;
                        }
                    return true;
                }).collect(Collectors.toList());
        setEntities(students);
        studentTable2.setItems(entities);
        onTimeHomeworksPane2.toFront();
    }

    public void showExamEntry(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        List<Student> students = StreamSupport.stream(service.findAllStudent().spliterator(), false)
                .filter(student -> getGradesAverage(student, grades) > 4)
                .collect(Collectors.toList());
        setEntities(students);
        studentTable3.setItems(entities);
        examEntryPane3.toFront();
    }

    public void showHardestHomework(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        Map<Integer, Pair<Double, Integer>> data = new HashMap<>();
        for(Grade grade : grades) {
            Integer id = grade.getId().getHomeworkId();
            if (data.containsKey(id)) {
                Pair<Double, Integer> value = data.get(id);
                data.put(id, new Pair<>(value.getKey() + grade.getGivenGrade(), value.getValue() + 1));
            } else
                data.put(id, new Pair<>(grade.getGivenGrade(), 1));
        }
        var values = data.entrySet().stream()
                .map(x -> new Pair<>(x.getKey(), x.getValue().getKey() / x.getValue().getValue()))
                .sorted((x, y) -> (int) (x.getValue() - y.getValue()))
                .collect(Collectors.toList());
        hardestHomeworkLabel4.setText(service.findOneHomework(values.get(0).getKey()).toString());
        hardestHomeworkPane4.toFront();
    }

    @Override
    protected void postInit() {
    }

    @Override
    public void addEntity(ActionEvent actionEvent) {
    }

    @Override
    public void updateAddFields() {
    }

    @Override
    public void searchEntity(Event actionEvent) {
    }

    @Override
    public void updateEntity(TableColumn.CellEditEvent<Student, Object> event) {
    }

    @Override
    public void clearFields(ActionEvent actionEvent) {
    }
}

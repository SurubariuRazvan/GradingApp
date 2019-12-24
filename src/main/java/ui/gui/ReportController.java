package ui.gui;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import domain.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
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
    public AreaChart<Double, Integer> chart1;
    public PieChart pie2;
    public PieChart pie3;
    public AreaChart<String, Double> chart4;
    public StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable(studentTableId1, studentTableFamilyName1, studentTableFirstName1, studentTableGroup1, studentTableEmail1, studentTableProfessor1);
        studentTableGradesAverage1.setCellValueFactory((TableColumn.CellDataFeatures<Student, Double> param) -> new ReadOnlyObjectWrapper<>(getGradesAverage(param.getValue(), service.findAllGrade())));
        initTable(studentTableId2, studentTableFamilyName2, studentTableFirstName2, studentTableGroup2, studentTableEmail2, studentTableProfessor2);
        initTable(studentTableId3, studentTableFamilyName3, studentTableFirstName3, studentTableGroup3, studentTableEmail3, studentTableProfessor3);

        chart1.getData().add(new XYChart.Series<>());
        chart4.getData().add(new XYChart.Series<>());
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

    private void saveToPdf(String content, String fileName) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("./src/main/resources/pdf/" + fileName + ".pdf"));
            document.open();
            Chunk chunk = new Chunk(content);
            document.add(chunk);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private <E extends Entity> void saveToPdf(TableView<E> table, String fileName) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("./src/main/resources/pdf/" + fileName + ".pdf"));
            document.open();
            int nrRows = table.getItems().size();
            int nrColumns = table.getColumns().size();
            PdfPTable pdfTable = new PdfPTable(nrColumns);
            addTableHeader(pdfTable, table);
            addData(pdfTable, table, nrRows, nrColumns);
            document.add(pdfTable);
            document.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private <E extends Entity> void addData(PdfPTable pdfTable, TableView<E> table, int nrRows, int nrColumns) {
        String[][] matrix = new String[nrRows][nrColumns];
        var columns = table.getColumns();
        for (int j = 0; j < nrColumns; j++)
            for (int i = 0; i < nrRows; i++)
                matrix[i][j] = columns.get(j).getCellData(i).toString();
        for (int i = 0; i < nrRows; i++)
            for (int j = 0; j < nrColumns; j++)
                pdfTable.addCell(matrix[i][j]);
    }


    private <E extends Entity> void addTableHeader(PdfPTable pdfTable, TableView<E> table) {
        for (var column : table.getColumns()) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(column.getText()));
            pdfTable.addCell(header);
        }
    }


    private Double getGradesAverage(Student student, Iterable<Grade> grades) {
        Double value = 0.0;
        Integer totalWeight = 0;
        for (Grade grade : grades)
            if (grade.getId().getStudentId().equals(student.getId())) {
                Homework homework = service.findOneHomework(grade.getId().getHomeworkId());
                Integer weight = homework.getDeadlineWeek() - homework.getStartWeek();
                totalWeight += weight;
                value += grade.getGivenGrade() * weight;
            }
        if (totalWeight == 0)
            return 0.0;
        return value / totalWeight;
    }

    public void showGradesAverage(ActionEvent actionEvent) {
        studentTable1.setItems(iterableToObservableList(service.findAllStudent()));
        gradesAveragePane1.toFront();

        HashMap<Double, Integer> chartData = new HashMap<>();
        for (int i = 0; i < studentTable1.getItems().size(); i++) {
            Double givenGrade = studentTableGradesAverage1.getCellData(i);
            if (chartData.containsKey(givenGrade))
                chartData.put(givenGrade, chartData.get(givenGrade) + 1);
            else
                chartData.put(givenGrade, 1);
        }

        XYChart.Series<Double, Integer> series = new XYChart.Series<>();
        final Integer[] count = {0};
        chartData.entrySet().stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getKey))
                .forEach(z -> {
                    count[0] += z.getValue();
                    series.getData().add(new XYChart.Data<>(z.getKey(), count[0]));
                });

        chart1.getData().set(0, series);
    }

    public void showOnTimeHomeworks(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        Iterable<Student> students = service.findAllStudent();
        List<Student> onTimeStudents = StreamSupport.stream(students.spliterator(), false)
                .filter(student -> {
                    for (Grade grade : grades)
                        if (grade.getId().getStudentId().equals(student.getId())) {
                            Homework homework = service.findOneHomework(grade.getId().getHomeworkId());
                            if (service.getWeek(grade.getHandOverDate()) > homework.getDeadlineWeek())
                                return false;
                        }
                    return true;
                }).collect(Collectors.toList());
        studentTable2.setItems(FXCollections.observableList(onTimeStudents));
        onTimeHomeworksPane2.toFront();

        long totalStudents = StreamSupport.stream(students.spliterator(), false).count();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Studenti intarziati", totalStudents - onTimeStudents.size()),
                new PieChart.Data("Studenti punctuali", onTimeStudents.size()));

        pie2.setData(pieChartData);
    }

    public void showExamEntry(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        Iterable<Student> students = service.findAllStudent();
        List<Student> filteredStudents = StreamSupport.stream(service.findAllStudent().spliterator(), false)
                .filter(student -> getGradesAverage(student, grades) > 4)
                .collect(Collectors.toList());
        studentTable3.setItems(FXCollections.observableList(filteredStudents));
        examEntryPane3.toFront();

        long totalStudents = StreamSupport.stream(students.spliterator(), false).count();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Studenti care nu pot intra in examen", totalStudents - filteredStudents.size()),
                new PieChart.Data("Studenti care pot intra in examen", filteredStudents.size()));

        pie3.setData(pieChartData);
    }

    public void showHardestHomework(ActionEvent actionEvent) {
        Iterable<Grade> grades = service.findAllGrade();
        Map<Integer, Pair<Double, Integer>> data = new HashMap<>();
        for (Grade grade : grades) {
            Integer id = grade.getId().getHomeworkId();
            if (data.containsKey(id)) {
                Pair<Double, Integer> value = data.get(id);
                data.put(id, new Pair<>(value.getKey() + grade.getGivenGrade(), value.getValue() + 1));
            } else
                data.put(id, new Pair<>(grade.getGivenGrade(), 1));
        }
        List<Pair<Integer, Double>> sortedHomeworks = data.entrySet().stream()
                .map(x -> new Pair<>(x.getKey(), x.getValue().getKey() / x.getValue().getValue()))
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .collect(Collectors.toList());
        hardestHomeworkLabel4.setText(service.findOneHomework(sortedHomeworks.get(0).getKey()).toString());
        hardestHomeworkPane4.toFront();

        XYChart.Series<String, Double> series = new XYChart.Series<>();
        sortedHomeworks.forEach(x -> series.getData().add(new XYChart.Data<String, Double>(service.findOneHomework(x.getKey()).toString(), x.getValue())));

        chart4.getData().set(0, series);
    }

    @Override
    protected void postInit() {
    }

    public void saveGradesAverageToPdf(ActionEvent actionEvent) {
        saveToPdf(studentTable1, "Medii");
    }

    public void saveOnTimeHomeworksToPdf(ActionEvent actionEvent) {
        saveToPdf(studentTable2, "Studenti_punctuali");
    }

    public void saveExamEntryToPdf(ActionEvent actionEvent) {
        saveToPdf(studentTable3, "Studenti_nepicati");
    }

    public void saveHardestHomeworkToPdf(ActionEvent actionEvent) {
        saveToPdf(hardestHomeworkLabel4.getText(), "Cea_mai_grea_tema");
    }

    @Override
    public void refreshTable() {
    }
}

package service;

import domain.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import repository.CrudRepository;
import validation.Validator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class GradeService extends Service<GradeId, Grade> {

    public GradeService(CrudRepository<GradeId, Grade> repo, Validator<Grade> vali, UniversityYearStructure year) {
        super(repo, vali, year);
    }

    /**
     * saves a report with the given grade in a file that has the name of the student
     *
     * @param grade    gets the HomeworkId, GivenGrade, handOverDate, feedback fields
     * @param homework gets the deadlineWeek field
     * @param student  gets the familyName and firstName fields
     */
    @SuppressWarnings("unchecked")
    public void saveInStudentNameFile(Grade grade, Homework homework, Student student) {
        String fileName = "./src/main/resources/studentGrades/" + student.getFamilyName() + student.getFirstName() + ".json";
        JSONArray grades = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        try {
            grades = (JSONArray) jsonParser.parse(new FileReader(fileName));
        } catch (FileNotFoundException ignored) {

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        JSONObject o = new JSONObject();
        o.put("Tema", grade.getId().getHomeworkId());
        o.put("Nota", grade.getGivenGrade());
        o.put("Predată în săptămâna", year.getWeek(grade.getHandOverDate()));
        o.put("Deadline", homework.getDeadlineWeek());
        o.put("Feedback", grade.getFeedback());
        grades.add(o);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(grades.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a new Grade object, auto-generating the handOverDate and calculating the givenGrade
     *
     * @param student       Grade field
     * @param professor     Grade field
     * @param givenGrade    Grade field
     * @param homework      Grade field
     * @param feedback      Grade field
     * @param lateProfessor Grade field
     * @return a new Grade object
     */
    public Grade createGrade(Student student, Professor professor, Double givenGrade, Homework homework, String feedback, Integer lateProfessor) {
        Grade grade = new Grade(LocalDate.now(), professor.getId(), givenGrade, feedback);
        vali.validate(grade);
        grade.setGivenGrade(calculateFinalGrade(givenGrade, homework.getDeadlineWeek(), lateProfessor));
        grade.setId(new GradeId(homework.getId(), student.getId()));
        return grade;
    }

    public Double calculateFinalGrade(Double givenGrade, Integer deadlineWeek, Integer lateProfessor) {
        Integer late = (year.getWeek(LocalDate.now()) - lateProfessor) - deadlineWeek;
        if (late > 0 && late <= 2)
            givenGrade -= late;
        else if (late > 2)
            givenGrade = 1.0;
        return givenGrade;
    }
}
